package com.example.notificaciones_app;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\bg\u0018\u00002\u00020\u0001J\u001a\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@b\u0002\b\u0007\u00a2\u0006\u0002\u0010\u0006J\"\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tH\u00a7@b\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u00a2\u0006\u0002\u0010\n\u00ca\u0001\u0002\b\u000f\u00a8\u0006\u000e\u00c0\u0006\u0003"}, d2 = {"Lcom/example/notificaciones_app/RecordatorioDao;", "", "insertar", "", "recordatorio", "Lcom/example/notificaciones_app/Recordatorio;", "(Lcom/example/notificaciones_app/Recordatorio;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Landroidx/room/Insert;", "obtenerTodos", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Landroidx/room/Query;", "value", "SELECT * FROM recordatorios", "notificaciones-app:app_debug", "Landroidx/room/Dao;"})
@androidx.room.Dao()
public abstract interface RecordatorioDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertar(@org.jetbrains.annotations.NotNull()
    com.example.notificaciones_app.Recordatorio recordatorio, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM recordatorios")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object obtenerTodos(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.notificaciones_app.Recordatorio>> $completion);
}