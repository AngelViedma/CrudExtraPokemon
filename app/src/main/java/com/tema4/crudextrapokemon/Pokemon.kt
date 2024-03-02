package com.tema4.crudextrapokemon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pokemon(
    var id: String? = null,
    var nombre : String? = null,
    var tipo: String? = null,
    var poder: Double? = null,
    var salud: Double? = null,
    var imagen: String? = null
): Parcelable
