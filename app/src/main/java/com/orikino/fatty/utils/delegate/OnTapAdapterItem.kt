package com.orikino.fatty.utils.delegate

interface OnTapAdapterItem<T, String,Int> {
    fun onTap(data: T, str: String,pos : Int)
}