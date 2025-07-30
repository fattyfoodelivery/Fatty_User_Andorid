package com.orikino.fatty.ui.views.activities.account_setting.manage_address

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import coil.load
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityAddressDefinedBinding
import com.orikino.fatty.databinding.LayoutDialogRemoveCartBinding
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.domain.model.ManageAddress
import com.orikino.fatty.domain.view_model.AddressViewModel
import com.orikino.fatty.domain.viewstates.AddressViewState
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.FattyMap
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.hideSoftKeyboard
import com.orikino.fatty.utils.helper.showSnackBar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import java.util.Locale

@AndroidEntryPoint
class AddressDefinedActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAddressDefinedBinding

    private var dialogView: View? = null
    private var alertDialog: AlertDialog? = null
    private var updateStatus: String = ""
    private var customerAddress = CustomerAddressVO()
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottom_sheet: ConstraintLayout
    private val viewModel: AddressViewModel by viewModels()
    private var other: String = ""
    private var building: String = ""
    private var status: String = ""
    private var currentAddress: String = ""
    private var customer_phone: String = ""
    private var updateLat = 0.0
    private var updateLng = 0.0
    private var isFirst = true

    var locations: Location = Location("")
    private lateinit var fattyMap: FattyMap
    private var isEnable = false
    private var provider: LocationGooglePlayServicesProvider? = null
    private var isReady = MutableLiveData<Boolean>(false)
    private var addresss: String? = null

    companion object {
        const val LAT = "lat"
        const val LNG = "lng"
        fun getIntent(lat: Double, lng: Double): Intent {
            val intent = Intent(FattyApp.getInstance(), AddressDefinedActivity::class.java)
            intent.putExtra(LAT, lat)
            intent.putExtra(LNG, lng)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAddressDefinedBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        locations.latitude = intent.getDoubleExtra(LAT, 0.0)
        locations.longitude = intent.getDoubleExtra(LNG, 0.0)

        if (PreferenceUtils.readManageAddress()?.customer_address?.customer_address_id != 0) bindAddressUpdateUI() else defaultType()
        subscribeUI()
        askLocationPermission()
        setUpLocationProvider()
        setUpMap()
        checkLocationInfo()

        addCustomerLocation()
        cameraAnimateToCurrentAddress()

        onBackPress()


    }

    override fun onStart() {
        super.onStart()
        checkGPS()
    }

    private fun onBackPress() {
        _binding.ivBack.setOnClickListener {
            PreferenceUtils.readManageAddress()?.customer_address?.let { it1 ->
                PreferenceUtils.readManageAddress()?.status?.let { it2 ->
                    PreferenceUtils.readManageAddress()?.longitude?.let { it3 ->
                        PreferenceUtils.readManageAddress()?.latitude?.let { it4 ->
                            ManageAddress(
                                it1,
                                it2,
                                false,
                                it4,
                                it3
                            )
                        }
                    }
                }
            }?.let { it2 ->
                PreferenceUtils.writeManageAddress(
                    it2
                )
            }
            if (!PreferenceUtils.readManageAddress()?.status!!) {
                finish()
                startActivity(AddressPickUpMapBoxActivity.getIntent("", 4))

            } else finish()
        }


    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            fattyMap = FattyMap(this, it)

            fattyMap.setCameraIdleListener(onLocationReady = {
                locations.latitude = it.latitude
                locations.longitude = it.longitude
                try {
                    convertLatLangToAddress(locations)
                } catch (e: Exception) {
                    println(e.message)
                }

            }, onCameraStartMove = {
                //confirmBtnDisable()
                disableBtn()
            }
            )
            fattyMap.animateCamera(LatLng(locations.latitude, locations.longitude))
        }

    }

    /*private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            fattyMap = FattyMap(this, it)
            *//*getMyLocation {
                fattyMap.animateCamera(LatLng(latitude,longitude))
            }
            fattyMap.animateCamera(LatLng(it.,longitude))*//*

            fattyMap.setCameraIdleListener(onLocationReady = {
                SmartLocation.with(this)
                    .location(provider!!)
                    .oneFix()
                    .start {
                        locations.latitude = it.latitude
                        locations.longitude = it.longitude
                        try {
                            geoCodeLocationToAddress(locations)

                        }catch (e : Exception) {
                            e.printStackTrace()
                        }
                        //Log.d("mylocation", it.latitude.toString())
                        //onLocatonReady(it)
                        geoCodeLocationToAddress(it)
                    }
            }, onCameraStartMove = {
                //fattyMap.animateCamera(LatLng(locations.latitude,locations.longitude))
            })

            *//*fattyMap.setOnClickListener {
                //showConfirmDialog(resources.getString(R.string.update_address), resources.getString(R.string.Are_you_sure))
                true
            }*//*
        }


    }*/

    private fun convertLatLangToAddress(location : Location) {
        var addresses: List<Address> = listOf()
        var ss = ""
        try {
            val geocoder = Geocoder(this, Locale.US)
            addresses = geocoder.getFromLocation(locations.latitude, locations.longitude, 1)!!
            ss = addresses[0].getAddressLine(0)
            addresss = ss
            PreferenceUtils.readUserVO().copy(
                latitude
                = location.latitude, longitude = location.longitude
            )
                .let {
                    PreferenceUtils.writeUserVO(
                        it
                    )
                }
            try {
                showAddress(ss)
                isReady.postValue(true)

            } catch (e: Exception) {
            }
        } catch (e: Exception) {
        }

    }

    /*private fun geoCodeLocationToAddress(location: Location) {
        SmartLocation.with(this)
            .geocoding().reverse(location) { location, mutableList ->
                location.latitude = location.latitude
                location.longitude = location.longitude
                PreferenceUtils.readUserVO().copy(
                    latitude
                    = location.latitude, longitude = location.longitude
                )
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
    }*/

    private fun showAddress(address: String) {
        _binding.edtLocationAddress.setText(address)
        addresss = address
        if (locations.latitude != 0.0 && locations.longitude != 0.0) enableBtn()
        else disableBtn()
    }

    private fun enableBtn() {
        _binding.btnConfirmLocation.alpha = 1f
        _binding.btnConfirmLocation.isEnabled = true
    }

    private fun disableBtn() {
        _binding.btnConfirmLocation.alpha = 0.6f
        _binding.btnConfirmLocation.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        checkGPS()
    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        } else {
            //checkService()
        }
    }

    private fun checkService() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)


        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                //setUpMapBoxBottomSheet()
                //setUpMapBox()
            }
        } else {
            //if (result == 0) setUpMapBox() //setUpGoogleMapBottomSheet()
            //else setUpMapBox()//setUpMapBoxBottomSheet()

        }
    }

    private fun setUpLocationProvider() {
        provider = LocationGooglePlayServicesProvider()
        provider!!.setCheckLocationSettings(true)
    }


    private fun cameraAnimateToCurrentAddress() {

        _binding.imvCurrent.setOnClickListener {
            getMyLocation {
                fattyMap.animateCamera(LatLng(locations.latitude, locations.longitude))
            }
        }
    }

    private fun getMyLocation(onLocatonReady: (location: Location) -> Unit) {
        SmartLocation.with(this)
            .location(provider!!)
            .oneFix()
            .start {
                //Log.d("mylocation", it.latitude.toString())
                onLocatonReady(it)
            }


    }

    private fun askLocationPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (it.areAllPermissionsGranted()) {
                            checkGPS()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    /* ... */
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    @SuppressLint("ResourceAsColor")
    private fun bindAddressUpdateUI() {
        //println("RRRRRRRRRRRRRRRRRRRRR ${PreferenceUtils.readManageAddress()?.customer_address?.current_address}")
        _binding.edtBuilding.setText(PreferenceUtils.readManageAddress()?.customer_address?.building_system)
        _binding.edtLocationAddress.setText(PreferenceUtils.readManageAddress()?.customer_address?.current_address)
        //edt_phone.setText(Preference.readManageAddress().customer_address.customer_phone)
        status = PreferenceUtils.readManageAddress()?.customer_address?.address_type.toString()
        when (status) {
            "Home" -> {
                homeSelected()
            }

            "Work" -> {
                workSelected()
            }

            else -> {
                otherSelected()
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun checkLocationInfo() {

        _binding.rlHome.setOnClickListener {
            status = "Home"
            homeSelected()
        }
        _binding.rlWork.setOnClickListener {
            status = "Work"
            workSelected()
        }
        _binding.rlOther.setOnClickListener {
            status = "Other"
            otherSelected()
        }
    }

    private fun addCustomerLocation() {
        _binding.btnConfirmLocation.setOnClickListener {
            other = _binding.edtBuilding.text.toString()
            currentAddress = _binding.edtLocationAddress.text.toString()
            if (status != "" && currentAddress != "") {
                if (updateStatus == "update") PreferenceUtils.readUserVO().customer_id?.let { it1 ->
                    viewModel.updateCurrentAddress(
                        customerAddress.customer_address_id,
                        it1,
                        locations.latitude,
                        locations.longitude,
                        currentAddress,
                        PreferenceUtils.readUserVO().customer_phone,
                        building,
                        status,
                        _binding.rbtDefaultAddress.isChecked
                    )
                } else PreferenceUtils.readUserVO().customer_id?.let { it1 ->
                    viewModel.addCurrentAddress(
                        it1,
                        locations.latitude,
                        locations.longitude,
                        currentAddress,
                        PreferenceUtils.readUserVO().customer_phone,
                        building,
                        status,
                        _binding.rbtDefaultAddress.isChecked
                    )
                }
            } else {
                showSnackBar("Please fill your information correctly")
            }
            //finish()
        }
    }


    private fun subscribeUI() {

        viewModel.viewState.observe(this, Observer {
            render(it)
        })
    }

    private fun render(state: AddressViewState) {
        when (state) {
            is AddressViewState.OnLoadingAddCurrentAddress -> renderOnLoadingAddAddress()
            is AddressViewState.OnSuccessAddCurrentAddress -> renderOnSuccessAddCurrentAddress(state)
            is AddressViewState.OnFailAddCurrentAddress -> renderOnFailAddAddress(state)

            is AddressViewState.OnLoadingSetDefaultAddress -> renderOnLoadingSetDefaultAddress()
            is AddressViewState.OnSuccessSetDefaultAddress -> renderOnSuccessSetDefaultAddress(state)
            is AddressViewState.OnFailSetDefaultAddress -> renderOnFailSetDefaultAddress(state)

            is AddressViewState.OnLoadingUpdateCurrentAddress -> renderOnLoadingUpdateCurrentAddress()
            is AddressViewState.OnSuccessUpdateCurrentAddress -> renderOnSuccessUpdateCurrentAddress(
                state
            )

            is AddressViewState.OnFailUpdateCurrentAddress -> renderOnFailUpdateCurrentAddress(state)
            else -> {}

        }
    }

    private fun renderOnFailSetDefaultAddress(state: AddressViewState.OnFailSetDefaultAddress) {}
    private fun renderOnSuccessSetDefaultAddress(state: AddressViewState.OnSuccessSetDefaultAddress) {
        if (state.data.success) {
            clearInfo()
            //startActivity<ManageAddressActivity>()
            val intent = Intent(this, ManageAddressActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun renderOnLoadingSetDefaultAddress() {}

    private fun renderOnSuccessAddCurrentAddress(state: AddressViewState.OnSuccessAddCurrentAddress) {
        if (state.data.success) {
            clearInfo()
            //startActivity<ManageAddressActivity>()
            val intent = Intent(this, ManageAddressActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun renderOnLoadingAddAddress() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnSuccessUpdateCurrentAddress(state: AddressViewState.OnSuccessUpdateCurrentAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            clearInfo()
            //startActivity<ManageAddressActivity>()
            val intent = Intent(this, ManageAddressActivity::class.java)
            startActivity(intent)
            finish()
            /*if (PreferenceUtils.readFromCart() == true) {
                PreferenceUtils.writeSelectedAddress(
                    LatLng(
                        data.data.address_latitude,
                        data.data.address_longitude
                    )
                )*/
            //PreferenceUtils.writeIsSelected(data.data.customer_address_id)

            /*startActivity<CartActivity>(
                CartActivity.LAT to state.data.data.address_latitude,
                CartActivity.LNG to state.data.data.address_longitude,
                CartActivity.ADDRESS_ID to state.data.data.customer_address_id,
                CartActivity.ADDRESS to state.data.data.current_address,
                CartActivity.ADDRESS_TYPE to state.data.data.address_type,
                CartActivity.IS_Selected to true,
                CartActivity.IS_BACK to false,
                CartActivity.PHONE_NO to state.data.data.customer_phone
            )
            startActivity<ManageAddressActivity>(CheckOutActivity.getIntent(0.0,0.0,""))
            finish()
            */


        }
    }


    private fun renderOnFailAddAddress(state: AddressViewState.OnFailAddCurrentAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Issue" -> {
                showSnackBar("Server Issue")
            }

            "Another Login" -> {
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)

                    })
                    .show(supportFragmentManager, AddressDefinedActivity::class.simpleName)
            }

            "Denied" -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, AddressDefinedActivity::class.simpleName)

            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun renderOnLoadingUpdateCurrentAddress() {
        LoadingProgressDialog.showLoadingProgress(this)
    }


    private fun renderOnFailUpdateCurrentAddress(state: AddressViewState.OnFailUpdateCurrentAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Issue" -> {
                showSnackBar(resources.getString(R.string.no_internet_title))
            }

            "Another Login" -> {
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)

                    })
                    .show(supportFragmentManager, AddressDefinedActivity::class.simpleName)
            }

            "Denied" -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, AddressDefinedActivity::class.simpleName)

            "Failed" -> {
                showSnackBar("App Error")
            }

            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun defaultType() {
        status = "Home"
        homeSelected()
//        if (PreferenceUtils.readManageAddress()?.longitude != 0.0 && PreferenceUtils.readManageAddress()?.latitude != 0.0) {
//            //locations.latitude = PreferenceUtils.readManageAddress()?.latitude!!
//            //locations.longitude = PreferenceUtils.readManageAddress()?.longitude!!
//            getMyLocation {
//                fattyMap.animateCamera(LatLng(PreferenceUtils.readManageAddress()?.latitude!!,PreferenceUtils.readManageAddress()?.longitude!!))
//            }
//        }
        /*getMyLocation {
            fattyMap.animateCamera(LatLng(locations.latitude,locations.longitude))
        }*/
        //_binding.edtLocationAddress.setText(convertToCurrentAddress(LatLng(PreferenceUtils.readManageAddress()?.latitude!!,PreferenceUtils.readManageAddress()?.longitude!!)))

        println(
            "DDDDDDDDDDDd ${
                convertToCurrentAddress(
                    LatLng(
                        locations.latitude,
                        locations.longitude
                    )
                )
            }"
        )
    }


    private fun homeSelected() {
        _binding.rlHome.setBackgroundResource(R.drawable.bg_selected)
        _binding.imbHome.load(R.drawable.radio_check)

        _binding.rlWork.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imvWork.load(R.drawable.radio_uncheck)

        _binding.rlOther.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imvOther.load(R.drawable.radio_uncheck)

    }

    private fun workSelected() {
        _binding.rlHome.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imbHome.load(R.drawable.radio_uncheck)
        _binding.rlWork.setBackgroundResource(R.drawable.bg_selected)
        _binding.imvWork.load(R.drawable.radio_check)
        _binding.rlOther.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imvOther.load(R.drawable.radio_uncheck)

    }

    private fun otherSelected() {
        _binding.rlHome.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imbHome.load(R.drawable.radio_uncheck)
        _binding.rlWork.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imvWork.load(R.drawable.radio_uncheck)
        _binding.rlOther.setBackgroundResource(R.drawable.bg_selected)
        _binding.imvOther.load(R.drawable.radio_check)
    }

    private fun clearSelected() {
        _binding.rlHome.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imbHome.load(R.drawable.radio_uncheck)
        _binding.rlWork.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imvWork.load(R.drawable.radio_uncheck)
        _binding.rlOther.setBackgroundResource(R.drawable.bg_unselectd)
        _binding.imvOther.load(R.drawable.radio_uncheck)
    }


    private fun clearInfo() {
        status = ""
        clearSelected()
    }

    private fun showConfirmDialog(title: String, message: String) {
        var binding = LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            binding?.tvTitle?.text = title
            binding?.tvTitleDesc?.text = message

            binding?.btnClose?.setOnClickListener {
                dismiss()
            }
            binding?.btnRemove?.text = resources.getString(R.string.confirm_location)
            binding?.btnRemove?.setOnClickListener {
                dismiss()
                finish()
                PreferenceUtils.readManageAddress()?.customer_address?.let { it1 ->
                    PreferenceUtils.readManageAddress()?.status?.let { it2 ->
                        PreferenceUtils.readManageAddress()?.latitude?.let { it3 ->
                            PreferenceUtils.readManageAddress()?.longitude?.let { it4 ->
                                ManageAddress(
                                    it1,
                                    it2,
                                    true,
                                    it3,
                                    it4
                                )
                            }
                        }
                    }
                }?.let { it2 ->
                    PreferenceUtils.writeManageAddress(
                        it2
                    )
                }
                startActivity(AddressPickUpMapBoxActivity.getIntent("", 4))
            }
            show()
        }
    }

    /*private fun setUpCancelable() {
        //edt_address.requestFocus()
        edt_address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (charSequence?.length!! > 0) ivClear.visibility = View.VISIBLE
                else ivClear.visibility = View.GONE
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        ivClear.setOnClickListener { edt_address.setText("") }

    }*/

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                //edt_address.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(_binding.edtLocationAddress, InputMethodManager.SHOW_IMPLICIT)
                hideSoftKeyboard()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    /*private fun addCustomerLocation() {
        _binding.btnConfirmLocation.setOnClickListener {
            other = _binding.edtBuilding.text.toString()
            currentAddress = _binding.edtLocationAddress.text.toString()
            //customer_phone = edt_phone.text.toString()
            when{
                status == "" -> CustomToast(this,"Fill Info",false).createCustomToast()
                    //showSnackBar(resources.getString(R.string.fill_info))
                currentAddress == "" -> CustomToast(this,"Fill Info",false).createCustomToast()
                //edt_phone.text?.length == 0 -> edt_phone.error = resources.getString(R.string.phone_no_hint)
                else->{
                    //edt_phone.error = null
                    if (PreferenceUtils.readManageAddress()?.customer_address?.customer_address_id != 0)
                        PreferenceUtils.readManageAddress()?.customer_address?.let { cusAddr ->
                            cusAddr?.customer_address_id?.let { addrId ->
                                viewModel.updateCurrentAddress(
                                    addrId,
                                    latitude,
                                    longitude,
                                    currentAddress,
                                    customer_phone,
                                    other,
                                    status
                                )
                            }
                        }
                    else viewModel.addCurrentAddress(
                        latitude,
                        longitude,
                        currentAddress,
                        customer_phone,
                        other,
                        status
                    )
                }
            }
        }
    }*/

    private fun convertToCurrentAddress(latLng: LatLng): String {
        var addresses = listOf<Address>()
        var aa = ""
        try {
            val geocoder = Geocoder(this, Locale.US)
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)!!
            aa = addresses[0].getAddressLine(0)

        } catch (e: Exception) {
        }


        return aa
    }

}