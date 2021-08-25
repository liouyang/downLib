package com.loy.down.net

import java.io.File

/**
 *@Author loy
 *创建时间：2021/8/9 10:01
 *description: 监听类
 */
interface IDownOnListener {
    /**
     * 开始
     */
    fun onStart()

    /**
     * 完成
     */
    fun onComplete(t: File?)

    /**
     * 进度回调
     * @param readLength Long 读写的长度
     * @param countLength Long  总长度
     */
    fun updateProgress(readLength: Long, countLength: Long,progress:Int)

    /**
     * 异常
     * @param e Throwable?
     */
    fun onError(e: Throwable?)

}