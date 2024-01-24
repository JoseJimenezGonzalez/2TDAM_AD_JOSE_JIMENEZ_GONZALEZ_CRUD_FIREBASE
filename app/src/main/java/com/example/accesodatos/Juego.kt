package com.example.accesodatos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Juego(
    var id : String? = null,
    val imagen: String? = null,
    val nombre: String? = null,
    val estudio: String? = null,
    val fechaLanzamiento: String? = null,
    val genero: String? = null,
    val edad: String? = null,
    var ratingBar: Double? = null,
    var fechaCreacionBaseDatos: String? = null,
    var estado_noti:Int? = null,
    var user_notificacion:String? = null,
): Parcelable

