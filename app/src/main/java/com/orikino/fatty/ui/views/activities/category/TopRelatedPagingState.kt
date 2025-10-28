package com.orikino.fatty.ui.views.activities.category

import javax.inject.Inject

class TopRelatedPagingState@Inject constructor(){
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