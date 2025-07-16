package com.joy.fattyfood.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomTimer {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, callBack: () -> Unit) = scope.launch(Dispatchers.IO) {
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (true) {
                callBack()
                delay(repeatMillis)
            }
        } else {
            callBack()
        }
    }

    val timer: Job = startCoroutineTimer(delayMillis = 0, repeatMillis = 20000) {
        Log.d("TAG", "Background - tick")
        //doSomethingBackground()
        scope.launch(Dispatchers.Main) {
            //Log.d(TAG, "Main thread - tick")
            //doSomethingMainThread()

        }
    }

    fun startTimer() {
        timer.start()
    }

    fun cancelTimer() {
        timer.cancel()
    }
}