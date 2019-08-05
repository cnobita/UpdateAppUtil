package cn.nobita.updatelibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class CallSystemAction {
    private static final String TAG = "CallSystemAction";

    public static void openApk(Context context, String filePath) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            File apkFile = new File(filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                i.setDataAndType(contentUri, "application/vnd.android.package-archive");
                Log.e(TAG, Uri.fromFile(apkFile).getPath());
            } else {
                i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
