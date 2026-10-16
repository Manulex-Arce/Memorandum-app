
package com.example.notificaciones_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "¡Alarma recibida con éxito!")

        val titulo = intent.getStringExtra("EXTRA_TITULO")
            ?: "¡Es hora de tu recordatorio!"

        val channelId = "canal_recordatorios"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificaciones en Android 8.0 o superior.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Video",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alertas de recordatorios"
            }

            notificationManager.createNotificationChannel(channel)
        }

        // Mostrar la notificación habitual.
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Recordatorio Programado")
            .setContentText(titulo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )

        // Intentar iniciar la ventana flotante si se concedió el permiso.
        val tienePermisoOverlay =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    Settings.canDrawOverlays(context)

        if (tienePermisoOverlay) {
            try {
                val overlayIntent = Intent(
                    context,
                    OverlayService::class.java
                ).apply {
                    action = OverlayService.ACTION_START
                    putExtra("EXTRA_TITULO", titulo)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(overlayIntent)
                } else {
                    context.startService(overlayIntent)
                }

                Log.d("AlarmReceiver", "Inicio del servicio flotante solicitado")
            } catch (e: Exception) {
                Log.e(
                    "AlarmReceiver",
                    "No se pudo iniciar la ventana flotante",
                    e
                )
            }
        } else {
            Log.w(
                "AlarmReceiver",
                "Falta el permiso para mostrar ventanas flotantes"
            )
        }
    }
}
