package com.example.accesodatos

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.accesodatos.databinding.ActivityEditarJuegoBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class EditarJuegoActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityEditarJuegoBinding

    private var urlCover: Uri? = null
    private lateinit var cover: ImageView
    private lateinit var dbRef: DatabaseReference
    private lateinit var stRef: StorageReference
    private  lateinit var  pojoJuego:Juego
    private lateinit var listaJuegos: MutableList<Juego>

    private lateinit var job: Job

    private var esFechaValida = true
    private var fechaLanzamientoFormateada = ""
    private var fechaCreacionBasedatos = ""

    private var opcionSeleccionadaGenero: String = ""
    private var opcionSeleccionadaEdad: String = ""

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

        pojoJuego = intent.parcelable("juego")!!

        opcionSeleccionadaGenero = pojoJuego.genero!!
        opcionSeleccionadaEdad = pojoJuego.edad!!

        //Voy solo a por el nombre en principio
        binding.tietNombreJuego.setText(pojoJuego.nombre)
        binding.tietNombreEstudio.setText(pojoJuego.estudio)
        binding.tietFechaLanzamiento.setText(pojoJuego.fechaLanzamiento)
        fechaLanzamientoFormateada = pojoJuego.fechaLanzamiento ?: ""
        binding.rbPuntuacion.rating = pojoJuego.ratingBar?.toFloat() ?: 0.0f
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


        fechaCreacionBasedatos = pojoJuego.fechaCreacionBaseDatos!!

        //Guardamos la eleccion del spinner edad
        spinnerEdades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                // Obtener la opción seleccionada
                val selectedItem = edadesRecomendadasJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaEdad = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }

        //Guardamos la eleccion del spinner genero
        spinnerGeneros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                // Obtener la opción seleccionada
                val selectedItem = opcionesGeneroJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaGenero = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }


        //Fecha lanzamiento
        binding.tietFechaLanzamiento.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val selectedDate = Date(selectedDateInMillis)
                val currentDate = Date()

                if(currentDate.after(selectedDate)){
                    esFechaValida = true
                    fechaLanzamientoFormateada = obtenerFechaLanzamientoFormateada(selectedDate)
                    binding.tietFechaLanzamiento.setText(fechaLanzamientoFormateada)
                }else{
                    Toast.makeText(this, "Fecha de lanzamiento erronea", Toast.LENGTH_SHORT).show()
                    esFechaValida = false
                }
            }

            picker.show(supportFragmentManager, "jeje")
        }

        Glide.with(applicationContext)
            .load(pojoJuego.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(binding.ivImagenJuego)

        dbRef = FirebaseDatabase.getInstance().reference
        stRef = FirebaseStorage.getInstance().reference

        listaJuegos = Utilidades.obtenerListaJuegos(dbRef)

        binding.btnIntroducirJuego.setOnClickListener {
            //Varibles que estan el los edit text
            var nombreJuego = binding.tietNombreJuego.text.toString()
            var nombreEstudioDesarrollo = binding.tietNombreEstudio.text.toString()
            var puntuacionRatingBar = binding.rbPuntuacion.rating.toString().toDouble()
            if (nombreJuego.trim().isEmpty() || nombreEstudioDesarrollo.trim()
                    .isEmpty() || fechaLanzamientoFormateada.isEmpty() || !esFechaValida
            ) {
                Toast.makeText(this, "Faltan datos en el formulario", Toast.LENGTH_SHORT).show()
            } else if (Utilidades.existeJuego(listaJuegos, nombreJuego.trim()) && nombreJuego != pojoJuego.nombre) {
                Toast.makeText(this, "Ese juego ya existe", Toast.LENGTH_SHORT).show()
            } else {
                var urlCoverFirebase = String()
                var idJuego = pojoJuego.id
                //Chivato
                Log.d("opcionEdad", opcionSeleccionadaEdad)
                Log.d("opcionGenero", opcionSeleccionadaGenero)
                //GlobalScope(Dispatchers.IO)
                launch {
                    if(urlCover == null){
                        urlCoverFirebase = pojoJuego.imagen!!
                    }else{
                        val urlCoverFirebase = Utilidades.guardarImagenCover(stRef, pojoJuego.id!!, urlCover!!)
                    }
                    Utilidades.modificarJuego(
                        dbRef,
                        idJuego!!,
                        nombreJuego.trim(),
                        nombreEstudioDesarrollo.trim(),
                        fechaLanzamientoFormateada,
                        opcionSeleccionadaEdad,
                        opcionSeleccionadaGenero,
                        urlCoverFirebase,
                        puntuacionRatingBar,
                        fechaCreacionBasedatos
                    )
                    Utilidades.tostadaCorrutina(
                        thisActivity,
                        applicationContext,
                        "Juego modificado con exito"
                    )
                }
            }
        }
        //Cuando le da click en la imagen para guardar imagen del juego
        binding.ivImagenJuego.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
        binding.ivBack.setOnClickListener {
            val intent = Intent(this, VerJuegosActivity::class.java)
            startActivity(intent)
        }
    }
    //La extension de funcion parcelable utilizara el metodo adecuado segun la version de la API
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            urlCover = uri
            cover.setImageURI(uri)
        }
    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
    private fun obtenerFechaLanzamientoFormateada(fechaNacimiento: Date): String {
        val formato = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return formato.format(fechaNacimiento)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}