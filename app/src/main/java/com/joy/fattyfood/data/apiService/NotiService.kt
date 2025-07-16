package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface NotiService {

    @POST(ApiRouteConstant.routeUserNoti)
    @FormUrlEncoded
    suspend fun userNotiList(
        @Query("page") page : Int,
        @Query("filter_date") filter_date : String,
        @Field("user_id") userId : Int
    ) : Response<UserNotiListResponse>

    @POST(ApiRouteConstant.routeSystemNoti)
    @FormUrlEncoded
    suspend fun systemNotiList(
        @Query("page") page : Int,
        @Query("filter_date") filter_date : String,
        @Field("user_id") userId : Int
    ) : Response<SystemNotiListResponse>



    @POST(ApiRouteConstant.routeReadNoti)
    @FormUrlEncoded
    suspend fun readNoti(
        @Field("noti_id") noti_id : Int,
        @Field("noti_type") noti_type : String,
        @Field("user_id") userId : Int
    ) : Response<ReadNotiResponse>


    @POST(ApiRouteConstant.routeInboxNotification)
    @FormUrlEncoded
    suspend fun fetchInboxList(
        @Field("customer_id") customer_id: Int,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("noti_type") noti_type: Int,
        @Field("page") page: Int = 1
    ): Response<InboxResponse>

    @GET(ApiRouteConstant.routeNotificationMenuTypeList)
    suspend fun fetchNotificationTypeList(): Response<NotiTypeListResponse>
}