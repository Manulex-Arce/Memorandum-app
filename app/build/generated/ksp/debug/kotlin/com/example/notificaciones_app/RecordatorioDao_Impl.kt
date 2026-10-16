package com.example.notificaciones_app

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RecordatorioDao_Impl(
  __db: RoomDatabase,
) : RecordatorioDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRecordatorio: EntityInsertAdapter<Recordatorio>
  init {
    this.__db = __db
    this.__insertAdapterOfRecordatorio = object : EntityInsertAdapter<Recordatorio>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `recordatorios` (`id`,`mensaje`,`hora`,`minuto`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Recordatorio) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.mensaje)
        statement.bindLong(3, entity.hora.toLong())
        statement.bindLong(4, entity.minuto.toLong())
      }
    }
  }

  public override suspend fun insertar(recordatorio: Recordatorio): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfRecordatorio.insertAndReturnId(_connection, recordatorio)
    _result
  }

  public override suspend fun obtenerTodos(): List<Recordatorio> {
    val _sql: String = "SELECT * FROM recordatorios ORDER BY id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMensaje: Int = getColumnIndexOrThrow(_stmt, "mensaje")
        val _columnIndexOfHora: Int = getColumnIndexOrThrow(_stmt, "hora")
        val _columnIndexOfMinuto: Int = getColumnIndexOrThrow(_stmt, "minuto")
        val _result: MutableList<Recordatorio> = mutableListOf()
        while (_stmt.step()) {
          val _item: Recordatorio
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpMensaje: String
          _tmpMensaje = _stmt.getText(_columnIndexOfMensaje)
          val _tmpHora: Int
          _tmpHora = _stmt.getLong(_columnIndexOfHora).toInt()
          val _tmpMinuto: Int
          _tmpMinuto = _stmt.getLong(_columnIndexOfMinuto).toInt()
          _item = Recordatorio(_tmpId,_tmpMensaje,_tmpHora,_tmpMinuto)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
