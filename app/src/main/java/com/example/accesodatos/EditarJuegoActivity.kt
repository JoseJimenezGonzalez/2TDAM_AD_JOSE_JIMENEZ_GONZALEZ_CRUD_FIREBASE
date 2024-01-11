package com.example.accesodatos

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.accesodatos.databinding.ActivityEditarJuegoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job

class EditarJuegoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarJuegoBinding

    private var urlCover: Uri? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var stRef: StorageReference
    private  lateinit var  pojoJuego:Juego
    private lateinit var listaJuegos: MutableList<Juego>

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarJuegoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val thisActivity = this
        job = Job()

        pojoJuego = intent.getParcelableExtra<Juego>("juego")!!

        //Voy solo a por el nombre en principio
        binding.tietNombreJuego.setText(pojoJuego.nombre)

        Glide.with(applicationContext)
            .load(pojoJuego.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(binding.ivImagenJuego)

        dbRef = FirebaseDatabase.getInstance().getReference()
        stRef = FirebaseStorage.getInstance().getReference()

        listaJuegos = Utilidades.obtenerListaJuegos(dbRef)
    }
}