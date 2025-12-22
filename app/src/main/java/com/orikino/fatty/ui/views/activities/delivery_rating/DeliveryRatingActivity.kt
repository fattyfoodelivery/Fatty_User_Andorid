package com.orikino.fatty.ui.views.activities.delivery_rating

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityDeliveryRatingBinding
import com.orikino.fatty.domain.model.RatingType
import com.orikino.fatty.domain.view_model.DeliveryRatingViewModel
import com.orikino.fatty.domain.viewstates.DeliveryRatingViewState
import com.orikino.fatty.service.FattyPushyService
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DeliveryRatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryRatingBinding

    private val viewModel: DeliveryRatingViewModel by viewModels()
    companion object {
        const val ORDER_ID = "order_id"
        const val ORDER_TYPE = "order_type"
        const val DOUBLE_SCREEN_CHECK = "double_screen_check"
        fun getIntent(context: Context, order_id: Int, orderType: String?): Intent {
            val intent = Intent(context, DeliveryRatingActivity::class.java)
            intent.putExtra(ORDER_ID, order_id)
            intent.putExtra(ORDER_TYPE, orderType)
            return intent
        }
    }

    private var orderId = 0
    private var orderType = "food"
    private var rider: RatingType? = null
    private var restaurant: RatingType? = null
    private var checkedValue: MutableList<String> = ArrayList()
    private var ratingType: String = "rider"
    private var checkBoxCondition = ""
    private var checkOrderType = ""
    private var star = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeliveryRatingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fixCutoutOfEdgeToEdge(binding.root)
        orderId = intent.getIntExtra(ORDER_ID, 0)

        if (Constants.getPreferenceInt(this, DOUBLE_SCREEN_CHECK) == orderId){
            finish()
        }else{
            Constants.setPreferenceInt(this, DOUBLE_SCREEN_CHECK,orderId)
        }

        orderType = intent.getStringExtra(ORDER_TYPE).toString()
        viewModel.orderId = orderId
        subscribeUI()
        ratingService()
        rating()
        checkOrderType()
        newRating()
        stopService()
        onBackPress()
        hideView()
    }

    private fun onBackPress() {
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun checkOrderType() {
        if (orderType != "food") {
            binding.txtOne.visibility = View.GONE
            binding.txtTwo.visibility = View.GONE
        }
    }

    private fun newRating() {
        viewModel.fetchRating()
    }

    private fun rating() {
        binding.btnSummit.setOnClickListener {
            /*viewModel.description = et_feedback.text.toString().trim()
            viewModel.rating = rating_bar.rating.toInt()*/
            var option = ""
            for (i in checkedValue) {
                option += i
                if (i != checkedValue[checkedValue.size - 1]) {
                    option += ", "
                }
            }

            val comment = binding.etFeedback.text.toString().trim()
            val locale = PreferenceUtils.readLanguage()
            if (option != "")
                locale?.let { it1 ->
                    viewModel.uploadRating(ratingType, orderId, star, option, comment,
                        it1
                    )
                }
            else
                Toast.makeText(
                    this,
                    getString(R.string.need_to_check_at_least_one_option), Toast.LENGTH_SHORT
                ).show()
            //viewModel.deliveryRating()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun ratingService() {
        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            visibleView()
            when (rating) {
                1.0f -> {
                    binding.textViewRatingResult.text = getString(R.string.worst)
                    star = "*"
                }

                2.0f -> {
                    binding.textViewRatingResult.text = getString(R.string.bad)
                    star = "**"
                }

                3.0f -> {
                    binding.textViewRatingResult.text = getString(R.string.good)
                    star = "***"
                }

                4.0f -> {
                    binding.textViewRatingResult.text = getString(R.string.very_good)
                    star = "****"
                }

                else -> {
                    binding.textViewRatingResult.text = getString(R.string.excellent)
                    star = "*****"
                }
            }
            if (rating < 1.0f) {
                binding.ratingBar.rating = 0.0f
            }
            if (rating < 3.0f) {
                val key: MutableList<String> = ArrayList()
                val value: MutableList<String> = ArrayList()
                if (ratingType == "rider") {
                    binding.txtOne.background = resources.getDrawable(R.drawable.bg_circle_rating_enable)
                    binding.txtOne.setTextColor(resources.getColor(R.color.fatty))
                    for (i in rider!!.bad) {
                        key.add(i.id.toString())
                        value.add(i.option)
                    }
                } else {
                    binding.txtTwo.setTextColor(resources.getColor(R.color.fatty))
                    binding.txtTwo.background = resources.getDrawable(R.drawable.bg_circle_rating_enable)
                    for (i in restaurant!!.bad) {
                        key.add(i.id.toString())
                        value.add(i.option)
                    }
                }
                checkBox(key, value, "bad", ratingType)
                Log.d("TTW Rating ###", rider!!.bad[0].option)
            } else {
                val key: MutableList<String> = ArrayList()
                val value: MutableList<String> = ArrayList()
                if (ratingType == "rider") {
                    binding.txtOne.setTextColor(resources.getColor(R.color.fatty))
                    binding.txtOne.background = resources.getDrawable(R.drawable.bg_circle_rating_enable)
                    for (i in rider!!.good) {
                        key.add(i.id.toString())
                        value.add(i.option)
                    }
                } else {
                    binding.txtTwo.setTextColor(resources.getColor(R.color.fatty))
                    binding.txtTwo.background = resources.getDrawable(R.drawable.bg_circle_rating_enable)
                    for (i in restaurant!!.good) {
                        key.add(i.id.toString())
                        value.add(i.option)
                    }
                }
                checkBox(key, value, "good", ratingType)
                Log.d("TTW Rating ###", rider!!.good[0].option)
            }
        }
    }

    private fun checkBox(key: List<String>, value: List<String>, badGood: String, type: String) {
        if (checkBoxCondition != badGood) {
            clearCheckBox(key, value, badGood, type)
        } else if (type != checkOrderType) {
            clearCheckBox(key, value, badGood, type)
        }
        checkBoxCondition = badGood
    }

    private fun clearCheckBox(
        key: List<String>,
        value: List<String>,
        badGood: String,
        type: String
    ) {
        binding.linearLayout.removeAllViews()
        checkedValue.clear()
        checkBoxCondition = badGood
        checkOrderType = type
        for (i in key.indices) {
            val row = TableRow(this)
            row.id = i
            row.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            val checkBox = CheckBox(this)
            checkBox.id = i
            checkBox.text = value[i]
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedValue.add(key[i])
                } else {
                    checkedValue.remove(key[i])
                }
            }
            row.addView(checkBox)
            binding.linearLayout.addView(row)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun finishRating() {
        checkBoxCondition = ""
        ratingType = "rider"
        orderType = "food"
        binding.txtOne.visibility = View.VISIBLE
        binding.txtTwo.visibility = View.VISIBLE
        binding.txtOneFinish.visibility = View.GONE
        binding.txtTwoFinish.visibility = View.GONE
        binding.txtOne.text = "1"
        binding.txtTwo.text = "2"
        binding.txtOne.setTextColor(R.color.black)
        binding.txtTwo.setTextColor(R.color.black)
        binding.txtOne.background = resources.getDrawable(R.drawable.bg_circle_rating_disable)
        binding.txtTwo.background = resources.getDrawable(R.drawable.bg_circle_rating_disable)
    }

    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }

    private fun clearForm() {
        binding.etFeedback.setText("")
        hideView()
        binding.ratingBar.rating = 0.0f
        star = ""
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun subscribeUI() {
        binding.etFeedback.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                binding.txtCountView.text = "${s!!.length}/300"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        viewModel.viewState.observe(this, { render(it) })

    }

    private fun render(state : DeliveryRatingViewState){
        when(state){
            is DeliveryRatingViewState.OnLoadingDeliveryRating -> {
                renderOnLoadDeliveryRating()
            }

            is DeliveryRatingViewState.OnFailDeliveryRating -> {
                renderOnFailDeliveryRating(state)
            }
            is DeliveryRatingViewState.OnSuccessDeliveryRating -> {
                renderOnSuccessDeliveryRating(state)
            }

            is DeliveryRatingViewState.OnLoadingRating -> {
                renderOnLoadRating()
            }
            is DeliveryRatingViewState.OnFailRating -> {
                renderOnFailRating(state)
            }
            is DeliveryRatingViewState.OnSuccessRating -> {
                renderOnSuccessRating(state)
            }

            is DeliveryRatingViewState.OnLoadingRatingValue -> {
                renderOnLoadRatingValue()
            }
            is DeliveryRatingViewState.OnFailRatingValue -> {
                renderOnFailRatingValue(state)
            }

            is DeliveryRatingViewState.OnSuccessRatingValue -> {
                renderOnSuccessRatingValue(state)
            }
        }

    }


    private fun renderOnLoadDeliveryRating() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnFailDeliveryRating(state: DeliveryRatingViewState.OnFailDeliveryRating) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Connection Issue" -> showSnackBar(resources.getString(R.string.no_internet_title))
            "Another Login" ->
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        startActivity(Intent(this, SplashActivity::class.java))
                    })
                    .show(supportFragmentManager, HomeFragment::class.simpleName)

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, HomeFragment::class.simpleName)

            else -> showSnackBar(state.message)

        }
    }

    private fun renderOnSuccessDeliveryRating(state: DeliveryRatingViewState.OnSuccessDeliveryRating) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            showSnackBar(getString(R.string.successfully_rating_our_service))
            clearForm()
            onBackPressed()
        }
    }

    private fun renderOnLoadRating() {
        LoadingProgressDialog.hideLoadingProgress()
    }

    private fun renderOnFailRating(state: DeliveryRatingViewState.OnFailRating) {
        LoadingProgressDialog.hideLoadingProgress()

    }

    private fun renderOnSuccessRating(state: DeliveryRatingViewState.OnSuccessRating) {
        LoadingProgressDialog.showLoadingProgress(this)
        if (orderType != "food") {
            Toast.makeText(
                this,
                getString(R.string.successfully_rating_our_service), Toast.LENGTH_SHORT
            )
                .show()
            clearForm()
            stopService()
            finish()
        } else {
            if (ratingType == "rider") {
                binding.txtTwo.background =
                    resources.getDrawable(R.drawable.bg_circle_rating_enable)
                binding.txtTwo.setTextColor(resources.getColor(R.color.fatty))
                ratingType = "restaurant"
                checkedValue.clear()
                binding.txtOne.text = ""
                binding.textViewRatingService.text =
                    getString(R.string.let_s_rate_our_restaurant_s_service)
                binding.txtOne.visibility = View.GONE
                binding.txtOneFinish.visibility = View.VISIBLE
                showSnackBar(getString(R.string.successfully_rating_our_service))
                clearForm()
                hideView()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.successfully_rating_our_service),
                    Toast.LENGTH_SHORT
                ).show()
                //showSnackBar("Successfully Rating our service")
                clearForm()
                //hideView()
                binding.txtTwo.text = ""
                binding.txtTwo.visibility = View.GONE
                binding.txtTwoFinish.visibility = View.VISIBLE
                checkedValue.clear()
                ratingType = "rider"
                stopService()
                finish()
                //finishRating()
            }
        }
    }


    private fun renderOnLoadRatingValue(){
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnFailRatingValue(state: DeliveryRatingViewState.OnFailRatingValue){
        LoadingProgressDialog.hideLoadingProgress()

    }

    private fun renderOnSuccessRatingValue(state: DeliveryRatingViewState.OnSuccessRatingValue){
        LoadingProgressDialog.hideLoadingProgress()
        rider = state.data.data!!.rider
        restaurant = state.data.data.restaurant
        Log.d("TTW ###", state.data.data.rider.bad.size.toString())
    }


    private fun hideView() {
        binding.etFeedback.visibility = View.GONE
        binding.btnSummit.visibility = View.GONE
        binding.scrollbar.visibility = View.GONE
        binding.txtCountView.visibility = View.GONE
        binding.txtPerfect.visibility = View.VISIBLE
        binding.txtVeryBad.visibility = View.VISIBLE
        binding.textViewRatingResult.visibility = View.GONE
    }

    private fun visibleView() {
        binding.txtPerfect.visibility = View.GONE
        binding.txtVeryBad.visibility = View.GONE
        binding.etFeedback.visibility = View.VISIBLE
        binding.btnSummit.visibility = View.VISIBLE
        binding.scrollbar.visibility = View.VISIBLE
        binding.txtCountView.visibility = View.VISIBLE
        binding.textViewRatingResult.visibility = View.VISIBLE
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}