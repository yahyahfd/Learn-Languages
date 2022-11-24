package fr.uparis.zhou.mobiles_projet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mot (
    @PrimaryKey var mot: String,
    var src: String,
    var dst: String,
    var url: String
    )