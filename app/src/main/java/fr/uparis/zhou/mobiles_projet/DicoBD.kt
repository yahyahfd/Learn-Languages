package fr.uparis.zhou.mobiles_projet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Mot::class], version = 12, exportSchema = false)
abstract class DicoBD : RoomDatabase() {
    abstract fun myDao(): MyDao

    companion object {
        @Volatile
        private var instance: DicoBD? = null
        fun getDatabase(context: Context): DicoBD {
            if (instance != null)
                return instance!!
            val db = Room.databaseBuilder(
                context.applicationContext,
                DicoBD::class.java, "dico"
            )
                .fallbackToDestructiveMigration()
                .build()
            instance = db
            return instance!!
        }
    }
}