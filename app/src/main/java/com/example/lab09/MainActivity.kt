package com.example.lab09

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) {
                v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left,systemBars.top,systemBars.right,systemBars.bottom)
            insets
        }

        val btnPlay = findViewById<Button>(R.id.play_button)
        val btnStop = findViewById<Button>(R.id.stop_button)

        btnPlay.setOnClickListener(onClickListenerPlay())
        btnStop.setOnClickListener(onClickListenerStop())
    }
    private fun onClickListenerPlay():(View)->Unit={
        val audioPlayServiceIntent = Intent(applicationContext,AudioPlayService::class.java)
        audioPlayServiceIntent.putExtra(AudioPlayService.FILENAME,"musica.mp3")
        audioPlayServiceIntent.putExtra(AudioPlayService.COMMAND,AudioPlayService.PLAY)
        startService(audioPlayServiceIntent)

    }
    private fun onClickListenerStop():(View)->Unit= {
        val audioPlayServiceIntent = Intent(applicationContext,AudioPlayService::class.java)
        audioPlayServiceIntent.putExtra(AudioPlayService.COMMAND,AudioPlayService.STOP)
        startService(audioPlayServiceIntent)
    }


}

