package com.orikino.fatty.ui.views.activities.account_setting.currency

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.adapters.CurrencyAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityCurrencyBinding
import com.orikino.fatty.domain.model.CurrencyVO
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
    var isSelecting = false

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
        //currencyNavigator()
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

    private fun renderSucessCurrency(state: com.orikino.fatty.domain.viewstates.AboutViewState.OnSuccessCurrency) {
        // Log the list AS SOON AS IT'S RECEIVED in the state from LiveData
        Log.d("Activity_renderSuccess", "Received state.data: ${state.data.map { Pair(it.currency_id, it.isCheck) }}")
        Log.d("Activity_renderSuccess", "Current isSelecting flag: $isSelecting")

        val listForAdapter: List<CurrencyVO>

        if (isSelecting) {
            // If we are in "selecting" mode (i.e., a click just happened),
            // we trust the list from the ViewModel directly.
            // This list SHOULD have the correct 'isCheck' state from ViewModel's changeIsCheck.
            listForAdapter = state.data
            Log.d("Activity_renderSuccess", "Using state.data directly (isSelecting=true): ${listForAdapter.map { Pair(it.currency_id, it.isCheck) }}")
        } else {
            // This block is for the initial load OR if isSelecting somehow became false.
            // It recalculates 'isCheck' based on preferences.
            Log.d("Activity_renderSuccess", "Recalculating list based on preferences (isSelecting=false)")
            listForAdapter = state.data.map { currencyVO ->
                val isPreferred = currencyVO.currency_id == PreferenceUtils.readCurrCurrency()?.currency_id
                if (isPreferred) {
                    // Make sure 'position' is a valid field and index for your 'lastSelected' logic
                    // lastSelected = currencyVO.position
                }
                // Assuming CurrencyVO is a data class, use .copy()
                currencyVO.copy(isCheck = isPreferred)
            }
            Log.d("Activity_renderSuccess", "List after preference check (isSelecting=false): ${listForAdapter.map { Pair(it.currency_id, it.isCheck) }}")
        }

        // It's crucial that your adapter can handle a List<CurrencyVO>
        // and that it correctly updates its internal data and refreshes the UI.
        // The cast to MutableList might be unnecessary if your adapter takes List.
        currencyAdapter.setNewData(listForAdapter.toMutableList()) // Or just listForAdapter if adapter takes List

        // IMPORTANT: After processing an update that came from a selection,
        // you might want to reset isSelecting if its purpose is only to bypass
        // the preference check for a single update cycle.
        // However, if other logic depends on isSelecting remaining true, be careful.
        // If isSelecting is ONLY for this renderSucessCurrency logic:
        // if (isSelecting) isSelecting = false;
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
            isSelecting = true
            viewModel.changeIsCheck(data)
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
            if (viewModel.selectedCurrency != null)
                PreferenceUtils.writeCurrencyVO(viewModel.selectedCurrency!!)
            MainActivity.isCurrencyUpdate.postValue(true)
            finish()
        }
    }

    private fun currencyNavigator() {
        _binding.btnChange.setOnClickListener {
            //CustomToast(this,"Successfully change to ${viewModel.currencyVO.currency_id}",true).createCustomToast()

        }
    }

    /*private fun chooseCurrency(data: CurrencyResponse.CurrencyVO) {

        println("sslslslls ${data.currency_id}")
        CustomToast(this,"Successfully change to ${data.currency_id}",true).createCustomToast()
    }*/

}