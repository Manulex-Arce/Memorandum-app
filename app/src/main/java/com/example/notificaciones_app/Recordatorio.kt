package com.example.notificaciones_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordatorios")
data class Recordatorio(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mensaje: String,
    val anio: Int,
    val mes: Int,
    val dia: Int,
    val hora: Int,
    val minuto: Int
)