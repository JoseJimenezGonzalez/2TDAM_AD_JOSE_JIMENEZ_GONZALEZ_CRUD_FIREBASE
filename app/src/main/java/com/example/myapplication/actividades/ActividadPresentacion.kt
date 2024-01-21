package com.example.myapplication.actividades

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityActividadPresentacionBinding


class ActividadPresentacion : AppCompatActivity() {

    private lateinit var binding: ActivityActividadPresentacionBinding

    private var puntos = 0
    private val handler = Handler(Looper.getMainLooper())
    private val actualizarTextoRunnable: Runnable = object : Runnable {
        override fun run() {
            // Actualizar el texto con "Cargando" y puntos
            val textoCargando = "Cargando" + ".".repeat(puntos)
            binding.tvCargando.text = textoCargando

            // Incrementar los puntos y programar la próxima actualización después de 1 segundo
            puntos++
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityActividadPresentacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciarUI()
    }

    private fun iniciarUI() {
        mostrarAnimacionBienvenida()
        // Iniciar la actualización del texto
        handler.post(actualizarTextoRunnable)
    }

    private fun mostrarAnimacionBienvenida(){
        object : CountDownTimer(3000, 1000){
            override fun onTick(p0: Long) {
                //TODO("Not yet implemented")
            }

            override fun onFinish() {
                iniciarNavegacion()
            }

        }.start()
    }

    private fun iniciarNavegacion() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        // Detener el handler al destruir la actividad para evitar fugas de memoria
        handler.removeCallbacks(actualizarTextoRunnable)
        super.onDestroy()
    }
}