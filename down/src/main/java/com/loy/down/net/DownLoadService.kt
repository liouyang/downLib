package com.loy.down.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author  loy
 * @date 2020/1/13.
 * description：构建一个下载的service
 */

object DownLoadService {
    private var retrofit: Retrofit

    init {
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
        val okHttpClient = builder.build()
        retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BaseApi.HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    /**
     * 创建Retrofit Api Service
     *
     * @param service apiService类型
     */
    fun <T> createApiService(service: Class<T>): T {
        return retrofit.create(service)
    }

}