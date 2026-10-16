package com.example.notificaciones_app
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone



class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var btnElegirVideo: Button
    private lateinit var btnElegirAudio: Button
    private lateinit var btnQuitarVideo: Button
    private lateinit var btnQuitarAudio: Button
    private lateinit var btnElegirFecha: Button
    private lateinit var tvFechaSeleccionada: TextView

    private var anioSeleccionado: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var mesSeleccionado: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var diaSeleccionado: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private val notificationPermissionLauncher =
    registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* si lo niega, la app sigue; el overlay se pide aparte */ }

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
        btnElegirFecha = findViewById(R.id.btnElegirFecha)
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada)

        actualizarTextoFecha()

        btnElegirFecha.setOnClickListener {
            mostrarSelectorFecha()
        }

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
                return@setOnClickListener
            }

            val tiempoEnMilisegundos = calcularMilisegundos(
                anioSeleccionado,
                mesSeleccionado,
                diaSeleccionado,
                hora,
                minuto
            )

            if (tiempoEnMilisegundos <= System.currentTimeMillis()) {
                Toast.makeText(
                    this,
                    "Elige una fecha y hora futuras",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 4. Guardar en la base de datos usando una corrutina (hilo secundario)
            lifecycleScope.launch {
                val nuevoRecordatorio = Recordatorio(
                    mensaje = mensaje,
                    anio = anioSeleccionado,
                    mes = mesSeleccionado,
                    dia = diaSeleccionado,
                    hora = hora,
                    minuto = minuto
                )

                // Insertar mediante el DAO (Room nos devuelve el ID generado)
                val idGenerado = database.recordatorioDao().insertar(nuevoRecordatorio)

                // Programar la alarma en el sistema operativo
                programarAlarmaSistema(idGenerado, tiempoEnMilisegundos, mensaje)

                // Mostrar mensaje de éxito en la interfaz principal
                runOnUiThread {
                    val fechaTexto = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%04d",
                        diaSeleccionado,
                        mesSeleccionado + 1,
                        anioSeleccionado
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "¡Guardado! '$mensaje' el $fechaTexto a las ${String.format("%02d:%02d", hora, minuto)}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Limpiar el campo de texto
                    etMensaje.text.clear()
                }
            }
            }
    solicitarPermisosSiFaltan()
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

            override fun onResume() {
            super.onResume()
            // Por si el usuario vuelve de Ajustes tras conceder el overlay
            // no volvemos a abrir Ajustes si ya está concedido.
        }

        private fun solicitarPermisosSiFaltan() {
            // Notificaciones (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!granted) {
                    notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }

            // Ventana flotante (solo si NO está activo)
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(
                    this,
                    "Activa el permiso de ventana flotante para las alarmas",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
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

    private fun mostrarSelectorFecha() {
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Selecciona la fecha")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    datePicker.addOnPositiveButtonClickListener { utcMillis ->
        val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = utcMillis
        }
        anioSeleccionado = calendario.get(Calendar.YEAR)
        mesSeleccionado = calendario.get(Calendar.MONTH)
        diaSeleccionado = calendario.get(Calendar.DAY_OF_MONTH)
        actualizarTextoFecha()
    }

    datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun actualizarTextoFecha() {
        val fechaTexto = String.format(
            Locale.getDefault(),
            "%02d/%02d/%04d",
            diaSeleccionado,
            mesSeleccionado + 1,
            anioSeleccionado
        )
        tvFechaSeleccionada.text = "Fecha: $fechaTexto"
        btnElegirFecha.text = "Cambiar fecha"
    }

    // Función auxiliar para calcular el tiempo en milisegundos de fecha + hora
    private fun calcularMilisegundos(
        anio: Int,
        mes: Int,
        dia: Int,
        hora: Int,
        minuto: Int
    ): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, anio)
            set(Calendar.MONTH, mes)
            set(Calendar.DAY_OF_MONTH, dia)
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
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