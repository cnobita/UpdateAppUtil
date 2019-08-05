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
