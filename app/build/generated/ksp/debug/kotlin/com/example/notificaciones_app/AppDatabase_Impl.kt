package com.example.notificaciones_app

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _recordatorioDao: Lazy<RecordatorioDao> = lazy {
    RecordatorioDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "56ef4aae0d757d48fbb77deafa977679", "06b991f0c87c7860f1a2e5535847f213") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `recordatorios` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mensaje` TEXT NOT NULL, `hora` INTEGER NOT NULL, `minuto` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '56ef4aae0d757d48fbb77deafa977679')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `recordatorios`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsRecordatorios: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRecordatorios.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecordatorios.put("mensaje", TableInfo.Column("mensaje", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecordatorios.put("hora", TableInfo.Column("hora", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecordatorios.put("minuto", TableInfo.Column("minuto", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRecordatorios: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRecordatorios: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRecordatorios: TableInfo = TableInfo("recordatorios", _columnsRecordatorios, _foreignKeysRecordatorios, _indicesRecordatorios)
        val _existingRecordatorios: TableInfo = read(connection, "recordatorios")
        if (!_infoRecordatorios.equals(_existingRecordatorios)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |recordatorios(com.example.notificaciones_app.Recordatorio).
              | Expected:
              |""".trimMargin() + _infoRecordatorios + """
              |
              | Found:
              |""".trimMargin() + _existingRecordatorios)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "recordatorios")
  }

  public override fun clearAllTables() {
    super.performClear(false, "recordatorios")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(RecordatorioDao::class, RecordatorioDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun recordatorioDao(): RecordatorioDao = _recordatorioDao.value
}
