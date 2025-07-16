package com.joy.fattyfood.service

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.joy.fattyfood.utils.PreferenceUtils
import me.pushy.sdk.Pushy
import java.net.URL

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
@SuppressLint("StaticFieldLeak")
class RegisterForPushNotificationsAsync(activity: AppCompatActivity) : AsyncTask<Void, Void, Any>() {
    var mCtx: AppCompatActivity = activity

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void): Any {
        try {
            // Register the device for notifications
            val deviceToken = Pushy.register(mCtx.applicationContext)
            PreferenceUtils.writeDeviceToken(deviceToken)
            // Registration succeeded, log token to logcat
            Log.d("Pushy", "Pushy device token: " + deviceToken)

            // Send the token to your backend server via an HTTP GET request
            URL("https://{YOUR_API_HOSTNAME}/register/device?token=" + deviceToken).openConnection()

            // Provide token to onPostExecute()
            return deviceToken
        } catch (exc: Exception) {
            // Registration failed, provide exception to onPostExecute()
            return exc
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: Any) {
        if(result is Exception){ }
        else{ PreferenceUtils.writeDeviceToken(result.toString()) }
        /*var message: String

        // Registration failed?
        if (result is Exception) {
            // Log to console
            //result.message?.let { Log.e("Pushy", it) }

            // Display error in alert
            //message = result.message.toString()
        }
        else {
            // Registration success, result is device token
            message = "Pushy device token: " + result.toString() + "\n\n(copy from logcat)"

        }

        // Display dialog
        android.app.AlertDialog.Builder(mCtx.applicationContext)
            .setTitle("Pushy")
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()*/
    }
}