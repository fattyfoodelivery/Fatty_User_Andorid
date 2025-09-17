package com.orikino.fatty.ui.views.activities.account_setting.manage_address

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.orikino.fatty.R
import com.orikino.fatty.adapters.AddressAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityManageAddressBinding
import com.orikino.fatty.domain.view_model.AddressViewModel
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SuccessDialog
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.domain.model.*
import com.orikino.fatty.domain.viewstates.AddressViewState
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageAddressActivity : AppCompatActivity(){

    private lateinit var manageAddressBinding: ActivityManageAddressBinding

    private var addressAdapter : AddressAdapter? = null

    var customerId: Int? = 0
    private var fromCart = false
    private var isBack = false
    private val viewModel: AddressViewModel by viewModels()

    companion object {
        const val FROM_CART = "from_cart"
        fun getIntent(fromCart: Boolean): Intent {
            val intent = Intent(FattyApp.getInstance(), ManageAddressActivity::class.java)
            intent.putExtra(FROM_CART, fromCart)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manageAddressBinding = ActivityManageAddressBinding.inflate(layoutInflater)
        setContentView(manageAddressBinding.root)

        subscribeUI()
        setUpCustomerAddressList()
        fromCart = intent.getBooleanExtra(FROM_CART, false)
        PreferenceUtils.writeFromCart(fromCart)
        manageAddressBinding.swipeRefresh.setOnRefreshListener {
            manageAddressBinding.swipeRefresh.isRefreshing = false
            PreferenceUtils.readUserVO().customer_id?.let { viewModel.customerAddressList(it) }
        }
        navigateToAddCurrentAddressView()
        onBackPress()
    }

    private fun onBackPress() {
        manageAddressBinding.ivBack.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        PreferenceUtils.readUserVO().customer_id?.let { viewModel.customerAddressList(it) }
    }


    private fun subscribeUI() {
        viewModel.viewState.observe(this){ render(it) }
        viewModel.manageAddressLiveDataList.observe(this) {
            addressAdapter?.setNewData(it)
        }
    }

    private fun render(state: AddressViewState) {
        when (state) {
            is AddressViewState.OnLoadingCustomerAddressList -> renderOnLoadingCustomerAddressList()
            is AddressViewState.OnSuccessCustomerAddressList -> renderOnSuccessCustomerAddressList(state)
            is AddressViewState.OnFailCustomerAddressList -> renderOnFailCustomerAddressList(state)

            is AddressViewState.OnLoadingSetDefaultAddress -> renderOnLoadingSetDefaultAddress()
            is AddressViewState.OnSuccessSetDefaultAddress -> renderOnSuccessSetDefaultAddress(state)
            is AddressViewState.OnFailSetDefaultAddress -> renderOnFailSetDefaultAddress(state)

            is AddressViewState.OnLoadingDeleteAddress -> renderOnLoadingDeleteAddress()
            is AddressViewState.OnSuccessDeleteAddress -> renderOnSuccessDeleteAddress(state)
            is AddressViewState.OnFailDeleteAddress -> renderOnFailDeleteAddress(state)

            is AddressViewState.OnLoadingUpdateCurrentAddress -> renderOnLoadingUpdateCurrentAddress()
            is AddressViewState.OnSuccessUpdateCurrentAddress -> renderOnSuccessUpdateCurrentAddress(state)
            is AddressViewState.OnFailUpdateCurrentAddress -> renderOnFailUpdateCurrentAddress(state)
            else -> {}
        }
    }

    private fun renderOnFailUpdateCurrentAddress(state: AddressViewState.OnFailUpdateCurrentAddress) {
        showSnackBar(state.message)
    }

    private fun renderOnLoadingUpdateCurrentAddress() {}
    private fun renderOnSuccessUpdateCurrentAddress(state: AddressViewState.OnSuccessUpdateCurrentAddress) {
        addressAdapter?.addNewData(state.data.data)
    }

    private fun renderOnLoadingDeleteAddress() {

    }
    private fun renderOnSuccessDeleteAddress(state: AddressViewState.OnSuccessDeleteAddress) {
        addressAdapter?.setNewData(state.data.data)
    }

    private fun renderOnFailDeleteAddress(state: AddressViewState.OnFailDeleteAddress) {
        showSnackBar(state.message)
    }

    private fun renderOnLoadingSetDefaultAddress() {}
    private fun renderOnSuccessSetDefaultAddress(state: AddressViewState.OnSuccessSetDefaultAddress) {
        if (state.data.success) {
            LoadingProgressDialog.hideLoadingProgress()
            //addressAdapter?.setNewData(state.data.data)
            addressAdapter?.setNewData(state.data.data)
        }
    }

    private fun renderOnFailSetDefaultAddress(state: AddressViewState.OnFailSetDefaultAddress) {

    }

    private fun renderOnLoadingCustomerAddressList() {
        //LoadingProgressDialog.showLoadingProgress(this@ManageAddressActivity)
    }

    private fun renderOnSuccessCustomerAddressList(state: AddressViewState.OnSuccessCustomerAddressList) {
        if (state.data.success) viewModel.manageAddressLiveDataList.postValue(state.data.data)

    }

    private fun renderOnFailCustomerAddressList(state: AddressViewState.OnFailCustomerAddressList) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.notification))
            }

            Constants.ANOTHER_LOGIN -> startActivity(LoginActivity.getIntent("manage_address"))


            Constants.DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)

            Constants.FAILED -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun navigateToAddCurrentAddressView() {
        manageAddressBinding.btnAddNewAddr.setOnClickListener {
            if (PreferenceUtils.readUserVO().customer_id == 0) {
                SuccessDialog.Builder(
                    this,
                    resources.getString(R.string.login_message),
                    callback = {
                        //startActivity<LoginActivity>()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
                    .show(supportFragmentManager, ManageAddressActivity::class.simpleName)
            } else {
                /*startActivity<AddressPickUpMapBoxActivity>(
                    AddressPickUpMapBoxActivity.INTENT_FROM to 1
                )*/
                val intent = Intent(this, AddressPickUpMapBoxActivity::class.java)
                intent.putExtra(AddressPickUpMapBoxActivity.INTENT_FROM, 1)
                startActivity(intent)
                //checkService()
            }
        }
    }

    private fun checkService() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)

        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                // prompt the dialog to update google play
                //finish()

                /*println("CustomerAddressPickUpMapBoxActivity 11111111111111")
                // startActivity<CustomerAddressPickUpMapBoxActivity>()
                startActivity(AddressDefinedActivity.getIntent(
                    0.0,0.0
                ))*/
                setUpMapBox()
            }
        } else {
            if (result == 0) {
                /*finish()
                println("CustomerAddressPickUpActivity 222222222222222")
                startActivity(AddressPickUpMapBoxActivity.getIntent("Add New Address", 3))*/
                setUpMapBox()
            } else {
                setUpMapBox()
                /*finish()
                println("CustomerAddressPickUpMapBoxActivity 3333333333333333")
                startActivity(AddressDefinedActivity.getIntent(0.0,0.0))*/
            }
        }
    }

    private fun setUpMapBox() {
        /*if (fragmentManager?.findFragmentByTag("signature") == null) {
            val fullDialogMap = MapsFragment.newInstance(this)
            supportFragmentManager.let {
                fullDialogMap.show(
                    it, "signature"
                )
            }
        } else {
        }*/
    }

    private fun setUpCustomerAddressList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        manageAddressBinding.rvCustomerAddress.layoutManager = linearLayoutManager
        manageAddressBinding.rvCustomerAddress.addItemDecoration(
            EqualSpacingItemDecoration(
                spacing = 24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        manageAddressBinding.rvCustomerAddress.setHasFixedSize(true)
        manageAddressBinding.rvCustomerAddress.isNestedScrollingEnabled = true
        addressAdapter = AddressAdapter(FattyApp.getInstance()) { data, str, pos ->
            when (str) {
                "root" -> onTapItemChange(pos)
                "default" -> viewModel.setUpDefaultAddress(data.customer_address_id)
                "delete" -> viewModel.deleteAddress(data.customer_address_id)
                "update" -> viewModel.updateCurrentAddress(data.customer_address_id,data.customer_id,data.address_latitude,data.address_longitude,data.current_address,data.customer_phone?: "",data.building_system?:"",data.address_type, data.is_default)
            }
        }
        manageAddressBinding.rvCustomerAddress.adapter = addressAdapter
    }

    private fun onTapItemChange(pos : Int) {
    }

    private fun setDefaultAddress(data: CustomerAddressVO) {
        //viewModel.setUpDefaultAddress(data.customer_address_id)
        /*when (data.is_default) {
            true -> {
                radioDefault.isChecked = true
                setAsDefaultAddressDialog(
                    resources.getString(R.string.remove_default_address),
                    resources.getString(R.string.sure_to_confirm),
                    resources.getString(R.string.confirm),
                    data.customer_address_id
                )
            }
            else -> {
                radioDefault.isChecked = false
                setAsDefaultAddressDialog(
                    resources.getString(R.string.set_as_default_address),
                    resources.getString(R.string.sure_to_confirm),
                    resources.getString(R.string.confirm),
                    data.customer_address_id
                )
            }
        }*/
    }

    private fun deleteAddress() {
    }

    private fun updateAddress() {
    }

    private fun fromCartUpdate() {

    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}
