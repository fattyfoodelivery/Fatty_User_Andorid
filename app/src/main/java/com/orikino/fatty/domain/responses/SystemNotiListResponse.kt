package com.orikino.fatty.domain.responses

data class SystemNotiListResponse(
    var success : Boolean = false,
    var message : String = "",
    var data : SystemNotiVO = SystemNotiVO()
)

data class SystemNotiVO(
    var unread_count : Int = 0,
    var notification : SystemNotification = SystemNotification()
)

data class SystemNotification(
    var data : MutableList<SystemNotificationVO> = mutableListOf()
)

data class SystemNotificationVO(
    var id : Int = 0,
    var title : String = "",
    var body : String = "",
    var image : String = "",
    var read : Boolean = false,
    var time : String = ""
)

/*data class UserNotiListResponse(
    var success : Boolean = false,
    var message : String = "",
    var data : UserNotiVO = UserNotiVO()
)


data class UserNotiVO(
    var unread_count : Int = 0,
    var notification : UserNotification = UserNotification()
)

data class UserNotification(
    var data : MutableList<UserNotificationVO> = mutableListOf()
)


data class UserNotificationVO(
    var id : Int = 0,
    var order_id : String = "",
    var order_status_id : String = "",
    var order_type : String = "",
    var title : String = "",
    var body : String = "",
    var read : Boolean = false,
    var time : String = ""
)*/


