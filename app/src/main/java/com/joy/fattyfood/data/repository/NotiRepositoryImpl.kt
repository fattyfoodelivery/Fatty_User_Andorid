package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.data.apiService.NotiService
import retrofit2.Response
import javax.inject.Inject

class NotiRepositoryImpl @Inject constructor(
    private val notiService: NotiService
) : NotiRepository{

    override suspend fun fetchUserNotiList(
        page : Int,
        filter_date : String,
        customer_id : Int
    ): Response<UserNotiListResponse> = notiService.userNotiList(page,filter_date,customer_id
    )

    override suspend fun fetchSystemNotiList(
        page: Int,
        filter_date: String,
        customer_id: Int
    ): Response<SystemNotiListResponse> = notiService.systemNotiList(page,filter_date,customer_id)

    override suspend fun fetchInboxNotiList(
        customer_id: Int,
        start_date: String,
        end_date: String,
        noti_type: Int,
        page: Int
    ): Response<InboxResponse> = notiService.fetchInboxList(customer_id, start_date, end_date, noti_type, page)

    override suspend fun fetchNotiType(): Response<NotiTypeListResponse> = notiService.fetchNotificationTypeList()

    override suspend fun readNoti(
        noti_id: Int,
        noti_type: String,
        user_id: Int
    ): Response<ReadNotiResponse> = notiService.readNoti(noti_id,noti_type,user_id)


}