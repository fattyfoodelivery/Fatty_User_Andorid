package com.orikino.fatty.ui.views.activities.account_setting.currency

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.adapters.CurrencyAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityCurrencyBinding
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.PreferenceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyActivity : AppCompatActivity() {

    var lastSelected = 1//PreferenceUtils.readCurrencyId()?.position
    lateinit var _binding : ActivityCurrencyBinding
    private val viewModel : AboutViewModel by viewModels()

    lateinit var currencyAdapter: CurrencyAdapter

    var currency : String = ""

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(), CurrencyActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        setUpObserver()
        setUpCurrency()
        currencyNavigator()
        navigator()

    }

    private fun setUpObserver() {
        viewModel.fetchCurrency()
        viewModel.viewState.observe(this, Observer {
            render(it)
        })
    }

    private fun render(state : com.orikino.fatty.domain.viewstates.AboutViewState) {
        when(state) {
            is com.orikino.fatty.domain.viewstates.AboutViewState.OnFailCurrency -> {
                renderError(state)
            }
            is com.orikino.fatty.domain.viewstates.AboutViewState.OnSuccessCurrency -> {
                renderSucessCurrency(state)
            }
            else -> {

            }
        }
    }

    private fun renderError(state: com.orikino.fatty.domain.viewstates.AboutViewState.OnFailCurrency) {
        CustomToast(this,state.message,false).createCustomToast()
    }

    private fun renderSucessCurrency(state : com.orikino.fatty.domain.viewstates.AboutViewState.OnSuccessCurrency) {
        if (state.data.success) {
            currencyAdapter.setNewData(state.data.data)

            /*viewModel.currencyVO = CurrencyResponse.CurrencyVO(
                currency_id = data.data.get(0).currency_id,
                currency_name = data.data.get(0).currency_name,
                currency_symbol = data.data.get(0).currency_symbol,
                image = data.data.get(0).image,
                position = lastSelected
            )*/
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun setUpCurrency() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL,false)
        _binding?.rvCurrency?.layoutManager = linearLayoutManager
            _binding?.rvCurrency?.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.HORIZONTAL
            )
        )
        _binding?.rvCurrency?.setHasFixedSize(true)
        _binding?.rvCurrency?.isNestedScrollingEnabled = true
        currencyAdapter = CurrencyAdapter(FattyApp.getInstance()) { data,str,pos ->
            //viewModel.currencyVO.isCheck != viewModel.currencyVO.isCheck
            currencyAdapter.notifyDataSetChanged()
        }
        _binding?.rvCurrency?.adapter = currencyAdapter
    }

    private fun navigator() {
        _binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        _binding.btnChange.setOnClickListener {
            PreferenceUtils.clearCartData()
            //PreferenceUtils.writeCurrencyId(viewModel.currencyVO)
            MainActivity.isCurrencyUpdate.postValue(true)
            onBackPressed()
        }
    }

    private fun currencyNavigator() {
        _binding.btnChange.setOnClickListener {
            CustomToast(this,"Successfully change to ${viewModel.currencyVO.currency_id}",true).createCustomToast()
            finish()
        }
    }

    /*private fun chooseCurrency(data: CurrencyResponse.CurrencyVO) {

        println("sslslslls ${data.currency_id}")
        CustomToast(this,"Successfully change to ${data.currency_id}",true).createCustomToast()
    }*/

}