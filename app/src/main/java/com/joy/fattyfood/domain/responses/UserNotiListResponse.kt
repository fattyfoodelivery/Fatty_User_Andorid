package com.joy.fattyfood.domain.responses

data class UserNotiListResponse(
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
)

/*
"data" = UserNotiVO
"links": {
    "first": "http://127.0.0.1:8000/api/v2/fatty/202221/lashio/main/admin/notifications/user?page=1",
    "last": "http://127.0.0.1:8000/api/v2/fatty/202221/lashio/main/admin/notifications/user?page=1",
    "prev": null,
    "next": null
},
"meta": {
    "current_page": 1,
    "from": 1,
    "last_page": 1,
    "links": [
    {
        "url": null,
        "label": "&laquo; Previous",
        "active": false
    },
    {
        "url": "http://127.0.0.1:8000/api/v2/fatty/202221/lashio/main/admin/notifications/user?page=1",
        "label": "1",
        "active": true
    },
    {
        "url": null,
        "label": "Next &raquo;",
        "active": false
    }
    ],
    "path": "http://127.0.0.1:8000/api/v2/fatty/202221/lashio/main/admin/notifications/user",
    "per_page": 20,
    "to": 1,
    "total": 1
}*/
