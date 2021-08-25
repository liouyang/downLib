package com.loy.down.net


import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * @author  loy
 * @date 2020/1/13.
 * description：文件下载
 */

interface DownLoadApi {

    @Streaming
    @GET
    fun download(@Url url: String): Observable<ResponseBody>
}

