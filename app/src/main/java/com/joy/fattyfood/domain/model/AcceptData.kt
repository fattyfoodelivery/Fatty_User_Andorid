package com.joy.fattyfood.domain.model

interface AcceptData<T> {
    fun bindData(data: T)
}