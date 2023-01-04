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

    @Query("SELECT * FROM Mot")
    fun loadEverything(): List<Mot>

    @Query("SELECT * FROM Mot WHERE mot LIKE (:nom) || '%'")
    fun loadPartialName(nom: String): LiveData<List<Mot>>

    @Query("SELECT * FROM Mot WHERE src LIKE (:src) || '%'")
    fun loadPartialSrc(src: String): LiveData<List<Mot>>

    @Query("SELECT * FROM Mot WHERE mot LIKE (:dst) || '%'")
    fun loadPartialDst(dst: String): LiveData<List<Mot>>

    @Delete
    fun deleteMots(vararg mots: Mot): Int

    @Update
    fun updateMots(vararg mots: Mot): Int

    @Query("SELECT * FROM mot WHERE maitrise <= :maitnb " +
            "OR (maitrise > :maitnb AND lastVu <= :date) ORDER BY RANDOM() LIMIT :nb")
    fun loadTenWords(nb: Int, date: Long, maitnb: Int): List<Mot>

    @Query("SELECT * FROM mot WHERE src = :src AND dst = :dst AND (maitrise <= :maitnb OR " +
            "(maitrise > :maitnb AND lastVu <= :date)) ORDER BY RANDOM() LIMIT :nb")
    fun loadTenWordsBis(nb: Int, date: Long, maitnb: Int, src: String, dst: String): List<Mot>


    @Query("SELECT * FROM mot WHERE mot = :id")
    fun getMotbyID(id: String): Mot
}