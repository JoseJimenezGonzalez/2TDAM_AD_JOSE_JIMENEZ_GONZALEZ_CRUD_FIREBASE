package com.example.accesodatos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.accesodatos.databinding.ActivityCrearJuegoBinding
import com.example.accesodatos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotonAgregarJuego()
        configurarBotonVerJuegos()
    }

    private fun configurarBotonVerJuegos() {
        binding.btnVerJuegos.setOnClickListener {
            //Pasamos a la siguiente actividad
            val intent = Intent(this@MainActivity, VerJuegosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarBotonAgregarJuego() {
        binding.btnAddJuego.setOnClickListener {
            //Pasamos a la siguiente actividad
            val intent = Intent(this@MainActivity, CrearJuegoActivity::class.java)
            startActivity(intent)
        }
    }
}