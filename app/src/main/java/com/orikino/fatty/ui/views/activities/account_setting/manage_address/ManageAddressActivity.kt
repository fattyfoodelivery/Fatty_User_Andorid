package com.orikino.fatty.ui.views.activities.account_setting.manage_address

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.orikino.fatty.R
import com.orikino.fatty.adapters.AddressAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityManageAddressBinding
import com.orikino.fatty.databinding.LayoutConfirmCancelDialogBinding
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
import com.orikino.fatty.domain.responses.MyOrderHistoryResponse
import com.orikino.fatty.domain.viewstates.AddressViewState
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
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
    private var addressSize : Int = 0
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
        manageAddressBinding.emptyView.emptyImage.setImageResource(R.drawable.ic_no_address)
        manageAddressBinding.emptyView.emptyMessage.text = getString(R.string.no_data_available)
        manageAddressBinding.emptyView.emptyMessageDes.text = ""
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
            addressSize = it.size
            addressAdapter?.setNewData(it)
            if (it.isNotEmpty())
                manageAddressBinding.emptyView.root.visibility = android.view.View.GONE
            else
                manageAddressBinding.emptyView.root.visibility = android.view.View.VISIBLE
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
        if (state.data.data.isEmpty())
            manageAddressBinding.emptyView.root.visibility = android.view.View.VISIBLE
        else
            manageAddressBinding.emptyView.root.visibility = android.view.View.GONE
        addressAdapter?.setNewData(state.data.data)
    }

    private fun renderOnFailDeleteAddress(state: AddressViewState.OnFailDeleteAddress) {
        showSnackBar(state.message)
    }

    private fun renderOnLoadingSetDefaultAddress() {
        LoadingProgressDialog.showLoadingProgress(this@ManageAddressActivity)
    }
    private fun renderOnSuccessSetDefaultAddress(state: AddressViewState.OnSuccessSetDefaultAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            LoadingProgressDialog.hideLoadingProgress()
            //addressAdapter?.setNewData(state.data.data)
            addressAdapter?.setNewData(state.data.data)
        }
    }

    private fun renderOnFailSetDefaultAddress(state: AddressViewState.OnFailSetDefaultAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        showSnackBar(state.message)
    }

    private fun renderOnLoadingCustomerAddressList() {
        LoadingProgressDialog.showLoadingProgress(this@ManageAddressActivity)
    }

    private fun renderOnSuccessCustomerAddressList(state: AddressViewState.OnSuccessCustomerAddressList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            manageAddressBinding.tvTitle.text = getString(R.string.manage_address).plus(" ").plus("(${state.data.data.size}/5)")
            viewModel.manageAddressLiveDataList.postValue(state.data.data)
            if (state.data.data.size >= 5){
                manageAddressBinding.btnAddNewAddr.gone()
            }else{
                manageAddressBinding.btnAddNewAddr.show()
            }
        }
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
                ConfirmDialog.Builder(
                    this,
                    resources.getString(R.string.hello),
                    resources.getString(R.string.login_message),
                    resources.getString(R.string.login),
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
                if (addressSize >= 5){
                    showSnackBar("Maximum Address Size Reach!")
                }else{
                    val intent = Intent(this, AddressPickUpMapBoxActivity::class.java)
                    intent.putExtra(AddressPickUpMapBoxActivity.INTENT_FROM, 1)
                    startActivity(intent)
                }

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
                "root" -> {
                    PreferenceUtils.writeToEditAddress(data)
                    startActivity(AddressPickUpMapBoxActivity.getIntent(true))
                }
                "default" -> {
                    if (data.is_default){
                        showSnackBar("Can't remove default address")
                    }else{
                        setDefault(data)
                    }
                }
                "delete" -> {
                    deleteAddress(data)

                }
                "update" -> {
                    PreferenceUtils.writeToEditAddress(data)
                    startActivity(AddressPickUpMapBoxActivity.getIntent(true))
                    //viewModel.updateCurrentAddress(data.customer_address_id,data.customer_id,data.address_latitude,data.address_longitude,data.current_address,data.customer_phone?: "",data.building_system?:"",data.address_type, data.is_default, data.secondary_phone)
                }
            }
        }
        manageAddressBinding.rvCustomerAddress.adapter = addressAdapter
    }

    private fun setDefault(data: CustomerAddressVO) {
        val dialogView = LayoutConfirmCancelDialogBinding.inflate(LayoutInflater.from(this))//layoutInflater.inflate(R.layout.layout_dialog_remove_cart, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView.tvTitle.text = getString(R.string.txt_information)
            dialogView.tvDesc.text = getString(R.string.txt_are_you_sure_to_set_up_default_address)
            dialogView.btnContact.text = getString(R.string.confirm)
            dialogView.btnCancel.text = getString(R.string.str_cancel)
            dialogView.btnCancel.setOnClickListener {
                dismiss()
            }
            dialogView.ivClose.setOnClickListener { dismiss() }
            dialogView.btnContact.setOnClickListener {
                dismiss()
                //orderHistoryAdapter?.submitList(mutableListOf())

                //viewModel.orderHistoriesList.clear()
                //viewModel.pastOrderHistoriesList.clear()
                //viewModel.foodOrderList.clear()
                //viewModel.parcelOrderHistoriesList.clear()
                //viewModel.parcelPastOrderHistoriesList.clear()
                //viewModel.parcelOrderList.clear()
                viewModel.setUpDefaultAddress(data.customer_address_id)
            }

            show()
        }

    }

    private fun deleteAddress(data: CustomerAddressVO) {
        showConfirmDialog(
            getString(R.string.txt_delete_address),//resources.getString(R.string.are_you_sure_to_delete_this_order),
            getString(R.string.txt_are_you_sure_to_delete_this_address),
            //resources.getString(R.string.sure_to_cancel),
            data
        )

    }

    private fun showConfirmDialog(title: String, message: String, data : CustomerAddressVO) {
        val dialogView = LayoutConfirmCancelDialogBinding.inflate(LayoutInflater.from(this))//layoutInflater.inflate(R.layout.layout_dialog_remove_cart, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView.tvTitle.text = title
            if(message.isEmpty()){
                dialogView.tvDesc.gone()
            } else {
                dialogView.tvDesc.show()
            }
            dialogView.tvDesc.text = message
            dialogView.btnContact.text = getString(R.string.str_delete)
            dialogView.btnCancel.text = getString(R.string.str_cancel)
            dialogView.btnCancel.setOnClickListener {
                dismiss()
            }
            dialogView.ivClose.setOnClickListener { dismiss() }
            dialogView.btnContact.setOnClickListener {
                dismiss()
                //orderHistoryAdapter?.submitList(mutableListOf())

                //viewModel.orderHistoriesList.clear()
                //viewModel.pastOrderHistoriesList.clear()
                //viewModel.foodOrderList.clear()
                //viewModel.parcelOrderHistoriesList.clear()
                //viewModel.parcelPastOrderHistoriesList.clear()
                //viewModel.parcelOrderList.clear()
                viewModel.deleteAddress(data.customer_address_id)
            }

            show()
        }
    }


    private fun onTapItemChange(pos : Int) {
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
