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
 * Clase AppDatabase
 * Base de datos Room de la aplicación.
 * Singleton que garantiza una única instancia durante el ciclo de vida del proceso.
 * Expone los DAOs necesarios para operar sobre las entidades persistidas
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
@Database(entities = {Ecuacion.class, ProgresoLeccion.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /** Instancia única de la base de datos; volatile garantiza visibilidad entre hilos */
    private static volatile AppDatabase INSTANCE;

    // ─────────────────────────────────────────────────────────────────────────
    //  DAOs abstractos
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método ecuacionDao.
     * Devuelve el DAO para operaciones sobre la entidad Ecuacion.
     *
     * @return implementación generada por Room de EcuacionDao.
     */
    public abstract EcuacionDao ecuacionDao();

    /**
     * Método progresoLeccionDao.
     * Devuelve el DAO para operaciones sobre la entidad ProgresoLeccion.
     *
     * @return implementación generada por Room de ProgresoLeccionDao.
     */
    public abstract ProgresoLeccionDao progresoLeccionDao();

    // ─────────────────────────────────────────────────────────────────────────
    //  Migración de esquema
    // ─────────────────────────────────────────────────────────────────────────

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        /**
         * Método migrate.
         * Migración de la versión 1 a la versión 2.
         *
         * Añade la tabla ProgresoLeccion con llave primaria compuesta
         * (username, leccionId) para registrar el progreso por
         * usuario en cada lección.
         */
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

    // ─────────────────────────────────────────────────────────────────────────
    //  Singleton
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método getInstance.
     * Devuelve la instancia singleton de la base de datos, creándola si aún no existe.
     *
     * Utiliza el patrón double-checked locking para ser seguro en
     * entornos multihilo sin incurrir en el coste de sincronización en cada llamada.
     *
     * El archivo de base de datos se llama "algeplus_db" y se almacena
     * en el directorio privado de la aplicación.
     *
     * @param context contexto de la aplicación (Application o Activity);
     *                internamente se usa getApplicationContext() para evitar
     *                fugas de memoria.
     * @return instancia única de AppDatabase.
     */
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
