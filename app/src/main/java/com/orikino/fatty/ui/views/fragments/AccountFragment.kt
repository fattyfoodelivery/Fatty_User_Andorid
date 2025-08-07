package com.orikino.fatty.ui.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.orikino.fatty.R
import com.orikino.fatty.domain.viewstates.AboutViewState
import com.orikino.fatty.databinding.FragmentAccountBinding
import com.orikino.fatty.databinding.ItemEditDialogBinding
import com.orikino.fatty.databinding.LayoutConfirmCancelDialogBinding
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.ParcelInfoVO
import com.orikino.fatty.domain.model.ParcelSenderReceiverVO
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.ui.views.activities.account_setting.about.AboutUsActivity
import com.orikino.fatty.ui.views.activities.account_setting.app_version.VersionUpdateActivity
import com.orikino.fatty.ui.views.activities.account_setting.currency.CurrencyActivity
import com.orikino.fatty.ui.views.activities.account_setting.delete.AccountDeleteActivity
import com.orikino.fatty.ui.views.activities.account_setting.guide.AppGuideActivity
import com.orikino.fatty.ui.views.activities.account_setting.help_center.HelpCenterActivity
import com.orikino.fatty.ui.views.activities.account_setting.language.SettingLanguageActivity
import com.orikino.fatty.ui.views.activities.account_setting.manage_address.ManageAddressActivity
import com.orikino.fatty.ui.views.activities.account_setting.privacy.PrivacyActivity
import com.orikino.fatty.ui.views.activities.account_setting.term_condition.TermAndConditionActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.*


@AndroidEntryPoint
class AccountFragment : Fragment() {


    private var accountBinding : FragmentAccountBinding? = null

    private val viewModel : AboutViewModel by viewModels()

    private var isUpdate = false

    var customer : CustomerVO = CustomerVO()
    var mProfileUri : Uri? = null
    companion object {

        const val REQUEST_CODE_PERMISSIONS = 100

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!


                mProfileUri = fileUri
                updateProfile(fileUri)
                //accountBinding?.imvProfile?.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.d("TAG", "Image Picker Error: ")
            } else {
                Log.d("TAG", "Cancel Image Pick: ")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    private fun updateProfile(uri: Uri) {

        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        viewModel.image = encodeImage(BitmapFactory.decodeStream(inputStream))

        if (accountBinding?.tvUserName?.text.toString().isNotEmpty()) viewModel.userName =
            accountBinding?.tvUserName?.text.toString()
        else accountBinding?.tvUserName?.error = "Please Enter Name"
        if (viewModel.userName.isNotEmpty()) PreferenceUtils.readUserVO().customer_phone.let { it1 ->
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.updateProfile(
                    PreferenceUtils.readDeviceToken().toString(),
                    it,
                    viewModel.userName,
                    it1,
                    viewModel.image,
                    PreferenceUtils.readDeviceToken().toString()
                )
            }
        }

    }


    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        accountBinding = FragmentAccountBinding.inflate(inflater,container,false)
        return (accountBinding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        subscribeUI()
        setUpEditProfile()
        navigateToPages()


    }

    override fun onResume() {
        super.onResume()
        setUpProfileInfo()
    }

