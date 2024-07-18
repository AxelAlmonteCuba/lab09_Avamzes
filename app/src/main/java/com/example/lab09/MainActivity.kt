package com.example.lab09

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding

class MainActivity : ComponentActivity() {
    private val REQUEST_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Solicitar permiso para notificaciones en Android 13 y versiones posteriores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            }
        }

        val btnPlay = findViewById<Button>(R.id.play_button)
        val btnPause = findViewById<Button>(R.id.pause_button)
        val btnResume = findViewById<Button>(R.id.resume_button)
        val btnStop = findViewById<Button>(R.id.stop_button)

        btnPlay.setOnClickListener(onClickListenerPlay())
        btnPause.setOnClickListener(onClickListenerPause())
        btnResume.setOnClickListener(onClickListenerResume())
        btnStop.setOnClickListener(onClickListenerStop())
    }

    private fun onClickListenerPlay(): (View) -> Unit = {
        val audioPlayServiceIntent = Intent(applicationContext, AudioPlayService::class.java)
        audioPlayServiceIntent.putExtra(AudioPlayService.FILENAME, "las_meninas.mp3")
        audioPlayServiceIntent.putExtra(AudioPlayService.COMMAND, AudioPlayService.PLAY)
        startService(audioPlayServiceIntent)
    }

    private fun onClickListenerPause(): (View) -> Unit = {
        val audioPlayServiceIntent = Intent(applicationContext, AudioPlayService::class.java)
        audioPlayServiceIntent.putExtra(AudioPlayService.COMMAND, AudioPlayService.PAUSE)
        startService(audioPlayServiceIntent)
    }

    private fun onClickListenerResume(): (View) -> Unit = {
        val audioPlayServiceIntent = Intent(applicationContext, AudioPlayService::class.java)
        audioPlayServiceIntent.putExtra(AudioPlayService.COMMAND, AudioPlayService.RESUME)
        startService(audioPlayServiceIntent)
    }

    private fun onClickListenerStop(): (View) -> Unit = {
        val audioPlayServiceIntent = Intent(applicationContext, AudioPlayService::class.java)
        audioPlayServiceIntent.putExtra(AudioPlayService.COMMAND, AudioPlayService.STOP)
        startService(audioPlayServiceIntent)
    }
}