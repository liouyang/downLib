package com.example.myapplication

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.loy.down.DownApkManager
import com.loy.down.net.IDownOnListener
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        //默认路径 包名/cache/apk/ 无需权限
//        DownApkManager.instance.downLoad(this, "url", object : IDownOnListener {
//            override fun onStart() {
//                //开始
//            }
//
//            /**
//             * 下载之后的路径
//             * @param t File?
//             */
//            override fun onComplete(t: File?) {
//                //完成
//            }
//
//            /**
//             * 进度
//             * @param readLength Long 下载长度
//             * @param countLength Long 总字节长度
//             * @param progress Int 进度 1-100
//             */
//            override fun updateProgress(readLength: Long, countLength: Long, progress: Int) {
//                //进度
//            }
//
//            override fun onError(e: Throwable?) {
//                //异常
//            }
//
//        })
//
//        // 自定义路径
//        DownApkManager.instance.downLoad(this, "filePath", "fileName", "url", object : IDownOnListener {
//            override fun onStart() {
//                //开始
//            }
//
//            /**
//             * 下载之后的路径
//             * @param t File?
//             */
//            override fun onComplete(t: File?) {
//                //完成
//            }
//
//            /**
//             * 进度
//             * @param readLength Long 下载长度
//             * @param countLength Long 总字节长度
//             * @param progress Int 进度 1-100
//             */
//            override fun updateProgress(readLength: Long, countLength: Long, progress: Int) {
//                //进度
//            }
//
//            override fun onError(e: Throwable?) {
//                //异常
//            }
//
//        })
    }
}