package com.loy.down.ext

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.math.BigDecimal
import java.util.*


/**
 * 所有存储都需调用getExternalFilesDir，并传入FolderPath.kt中的变量来获取存储目录
 */

/**
 * @author larson.
 * @date 2019-12-11
 * @description 文件夹路径
 * Android/data/包名/cache:[android.content.Context.getExternalCacheDir]
 * Android/data/包名/files:[android.content.Context.getExternalFilesDir]
 */

/**
 * 下载的APK文件根目录
 */
fun Context.apkRootPath(): String {
    return File("${externalCacheDir?.path}/${FolderPath.APK}/").absolutePath
}


