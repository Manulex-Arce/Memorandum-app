package com.example.notificaciones_app;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b'\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&\u00ca\u0001\u001a\b\b\u0012\f\b\t\u0012\b\b\fJ\u0004\b\t0\n\u0012\b\b\u000b\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006\u0007"}, d2 = {"Lcom/example/notificaciones_app/AppDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "recordatorioDao", "Lcom/example/notificaciones_app/RecordatorioDao;", "Companion", "notificaciones-app:app_debug", "Landroidx/room/Database;", "entities", "Lcom/example/notificaciones_app/Recordatorio;", "version"})
@androidx.room.Database(entities = {com.example.notificaciones_app.Recordatorio.class}, version = 1)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.example.notificaciones_app.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.notificaciones_app.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.notificaciones_app.RecordatorioDao recordatorioDao();
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/notificaciones_app/AppDatabase$Companion;", "", "<init>", "()V", "INSTANCE", "Lcom/example/notificaciones_app/AppDatabase;", "obtenerBaseDatos", "context", "Landroid/content/Context;", "notificaciones-app:app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.notificaciones_app.AppDatabase obtenerBaseDatos(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}