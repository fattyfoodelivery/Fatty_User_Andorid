package com.orikino.fatty.ui.views.activities.account_setting.edit_profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityEditProfileBinding
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.domain.viewstates.AboutViewState
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.getValue

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding

    private val viewModel : AboutViewModel by viewModels()

    private var userVo : CustomerVO? = null
    private var customerName : String = ""
    private var mProfileUri : String? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == RESULT_OK) {
                val fileUri = data?.data!!
                val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
                mProfileUri = encodeImage(BitmapFactory.decodeStream(inputStream))
                binding.ivProfile.load(fileUri) {
                    error(R.drawable.ic_profile_placeholder)
                    placeholder(R.drawable.ic_profile_placeholder)
                }
                binding.ivProfileBorder.gone()
                binding.btnChange.alpha = 1f
                binding.btnChange.isEnabled = true
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.d("TAG", "Image Picker Error: ")
            } else {
                Log.d("TAG", "Cancel Image Pick: ")
            }
        }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    companion object {
        fun getIntent(): Intent {
            val intent = Intent(FattyApp.getInstance(), EditProfileActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userVo = PreferenceUtils.readUserVO()
        initView()
        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.viewState.observe(this) { render(it) }
    }

    private fun render(state : AboutViewState) {
        when(state) {

            is AboutViewState.OnLoadingUpdateName -> renderOnLoadingUpdateName()
            is AboutViewState.OnSuccessUpdateName -> renderOnSuccessUpdateName(state)
            is AboutViewState.OnFailUpdateName -> renderOnFailUpdateName(state)
            else -> {}
        }
    }

    private fun renderOnLoadingUpdateName() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnSuccessUpdateName(state: AboutViewState.OnSuccessUpdateName) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.writeUserVO(state.data.data)
            finish()
        }
    }


    private fun renderOnFailUpdateName(state: AboutViewState.OnFailUpdateName) {
        LoadingProgressDialog.hideLoadingProgress()
    }

    private fun initView(){
        userVo?.let { user ->
            if (!user.image.isNullOrEmpty()){
                binding.ivProfile.load(PreferenceUtils.IMAGE_URL.plus("/customer/").plus(user.image)) {
                    error(R.drawable.ic_profile_placeholder)
                    placeholder(R.drawable.ic_profile_placeholder)
                }
                binding.ivProfileBorder.visibility = View.GONE
            }
            binding.edtName.setText(user.customer_name)
            binding.ivClear.setOnClickListener {
                binding.ivClear.gone()
                binding.edtName.text.clear()
                binding.btnChange.isEnabled = false
                binding.btnChange.alpha = 0.5f
            }
            binding.edtName.addTextChangedListener { text ->
                customerName = text.toString().trim()
                if (customerName.isEmpty() && mProfileUri.isNullOrEmpty()) {
                    binding.ivClear.gone()
                    binding.btnChange.alpha = 0.5f
                    binding.btnChange.isEnabled = false
                } else {
                    if (user.customer_name != text.toString()){
                        binding.ivClear.show()
                        binding.btnChange.alpha = 1f
                        binding.btnChange.isEnabled = true
                    }else{
                        if (!mProfileUri.isNullOrEmpty()){
                            binding.btnChange.alpha = 1f
                            binding.btnChange.isEnabled = true
                        }else{
                            binding.btnChange.alpha = 0.5f
                            binding.btnChange.isEnabled = false
                        }
                    }
                }
            }
            binding.btnAddNewPhoto.setOnClickListener {
                ImagePicker.with(this)
                    .compress(1024)
                    .cropSquare()
                    .maxResultSize(70, 70)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
            binding.btnChange.setOnClickListener {
                if (binding.edtName.text.toString().isEmpty()) {
                    binding.edtName.error = "Please Enter Name"
                    return@setOnClickListener
                }
                val newUserVo = CustomerVO(
                    customer_id = user.customer_id,
                    customer_type_id = user.customer_type_id,
                    is_restricted = user.is_restricted,
                    customer_name = customerName.ifEmpty {
                        user.customer_name
                    },
                    customer_phone = user.customer_phone,
                    latitude = user.latitude,
                    longitude = user.longitude,
                    fcm_token = user.fcm_token,
                    image = if (mProfileUri.isNullOrEmpty()) null else mProfileUri
                )
                viewModel.updateCusName(PreferenceUtils.readDeviceToken().toString(),newUserVo)
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}