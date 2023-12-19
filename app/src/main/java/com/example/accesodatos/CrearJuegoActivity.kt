package com.example.accesodatos

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.accesodatos.databinding.ActivityCrearJuegoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job

class CrearJuegoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearJuegoBinding

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Guarda la URI de la imagen seleccionada en la variable
                uriImagenSeleccionada = it

                // Puedes mostrar la imagen seleccionada en tu interfaz de usuario si es necesario
                // imageView.setImageURI(it)
            }
        }
    private var uriImagenSeleccionada: Uri? = null
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("juegos")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("imagenes")




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


        //Volver atras boton
        binding.ivBack.setOnClickListener {
            //Pasamos a la siguiente actividad
            val intent = Intent(this@CrearJuegoActivity, MainActivity::class.java)
            startActivity(intent)
        }

        //Configurar el spinner de genero de juego
        //Obtenemos el spinner
        val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
        //Creamos un array con el que vamos a inflar al spinner

        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapterGenero = ArrayAdapter(this, android.R.layout.simple_list_item_1, opcionesGeneroJuegos)
        // Aplicar el adaptador al Spinner
        spinnerGenero.adapter = adapterGenero

        // Configurar un listener para manejar la selección de elementos en el Spinner
        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Obtener la opción seleccionada
                val selectedItem = opcionesGeneroJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaGenero = selectedItem

                // Mostrar la opción seleccionada
                Toast.makeText(this@CrearJuegoActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }


        //Configurar el spinner de edades
        //Obtenemos el spinner
        val spinnerEdad = findViewById<Spinner>(R.id.spinnerEdad)
        //Creamos un array con el que vamos a inflar al spinner

        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapterEdad = ArrayAdapter(this, android.R.layout.simple_list_item_1, edadesRecomendadasJuegos)
        // Aplicar el adaptador al Spinner
        spinnerEdad.adapter = adapterEdad

        // Configurar un listener para manejar la selección de elementos en el Spinner
        spinnerEdad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Obtener la opción seleccionada
                val selectedItem = edadesRecomendadasJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaEdad = selectedItem

                // Mostrar la opción seleccionada
                Toast.makeText(this@CrearJuegoActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }


        //Cuando le da click al boton de guardar juego
        binding.btnIntroducirJuego.setOnClickListener {
            guardarJuegoEnFirebase()
        }
        binding.ivImagenJuego.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }
    }
    // Función para guardar un juego en Firebase
    private fun guardarJuegoEnFirebase() {
        // Verifica si se ha seleccionado una imagen
        if (uriImagenSeleccionada != null) {
            // Genera un nombre único para la imagen (puedes personalizar según tus necesidades)
            val nombreImagen = "imagen_${System.currentTimeMillis()}.jpg"

            // Referencia al lugar en Firebase Storage donde se almacenará la imagen
            val referenciaImagen = storageReference.child(nombreImagen)

            // Sube la imagen al Storage
            referenciaImagen.putFile(uriImagenSeleccionada!!)
                .addOnSuccessListener {
                    // Aquí puedes manejar el éxito de la carga, por ejemplo, obtener la URL de la imagen
                    referenciaImagen.downloadUrl.addOnSuccessListener { url ->
                        // URL de la imagen en Firebase Storage
                        val urlImagenFirebase = url.toString()

                        // Crea un nuevo objeto Juego con la URL de la imagen y otros detalles
                        val nuevoJuego = Juego(
                            imagen = urlImagenFirebase,
                            nombre = "Nombre del juego", // Reemplaza con el nombre real del juego
                            estudio = "Estudio del juego", // Reemplaza con el estudio real del juego
                            fechaLanzamiento = "Fecha de lanzamiento", // Reemplaza con la fecha real del lanzamiento
                            genero = opcionSeleccionadaGenero,
                            edad = opcionSeleccionadaEdad
                        )

                        // Genera una clave única para el juego en la base de datos
                        val claveJuego = databaseReference.push().key

                        // Guarda el juego en la base de datos usando la clave generada
                        claveJuego?.let { clave ->
                            databaseReference.child(clave).setValue(nuevoJuego)
                        }

                        // Realiza otras acciones después de agregar el juego si es necesario
                        Toast.makeText(this@CrearJuegoActivity, "Juego creado con éxito", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CrearJuegoActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener { e ->
                    // Maneja los errores de carga de la imagen
                    Toast.makeText(this@CrearJuegoActivity, "Error al cargar la imagen: $e", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Maneja el caso en el que no se ha seleccionado ninguna imagen
            Toast.makeText(this@CrearJuegoActivity, "Selecciona una imagen para el juego", Toast.LENGTH_SHORT).show()
        }
    }
}