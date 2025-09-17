package com.orikino.fatty.ui.views.activities.chat

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orikino.fatty.databinding.ActivityChattingBinding
import com.orikino.fatty.domain.model.ActiveOrderVO
import com.orikino.fatty.domain.model.FattyMessage
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.getVerticalLayoutManager
import com.orikino.fatty.adapters.AdapterMessage
import com.orikino.fatty.model.repository.FirebaseMessageRepository
import com.orikino.fatty.utils.LocaleHelper

class ChattingActivity : AppCompatActivity() {

    lateinit var binding : ActivityChattingBinding

    companion object{
        const val ORDER = "order-id"
        const val RIDER_ID = "rider-id"
    }
    private var riderId : Int = 0
    private var order : String =""
    private var orders : ActiveOrderVO = ActiveOrderVO()
    private val messageAdapter: AdapterMessage by lazy {
        AdapterMessage(mutableListOf(), PreferenceUtils.readUserVO().customer_id.toString())
    }
    private lateinit var firebaseMessageRepository: FirebaseMessageRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        riderId = intent.getIntExtra(RIDER_ID,0)
        order = intent.getStringExtra(ORDER).toString()
        orders = Gson().fromJson(order, object : TypeToken<ActiveOrderVO>() {}.type)
        firebaseMessageRepository = FirebaseMessageRepository()
        firebaseMessageRepository.loadMessage(orders) { messageAdapter.addAll(it as ArrayList<FattyMessage>) }
        setUpView()
        onBackPress()
    }

    private fun setUpView() {
        binding.rvMessage.apply {
            adapter = messageAdapter
            layoutManager = getVerticalLayoutManager()
        }
        sendMessage()
    }

    private fun sendMessage(){
        binding.btnSentd.setOnClickListener {
            firebaseMessageRepository.sendMessage(orders,binding.edtMessage.text.toString()){
                messageAdapter.addAll(it.toMutableList())
                binding.rvMessage.scrollToPosition(messageAdapter.messages.size - 1)
            }
            binding.edtMessage.setText("")
        }
    }

    private fun onBackPress(){
        binding.tbMessageBack.setOnClickListener {
            onBackPressed()
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