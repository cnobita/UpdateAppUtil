package cn.nobita.updateapputil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.nobita.updatelibrary.UpdateAppUtils;

public class MainActivity extends AppCompatActivity {
    String url = "http://120.25.166.17:9098/TSCWEB/ahyg/app-v17.apk";
//    String url = "http://120.25.166.17:9098/TSCWEB/ahyg/app.apk";
//    String url = "http://120.25.166.17:9098/TSCWEB/ahyg/ap.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button tvBtn = findViewById(R.id.tv_btn);
        Button tvBtn2 = findViewById(R.id.tv_btn2);
        Button tvBtn3 = findViewById(R.id.tv_btn3);
        tvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAppUtils.from(MainActivity.this)
                        .setApkUrl(url)
                        .initXutils3(getApplication())
                        .setServerVersionCode(2)
                        .setForcedUpdate(true)
                        .setServerVersionName("v2.0")
                        .setUpdateInfo("1.优化了界面；2.提升了启动速度")
                        .start();
            }
        });
        tvBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAppUtils.from(MainActivity.this)
                        .setApkUrl(url)
                        .setForcedUpdate(true)
                        .setDownLoadByType(UpdateAppUtils.DOWNLOAD_BY_BROWSER)
                        .setServerVersionCode(2)
                        .setUpdateInfo("")
                        .start();
            }
        });
        tvBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAppUtils.from(MainActivity.this)
                        .initXutils3(getApplication())
                        .setApkUrl(url)
                        .setForcedUpdate(true)
                        .setDownLoadByType(UpdateAppUtils.DOWNLOAD_BY_BROADCAST)
                        .setServerVersionCode(2)
                        .setUpdateInfo("")
                        .start();
            }
        });
    }
}
