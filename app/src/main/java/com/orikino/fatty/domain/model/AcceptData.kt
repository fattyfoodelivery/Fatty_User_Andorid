package com.orikino.fatty.domain.model

interface AcceptData<T> {
    fun bindData(data: T)
}