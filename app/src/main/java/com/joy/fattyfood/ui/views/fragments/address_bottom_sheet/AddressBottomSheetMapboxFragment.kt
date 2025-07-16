package com.joy.fattyfood.ui.views.fragments.address_bottom_sheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.FragmentAddressBottomSheetMapboxBinding
import com.joy.fattyfood.ui.views.fragments.rest_more_info.FoodAddOnBottomSheetFragment
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.showSnackBar
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import java.util.Locale


class AddressBottomSheetMapboxFragment(val onConfirmAddress: (LatLng) -> Unit = {}) : BottomSheetDialogFragment()
    /*OnMapReadyCallback*/ {


    var location: Location = Location("")
    //private lateinit var mapBox: MapboxMap
    private lateinit var mapView: MapView
    private var isEnable = false
    private var addresses: List<Address> = listOf()
    private var isReady = MutableLiveData(false)
    private var addresss: String? = null

    private var binding : FragmentAddressBottomSheetMapboxBinding? = null
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*MapboxMap.getInstance(
            requireContext(),
            "pk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsc3ZwcjRiajFuNXQya3FrZnRlanM5dDEifQ.Z2qH4uwufB38l75-YaMCng"
        )*/
        binding = FragmentAddressBottomSheetMapboxBinding.inflate(inflater,container,false)
        mapView = MapView(requireContext())
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .pitch(0.0)
                .zoom(2.0)
                .bearing(0.0)
                .build()
        )
        binding?.mapBoxMap?.addView(mapView)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        

        //binding?.mapBoxMap?.onCreate(savedInstanceState)
        //binding?.mapBoxMap?.getMapAsync(this)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        arguments?.getBoolean(FoodAddOnBottomSheetFragment.FAST_ORDER)?.let { isEnable = it }
        subscribeUI()
        confirmCurrentAddress()
    }
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun confirmBtnEnable() {
        try {
            binding?.btnConfirmAddress?.isEnabled = true
            binding?.btnConfirmAddress?.backgroundTintList =
                context?.resources?.getColorStateList(R.color.fattyPrimary)
        } catch (e: Exception) {
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun confirmBtnDisable() {
        try {
            binding?.btnConfirmAddress?.isEnabled = false
            binding?.btnConfirmAddress?.setTextColor(resources.getColor(R.color.white))
            binding?.btnConfirmAddress?.backgroundTintList =
                context?.resources?.getColorStateList(R.color.gray_black)
        } catch (e: Exception) {
        }

    }

    private fun subscribeUI() {}

    private fun getMyLocation() {
        /*PreferenceUtils.readUserVO()?.let {
            it
                ?.copy(
                    latitude = mapBox.locationComponent.lastKnownLocation?.latitude!!,
                    longitude = mapBox.locationComponent.lastKnownLocation?.longitude!!
                )?.let { it1 ->
                    PreferenceUtils.writeUserVO(
                        it1
                    )
                }
        }
        try {
            if (location.latitude != 0.0 && location.longitude != 0.0) confirmBtnEnable()
            else confirmBtnDisable()
            showAddress(
                convertLatLangToAddress(
                    mapBox.locationComponent.lastKnownLocation?.latitude!!,
                    mapBox.locationComponent.lastKnownLocation?.longitude!!
                )
            )
        } catch (e: Exception) {
        }
            binding?.mapBoxMap?.animateCamera(
            com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLng(
                com.mapbox.mapboxsdk.geometry.LatLng(
                    mapBox.locationComponent.lastKnownLocation?.latitude!!,
                    mapBox.locationComponent.lastKnownLocation?.longitude!!
                )
            )
        )*/
    }

    private fun confirmCurrentAddress() {
        binding?.ivMyLocation?.setOnClickListener {
            getMyLocation()
        }

        binding?.btnConfirmAddress?.setOnClickListener {
            if (location.latitude != 0.0 && location.longitude != 0.0) {
                PreferenceUtils.isBackground = false
                onConfirmAddress(
                    LatLng(
                        PreferenceUtils.readUserVO()?.latitude ?: 0.0,
                        PreferenceUtils.readUserVO()?.longitude ?: 0.0
                    )
                )
                dismiss()
            } else showSnackBar("Please wait")
        }
    }

    private fun showAddress(address: String) {
        binding?.tvAddress?.text = address
        addresss = address
    }

    private fun convertLatLangToAddress(lat: Double, lng: Double): String {
        var address = ""
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            addresses = geocoder.getFromLocation(lat, lng, 1)!!
            address = addresses[0].getAddressLine(0)
        } catch (e: Exception) {
        }
        return address

    }

    override fun onResume() {
        super.onResume()
        //binding?.mapBoxMap?.onResume()
    }

    override fun onPause() {
        super.onPause()
        //binding?.mapBoxMap?.onPause()
    }

    override fun onStart() {
        super.onStart()
        //binding?.mapBoxMap?.onStart()
    }

    override fun onStop() {
        super.onStop()
        //binding?.mapBoxMap?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        //binding?.mapBoxMap?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //binding?.mapBoxMap?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        //binding?.mapBoxMap?.onLowMemory()
    }

    companion object {
        @JvmStatic
        fun newInstance(onConfirmAddress: (LatLng) -> Unit = {}) =
            AddressBottomSheetMapboxFragment(onConfirmAddress)


    }

    /*override fun onMapReady(mapboxMap: MapboxMap) {
        mapBox = mapboxMap

        mapBox.uiSettings.isAttributionEnabled = false
        mapBox.uiSettings.isLogoEnabled = false
        mapBox.uiSettings.isCompassEnabled = false
        mapBox.locationComponent.isLocationComponentActivated
        mapBox.animateCamera(
            com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLng(
                com.mapbox.mapboxsdk.geometry.LatLng(
                    mapBox.cameraPosition.target.latitude,
                    mapBox.cameraPosition.target.longitude
                )
            )
        )

        mapBox.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            try {
                enableLocationComponent(style)
                Handler().postDelayed({
                    location.latitude = mapBox.cameraPosition.target.latitude
                    location.longitude = mapBox.cameraPosition.target.longitude
                    PreferenceUtils.readUserVO()
                        ?.copy(latitude = location.latitude, longitude = location.longitude)?.let {
                            PreferenceUtils.writeUserVO(
                                it
                            )
                        }
                    try {
                        Log.d("LOCATION", "${location.latitude} ${location.longitude}")
                        if (location.latitude != 0.0 && location.longitude != 0.0) confirmBtnEnable()
                        else confirmBtnDisable()
                        showAddress(convertLatLangToAddress(location.latitude, location.longitude))
                        mapBox.addOnCameraMoveListener {
                            confirmBtnDisable()
                        }
                        mapBox.addOnCameraIdleListener { confirmBtnEnable() }
                        //progress_map.visibility = View.GONE
                    } catch (e: Exception) {
                    }

                    isReady.postValue(true)


                }, 5000)

                onChangeCurrentLocation()
            } catch (e: Exception) {
            }

        }
    }

    private fun onChangeCurrentLocation() {
        mapBox.addOnMoveListener(object : MapboxMap.OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                confirmBtnDisable()
            }

            override fun onMove(detector: MoveGestureDetector) {
                confirmBtnDisable()
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
                location.latitude = mapBox.cameraPosition.target.latitude
                location.longitude = mapBox.cameraPosition.target.longitude
                Log.d("LOCATION", "${location.latitude} ${location.longitude}")
                PreferenceUtils.readUserVO()?.let {
                    PreferenceUtils.writeUserVO(
                        it
                            .copy(latitude = location.latitude, longitude = location.longitude)
                    )
                }
                try {
                    if (location.latitude != 0.0 && location.longitude != 0.0) confirmBtnEnable()
                    else confirmBtnDisable()
                    showAddress(convertLatLangToAddress(location.latitude, location.longitude))
                } catch (e: Exception) {
                }
                isReady.postValue(true)
            }
        })

        mapBox.addOnFlingListener {

            Toast.makeText(requireContext(), "onFling", Toast.LENGTH_SHORT).show()

        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
            .foregroundDrawable(R.drawable.ic_current_location_40dp)
            .bearingDrawable(R.drawable.ic_current_location_40dp)
            .accuracyAlpha(0f)
            .elevation(0f)
            .trackingGesturesManagement(true)
            .accuracyColor(ContextCompat.getColor(requireContext(), R.color.white))
            .build()

        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

        mapBox.locationComponent.apply {

            // Activate the LocationComponent with options
            activateLocationComponent(locationComponentActivationOptions)

            // Enable to make the LocationComponent visible
            isLocationComponentEnabled = true

            // Set the LocationComponent's camera mode
            cameraMode = CameraMode.TRACKING

            // Set the LocationComponent's render mode
            renderMode = RenderMode.COMPASS
        }*//**//*
        mapBox.locationComponent.addOnLocationStaleListener { }

    }*/
}