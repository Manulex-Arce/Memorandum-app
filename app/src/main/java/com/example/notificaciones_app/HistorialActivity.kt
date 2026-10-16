package com.example.notificaciones_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        supportActionBar?.title = "Recordatorios Pendientes"
        database = AppDatabase.obtenerBaseDatos(this)

        recyclerView = findViewById(R.id.recyclerViewRecordatorios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarRecordatorios()
    }

    private fun cargarRecordatorios() {
        lifecycleScope.launch {
            // Consultar todos los registros de la base de datos Room
            val listaRecordatorios = database.recordatorioDao().obtenerTodos()

            // Actualizar la interfaz en el hilo principal
            runOnUiThread {
                val adapter = RecordatorioAdapter(listaRecordatorios)
                recyclerView.adapter = adapter
            }
        }
    }
}