    private fun setUpEditProfile() {
        accountBinding?.imvProfile?.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .cropSquare()
                .maxResultSize(70,70)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }


    }



    private fun setUpProfileInfo() {

        if (!isUpdate) {
            accountBinding?.ivCurrency?.text = PreferenceUtils.readCurrCurrency()?.currency_symbol
            accountBinding?.tvUserName?.text = PreferenceUtils.readUserVO().customer_name
            accountBinding?.tvUserPhone?.text = PreferenceUtils.readUserVO().customer_phone
            accountBinding?.imvProfile?.load(PreferenceUtils.IMAGE_URL.plus("/customer/").plus(PreferenceUtils.readUserVO().image)) {
                error(R.drawable.add_profile)
                placeholder(R.drawable.add_profile)
            }

            if(PreferenceUtils.readUserVO().is_restricted == 1){
                accountBinding?.rlUserStatus?.show()
            }
            else{
                accountBinding?.rlUserStatus?.gone()

            }
        } else {
            accountBinding?.ivCurrency?.text = PreferenceUtils.readCurrCurrency()?.currency_symbol
            accountBinding?.tvUserName?.text = customer.customer_name
            accountBinding?.tvUserPhone?.text = customer.customer_phone
            accountBinding?.imvProfile?.load(PreferenceUtils.IMAGE_URL.plus("/customer/").plus(PreferenceUtils.readUserVO().image)) {
                error(R.drawable.add_profile)
                placeholder(R.drawable.add_profile)
            }
            if(customer.is_restricted == 1){
                accountBinding?.rlUserStatus?.show()
            }
            else{
                accountBinding?.rlUserStatus?.gone()

            }
        }

    }

    private fun subscribeUI() {
        viewModel.viewState.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(state : AboutViewState) {
        when(state) {
            is AboutViewState.OnSuccessLogout -> renderSuccessLogout()
            is AboutViewState.OnFailLogout -> renderFailLogout()

            is AboutViewState.OnLoadingUpdateName -> renderOnLoadingUpdateName()
            is AboutViewState.OnSuccessUpdateName -> renderOnSuccessUpdateName(state)
            is AboutViewState.OnFailUpdateName -> renderOnFailUpdateName(state)

            is AboutViewState.OnLoadUpdateProfile -> renderOnLoadUpdateProfile()
            is AboutViewState.OnSuccessUpdateProfile -> renderOnSuccessUpdateProfile(state)
            is AboutViewState.OnFailUpdateProfile -> renderOnFailUpdateProfile(state)
            else -> {}
        }
    }


    private fun renderOnLoadUpdateProfile() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }
    private fun renderOnSuccessUpdateProfile(state: AboutViewState.OnSuccessUpdateProfile) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.writeUserVO(state.data.data)
            CustomToast(requireContext(),state.data.message,true).createCustomToast()
            updateUserPhoto()
        }
    }

    private fun updateUserPhoto() {
        accountBinding?.imvProfile?.load(PreferenceUtils.IMAGE_URL.plus("/customer/").plus(PreferenceUtils.readUserVO().image)){
            error(R.drawable.add_profile)
            placeholder(R.drawable.add_profile)
        }
    }
    private fun renderOnFailUpdateProfile(state: AboutViewState.OnFailUpdateProfile) {
        LoadingProgressDialog.hideLoadingProgress()
    }
    private fun renderOnLoadingUpdateName() {
        LoadingProgressDialog.showLoadingProgress(requireContext())
    }

    private fun renderOnSuccessUpdateName(state: AboutViewState.OnSuccessUpdateName) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            isUpdate = true
            PreferenceUtils.writeUserVO(state.data.data)
            setUpProfileInfo()
        }
    }


    private fun renderOnFailUpdateName(state: AboutViewState.OnFailUpdateName) {
        LoadingProgressDialog.hideLoadingProgress()
    }


    private fun renderSuccessLogout() {
        clearCache()
        requireActivity().finish()
        //requireContext().startActivity<SplashActivity>()
        val intent = Intent(requireContext(), SplashActivity::class.java)
        requireContext().startActivity(intent)
    }

    private fun renderFailLogout() {

    }

    private fun navigateToPages() {

        accountBinding?.apply {
            cvEdit.setOnClickListener {
                PreferenceUtils.needToShow = false
                updateUserName()
            }
            llManageAddress.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(ManageAddressActivity.getIntent(false))
            }
            llLanguage.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(SettingLanguageActivity.getIntent())
            }
            llCurrency.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(CurrencyActivity.getIntent())
            }
            llGuide.setOnClickListener {
                PreferenceUtils.needToShow = false
                //requireActivity().startActivity<AppGuideActivity>()
                val intent = Intent(requireContext(), AppGuideActivity::class.java)
                requireContext().startActivity(intent)
            }
            llHelp.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(HelpCenterActivity.getIntent())
            }
            llVersionUpdate.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(VersionUpdateActivity.getIntent())
            }
            llAbout.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(AboutUsActivity.getIntent())
            }
            llPrivacy.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(PrivacyActivity.getIntent())
            }
            llTermCondition.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(TermAndConditionActivity.getIntent())
            }
            llDelete.setOnClickListener {
                PreferenceUtils.needToShow = false
                startActivity(AccountDeleteActivity.getIntent())
            }

            llLogout.setOnClickListener {
                PreferenceUtils.needToShow = false
                showConfirmDialog(resources.getString(R.string.logout_title),resources.getString(R.string.are_you_sure),resources.getString(R.string.logout_label))
            }

        }
    }

    private fun showConfirmDialog(title : String,message: String,btn : String) {
        val dialogView = LayoutConfirmCancelDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView.tvTitle.text = title
            dialogView.tvDesc.text = message

            dialogView.btnCancel.setOnClickListener {
                dismiss()
            }
            dialogView.btnContact.text = resources.getString(R.string.confirm)
            dialogView.btnContact.setOnClickListener {
                dismiss()
                PreferenceUtils.readUserVO().customer_id?.let { viewModel.fetchLogout(it) }
            }
            show()
        }
    }

    private fun updateUserName() {
        var customerName : String = ""
        val dialogView = ItemEditDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView.ivClose.setOnClickListener {
                dismiss()
            }
            dialogView.ivClear.gone()
            dialogView.ivClear.setOnClickListener {
                dialogView.edtName.text.clear()
                dialogView.btnChange.isEnabled = false
                dialogView.btnChange.alpha = 0.8f
            }
            dialogView.edtName.addTextChangedListener {
                customerName = it?.toString()?.trim() ?: ""
                if (customerName.isEmpty()) {
                    dialogView.btnChange.alpha = 0.8f
                    dialogView.btnChange.isEnabled = false
                } else {
                    dialogView.ivClear.show()
                    dialogView.btnChange.alpha = 1f
                    dialogView.btnChange.isEnabled = true
                }
            }
            dialogView.btnChange.text = resources.getString(R.string.change)
            dialogView.btnChange.setOnClickListener {
                dismiss()
                customer = CustomerVO(
                    customer_id = PreferenceUtils.readUserVO().customer_id,
                    customer_type_id = PreferenceUtils.readUserVO().customer_type_id,
                    is_restricted = PreferenceUtils.readUserVO().is_restricted,
                    customer_name = customerName,
                    customer_phone = PreferenceUtils.readUserVO().customer_phone,
                    image = PreferenceUtils.readUserVO().image,
                    latitude = PreferenceUtils.readUserVO().latitude,
                    longitude = PreferenceUtils.readUserVO().longitude,
                    fcm_token = PreferenceUtils.readUserVO().fcm_token
                )
                viewModel.updateCusName(PreferenceUtils.readDeviceToken().toString(),customer)
            }
            show()
        }
    }
    private fun clearCache() {
        PreferenceUtils.writeUserVO(CustomerVO())
        PreferenceUtils.writeFirstTime(true)
        PreferenceUtils.writeLanguage("en")
        PreferenceUtils.writeFoodOrderList(mutableListOf())
        PreferenceUtils.writeAddToCart(false)
        PreferenceUtils.writeRestaurant(FoodMenuByRestaurantVO())
        PreferenceUtils.writeSenderReceiver(ParcelSenderReceiverVO())
        PreferenceUtils.writeParcelInfo(ParcelInfoVO())
        PreferenceUtils.writeIsSelected(0)
    }



}