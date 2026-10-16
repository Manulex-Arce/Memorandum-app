package com.example.notificaciones_app
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar



class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var btnElegirVideo: Button
    private lateinit var btnElegirAudio: Button
    private lateinit var btnQuitarVideo: Button
    private lateinit var btnQuitarAudio: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnElegirVideo = findViewById(R.id.btnElegirVideo)
        btnElegirAudio = findViewById(R.id.btnElegirAudio)
        btnQuitarVideo = findViewById(R.id.btnQuitarVideo)
        btnQuitarAudio = findViewById(R.id.btnQuitarAudio)

        btnElegirVideo.setOnClickListener {
            videoPicker.launch(arrayOf("video/*"))
        }

        btnElegirAudio.setOnClickListener {
            audioPicker.launch(arrayOf("audio/*"))
        }

        btnQuitarVideo.setOnClickListener {
            MediaSettings.clearVideo(this)
            actualizarEstadoMedia()
            Toast.makeText(this, "Video quitado", Toast.LENGTH_SHORT).show()
        }

        btnQuitarAudio.setOnClickListener {
            MediaSettings.clearAudio(this)
            actualizarEstadoMedia()
            Toast.makeText(this, "Audio quitado", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnPermisoOverlay).setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Permiso de ventana flotante concedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        actualizarEstadoMedia()

        // 1. Inicializar la base de datos
        database = AppDatabase.obtenerBaseDatos(this)

        // 2. Vincular las vistas del XML con variables en Kotlin
        val etMensaje = findViewById<EditText>(R.id.etMensaje)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        // Vincular el botón del historial (¡Asegúrate de agregar este botón en tu activity_main.xml!)
        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)

        // Configurar el botón para abrir el historial
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        // Hacer que el TimePicker use formato de 24 horas
        timePicker.setIs24HourView(true)

        // 3. Escuchar cuando se presione el botón "Guardar"
        btnGuardar.setOnClickListener {
            val mensaje = etMensaje.text.toString()
            val hora = timePicker.hour
            val minuto = timePicker.minute

            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Por favor escribe un mensaje", Toast.LENGTH_SHORT).show()
            } else {
                // 4. Guardar en la base de datos usando una corrutina (hilo secundario)
                lifecycleScope.launch {
                    val nuevoRecordatorio = Recordatorio(
                        mensaje = mensaje,
                        hora = hora,
                        minuto = minuto
                    )

                    // Insertar mediante el DAO (Room nos devuelve el ID generado)
                    val idGenerado = database.recordatorioDao().insertar(nuevoRecordatorio)

                    // Calcular los milisegundos exactos para la alarma
                    val tiempoEnMilisegundos = calcularMilisegundos(hora, minuto)

                    // Programar la alarma en el sistema operativo
                    programarAlarmaSistema(idGenerado, tiempoEnMilisegundos, mensaje)

                    // Mostrar mensaje de éxito en la interfaz principal
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "¡Guardado y programado! '$mensaje' a las ${String.format("%02d:%02d", hora, minuto)}",
                            Toast.LENGTH_LONG
                        ).show()

                        // Limpiar el campo de texto
                        etMensaje.text.clear()
                    }
                }
            }
        }
    }


    private val videoPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                try {
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Algunos proveedores no permiten persistir el permiso.
                }

                MediaSettings.saveVideo(this, it)
                actualizarEstadoMedia()
                Toast.makeText(
                    this,
                    "Video seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val audioPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                try {
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Algunos proveedores no permiten persistir el permiso.
                }

                MediaSettings.saveAudio(this, it)
                actualizarEstadoMedia()
                Toast.makeText(
                    this,
                    "Audio seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun actualizarEstadoMedia() {
        val hayVideo = MediaSettings.hasVideo(this)
        val hayAudio = MediaSettings.hasAudio(this)

        btnElegirVideo.text = if (hayVideo) "Video listo" else "Elegir video"
        btnElegirAudio.text = if (hayAudio) "Audio listo" else "Elegir audio"
        btnQuitarVideo.isEnabled = hayVideo
        btnQuitarAudio.isEnabled = hayAudio
    }

    // Función auxiliar para calcular el tiempo en milisegundos de la hora seleccionada
    private fun calcularMilisegundos(hora: Int, minuto: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si la hora elegida ya pasó hoy, programarla para mañana automáticamente
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    // Función para registrar la alarma en el AlarmManager de Android
    private fun programarAlarmaSistema(idRecordatorio: Long, tiempoEnMilisegundos: Long, mensaje: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_TITULO", mensaje)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            idRecordatorio.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tiempoEnMilisegundos,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}