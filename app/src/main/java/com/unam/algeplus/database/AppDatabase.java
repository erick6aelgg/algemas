package com.unam.algeplus.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.unam.algeplus.model.Ecuacion;
import com.unam.algeplus.model.ProgresoLeccion;

/**
 * Base de datos Room de la aplicación.
 * Singleton que garantiza una única instancia durante el ciclo de vida del proceso.
 */
@Database(entities = {Ecuacion.class, ProgresoLeccion.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract EcuacionDao ecuacionDao();
    public abstract ProgresoLeccionDao progresoLeccionDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `ProgresoLeccion` (" +
                    "`username` TEXT NOT NULL, " +
                    "`leccionId` INTEGER NOT NULL, " +
                    "`puntaje` INTEGER NOT NULL, " +
                    "`completada` INTEGER NOT NULL, " +
                    "`vecesCompletada` INTEGER NOT NULL, " +
                    "`fechaActualizacion` TEXT NOT NULL, " +
                    "PRIMARY KEY(`username`, `leccionId`))");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "algeplus_db"
                    ).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return INSTANCE;
    }
}
