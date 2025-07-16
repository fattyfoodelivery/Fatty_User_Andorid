package com.orikino.fatty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joy.fattyfood.databinding.LayoutItemMessageBinding
import com.joy.fattyfood.domain.model.FattyMessage

class AdapterMessage(
    internal val messages: MutableList<FattyMessage>,
    private val customerId: String
) : RecyclerView.Adapter<AdapterMessage.ViewHolder>() {

    private lateinit var binding: LayoutItemMessageBinding

    class ViewHolder(val binding: LayoutItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    fun message() = messages

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            if (messages[position].fromUserId == "c${customerId}") {
                tvCustomerMessage.visibility = View.VISIBLE
                tvCustomerMessage.text = messages[position].messageText
                tvRiderMessage.visibility = View.GONE
            } else {
                tvRiderMessage.text = messages[position].messageText
                tvRiderMessage.visibility = View.VISIBLE
                tvCustomerMessage.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: FattyMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun addAll(data: MutableList<FattyMessage>) {
        messages.clear()
        messages.addAll(data)
        messages.sortBy { it.sentAt }
        notifyDataSetChanged()
    }
}