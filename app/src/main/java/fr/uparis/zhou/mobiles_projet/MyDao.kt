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

    //On charge (complète la liste de) 10 mots les moins vu/écartés sauf si lastVu > 1 mois -> nbVu redevient 0
    @Query("SELECT * FROM mot WHERE maitrise <= 2 OR (maitrise > 2 AND lastVu <= :date) ORDER BY RANDOM() LIMIT :nb")
    fun loadTenWords(nb: Int, date: Long): List<Mot>


    //On charge (complète la liste de) 10 mots les moins vu/écartés sauf si lastVu > 1 mois -> nbVu redevient 0
    @Query("SELECT * FROM mot WHERE src = :src AND dst = :dst AND (maitrise <= 2 OR (maitrise > 2 AND lastVu <= :date)) ORDER BY RANDOM() LIMIT :nb")
    fun loadTenWordsBis(nb: Int, date: Long, src: String, dst: String): List<Mot>


    @Query("SELECT * FROM mot WHERE mot = :id")
    fun getMotbyID(id: String): Mot
}