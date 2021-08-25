##  downLib

```中文
一个简单下载file 以及APK
```

```
//项目下的build.gradle 添加
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
      	...
    }
}
```

```
//maven
implementation 'com.github.liouyang:downLib:v1.0'
```



#### for Java Writing

```
 //默认路径 包名/cache/apk/ 无需权限
        DownApkManager.instance.downLoad(this, "url", object : IDownOnListener {
            override fun onStart() {
                //开始
            }

            /**
             * 下载之后的路径
             * @param t File?
             */
            override fun onComplete(t: File?) {
                //完成
            }

            /**
             * 进度
             * @param readLength Long 下载长度
             * @param countLength Long 总字节长度
             * @param progress Int 进度 1-100
             */
            override fun updateProgress(readLength: Long, countLength: Long, progress: Int) {
                //进度
            }

            override fun onError(e: Throwable?) {
                //异常
            }

        })
```

```
// 自定义路径
DownApkManager.instance.downLoad(this, "filePath", "fileName", "url", object : IDownOnListener {
    override fun onStart() {
        //开始
    }

    /**
     * 下载之后的路径
     * @param t File?
     */
    override fun onComplete(t: File?) {
        //完成
    }

    /**
     * 进度
     * @param readLength Long 下载长度
     * @param countLength Long 总字节长度
     * @param progress Int 进度 1-100
     */
    override fun updateProgress(readLength: Long, countLength: Long, progress: Int) {
        //进度
    }

    override fun onError(e: Throwable?) {
        //异常
    }

})
```

## for viewModel Writing

```
private val updateViewModel by viewModels<UpdateViewModel>()
```

```
 /**
     *使用系统的DownloadManager 下载APK 下载完成会自动安装
     * @param fileUrl String 地址
     * @param filePath String 路径
     * @param fileName String 文件名
     */
updateViewModel.downLoadApk(fileUrl: String, filePath: String, fileName: String)
```



```
 /**
     *自定义下载
     * @param fileUrl String 地址
     * @param filePath String 路径
     * @param fileName String 文件名
     */
updateViewModel.startDownload(fileUrl: String, filePath: String, fileName: String)

 /** 订阅
     * 进度
     */
  updateViewModel.progressLiveData.observe(this, {int-> })

   /** 订阅
     *完成
     */
  updateViewModel.onCompletLiveData.observe(this, {file-> })

   /** 订阅
     * 失败
     */
  updateViewModel.onErrorLiveData.observe(this, {throwable-> })


```

------

```
 /**
     *安装apk方法
     * @param apkFile String 文件地址
     */
updateViewModel.installApk(apkFile: File)
```

