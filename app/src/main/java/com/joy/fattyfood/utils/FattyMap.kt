package com.joy.fattyfood.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.RawRes
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.joy.fattyfood.R
import com.joy.fattyfood.utils.helper.VALUE_MAP_TILE_COUNT
import com.joy.fattyfood.utils.helper.VALUE_MAP_ZOOM
import io.nlopez.smartlocation.SmartLocation

class FattyMap(
    private val context: Context, private val mMap: GoogleMap
) {
    init {
        //Mapbox.getInstance(context,"")
        setupMapView()
    }

    private fun setupMapView() {
        mMap.clear()
        val cameraPosition = CameraPosition.Builder()
            .zoom(VALUE_MAP_ZOOM)
            .target(getLastKnowLocation())
            .tilt(VALUE_MAP_TILE_COUNT)
            .build()
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition)
        )


    }

    fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .zoom(VALUE_MAP_ZOOM)
            .target(latLng)
            .tilt(VALUE_MAP_TILE_COUNT)
            .build()
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition)
        )
    }

    private fun setMapStyle(@RawRes styleResource: Int) {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, styleResource))
    }

    fun setMapViewBond(markersList: List<LatLng>) {
        val latLongBuilder: LatLngBounds.Builder = LatLngBounds.builder()
        markersList.map {
            latLongBuilder.include(it)
        }

        val bounds = latLongBuilder.build()
        val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 700, 700, 50)
        mMap.animateCamera(cu)

//        val cameraPosition = CameraPosition.Builder()
//            .zoom(VALUE_MAP_ZOOM)
//            .target(markersList[0])
//            .target(markersList[1])
//            .target(markersList[2])
//            .tilt(VALUE_MAP_TILE_COUNT)
//            .build()
//        mMap.animateCamera(
//            CameraUpdateFactory.newCameraPosition(cameraPosition)
//        )
    }


    fun setMapViewBondParcel(markersList: List<LatLng>) {
        val latLongBuilder: LatLngBounds.Builder = LatLngBounds.builder()
        markersList.map {
            latLongBuilder.include(it)
        }

        val bounds = latLongBuilder.build()
        val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 1000, 700, 100)
        mMap.animateCamera(cu)

//        val cameraPosition = CameraPosition.Builder()
//            .zoom(VALUE_MAP_ZOOM)
//            .target(markersList[0])
//            .target(markersList[1])
//            .target(markersList[2])
//            .tilt(VALUE_MAP_TILE_COUNT)
//            .build()
//        mMap.animateCamera(
//            CameraUpdateFactory.newCameraPosition(cameraPosition)
//        )
    }


    fun drawMaker(location: LatLng, @LayoutRes icon: Int) {
        val marker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                icon,
                null
            )
        mMap.addMarker(
            MarkerOptions().position(location)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapMarkerFromView(
                            marker,
                        )
                    )
                )
        )
    }

    fun drawRoute(start: LatLng, end: LatLng): List<Marker?> {
        setMapViewBond(listOf(start, end))
        val pinpointMarker =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.item_restaurant_pinpoint,
                null
            )
        val pinpointRider =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.item_rider_pinpoint,
                null
            )
        val resturRantMarker = mMap.addMarker(
            MarkerOptions().position(end)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapMarkerFromView(
                            pinpointMarker,
                        )
                    )
                )
        )
        val riderMarker = mMap.addMarker(
            MarkerOptions().position(start)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapMarkerFromView(
                            pinpointRider,
                        )
                    )
                )
        )
        return listOf(riderMarker, resturRantMarker)
    }

    private fun getBitmapMarkerFromView(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = view.background
        drawable?.draw(canvas)
        view.draw(canvas)
        return returnedBitmap
    }

    fun setCameraIdleListenerForAddressUpdate(
        latLng: LatLng,
        onLocationReady: (latLng: LatLng) -> Unit


    ) {

        mMap.setOnCameraIdleListener {
            val midLatLng: LatLng = latLng
            onLocationReady(midLatLng)
            Log.d("SelectionLocation", "$midLatLng")
        }
    }

    fun setCameraIdleListener(
        onLocationReady: (latLng: LatLng) -> Unit,
        onCameraStartMove: () -> Unit
    ) {
        mMap.setOnCameraIdleListener {
            val midLatLng: LatLng = mMap.cameraPosition.target
            onLocationReady(midLatLng)
            Log.d("SelectionLocation", "$midLatLng")
        }
        mMap.setOnCameraMoveListener {
            onCameraStartMove()
        }
    }

    fun setCameraUpdateListener(
        onLocationReady: (latLng: LatLng) -> Unit,
        onCameraStartMove: () -> Unit
    ) {
        mMap.setOnCameraIdleListener {
            val midLatLng: LatLng = mMap.cameraPosition.target
            onLocationReady(midLatLng)
            Log.d("SelectionLocation", "$midLatLng")
        }
        mMap.setOnCameraMoveListener {
            onCameraStartMove()
        }
    }

    fun setOnClickListener(onClick: () -> Unit) {
        mMap.setOnMapClickListener {
            onClick.invoke()
        }
    }

    private fun getLastKnowLocation(): LatLng {
        var lat = 0.0
        var long = 0.0
        val location = SmartLocation.with(context)
            .location().lastLocation
        return if (location == null) {
            PreferenceUtils.readUserVO()?.latitude?.let {
                lat = it
            }
            PreferenceUtils.readUserVO()?.longitude?.let {
                long = it
            }
            LatLng(lat, long)
        } else {
            LatLng(location.latitude, location.longitude)
        }
    }


    /*@RequiresApi(Build.VERSION_CODES.M)
    fun drawCircle() {
        mMap.addCircle(
            CircleOptions()
                .center(MOCK_LATLONG)
                .radius(32.0)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(context.getColor(R.color.fatty))
        )
        mMap.addCircle(
            CircleOptions()
                .center(MOCK_LATLONG)
                .radius(6.0)
                .strokeWidth(3f)
                .strokeColor(Color.WHITE)
                .fillColor(context.getColor(R.color.fatty))
        )
    }

     */


}