package com.orikino.fatty.ui.views.activities.services

import javax.inject.Inject

class ServiceShopPagingState @Inject constructor(){
    var listCurrentPage = 1

    var listMaxPage = 1

    fun increaseListCurrentPage() {
        listCurrentPage += 1
    }

    fun resetListPage() {
        listCurrentPage = 1
    }

    fun shouldNextPageList() : Boolean {
        return listCurrentPage <= listMaxPage
    }
}