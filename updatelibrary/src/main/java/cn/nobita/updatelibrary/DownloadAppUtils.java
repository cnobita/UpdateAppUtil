package cn.nobita.updatelibrary;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class DownloadAppUtils {
    private static final String TAG = DownloadAppUtils.class.getSimpleName();
    public static long downloadUpdateApkId = -1;//下载更新Apk 下载任务对应的Id
    public static String downloadUpdateApkFilePath;//下载更新Apk 文件路径

    private static Callback.Cancelable cancelable;
    private static UpdateAppReceiver mReceiver;


    /**
     * 通过浏览器下载APK包
     *
     * @param context
     * @param url
     */
    public static void downloadForWebView(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //启动手机中的下载器
        //intent.setDataAndType(uri,"application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 使用xutils 内置ProgressDialog 下载
     *
     * @param application 初始化xtuils3
     * @param isDebug     是否开启调试模式
     * @param context
     * @param url         下载文件地址
     * @param saveApkName 存储文件名字
     */
    public static void download(Application application, boolean isDebug, final Context context, String url, final String saveApkName) {
        if (null != application) {
            x.Ext.init(application);
            if (isDebug) {
                x.Ext.setDebug(isDebug);
            }
        }
        download(context, url, saveApkName, false, new ProgressDialog(context));
    }

    public static void download(final Context context, String url, final String saveApkName, final boolean isShowBroadcase, final ProgressDialog mProgressDialog) {
        if (isShowBroadcase) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("cn.cnobita.updateapk");
            mReceiver = new UpdateAppReceiver();
            context.registerReceiver(mReceiver, filter);
        }

        final String apkLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName() + File.separator + getAppName(context) + ".apk";
        File apk = new File(apkLocalPath);
        if (apk.exists()) {
            apk.delete();
        }

        downloadUpdateApkFilePath = apkLocalPath;
        // mDownloadUrl为JSON从服务器端解析出来的下载地址
        RequestParams requestParams = new RequestParams(url);
        // 为RequestParams设置文件下载后的保存路径
        requestParams.setSaveFilePath(apkLocalPath);
        // 下载完成后自动为文件命名
        requestParams.setAutoRename(false);
        cancelable = x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Log.i(TAG, "下载成功");
                CallSystemAction.openApk(context, apkLocalPath);
                if (!isShowBroadcase) {
                    mProgressDialog.dismiss();
                } else {
                    unRegisterReceiver(context);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "下载失败" + ex.getMessage());

                if (!isShowBroadcase) {
                    mProgressDialog.dismiss();
                } else {
                    unRegisterReceiver(context);
                }
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "取消下载");
                if (!isShowBroadcase) {
                    mProgressDialog.dismiss();
                } else {
                    unRegisterReceiver(context);
                }
                cex.printStackTrace();
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "结束下载");
                if (!isShowBroadcase) {
                    mProgressDialog.dismiss();
                } else { //unRegisterReceiver(context);
                }
            }

            @Override
            public void onWaiting() {
                // 网络请求开始的时候调用
                Log.i(TAG, "等待下载");
            }

            @Override
            public void onStarted() {
                // 下载的时候不断回调的方法
                Log.i(TAG, "开始下载");
                if (!isShowBroadcase) {
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMessage("正在下载中......");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (null != cancelable) {
                                cancelable.cancel();
                            }
                        }
                    });
                    mProgressDialog.show();
                } else {
                    send(context, 0, 100, saveApkName, true);
                }

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.e("cyx", "apk总大小 " + total);
                // 当前的下载进度和文件总大小
                int unitRate = 1024;
                long totalKb = total / unitRate;
                long currentKb = current / unitRate;
                long totalMb = totalKb / unitRate;
                long currentMb = currentKb / unitRate;
                if (!isShowBroadcase) {
                    if (totalKb <= unitRate) {
                        mProgressDialog.setProgressNumberFormat("%1d Kb /%2d Kb");
                        mProgressDialog.setMax((int) totalKb);
                        mProgressDialog.setProgress((int) currentKb);
                    } else {
                        mProgressDialog.setProgressNumberFormat("%1d Mb /%2d Mb");
                        mProgressDialog.setMax((int) totalMb);
                        mProgressDialog.setProgress((int) currentMb);
                    }
                } else {
                    double c;
                    if (totalKb <= unitRate) {
                        c = (double) currentKb / totalKb;
                    } else {
                        c = (double) currentMb / totalMb;
                    }
                    int curr = (int) (c * 100);
                    send(context, curr, 100, saveApkName, false);
                }
            }
        });

    }

    /**
     * 获取应用程序名称
     */
    private static String getAppName(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context. getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "newApp";
    }


    private static void unRegisterReceiver(Context context) {
        try {
            if (null != mReceiver) {
                context.unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void send(Context context, int progress, int total, String saveApkName, boolean sound) {
        Intent intent = new Intent("cn.cnobita.updateapk");
        intent.putExtra("total", total);
        intent.putExtra("progress", progress);
        intent.putExtra("sound", sound);
        intent.putExtra("title", saveApkName);
        context.sendBroadcast(intent);
    }

    public static void downloadForBroadcast(Application application, boolean isDebug, boolean isShowBroadcase, Context context, String apkUrl, String saveApkName) {
        download(application, isDebug, context, apkUrl, saveApkName, isShowBroadcase);
    }

    public static void download(Application application, boolean isDebug, final Context context, String url, final String saveApkName, boolean isShowBroadcase) {
        if (null != application) {
            x.Ext.init(application);
            if (isDebug) {
                x.Ext.setDebug(isDebug);
            }
        }
        download(context, url, saveApkName, isShowBroadcase, null);
    }
}
