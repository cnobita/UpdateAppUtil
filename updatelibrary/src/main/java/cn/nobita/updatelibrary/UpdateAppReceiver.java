package cn.nobita.updatelibrary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.io.File;

public class UpdateAppReceiver extends BroadcastReceiver {
    private NotificationManager nm = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        int notifyId = 1;
        int progress = intent.getIntExtra("progress", 0);
        int total = intent.getIntExtra("total", 100);
        boolean sound = intent.getBooleanExtra("sound", false);
        String title = intent.getStringExtra("title");

        if (Build.VERSION.SDK_INT >= 26) {
            String channelID = "1";
            String channelName = "channel_name";
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            Notification notification = new Notification.Builder(context, channelID)
                    .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    .setContentTitle("正在下载" + title)
                    .setProgress(total, progress, false)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .build();
            if (null == nm) {
                nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            nm.createNotificationChannel(channel);
            nm.notify(notifyId, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("正在下载" + title);
            builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
            builder.setProgress(total, progress, false);
            Notification notification = builder.build();
            if (sound) {
                notification.defaults = Notification.DEFAULT_SOUND;
            }
            if (null == nm) {
                nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            nm.notify(notifyId, notification);
        }

        if (progress == 100) {
            if (nm != null) {
                nm.cancel(notifyId);
            }

            if (DownloadAppUtils.downloadUpdateApkFilePath != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                File apkFile = new File(DownloadAppUtils.downloadUpdateApkFilePath);
                CallSystemAction.openApk(context, apkFile.getAbsolutePath());
            }
        }
    }
}
