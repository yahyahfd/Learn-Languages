package fr.uparis.zhou.mobiles_projet

import android.app.Application

class DicoApplication : Application() {
    val database by lazy{
        DicoBD.getDatabase(this)
    }
}