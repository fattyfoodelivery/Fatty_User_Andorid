package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class InboxViewState{
    object OnLoadingInbox :  InboxViewState()
    data class OnSuccessInbox(val data : InboxResponse) : InboxViewState()
    data class OnFailInbox(val message : String) : InboxViewState()

    object OnLoadingNotiType : InboxViewState()
    data class OnSuccessNotificationType(val data : NotiTypeListResponse) : InboxViewState()
    data class OnFailNotificationType(val message: String) : InboxViewState()


    object OnLoadingUserNotiList : InboxViewState()
    data class OnSuccesUserNotiList(val data : UserNotiListResponse) : InboxViewState()
    data class OnFailUserNotiList(val message : String) : InboxViewState()


    object OnLoadingSystemNotiList : InboxViewState()
    data class OnSuccessSystemNotiList(val data : SystemNotiListResponse) : InboxViewState()
    data class OnFailSystemNotiList(val message : String) : InboxViewState()

    object OnLoadingReadNoti : InboxViewState()
    data class OnSuccessReadNoti(val data : ReadNotiResponse) : InboxViewState()
    data class OnFailReadNoti(val message : String) : InboxViewState()
}
