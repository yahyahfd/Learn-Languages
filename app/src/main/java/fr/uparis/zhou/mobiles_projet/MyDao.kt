package fr.uparis.zhou.mobiles_projet

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg Mot: Mot): List<Long>

    @Query("SELECT * FROM Mot")
    fun loadAll(): LiveData<List<Mot>>

    @Query("SELECT * FROM Mot WHERE mot LIKE (:nom) || '%'")
    fun loadPartialName(nom : String): LiveData<List<Mot>>

    @Delete
    fun deleteMots(vararg mots: Mot): Int

    @Update
    fun updateMots(vararg mots: Mot): Int
}