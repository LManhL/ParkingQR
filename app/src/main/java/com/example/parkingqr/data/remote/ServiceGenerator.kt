package com.example.parkingqr.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ServiceGenerator @Inject constructor() {

    companion object {
        private const val timeoutRead = 30
        private const val contentType = "Content-Type"
        private const val contentTypeValue = "application/json"
        private const val timeoutConnect = 30
        private const val BASE_URL = "http://192.168.113.153:8080"
    }

    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private val retrofit: Retrofit

    private var headerInterceptor = Interceptor { chain ->
        val original = chain.request()

        val request = original.newBuilder()
            .header(contentType, contentTypeValue)
            .method(original.method, original.body)
            .build()

        chain.proceed(request)
    }

    init {
        okHttpBuilder.addInterceptor(headerInterceptor)
        okHttpBuilder.connectTimeout(timeoutConnect.toLong(), TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(timeoutRead.toLong(), TimeUnit.SECONDS)
        val client = okHttpBuilder.build()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL).client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(getMoshi())
            )
            .build()
    }

    private fun getMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }


}