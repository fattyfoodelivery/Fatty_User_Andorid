package com.joy.fattyfood.ui.views.activities.account_setting.manage_address

import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.joy.fattyfood.HomeViewModel
import com.joy.fattyfood.R
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityAddressPickUpMapBoxBinding
import com.joy.fattyfood.domain.model.CustomerAddressVO
import com.joy.fattyfood.domain.model.ManageAddress
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.utils.FattyMap
import com.joy.fattyfood.utils.GpsTracker
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.activatedBtn
import com.joy.fattyfood.utils.helper.deactivatedBtn
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import java.util.Locale


@AndroidEntryPoint
class AddressPickUpMapBoxActivity : AppCompatActivity()/*, OnMapReadyCallback*/ {

    lateinit var _binding : ActivityAddressPickUpMapBoxBinding

    private val viewModel : HomeViewModel by viewModels()

    var onTapMap = false
    var from : Int = 0

    var location: Location = Location("")
    //private lateinit var mapBox: MapboxMap
    private var isEnable = false
    private var addresses: List<Address> = listOf()
    private var isReady = MutableLiveData(false)

    private lateinit var fattyMap: FattyMap
    private var provider: LocationGooglePlayServicesProvider? = null
    private var locations: Location = Location("")
    private var update = false
    private var addressVO = CustomerAddressVO()
    var addresss: String? = null
    private var from_view  : Int ? = 0



