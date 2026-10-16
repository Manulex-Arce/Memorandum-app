
package com.example.notificaciones_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class OverlayService : Service(),
    TextureView.SurfaceTextureListener {

    companion object {
        const val ACTION_START =
            "com.example.notificaciones_app.ACTION_START"

        const val ACTION_STOP =
            "com.example.notificaciones_app.OVERLAY_STOP"

        private const val CHANNEL_ID = "recordatorio_media"
        private const val NOTIFICATION_ID = 7021
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private var textureView: TextureView? = null

    private var videoPlayer: MediaPlayer? = null
    private var audioPlayer: MediaPlayer? = null
    private var videoSurface: Surface? = null

    override fun onCreate() {
        super.onCreate()
        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopEverything()
                return START_NOT_STICKY
            }

            ACTION_START -> {
                // El permiso de notificación debe gestionarse
                // correctamente en Android 13 y posteriores.
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification()
                )

                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(
                        this,
                        "Autoriza mostrar sobre otras aplicaciones",
                        Toast.LENGTH_LONG
                    ).show()
                    stopEverything()
                    return START_NOT_STICKY
                }

                showOverlay()
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios multimedia",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reproducción de alarmas multimedia"
            }

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                Notification.Builder(this)
            }

        return builder
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Recordatorio activo")
            .setContentText("La animación y el sonido están activos")
            .setOngoing(true)
            .build()
    }

    private fun showOverlay() {
        if (overlayView != null) return

        val videoUri = MediaSettings.videoUri(this)
        val audioUri = MediaSettings.audioUri(this)

        if (videoUri == null && audioUri == null) {
            stopEverything()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.TRANSPARENT)
            gravity = Gravity.CENTER
        }

        val video = TextureView(this).apply {
            surfaceTextureListener = this@OverlayService
        }
        textureView = video

        root.addView(
            video,
            LinearLayout.LayoutParams(
                dp(280),
                dp(280)
            )
        )

        val closeButton = Button(this).apply {
            text = "Detener alarma ✕"
            setOnClickListener {
                stopEverything()
            }
        }

        root.addView(
            closeButton,
            LinearLayout.LayoutParams(
                dp(220),
                dp(52)
            )
        )

        val params = WindowManager.LayoutParams(
            dp(300),
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        try {
            windowManager.addView(root, params)
            overlayView = root

            if (audioUri != null) {
                playAudio(audioUri)
            }

            // Si el video ya está disponible, el listener
            // iniciará la reproducción al crear la superficie.
        } catch (e: Exception) {
            stopEverything()
        }
    }

    override fun onSurfaceTextureAvailable(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        val uri = MediaSettings.videoUri(this) ?: return

        videoSurface = Surface(surfaceTexture)
        playVideo(uri, videoSurface!!)
    }

    private fun playVideo(uri: Uri, surface: Surface) {
        releaseVideo()

        try {
            videoPlayer = MediaPlayer().apply {
                setDataSource(this@OverlayService, uri)
                setSurface(surface)
                isLooping = true
                // Si hay audio aparte, el video va en silencio para no solaparse.
                if (MediaSettings.hasAudio(this@OverlayService)) {
                    setVolume(0f, 0f)
                }

                setOnPreparedListener { player ->
                    player.start()
                }

                setOnErrorListener { _, _, _ ->
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            releaseVideo()
        }
    }

    private fun playAudio(uri: Uri) {
        releaseAudio()

        try {
            audioPlayer = MediaPlayer().apply {
                setDataSource(this@OverlayService, uri)
                isLooping = true

                setOnPreparedListener { player ->
                    player.start()
                }

                setOnErrorListener { _, _, _ ->
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            releaseAudio()
        }
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) = Unit

    override fun onSurfaceTextureUpdated(
        surface: SurfaceTexture
    ) = Unit

    override fun onSurfaceTextureDestroyed(
        surface: SurfaceTexture
    ): Boolean {
        releaseVideo()
        videoSurface?.release()
        videoSurface = null
        return true
    }

    private fun releaseVideo() {
        videoPlayer?.run {
            try {
                stop()
            } catch (_: Exception) {
                // El reproductor puede no haber iniciado.
            }
            release()
        }
        videoPlayer = null
    }

    private fun releaseAudio() {
        audioPlayer?.run {
            try {
                stop()
            } catch (_: Exception) {
                // El reproductor puede no haber iniciado.
            }
            release()
        }
        audioPlayer = null
    }

    private fun stopEverything() {
        releaseVideo()
        releaseAudio()

        videoSurface?.release()
        videoSurface = null

        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
                // La ventana puede haberse retirado ya.
            }
        }

        overlayView = null
        textureView = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        releaseVideo()
        releaseAudio()
        videoSurface?.release()
        videoSurface = null

        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
                // Ya no existe.
            }
        }

        overlayView = null
        textureView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
