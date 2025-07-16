package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.domain.responses.SystemNotiListResponse
import com.joy.fattyfood.domain.responses.UserNotiListResponse
import retrofit2.Response

interface NotiRepository {

    suspend fun fetchUserNotiList(
        page: Int,
        filter_date: String,
        customer_id: Int
    ): Response<UserNotiListResponse>

    suspend fun fetchSystemNotiList(
        page: Int,
        filter_date: String,
        customer_id: Int
    ): Response<SystemNotiListResponse>

    suspend fun fetchInboxNotiList(
        customer_id: Int,
        start_date: String,
        end_date: String,
        noti_type: Int,
        page : Int
    ): Response<InboxResponse>

    suspend fun fetchNotiType() : Response<NotiTypeListResponse>

    suspend fun readNoti(noti_id : Int,noti_type : String,user_id : Int) : Response<ReadNotiResponse>
}