package com.example.notificaciones_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordatorioDao {
    @Insert
    suspend fun insertar(recordatorio: Recordatorio): Long

    // NUEVO: Obtener todos los recordatorios guardados
    @Query("SELECT * FROM recordatorios ORDER BY id DESC")
    suspend fun obtenerTodos(): List<Recordatorio>
}