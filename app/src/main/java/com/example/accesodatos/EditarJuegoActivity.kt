package com.example.accesodatos

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
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

    private val opcionesGeneroJuegos = arrayOf(
        "Acción",
        "Aventura",
        "Disparos en primera persona",
        "Disparos en tercera persona",
        "Sigilo",
        "Deportes",
        "Estrategia en tiempo real",
        "Estrategia por turnos",
        "Rol",
        "Simulación de vida",
        "Simulación de construcción",
        "Simulación de negocios",
        "Simulación de vuelo",
        "Simulación de conducción",
        "Simulación deportiva",
        "Carreras",
        "Lucha",
        "Arcade",
        "Plataforma",
        "Puzles",
        "Horror",
        "Survival Horror",
        "Mundo abierto",
        "Multijugador masivo en línea",
        "Música y ritmo",
        "Educativo",
        "Familiar",
    )
    private val edadesRecomendadasJuegos = arrayOf(
        "Para todos (E)",
        "Mayores de 10 años (E10+)",
        "Mayores de 13 años (T)",
        "Mayores de 17 años (M)",
        "Adultos (AO)",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarJuegoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val thisActivity = this
        job = Job()

        pojoJuego = intent.getParcelableExtra<Juego>("juego")!!

        //Voy solo a por el nombre en principio
        binding.tietNombreJuego.setText(pojoJuego.nombre)
        binding.tietNombreEstudio.setText(pojoJuego.estudio)
        binding.tietFechaLanzamiento.setText(pojoJuego.fechaLanzamiento)
        //Para los spinner tengo que obtener la posicion que ocupa
        //Posicion que ocupa la edad recomendada para jugar al juego
        var posicionEdadRecomendada = edadesRecomendadasJuegos.indexOf(pojoJuego.edad)
        var posicionGeneroDelJuego = opcionesGeneroJuegos.indexOf(pojoJuego.genero)
        //Ahora vamos con los spinner
        //Configurar el spinner
        //Obtenemos el spinner edades
        val spinnerEdades = findViewById<Spinner>(R.id.spinnerEdad)
        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, edadesRecomendadasJuegos)
        // Aplicar el adaptador al Spinner
        binding.spinnerEdad.adapter = adapter
        // Recuperar la posición guardada y establecerla en el Spinner
        binding.spinnerEdad.setSelection(posicionEdadRecomendada)

        //Obtenemos el spinner generos de los juegos
        val spinnerGeneros = findViewById<Spinner>(R.id.spinnerGenero)
        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapterGenero = ArrayAdapter(this, android.R.layout.simple_list_item_1, opcionesGeneroJuegos)
        // Aplicar el adaptador al Spinner
        binding.spinnerGenero.adapter = adapterGenero
        // Recuperar la posición guardada y establecerla en el Spinner
        binding.spinnerGenero.setSelection(posicionGeneroDelJuego)

        Glide.with(applicationContext)
            .load(pojoJuego.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(binding.ivImagenJuego)

        dbRef = FirebaseDatabase.getInstance().getReference()
        stRef = FirebaseStorage.getInstance().getReference()

        listaJuegos = Utilidades.obtenerListaJuegos(dbRef)

        binding.btnIntroducirJuego.setOnClickListener {

        }
    }
}