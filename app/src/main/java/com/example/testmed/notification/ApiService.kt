package com.example.testmed.notification


import com.example.testmed.CONTENT_TYPE
import com.example.testmed.ResponseData
import com.example.testmed.SERVER_KEY
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(@Body body: Sender): Response<ResponseData>
}