package com.loy.down

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.androidx.lifecycle.autoDispose
import com.loy.down.ext.apkRootPath
import com.loy.down.ext.defaultScheduler
import com.loy.down.net.DownLoadApi
import com.loy.down.net.DownLoadService
import com.loy.down.net.IDownOnListener
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 *@Author loy
 *创建时间：2021/8/7 17:32
 *description:
 */
class DownApkManager {

    companion object {
        val instance: DownApkManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownApkManager()
        }
    }


    fun downLoad(appCompatActivity: AppCompatActivity, url: String, iDownOnListener: IDownOnListener?) {
        downLoad(
            appCompatActivity, appCompatActivity.application.apkRootPath(),
            "${
                getAppName(
                    appCompatActivity.application
                )
            }-${
                System.currentTimeMillis()
            }.apk", url, iDownOnListener
        )
    }

    private fun getAppName(application: Application): String {
        val packageManager = application.packageManager
        val applicationInfo = application.applicationInfo
        return packageManager.getApplicationLabel(applicationInfo).toString()
    }

    fun downLoad(
        lifecycleOwner: LifecycleOwner,
        filePath: String,
        fileName: String,
        url: String,
        iDownOnListener: IDownOnListener?
    ) {
        DownLoadService.createApiService(DownLoadApi::class.java)
            .download(url)
            .map {
                val fileSize = it.contentLength()
                //写入文件
                return@map writeFile(
                    filePath,
                    fileName,
                    it,
                    fileSize
                ) { readLength: Long, pro: Int ->
                    Log.d("down", "apk下载中$pro")
                    iDownOnListener?.updateProgress(readLength, fileSize, pro)
                }
            }
            .defaultScheduler()
            .autoDispose(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .subscribe(object : Observer<File> {
                override fun onSubscribe(d: Disposable?) {
                    iDownOnListener?.onStart()
                }

                override fun onNext(t: File?) {
                    iDownOnListener?.onComplete(t)
                }

                override fun onError(e: Throwable?) {
                    iDownOnListener?.onError(e)
                }

                override fun onComplete() {
                }
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
        pro: (readLength: Long, pro: Int) -> Unit
    ): File {
        var inputStream: InputStream? = null
        val buf = ByteArray(1024)
        var len = 0
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
                pro(sum, progress)
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
     * @return String?
     */

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

}