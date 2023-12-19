package com.example.accesodatos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.accesodatos.databinding.ActivityCrearJuegoBinding

class CrearJuegoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearJuegoBinding
    private var opcionSeleccionadaGenero: String = ""
    private var opcionSeleccionadaEdad: String = ""
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
        val opcionesGeneroJuegos = arrayOf(
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


        //Configurar el spinner de genero de juego
        //Obtenemos el spinner
        val spinnerEdad = findViewById<Spinner>(R.id.spinnerGenero)
        //Creamos un array con el que vamos a inflar al spinner
        val edadesRecomendadasJuegos = arrayOf(
            "Para todos (E)",
            "Mayores de 10 años (E10+)",
            "Mayores de 13 años (T)",
            "Mayores de 17 años (M)",
            "Adultos (AO)",
        )
        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapterEdad = ArrayAdapter(this, android.R.layout.simple_list_item_1, opcionesGeneroJuegos)
        // Aplicar el adaptador al Spinner
        spinnerEdad.adapter = adapterEdad

        // Configurar un listener para manejar la selección de elementos en el Spinner
        spinnerEdad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Obtener la opción seleccionada
                val selectedItem = opcionesGeneroJuegos[position]

                // Asignar la opción seleccionada
                opcionSeleccionadaEdad = selectedItem

                // Mostrar la opción seleccionada
                Toast.makeText(this@CrearJuegoActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }
    }
}