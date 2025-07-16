package com.joy.fattyfood.ui.views.activities.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joy.fattyfood.databinding.ActivityChattingBinding
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.model.FattyMessage
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.getVerticalLayoutManager
import com.orikino.fatty.adapters.AdapterMessage
import com.orikino.fatty.model.repository.FirebaseMessageRepository

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
}