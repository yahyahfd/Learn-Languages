package fr.uparis.zhou.mobiles_projet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mot (
    @PrimaryKey var mot: String,
    var src: String,
    var dst: String,
    var url: String,
    var maitrise: Int?= 0,
    var lastVu: Long?= System.currentTimeMillis(), //la date de la dernière fois vu
    var used: Boolean?=false
    )