package com.loy.down

import android.annotation.SuppressLint
import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import autodispose2.autoDispose
import com.loy.down.ext.apkRootPath
import com.loy.down.ext.defaultScheduler
import com.loy.down.net.DownLoadApi
import com.loy.down.net.DownLoadService
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 *@Author loy
 *创建时间：2021/8/10 11:47
 *description: downLoadApk 使用系统下载
 *             startDownload 自定义进度下载 progressLiveData 需要订阅下载 进度和状态 -1 失败 101 完成 else 进度1-100
 */
class UpdateViewModel(application: Application) : AutoDisposeAndroidViewModel(application) {


    private var downloadManager: DownloadManager? = null

    private var mDownId: Long? = null

    private var savePath: String? = null

    val hasCanDown = MutableLiveData<Boolean>()


    private val contentUri: Uri = Uri.parse("content://downloads/my_downloads")

    // 下载apk的进度
    val progressLiveData = MutableLiveData<Int>()

    val onCompletLiveData = MutableLiveData<File>()

    val onErrorLiveData = MutableLiveData<Throwable>()

    private val receiver by lazy {
        return@lazy object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val completeDownloadId = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                checkStatus(completeDownloadId)
            }
        }
    }
    private val downloadObserver by lazy {
        return@lazy object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                checkStatus(mDownId ?: 0)
            }
        }
    }


    init {
        getApplication<Application>().registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        getApplication<Application>().contentResolver.registerContentObserver(contentUri, true, downloadObserver)
    }


    fun downLoadApk(url: String, folderPath: String, fileName: String, downTitle: String, downDescription: String) {
        savePath = isExistDir(folderPath, fileName).toString()
        //创建request对象
        val request = DownloadManager.Request(Uri.parse(url))
        //设置什么网络情况下可以下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        //设置通知栏的标题
        request.setTitle(downTitle)
        //设置通知栏的message
        request.setDescription(downDescription)
        //设置漫游状态下是否可以下载
        request.setAllowedOverRoaming(false)
        //设置文件存放目录
        request.setDestinationInExternalFilesDir(getApplication(), folderPath, fileName)
        //获取系统服务
        downloadManager =
            getApplication<Application>().getSystemService(Context.DOWNLOAD_SERVICE) as (DownloadManager)
        //进行下载
        mDownId = downloadManager?.enqueue(request)
    }


    fun getVersionName(application: Application): String {
        val packageManager = application.packageManager
        val info = packageManager.getPackageInfo(application.packageName, 0)
        return info.versionName
    }


    //检查下载状态
    @SuppressLint("Range")
    private fun checkStatus(downId: Long) {
        val query = DownloadManager.Query()
        //通过下载的id查找
        query.setFilterById(downId)
        val cursor = downloadManager?.query(query)
        if (cursor?.moveToFirst() == true) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                //下载暂停
                DownloadManager.STATUS_PAUSED -> {
                }

                //下载延迟
                DownloadManager.STATUS_PENDING -> {

                }

                //正在下载
                DownloadManager.STATUS_RUNNING -> {
                    hasCanDown.value = false
                }

                //下载完成
                DownloadManager.STATUS_SUCCESSFUL -> {
                    //下载完成安装APK
                    hasCanDown.value = true
                    installApk(File(savePath ?: ""))
                    cursor.close()
                }

                //下载失败
                DownloadManager.STATUS_FAILED -> {
                    hasCanDown.value = true
                    cursor.close()
                    downloadManager?.remove(downId)

                }
            }
        } else {
            if (downId > 0) {
                downloadManager?.remove(downId)
                hasCanDown.value = true
                mDownId = -1
            }

        }
    }


    /**
     * 安装apk
     * @param apkFile APK文件
     */
    fun installApk(apkFile: File) {
        // MD5校验
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            val apkUri = FileProvider.getUriForFile(
                getApplication<Application>(),
                //与manifest中定义的provider中的authorities="${applicationId}.FileProvider"保持一致
                "${getApplication<Application>().packageName}.FileProvider",
                apkFile
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        getApplication<Application>().startActivity(intent)
    }


    /**
     *
     * @param fileUrl String 地址
     * @param filePath String 路径
     * @param fileName String 文件名
     */
    fun startDownload(fileUrl: String, filePath: String, fileName: String) {
        DownLoadService.createApiService(DownLoadApi::class.java)
            .download(fileUrl)
            .map {
                val fileSize = it.contentLength()
                //写入文件
                return@map writeFile(
                    filePath,
                    fileName,
                    it,
                    fileSize
                ) { pro ->
                    Log.d("down", "apk下载中$pro")
                    progressLiveData.postValue(pro)
                }
            }
            .defaultScheduler()
            .autoDispose(this)
            .subscribe({
                onCompletLiveData.value = it
            }, {
                onErrorLiveData.value = it
            })
    }

    /**
     * 写文件
     * @param folderPath String 文件夹路径
     * @param fileName String  文件名
     * @param response ResponseBody 相应结果 io
     * @param totalLength Long  合并请求的总大小
     * @param pro Function1<Int, Unit>  回调读写进度
     * @return File 返回读写后 文件路径
     */
    private fun writeFile(
        folderPath: String,
        fileName: String,
        response: ResponseBody,
        totalLength: Long,
        pro: (Int) -> Unit
    ): File {
        var inputStream: InputStream? = null
        val buf = ByteArray(1024)
        var len: Int
        var fos: FileOutputStream? = null
        File(folderPath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val savePath = isExistDir(folderPath, fileName).toString()
        try {
            inputStream = response.byteStream()
            val file = File(savePath)

            fos = FileOutputStream(file)
            var sum: Long = 0
            while (true) {
                len = inputStream.read(buf)
                if (-1 == len) {
                    break
                }
                fos.write(buf, 0, len)
                sum += len.toLong()
                val progress = (sum * 1.0f / totalLength * 100).toInt()
                // 下载中
                pro(progress)
            }
            fos.flush()
            //下载成功
        } catch (e: Exception) {
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
            }
            try {
                fos?.close()
            } catch (e: IOException) {

            }
        }
        return File(savePath)
    }

    /**
     * 文件是否存在 不存在创建
     * @param folderPath String  路径
     * @param fileName String  路径
     * @return String?
     */
    private fun isExistDir(folderPath: String, fileName: String): String? {
        // 下载位置
        val file = File(folderPath)
        if (!file.exists()) {
            file.mkdir()
        }
        val fileNew = File(folderPath, fileName)
        if (!fileNew.exists()) {
            fileNew.createNewFile()
        }
        return fileNew.absolutePath
    }


    fun getAppName(application: Application): String {
        val packageManager = application.packageManager
        val applicationInfo = application.applicationInfo
        return packageManager.getApplicationLabel(applicationInfo).toString()
    }


    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(receiver)
        getApplication<Application>().contentResolver.unregisterContentObserver(downloadObserver)
    }

}