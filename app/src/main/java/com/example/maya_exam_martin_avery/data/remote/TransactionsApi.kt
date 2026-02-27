package com.example.maya_exam_martin_avery.data.remote

import com.example.maya_exam_martin_avery.data.remote.dto.PostRemoteTransactionBodyDto
import com.example.maya_exam_martin_avery.data.remote.dto.RemoteTransactionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TransactionsApi {
    @GET("posts")
    suspend fun getTransactions(): List<RemoteTransactionDto>

    @POST("posts")
    suspend fun postTransaction(@Body body: PostRemoteTransactionBodyDto): RemoteTransactionDto
}

