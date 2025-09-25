package com.orikino.fatty.ui.views.activities.parcel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityBookingOrderBinding
import com.orikino.fatty.databinding.LayoutCurrencyZoneBinding
import com.orikino.fatty.domain.model.ParcelInfoVO
import com.orikino.fatty.domain.model.ParcelSenderReceiverVO
import com.orikino.fatty.domain.view_model.TrackOrderViewModel
import com.orikino.fatty.domain.viewstates.TrackParcelViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.activities.track.TrackOrderParcelActivity
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingOrderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBookingOrderBinding


    private var dialogBinding : LayoutCurrencyZoneBinding? = null

    private val viewModel : TrackOrderViewModel by viewModels()
    private var lastSelectedPos = 0


    companion object {

        const val ORDER_ID = "order_id"

        var MODE = "current"
        const val IS_EDIT = "is-edit"
        const val IS_BACK = "is-back"
        const val SENDER_ADDRESS = "data"
        const val DEFAULT_ADDRESS = "default-address"
        const val AUTO_ADDRESS = "auto-address"
        const val IS_PARCEL_TYPE = "is-parcel-type"
    }

    private var lastSelected = 0
    private var isEdit = false
    private var isParcelType = false
    private var isBack = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra(IS_EDIT, false)
        isBack = intent.getBooleanExtra(IS_BACK, true)
        isParcelType = intent.getBooleanExtra(IS_PARCEL_TYPE, false)
        /*subscribeUI()
        setUpSwitchNote()
        if (isEdit) editMode()
        navigator()
        */

        setUpSubScribe()
        //checkZone()
        setUpCurrencyZoneId()
        confirmBookNowOrder()
        confirmGotBooking()
        onBackPress()
    }

    private fun onBackPress() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }


    private fun setUpSubScribe() {

        viewModel.viewStateParcel.observe(this) { render (it) }


    }

    private fun render(state : TrackParcelViewState) {
        when(state) {
            is TrackParcelViewState.OnLoadingCheckParcel -> renderOnLoadCheckParcel()
            is TrackParcelViewState.OnSuccessCheckParcel -> renderOnSuccessCheckParcel(state)
            is TrackParcelViewState.OnFailCheckParcel -> renderOnFailCheckParcel(state)
            else -> {}
        }
    }

    private fun renderOnLoadCheckParcel() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnSuccessCheckParcel(state: TrackParcelViewState.OnSuccessCheckParcel) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success == true) {
            PreferenceUtils.writeParcelZoneId(0)
            state.data.data?.order_id?.let { PreferenceUtils.writeCustomerOrderId(it) }
            PreferenceUtils.writeSenderReceiver(ParcelSenderReceiverVO())
            PreferenceUtils.writeParcelInfo(ParcelInfoVO())
            MODE = "current"
            state.data.data?.order_id?.let { showGotBookingView(it) }
        }else{
            AlertDialog.Builder(this)
                .setMessage(state.data.message)
                .setPositiveButton(getString(R.string.str_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showGotBookingView(orderId: Int) {
        binding.llBookNow.gone()
        binding.btnBookNow.gone()
        binding.llGotBookingView.show()
        binding.btnViewMyOrderGotBooking.show()
        MODE = "current"
        MainActivity.isNotification = true
        PreferenceUtils.needToShow = false
        startActivity(MainActivity.getIntent(this))
        finish()
        navigateToTrackOrderView(orderId)
    }

    private fun checkParcelInfo() {
        //MODE = "current"
        //navigateToTrackOrderView()
        /*when {
            viewModel.parcel_type == "" -> showSnackBar("Choose Parcel Type"*//*resources.getString(R.string.choose_parcel_type)*//*)
            else -> {

                //viewModel.item_qty = edtItemQty.text?.trim().toString()
                //viewModel.note = edtFullName.text?.trim().toString()
                var file = mutableListOf<File>()
                PreferenceUtils.writeParcelInfo(
                    ParcelInfoVO(
                        viewModel.parcel_id,
                        viewModel.parcel_type,
                        viewModel.weight,
                        viewModel.item_qty.toInt(),
                        viewModel.paymentMethodID,
                        viewModel.extraCover,
                        file,
                        viewModel.note
                    )
                )

                if (viewModel.isActivate) {
                    if (viewModel.extraCover.extra_cover_id == 0) {
                        showSnackBar("Extra Cover Cost"*//*resources.getString(R.string.extra_cover_cost)*//*)
                    } else {
                        file.forEach {
                            it.let {
                               // viewModel.part.add(getImageMultipleFile("parcel_image_list[]", it))
                            }

                        }
                       // viewModel.booingParcel(viewModel.part)
                    }
                    //else viewModel.booingParcel(viewModel.part)

                } else {
                    viewModel.file.forEach {
                        it.let {
                            viewModel.part.add(getImageMultipleFile("parcel_image_list[]", it))
                        }

                    }
                    viewModel.booingParcel(viewModel.part)
                }


                //println("seneddseseesseseses ${viewModel.isActivate} and ${viewModel.extraCover.extra_cover_id} and ${viewModel.part}")

            }
        }*/
    }

    private fun navigateToTrackOrderView(orderId : Int) {
        binding.btnViewMyOrderGotBooking.setOnClickListener {
            /*startActivity<TrackOrderParcelActivity>(
                TrackOrderParcelActivity.ORDER_STATUS to "parcel",
                TrackOrderParcelActivity.ORDER_ID to orderId
            )*/
            val intent = Intent(this, TrackOrderParcelActivity::class.java)
            intent.putExtra(TrackOrderParcelActivity.ORDER_STATUS, "parcel")
            intent.putExtra(TrackOrderParcelActivity.ORDER_ID, orderId)
            startActivity(intent)
            finish()
        }
    }


    private fun setUpCurrencyZoneId() {
        dialogBinding = LayoutCurrencyZoneBinding.inflate(LayoutInflater.from(this@BookingOrderActivity))
        val alertDialog = AlertDialog.Builder(this@BookingOrderActivity)
        alertDialog.setView(dialogBinding?.root)
        alertDialog.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)

            dialogBinding?.rbtnMuseCheck?.isClickable = true
            dialogBinding?.rbtnMuseCheck?.isFocusable = true
            dialogBinding?.rbtnLashioCheck?.isClickable = true
            dialogBinding?.rbtnLashioCheck?.isFocusable = true
            if (PreferenceUtils.readZoneId() == 1){
                lastSelected = 1
                dialogBinding?.rbtnLashioCheck?.isChecked = true
                dialogBinding?.rbtnMuseCheck?.isChecked = false
            }else{
                lastSelected = 2
                dialogBinding?.rbtnMuseCheck?.isChecked = true
                dialogBinding?.rbtnLashioCheck?.isChecked = false
            }
            PreferenceUtils.writeParcelZoneId(PreferenceUtils.readZoneId() ?: 1)
            dialogBinding?.rbtnLashioCheck?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (lastSelected != 1) {
                        lastSelected = 1
                    }
                    dialogBinding?.rbtnMuseCheck?.isChecked = !isChecked
                }
            }

            dialogBinding?.rbtnMuseCheck?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (lastSelected != 2) {
                        lastSelected = 2
                    }
                    dialogBinding?.rbtnLashioCheck?.isChecked = !isChecked
                }
            }

            dialogBinding?.btnConfirm?.setOnClickListener {
                dismiss()
                if (lastSelected == 1) {
                    PreferenceUtils.writeParcelZoneId(1)
                } else {
                    PreferenceUtils.writeParcelZoneId(2)
                }
            }
            show()
        }
    }


    private fun confirmBookNowOrder() {
        binding.btnBookNow.setOnClickListener {
            showConfirmDialog()
        }
    }

    private fun confirmGotBooking() {
        binding.btnViewMyOrderGotBooking.setOnClickListener {
            MainActivity.isOrderHistory = true
            PreferenceUtils.needToShow = false
            startActivity(MainActivity.getIntent(this))
            finish()
        }
    }

    private fun showConfirmDialog() {
        val title = getString(R.string.are_you_sure_you_want_to_send_a_parcel)
        val desc = getString(R.string.if_you_proceed_we_ll_call_you_to_confirm_after_you_ve_booked)
        val btn = getString(R.string.confirm)

        ConfirmDialog.Builder(this@BookingOrderActivity,title,desc,btn,
            callback = {
                PreferenceUtils.readParcelZoneId()?.let { PreferenceUtils.readUserVO().customer_id?.let { it1 ->
                    viewModel.checkParcel(
                        it1,it)
                } }
            }
        ).show(supportFragmentManager,BookingOrderActivity::class.java.simpleName)

    }


    private fun checkZone() {
        when (PreferenceUtils.readParcelZoneId()) {
            1 -> {
                lastSelectedPos = 1
            }

            2 -> {
                lastSelectedPos = 2

            }
            else -> {
                lastSelectedPos = 1
            }
        }
        setUpCurrencyZoneId()
    }


    private fun renderOnFailCheckParcel(state : TrackParcelViewState.OnFailCheckParcel) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message) {
            "Another Login" -> {
                PreferenceUtils.clearCache()
                //startActivity<LoginActivity>()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }


            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()
                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}
