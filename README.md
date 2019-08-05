# UpdateAppUtil
一键升级 已经适配Android 6、7、8、9


使用步骤：
要将Git项目放入您的构建中：

步骤1.将JitPack存储库添加到构建文件中

gradle

将其添加到存储库末尾的根build.gradle中：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
步骤2.添加依赖项

	dependencies {
	        implementation 'com.github.cnobita:UpdateAppUtil:v2.0'
	}
	
	使用说明

此版本为请求权限，使用前请获取SD卡、网络、网络状态等相关权限

方法一 内置ProgressDialog进度条下载

UpdateAppUtils.from(MainActivity.this)
                        .setApkUrl(url)
                        .initXutils3(getApplication())
                        .setServerVersionCode(2)
                        .setUpdateInfo("这里更新！")
                        .start();
方法二 使用浏览器下载

UpdateAppUtils.from(MainActivity.this)
                        .setApkUrl(url)
                        .setDownLoadByType(UpdateAppUtils.DOWNLOAD_BY_BROWSER)
                        .setServerVersionCode(2)
                        .setUpdateInfo("")
                        .start();
方法三 使用广播显示进度下载

UpdateAppUtils.from(MainActivity.this)
                        .initXutils3(getApplication())
                        .setApkUrl(url)
                        .setDownLoadByType(UpdateAppUtils.DOWNLOAD_BY_BROADCAST)
                        .setServerVersionCode(2)
                        .setUpdateInfo("")
                        .start();                       
