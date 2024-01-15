package com.example.accesodatos

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.accesodatos.databinding.ActivityCrearJuegoBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

//La anotación @AndroidEntryPoint es parte de Dagger Hilt y se utiliza en clases de Android para
//habilitar la inyección de dependencias en esas clases.
@AndroidEntryPoint
class CrearJuegoActivity: AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityCrearJuegoBinding

    private var urlImagen: Uri? = null
    private lateinit var cover: ImageView
    private lateinit var listaJuegos: MutableList<Juego>


    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stRef: StorageReference

    @Inject
    lateinit var job: Job

    private var esFechaValida = false
    private var fechaLanzamientoFormateada = ""


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

        binding = ActivityCrearJuegoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cover = binding.ivImagenJuego

        listaJuegos = Utilidades.obtenerListaJuegos(dbRef)

        configurarSpinnerGenero()
        configurarSpinnerEdad()
        configurarDatePicker()
        configurarBotonBack()
        configurarBotonIntroducirJuego()
        configurarBotonImageViewAccesoGaleria()
    }

    private fun configurarBotonImageViewAccesoGaleria() {
        //Cuando le da click en la imagen para guardar imagen del juego
        binding.ivImagenJuego.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    private fun configurarBotonIntroducirJuego() {
        //Cuando le da click al boton de guardar juego
        binding.btnIntroducirJuego.setOnClickListener {
            //Varibles que estan el los edit text
            val nombreJuego = binding.tietNombreJuego.text.toString()
            val nombreEstudioDesarrollo = binding.tietNombreEstudio.text.toString()
            val puntuacionRatingBar = binding.rbPuntuacion.rating.toString().toDouble()
            val fechaCreacionEnBaseDeDatos = Date()
            val fechaFormateadaCreacionJuegoBaseDeDatos = obtenerFechaLanzamientoFormateada(fechaCreacionEnBaseDeDatos)
            if (nombreJuego.trim().isEmpty() || nombreEstudioDesarrollo.trim()
                    .isEmpty() || fechaLanzamientoFormateada.isEmpty() || !esFechaValida
            ) {
                Toast.makeText(this, "Faltan datos en el formulario", Toast.LENGTH_SHORT).show()
            } else if (urlImagen == null) {
                Toast.makeText(this, "Falta seleccionar imagen", Toast.LENGTH_SHORT).show()
            } else if (Utilidades.existeJuego(listaJuegos, nombreJuego.trim())) {
                Toast.makeText(this, "Ese juego ya existe", Toast.LENGTH_SHORT).show()
            } else {
                val idGenerado: String? = dbRef.child("PS2").child("juegos").push().key
                //GlobalScope(Dispatchers.IO)
                launch {
                    val urlCoverFirebase =
                        Utilidades.guardarImagenCover(stRef, idGenerado!!, urlImagen!!)

                    Utilidades.escribirJuego(
                        dbRef, idGenerado,
                        nombreJuego.trim(),
                        nombreEstudioDesarrollo.trim(),
                        fechaLanzamientoFormateada,
                        opcionSeleccionadaEdad,
                        opcionSeleccionadaGenero,
                        urlCoverFirebase,
                        puntuacionRatingBar,
                        fechaFormateadaCreacionJuegoBaseDeDatos
                    )
                    Utilidades.tostadaCorrutina(
                        this@CrearJuegoActivity,
                        applicationContext,
                        "Juego creado con exito"
                    )
                }
            }
        }
    }

    private fun configurarBotonBack() {
        //Volver atras boton
        binding.ivBack.setOnClickListener {
            //Pasamos a la siguiente actividad
            val intent = Intent(this@CrearJuegoActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarDatePicker() {
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
    }

    private fun configurarSpinnerEdad() {
        //Configurar el spinner de edades
        //Obtenemos el spinner
        val spinnerEdad = findViewById<Spinner>(R.id.spinnerEdad)
        //Creamos un array con el que vamos a inflar al spinner

        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapterEdad =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, edadesRecomendadasJuegos)
        // Aplicar el adaptador al Spinner
        spinnerEdad.adapter = adapterEdad

        // Configurar un listener para manejar la selección de elementos en el Spinner
        spinnerEdad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtener la opción seleccionada
                val selectedItem = edadesRecomendadasJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaEdad = selectedItem

                // Mostrar la opción seleccionada
                Toast.makeText(
                    this@CrearJuegoActivity,
                    "Seleccionaste: $selectedItem",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }
    }

    private fun configurarSpinnerGenero() {
        //Configurar el spinner de genero de juego
        //Obtenemos el spinner
        val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
        //Creamos un array con el que vamos a inflar al spinner

        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapterGenero =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, opcionesGeneroJuegos)
        // Aplicar el adaptador al Spinner
        spinnerGenero.adapter = adapterGenero

        // Configurar un listener para manejar la selección de elementos en el Spinner
        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtener la opción seleccionada
                val selectedItem = opcionesGeneroJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaGenero = selectedItem

                // Mostrar la opción seleccionada
                Toast.makeText(
                    this@CrearJuegoActivity,
                    "Seleccionaste: $selectedItem",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
    private fun obtenerFechaLanzamientoFormateada(fechaLanzamiento: Date): String {
        return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(fechaLanzamiento)
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            urlImagen = uri
            cover.setImageURI(uri)
        }
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}