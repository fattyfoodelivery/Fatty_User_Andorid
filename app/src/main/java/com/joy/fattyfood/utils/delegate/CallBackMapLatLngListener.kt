package com.joy.fattyfood.utils.delegate

import com.google.android.gms.maps.model.LatLng

interface CallBackMapLatLngListener {

    fun onReceiveMapLatLng(latLng: LatLng)
}