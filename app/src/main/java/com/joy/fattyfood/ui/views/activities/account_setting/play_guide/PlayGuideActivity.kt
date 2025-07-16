package com.joy.fattyfood.ui.views.activities.account_setting.play_guide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityPlayGuideBinding

class PlayGuideActivity : AppCompatActivity() {

    private lateinit var playGuideBinding: ActivityPlayGuideBinding

    companion object {
        const val V_URL = "v_url"
        fun getIntent(url : String) : Intent {
            val intent = Intent(FattyApp.getInstance(), PlayGuideActivity::class.java)
            intent.putExtra(V_URL,url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playGuideBinding = ActivityPlayGuideBinding.inflate(layoutInflater)
        setContentView(playGuideBinding.root)


        val url = intent.getStringExtra(V_URL)

        val uri: Uri = Uri.parse(url)

        // on below line we are setting
        // video uri for our video view.
        playGuideBinding.videoView.setVideoURI(uri)

        // on below line we are creating variable
        // for media controller and initializing it.
        val mediaController = MediaController(this)

        // on below line we are setting anchor
        // view for our media controller.
        mediaController.setAnchorView(playGuideBinding.videoView)

        // on below line we are setting media player
        // for our media controller.
        mediaController.setMediaPlayer(playGuideBinding.videoView)

        // on below line we are setting media
        // controller for our video view.
        playGuideBinding.videoView.setMediaController(mediaController)

        // on below line we are
        // simply starting our video view.
        playGuideBinding.videoView.start()



    }

//    override fun onStart() {
//        super.onStart()
//        player?.playWhenReady = true
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        player?.playWhenReady = false
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        player?.release()
//    }
}