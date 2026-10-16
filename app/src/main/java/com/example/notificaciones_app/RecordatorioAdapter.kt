package com.example.notificaciones_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordatorioAdapter(private val listaRecordatorios: List<Recordatorio>) :
    RecyclerView.Adapter<RecordatorioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMensaje: TextView = view.findViewById(R.id.tvMensajeItem)
        val tvHora: TextView = view.findViewById(R.id.tvHoraItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recordatorio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordatorio = listaRecordatorios[position]
        holder.tvMensaje.text = recordatorio.mensaje

        // Formatear la hora y minuto con ceros a la izquierda (ej: 08:05)
        val horaFormateada = String.format("%02d:%02d", recordatorio.hora, recordatorio.minuto)
        holder.tvHora.text = "Hora programada: $horaFormateada"
    }

    override fun getItemCount() = listaRecordatorios.size
}