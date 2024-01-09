package com.example.accesodatos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.accesodatos.databinding.ActivityCrearJuegoBinding
import com.example.accesodatos.databinding.ActivityMainBinding
import com.example.accesodatos.databinding.ActivityVerJuegosBinding

class VerJuegosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerJuegosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerJuegosBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}