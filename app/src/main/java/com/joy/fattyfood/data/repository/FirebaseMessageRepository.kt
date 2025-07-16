package com.orikino.fatty.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.model.FattyMessage
import com.joy.fattyfood.utils.PreferenceUtils

class FirebaseMessageRepository {
    private val firebaseStoreDb = Firebase.firestore
    fun loadMessage(data: ActiveOrderVO, onLoadComplete: (data: List<FattyMessage>) -> Unit) {
        firebaseStoreDb.collection("message")
            .document(data.customer_order_id.toString())
            .collection(data.order_id.toString())
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener {
                val data = it.result.map {
                    val message = it.toObject(FattyMessage::class.java)
                    Log.d("fattymessage", message.toString())
                    message
                }.toList()

                onLoadComplete(data.filter {
                    it.channel =="customer"
                })
            }

    }

    //message
    fun sendMessage(
        order: ActiveOrderVO,
        message: String,
        onLoadComplete: (data: List<FattyMessage>) -> Unit
    ) {
        val msg = FattyMessage(
            messageText = message,
            fromUserId = "c${PreferenceUtils.readUserVO().customer_id}",
            toUserId = "r${order.rider?.rider_id}",
            orderId = order.order_id.toString(),
            channel = "customer"
        )
        firebaseStoreDb.collection("message")
            .document(order.customer_order_id.toString())
            .collection(order.order_id.toString())
            .add(msg)

        val ref = firebaseStoreDb.collection("message")
            .document(order.customer_order_id.toString())
            .collection(order.order_id.toString())
        ref.addSnapshotListener { value, error ->
            value!!.documents.sortBy {
                it.toObject(FattyMessage::class.java)!!.sentAt
            }
            val data = arrayListOf<FattyMessage>()
            value.documents.map { it ->
                val message = it.toObject(FattyMessage::class.java)
                if (message != null) {
                    data.add(message)
                }
            }
            onLoadComplete(data.filter {
                it.channel =="customer"
            })
        }

    }
}