    companion object {
        const val INTENT_FROM = "from_intent"

        private const val PARAM1 = "param1"
        private const val PARAM2 = "param2"

        fun getIntent(param1 : String,route : Int) : Intent {
            return Intent(FattyApp.getInstance(),AddressPickUpMapBoxActivity::class.java).apply {
                this.putExtra(PARAM1,param1)
                this.putExtra(PARAM2,route)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddressPickUpMapBoxBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        setUpMapBox()

        //_binding.mapView.onCreate(savedInstanceState)
        //_binding.mapView.getMapAsync(this)


        val title = intent.getStringExtra(PARAM1)
        _binding.tvAddress.text = title

        //from = intent.getIntExtra(PARAM2,0)

        from_view = intent.getIntExtra(INTENT_FROM,0)

        setUpLocationProvider()
        askLocationPermission()
        cameraAnimateToCurrentAddress()
        confirmLocation()
        onBack()

        //definedAddress()

    }


    private fun setUpMapBox() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment
        mapFragment?.getMapAsync { map ->
            fattyMap = FattyMap(this, map)

            fattyMap.setCameraIdleListener(onLocationReady = {
                locations.latitude = it.latitude
                locations.longitude = it.longitude
                _binding.tvAddress.text = convertLatLangToAddress(it.latitude, it.longitude)
                var aa =
                    PreferenceUtils.readManageAddress()?.customer_address?.customer_id?.let { it1 ->
                        PreferenceUtils.readManageAddress()?.customer_address?.customer_address_id?.let { it2 ->
                            PreferenceUtils.readManageAddress()?.customer_address?.address_type?.let { it3 ->
                                PreferenceUtils.readManageAddress()?.customer_address?.is_default?.let { it4 ->
                                    PreferenceUtils.readManageAddress()?.customer_address?.customer_phone?.let { it5 ->
                                        CustomerAddressVO(
                                            customer_address_id = it2,
                                            customer_id = it1,
                                            address_latitude = it.latitude,
                                            address_longitude = it.longitude,
                                            current_address = convertLatLangToAddress(it.latitude, it.longitude),
                                            customer_phone = it5,
                                            address_type = it3,
                                            is_default = it4,
                                            building_system = PreferenceUtils.readManageAddress()?.customer_address?.building_system
                                        )
                                    }
                                }
                            }
                        }
                    }
                if (aa != null) {
                    addressVO = aa
                }

            }, onCameraStartMove = {
                deactivatedBtn(_binding.btnConfirmLocation)
            })


        }
    }
    private fun onBack() {

        /*_binding.ivBack.setOnClickListener {
            if (from_view == 4) {
                finish()
            } else if (from_view == 2) {
                startActivity<MainActivity>(
                )
                finish()
            }

        }*/
        _binding.ivBack.setOnClickListener {
            if (PreferenceUtils.readManageAddress()?.isMapUpdate == true) {
                PreferenceUtils.readManageAddress()?.customer_address?.let { it1 ->
                    PreferenceUtils.readManageAddress()?.latitude?.let { it2 ->
                        PreferenceUtils.readManageAddress()?.longitude?.let { it3 ->
                            PreferenceUtils.readManageAddress()?.status?.let { it4 ->
                                ManageAddress(
                                    it1,
                                    it4,
                                    false,
                                    it2,
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
                finish()
                startActivity(AddressDefinedActivity.getIntent(locations.latitude,locations.longitude))
                //startActivity<CustomerDeliverAddressUpdateActivity>()
            } else {
                finish()
                startActivity(ManageAddressActivity.getIntent(false))
                //startActivity<ManageAddressActivity>()
            }
        }
    }

    private fun cameraAnimateToCurrentAddress() {
        _binding.imvLocation.setOnClickListener {
            getMyLocation {
                locations.latitude = it.latitude
                locations.longitude = it.longitude
                fattyMap.animateCamera(
                    LatLng(
                        it.latitude,
                        it.longitude
                    )
                )
            }
        }
    }


    private fun getMyLocation(onLocatonReady: (location: Location) -> Unit) {
        SmartLocation.with(this)
            .location(provider!!)
            .oneFix()
            .start {
                Log.d("mylocation", it.latitude.toString())
                onLocatonReady(it)
            }
    }


    private fun setUpLocationProvider() {
        provider = LocationGooglePlayServicesProvider()
        provider!!.setCheckLocationSettings(true)
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

    private fun confirmLocation() {
        _binding.btnConfirmLocation.setOnClickListener {
            if (from_view == 1) {
                if (locations.latitude != 0.0 && locations.longitude != 0.0) {
                    PreferenceUtils.readUserVO().latitude
                    PreferenceUtils.readUserVO().longitude
                    PreferenceUtils.readManageAddress()?.customer_address?.let { it1 ->
                        PreferenceUtils.readManageAddress()?.status?.let { it2 ->
                            ManageAddress(
                                it1,
                                it2,
                                false,
                                locations.latitude,
                                locations.longitude
                            )
                        }
                    }?.let { it2 ->
                        PreferenceUtils.writeManageAddress(
                            it2
                        )
                    }
                    PreferenceUtils.needToShow = false
                    PreferenceUtils.isBackground = false
                    //startActivity<AddressDefinedActivity>()
                    val intent = Intent(this, AddressDefinedActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else if (from_view == 2) {
                PreferenceUtils.needToShow = false
                PreferenceUtils.isBackground = false
                /*startActivity<MainActivity>(

                )*/
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else if (from_view == 3) {
                finish()
            }
            else {
                if (locations.latitude != 0.0 && locations.longitude != 0.0) {
                    if (PreferenceUtils.readManageAddress()?.isMapUpdate == true) {
                        PreferenceUtils.readManageAddress()?.status?.let { it1 ->
                            ManageAddress(
                                addressVO,
                                it1,
                                true,
                                locations.latitude,
                                locations.longitude
                            )
                        }?.let { it2 ->
                            PreferenceUtils.writeManageAddress(
                                it2
                            )
                        }
                        finish()
                        startActivity(AddressDefinedActivity.getIntent(locations.latitude,locations.longitude))
                        //startActivity<CustomerDeliverAddressUpdateActivity>()
                    } else {
                        PreferenceUtils.readManageAddress()?.customer_address?.let { it1 ->
                            PreferenceUtils.readManageAddress()?.status?.let { it2 ->
                                ManageAddress(
                                    it1,
                                    it2,
                                    false,
                                    locations.latitude,
                                    locations.longitude
                                )
                            }
                        }?.let { it2 ->
                            PreferenceUtils.writeManageAddress(
                                it2
                            )
                        }
                        finish()
                        startActivity(AddressDefinedActivity.getIntent(locations.latitude,locations.longitude))
                        //startActivity<CustomerDeliverAddressUpdateActivity>()
                    }
                }
            }

        }

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

        }
    }

    private fun definedAddress() {
        try {
            checkService()
        } catch (e: Exception) {
        }
    }

    private fun checkService() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)

        Log.d("TAG", "checkService: $result")

        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                setUpMapBox()
            }
        } else {
            if (result == 0) setUpMapBox()
            else setUpMapBox()

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        provider?.onActivityResult(requestCode, resultCode, data)
    }


    private fun convertLatLangToAddress(lat: Double, lng: Double): String {
        var addresses: List<Address> = listOf()
        var ss = ""
        try {
            val geocoder = Geocoder(this, Locale.US)
            addresses = geocoder.getFromLocation(lat, lng, 1)!!
            ss = addresses[0].getAddressLine(0)
            addresss = ss
            if (locations.latitude != 0.0 && locations.longitude != 0.0) activatedBtn(_binding.btnConfirmLocation)
            else deactivatedBtn(_binding.btnConfirmLocation)

        } catch (e: Exception) {
        }

        return ss
    }


}