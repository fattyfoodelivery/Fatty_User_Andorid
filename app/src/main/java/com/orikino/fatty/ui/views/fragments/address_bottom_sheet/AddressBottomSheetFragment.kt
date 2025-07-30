package com.orikino.fatty.ui.views.fragments.address_bottom_sheet

import android.app.Dialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orikino.fatty.R
import com.orikino.fatty.databinding.FragmentAddressBottomSheetBinding
import com.orikino.fatty.utils.FattyMap
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.showSnackBar
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider

class AddressBottomSheetFragment(val onConfirmAddress: (LatLng) -> Unit = {}) : BottomSheetDialogFragment() {

    private var binding : FragmentAddressBottomSheetBinding? = null
    var locations: Location = Location("")
    private lateinit var fattyMap: FattyMap
    private var isEnable = false
    private var provider: LocationGooglePlayServicesProvider? = null
    private var isReady = MutableLiveData<Boolean>(false)
    private var addresss: String? = null
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                binding?.ivClose?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
        
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressBottomSheetBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        setUpLocationProvider()
        try {
            setUpMap()
        } catch (e: Exception) {
        }
        confirmCurrentAddress()


        binding?.ivClose?.setOnClickListener {
            dialog?.dismiss()
        }
    }


    private fun confirmBtnEnable() {
        binding?.btnConfirmAddress?.isEnabled = true
        binding?.btnConfirmAddress?.backgroundTintList = context?.resources?.getColorStateList(R.color.fattyPrimary)
    }

    private fun confirmBtnDisable() {
        binding?.btnConfirmAddress?.isEnabled = false
        binding?.btnConfirmAddress?.setTextColor(resources.getColor(R.color.white))
        binding?.btnConfirmAddress?.backgroundTintList = context?.resources?.getColorStateList(R.color.gray_black)
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
        /*binding?.ivMyLocation?.setOnClickListener {
            getMyLocation {
                locations.latitude = it.latitude
                locations.longitude = it.longitude
                fattyMap.animateCamera(LatLng(it.latitude, it.longitude))
                geoCodeLocationToAddress(it)
            }
        }*/

        binding?.btnConfirmAddress?.setOnClickListener {
            if (locations.latitude != 0.0 && locations.longitude != 0.0) {
                PreferenceUtils.isBackground = false
                onConfirmAddress(
                    LatLng(
                        PreferenceUtils.readUserVO()?.latitude ?: 0.0,
                        PreferenceUtils.readUserVO()?.longitude ?: 0.0
                    )
                )
                fragmentManager?.findFragmentByTag("sheet")?.onDestroy()
                dismiss()
            } else showSnackBar("Please wait")
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

    private fun showAddress(address: String) {
        binding?.tvAddress?.text = address
        addresss = address
        if (locations.latitude != 0.0 && locations.longitude != 0.0) confirmBtnEnable()
        else confirmBtnDisable()
    }

    private fun geoCodeLocationToAddress(location: Location) {
        SmartLocation.with(requireContext())
            .geocoding().reverse(location) { location, mutableList ->
                location.latitude = location.latitude
                location.longitude = location.longitude
                PreferenceUtils.readUserVO().copy(latitude = location.latitude, longitude = location.longitude)
                    .let {
                        PreferenceUtils.writeUserVO(
                            it
                        )
                    }
                try {
                    showAddress(mutableList[0].getAddressLine(0))
                    isReady.postValue(true)

                } catch (e: Exception) {
                }

            }
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.confirm_address_map) as SupportMapFragment
        mapFragment.getMapAsync {
            fattyMap = FattyMap(requireContext(), it)
            getMyLocation {
                fattyMap.animateCamera(LatLng(it.latitude, it.longitude))
            }
            fattyMap.setCameraIdleListener(onLocationReady = {
                locations.latitude = it.latitude
                locations.longitude = it.longitude
                try {
                    geoCodeLocationToAddress(locations)
                } catch (e: Exception) {
                }

            }, onCameraStartMove = {
                confirmBtnDisable()
            }
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(onConfirmAddress: (LatLng) -> Unit = {}) =
            AddressBottomSheetFragment(onConfirmAddress)
    }
}