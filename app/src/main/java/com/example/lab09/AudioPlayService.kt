package com.example.lab09

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioPlayService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var serviceHandler: Handler
    private lateinit var handlerThread: HandlerThread

    companion object {
        const val FILENAME = "FILENAME"
        const val COMMAND = "COMMAND"
        const val PLAY = "PLAY"
        const val PAUSE = "PAUSE"
        const val RESUME = "RESUME"
        const val STOP = "STOP"
        const val CHANNEL_ID = "AudioPlayServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        handlerThread = HandlerThread("AudioPlayServiceThread")
        handlerThread.start()
        serviceHandler = Handler(handlerThread.looper)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quitSafely()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filename = intent.getStringExtra(FILENAME)
        val command = intent.getStringExtra(COMMAND)

        when (command) {
            PLAY -> {
                startForegroundService()
                serviceHandler.post { audioPlay(filename) }
            }
            PAUSE -> serviceHandler.post { audioPause() }
            RESUME -> serviceHandler.post { audioResume() }
            STOP -> {
                serviceHandler.post { audioStop() }
                stopForeground(true)
            }
        }

        return START_STICKY
    }

    private fun audioPlay(filename: String?) {
        if (filename != null) {
            val assetFileDescriptor = assets.openFd(filename)
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            assetFileDescriptor.close()
            mediaPlayer.prepare()
            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.isLooping = false
            mediaPlayer.start()
        }
    }

    private fun audioPause() {
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    private fun audioResume() {
        if (this::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun audioStop() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()

        val playIntent = Intent(this, AudioPlayService::class.java).apply {
            putExtra(COMMAND, PLAY)
        }
        val pauseIntent = Intent(this, AudioPlayService::class.java).apply {
            putExtra(COMMAND, PAUSE)
        }
        val resumeIntent = Intent(this, AudioPlayService::class.java).apply {
            putExtra(COMMAND, RESUME)
        }
        val stopIntent = Intent(this, AudioPlayService::class.java).apply {
            putExtra(COMMAND, STOP)
        }

        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)
        val pendingPauseIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val pendingResumeIntent = PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_IMMUTABLE)
        val pendingStopIntent = PendingIntent.getService(this, 3, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reproducción de Audio")
            .setContentText("Reproduciendo audio en segundo plano")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_play, "Play", pendingPlayIntent)
            .addAction(R.drawable.ic_pause, "Pause", pendingPauseIntent)
            .addAction(R.drawable.ic_resume, "Resume", pendingResumeIntent)
            .addAction(R.drawable.ic_stop, "Stop", pendingStopIntent)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Audio Play Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}