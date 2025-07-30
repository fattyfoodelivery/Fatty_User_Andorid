package com.orikino.fatty.utils.delegate

import com.google.android.gms.maps.model.LatLng

interface CallBackMapLatLngListener {

    fun onReceiveMapLatLng(latLng: LatLng)
}