package com.example.accesodatos

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Utilidades {
    companion object{


        fun existeJuego(clubs : List<Juego>, nombre:String):Boolean{
            return clubs.any{ it.nombre!!.lowercase()==nombre.lowercase()}
        }


        fun obtenerListaJuegos(dbRef: DatabaseReference):MutableList<Juego>{
            var lista = mutableListOf<Juego>()

            dbRef.child("PS2")
                .child("juegos")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo : DataSnapshot ->
                            val pojoJuego = hijo.getValue(Juego::class.java)
                            lista.add(pojoJuego!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun escribirJuego(dbRef: DatabaseReference, id:String, nombreJuego:String, nombreEstudioDesarrollador:String, fechaLanzamiento: String, edadRecomendada: String, genero: String, urlFirebase:String, puntuacionRatingBar: Double)=
            dbRef.child("PS2").child("juegos").child(id).setValue(Juego(
                id,
                urlFirebase,
                nombreJuego,
                nombreEstudioDesarrollador,
                fechaLanzamiento,
                genero,
                edadRecomendada,
                puntuacionRatingBar
            ))

        suspend fun guardarImagenCover(stoRef: StorageReference, id:String, imagen: Uri):String{
            lateinit var urlCoverFirebase: Uri

            urlCoverFirebase=stoRef.child("PS2").child("covers").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return urlCoverFirebase.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable{
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.ic_ps2_mando)
                .error(R.drawable.error_404)
            return options
        }
    }
}