package cn.nobita.updatelibrary;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UpdateAppUtils {

    private final String TAG = "UpdateAppUtils";
    private Context context;
    private String localVersionName;
    private String serverVersionName = "";
    private int localVersionCode;
    private int serverVersionCode;
    private String apkUrl = "";
    public static final int CHECK_BY_NAME = 1001;
    public static final int CHECK_BY_CODE = 1002;
    public static final int DOWNLOAD_BY_BROADCAST = 1003;
    public static final int DOWNLOAD_BY_APP = 1004;
    public static final int DOWNLOAD_BY_BROWSER = 1005;
    private int downloadType = DOWNLOAD_BY_APP;
    private int compareBy = CHECK_BY_CODE;
    private CallBack callBackMsg;
    private String updateInfo = "";
    private Application application;
    public static boolean isShowBroadcase = false;
    public static boolean isForcedUpdate = false;
    protected static final int REQUEST_CODE_LOCAL = 2;

    public interface CallBack {
        void backMassage(String msg);

        void sureback();

        void cancelback();
    }

    public static UpdateAppUtils from(Context Context) {
        return new UpdateAppUtils(Context);
    }


    private UpdateAppUtils(Context context) {
        this.context = context;
        getAppLocalVersion(context);
    }

    /**
     * 回调消息
     *
     * @param callBackMsg
     * @return
     */
    public UpdateAppUtils setCallBackMassage(CallBack callBackMsg) {
        this.callBackMsg = callBackMsg;
        return this;
    }

    /**
     * 使用广播显示进度时候设置
     */
    public UpdateAppUtils setIsShowBroadcaseProgress(boolean isShow) {
        UpdateAppUtils.isShowBroadcase = isShow;
        return this;
    }

    /**
     * 添加下载地址
     *
     * @param apkUrl
     * @return
     */
    public UpdateAppUtils setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
        return this;
    }

    /**
     * 是否强制更新
     *
     * @param isForcedUpdate
     * @return
     */
    public UpdateAppUtils setForcedUpdate(boolean isForcedUpdate) {
        UpdateAppUtils.isForcedUpdate = isForcedUpdate;
        return this;
    }

    /**
     * 设置使用下载方式
     *
     * @param type
     * @return
     */
    public UpdateAppUtils setDownLoadByType(int type) {
        this.downloadType = type;
        return this;
    }

    /**
     * 设置服务器版本名字
     *
     * @param serverVersionName
     * @return
     */
    public UpdateAppUtils setServerVersionName(String serverVersionName) {
        this.serverVersionName = serverVersionName;
        return this;
    }

    /**
     * 设置服务器版本号
     *
     * @param serverVersionCode
     * @return
     */
    public UpdateAppUtils setServerVersionCode(int serverVersionCode) {
        this.serverVersionCode = serverVersionCode;
        return this;
    }

    /**
     * 数据比对方式
     *
     * @param compareBy
     * @return
     */
    public UpdateAppUtils compare(int compareBy) {
        this.compareBy = compareBy;
        return this;
    }

    public UpdateAppUtils setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
        return this;
    }

    public UpdateAppUtils initXutils3(Application application) {
        this.application = application;
        return this;
    }

    /**
     * 更新
     */
    public void start() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            /**
             * Android 6.0申请权限
             */
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_LOCAL);
        } else {

            compareVersion();
        }
        //检查权限
        PermissionUtil.checkMorePermissions(context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionUtil.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        compareVersion();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        // 上一次申请权限被拒绝，可用于向用户说明权限原因，然后调用权限申请方法。
                        if (null != callBackMsg) {
                            callBackMsg.backMassage("权限获取失败：" + permission);
                        }
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        // 第一次申请权限或被禁止申请权限，建议直接调用申请权限方法。
                        // 显示前往设置页的dialog
                        try {
                            if (null != callBackMsg) {
                                callBackMsg.backMassage("请打开权限：" + permission);
                            }
                            PermissionUtil.toAppSetting(context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    /**
     * 比对版本
     */
    private void compareVersion() {
        switch (compareBy) {
            case CHECK_BY_CODE:
                if (serverVersionCode > localVersionCode) {
                    goUpdate();
                } else {
                    if (null != callBackMsg) {
                        callBackMsg.backMassage("当前版本:" + serverVersionCode + "已经是最新版本");
                    }
                    Log.i(TAG, "当前版本:" + serverVersionCode + "已经是最新版本");
                }
                break;
            case CHECK_BY_NAME:
                if (!serverVersionName.equals(localVersionName)) {
                    goUpdate();
                } else {
                    if (null != callBackMsg) {
                        callBackMsg.backMassage("当前版本:" + localVersionName + "已经是最新版本");
                        Log.i(TAG, "当前版本:" + localVersionName + "已经是最新版本");
                    }
                }
                break;
        }
    }

    private void goUpdate() {
        if ("".equals(updateInfo)) {
            updateInfo = "发现新版本:" + serverVersionName + "是否下载更新?";
        } else {
            updateInfo = "发现新版本:" + serverVersionName + "是否下载更新?\n" + "更新提示：" + updateInfo;
        }
        new NoticeAlertDialog(context, isForcedUpdate, new NoticeAlertDialog.Callback() {
            @Override
            public void callbackSure() {
                if (null != callBackMsg) {
                    callBackMsg.sureback();
                }
                if (!isWifi(context)) {
                    new NoticeAlertDialog(context, isForcedUpdate, new NoticeAlertDialog.Callback() {
                        @Override
                        public void callbackSure() {
                            //执行下载
                            goDownLoad();
                        }

                        @Override
                        public void callbackCancel() {
                            if (null != callBackMsg) {
                                callBackMsg.cancelback();
                            }
                        }
                    }).setContent("当前连接移动网络，确认是否继续下载更新？").showView();
                } else {
                    //执行下载
                    goDownLoad();
                }

            }

            @Override
            public void callbackCancel() {
                if (null != callBackMsg) {
                    callBackMsg.cancelback();
                }
            }
        })
                .setContent(updateInfo)
                .showView().setCancelable(false);
    }

    /**
     * 执行下载方法
     */
    private void goDownLoad() {
        if ("".equals(apkUrl)) {
            if (null != callBackMsg) {
                callBackMsg.backMassage("下载地址为空");
            }
            return;
        }

        switch (downloadType) {
            //app 内ProgressDialog 下载
            case DOWNLOAD_BY_APP:
                if (null == application) {
                    DownloadAppUtils.download(context, apkUrl, getAppName() + ".apk", false, new ProgressDialog(context));
                } else {
                    DownloadAppUtils.download(application, true, context, apkUrl, getAppName() + ".apk");
                }
                break;
            //启动广播显示进度条下载
            case DOWNLOAD_BY_BROADCAST:
                DownloadAppUtils.downloadForBroadcast(application, true, true, context, apkUrl, getAppName() + ".apk");
                break;
            //启动浏览器下载
            case DOWNLOAD_BY_BROWSER:
                DownloadAppUtils.downloadForWebView(context, apkUrl);
                break;
        }


    }

    /**
     * 检测wifi是否连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    private void getAppLocalVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            //版本名称
            localVersionName = info.versionName;
            //版本名称
            localVersionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取应用程序名称
     */
    private String getAppName() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "newApp";
    }
}
