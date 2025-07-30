package com.orikino.fatty.ui.views.fragments.address_bottom_sheet

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.orikino.fatty.R
import com.orikino.fatty.databinding.FragmentMapsBinding
import com.orikino.fatty.utils.FattyMap
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.delegate.CallBackMapLatLngListener
import com.orikino.fatty.utils.helper.showSnackBar
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import java.util.Locale

class MapsFragment(val callBackMapLatLngListener: CallBackMapLatLngListener) : DialogFragment() {

    private var binding : FragmentMapsBinding? = null

    var locations: Location = Location("")
    private lateinit var fattyMap: FattyMap
    private var isEnable = false
    private var provider: LocationGooglePlayServicesProvider? = null
    private var isReady = MutableLiveData<Boolean>(false)
    private var addresss: String? = null

    private val callback = OnMapReadyCallback { googleMap ->
        //val sydney = LatLng(-34.0, 151.0)
        //googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        setUpLocationProvider()
        try {
            setUpMap()
        } catch (e: Exception) {
        }
        confirmCurrentAddress()

       binding?.ivBack?.setOnClickListener {
           callBackMapLatLngListener.onReceiveMapLatLng(LatLng(
               PreferenceUtils.readUserVO().latitude ?: 0.0,
               PreferenceUtils.readUserVO().longitude ?: 0.0)
           )
           dismiss()
       }
    }

    private fun getMyLocation(onLocatonReady: (location: Location) -> Unit) {
        SmartLocation.with(requireContext())
            .location(provider!!)
            .oneFix()
            .start {
                Log.d("mylocation", it.latitude.toString())
                onLocatonReady(it)
            }
    }


    private fun setUpMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            fattyMap = FattyMap(requireContext(), it)
            getMyLocation {
                fattyMap.animateCamera(LatLng(it.latitude, it.longitude))
            }
            fattyMap.setCameraIdleListener(onLocationReady = {
                locations.latitude = it.latitude
                locations.longitude = it.longitude
                try {
                    convertLatLangToAddress(it.latitude, it.longitude)
                } catch (e: Exception) {
                    Log.e("MAP", e.message.toString())
                }

            }, onCameraStartMove = {
                confirmBtnDisable()
            }
            )
        }

    }

//    private fun geoCodeLocationToAddress(location: Location) {
//        SmartLocation.with(requireContext())
//            .geocoding().reverse(location) { location, mutableList ->
//                location.latitude = location.latitude
//                location.longitude = location.longitude
//                PreferenceUtils.readUserVO().copy(latitude
//                        = location.latitude, longitude = location.longitude)
//                    .let {
//                        PreferenceUtils.writeUserVO(
//                            it
//                        )
//                    }
//                try {
//                    showAddress(mutableList[0].getAddressLine(0))
//                    isReady.postValue(true)
//
//                } catch (e: Exception) {
//                    Log.e("MAP", e.message.toString())
//                }
//
//            }
//    }

    private fun convertLatLangToAddress(lat: Double, lng: Double): String {
        var addresses: List<Address> = listOf()
        var ss = ""
        try {
            val geocoder = Geocoder(requireContext(), Locale.US)
            addresses = geocoder.getFromLocation(lat, lng, 1)!!
            ss = addresses[0].getAddressLine(0)
            addresss = ss
            PreferenceUtils.readUserVO().copy(latitude = lat, longitude = lng)
                .let {
                    PreferenceUtils.writeUserVO(
                        it
                    )
                }
            try {
                showAddress(addresss!!)
                isReady.postValue(true)

            } catch (e: Exception) {
                Log.e("MAP", e.message.toString())
            }

        } catch (e: Exception) {
        }

        return ss
    }

    private fun showAddress(address: String) {
        binding?.tvAddress?.text = address
        addresss = address
        if (locations.latitude != 0.0 && locations.longitude != 0.0) confirmBtnEnable()
        else confirmBtnDisable()
    }

    private fun confirmBtnEnable() {
        binding?.btnConfirmLocation?.isEnabled = true
        binding?.btnConfirmLocation?.alpha = 1f
    }

    private fun confirmBtnDisable() {
        binding?.btnConfirmLocation?.isEnabled = false
        binding?.btnConfirmLocation?.alpha = 0.5f
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        provider?.onActivityResult(requestCode, resultCode, data)
    }

    private fun setUpLocationProvider() {
        provider = LocationGooglePlayServicesProvider()
        provider!!.setCheckLocationSettings(true)
    }

    private fun confirmCurrentAddress() {
        binding?.btnConfirmLocation?.setOnClickListener {
            if (locations.latitude != 0.0 && locations.longitude != 0.0) {
                PreferenceUtils.isBackground = false
                callBackMapLatLngListener.onReceiveMapLatLng(
                    LatLng(
                        PreferenceUtils.readUserVO().latitude?:0.0,
                        PreferenceUtils.readUserVO().longitude?:0.0
                    )
                )
                childFragmentManager.findFragmentByTag("signature")?.onDestroy()
                dismiss()
            } else showSnackBar("Please wait")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(onConfirmAddress: CallBackMapLatLngListener) =
            MapsFragment(onConfirmAddress)
    }

}