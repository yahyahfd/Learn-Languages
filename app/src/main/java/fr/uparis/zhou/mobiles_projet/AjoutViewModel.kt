package fr.uparis.zhou.mobiles_projet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.TimeUnit

class AjoutViewModel(application: Application) : AndroidViewModel(application)  {
    private val dao = (application as DicoApplication).database.myDao()

    private val insertInfo = MutableLiveData(0)
    fun insert(vararg mots: Mot) {
        Thread {
            val l = dao.insert(*mots)
            insertInfo.postValue(l.fold(0) { acc: Int, n: Long -> if (n >= 0) acc + 1 else acc })
            Log.d("insertion", "insert ${insertInfo.value} elements")
        }.start()
    }

    fun loadAllWords() = dao.loadAll()
    fun loadPartialWorld(prefixe: String) = dao.loadPartialName(prefixe)

    private val deleteResult : MutableLiveData<Int> = MutableLiveData()
    fun deleteWorl(p: Mot) {
        Thread{
            val l = dao.deleteMots(p)
            deleteResult.postValue( l )
        }.start()
    }

    private val updateResult : MutableLiveData<Int> = MutableLiveData()
    fun updateWord(m: Mot) {
        Thread{
            val i = dao.updateMots( m )
            updateResult.postValue( i )
        }.start()
    }
}