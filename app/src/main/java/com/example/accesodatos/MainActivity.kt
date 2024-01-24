package com.example.accesodatos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.accesodatos.databinding.ActivityMainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stRef: StorageReference

    private lateinit var androidId: String
    private lateinit var generador: AtomicInteger

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotonAgregarJuego()
        configurarBotonVerJuegos()

        crearCanalNotificaciones()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        dbRef = FirebaseDatabase.getInstance().reference
        generador = AtomicInteger(0)


        //CONTROLADOR NOTIFICACIONES
        dbRef.child("PS2").child("juegos")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojoJuego = snapshot.getValue(Juego::class.java)
                    if (!pojoJuego!!.user_notificacion.equals(androidId) && pojoJuego!!.estado_noti == Estado.CREADO) {
                        dbRef.child("PS2").child("juegos").child(pojoJuego.id!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        generarNotificacion(generador.incrementAndGet(), pojoJuego,
                            "Se ha creado un nuevo juego " + pojoJuego.nombre,
                            "Nuevos datos en la app",
                            VerJuegosActivity::class.java
                        )
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojoJuego = snapshot.getValue(Juego::class.java)
                    if (!pojoJuego!!.user_notificacion.equals(androidId) && pojoJuego!!.estado_noti == Estado.MODIFICADO) {
                        dbRef.child("PS2").child("juegos").child(pojoJuego.id!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        generarNotificacion(generador.incrementAndGet(), pojoJuego,
                            "Se ha editado el juego " + pojoJuego.nombre,
                            "Datos modificados en la app",
                            EditarJuegoActivity::class.java
                        )
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojoJuego = snapshot.getValue(Juego::class.java)
                    if (!pojoJuego!!.user_notificacion.equals(androidId)){
                        generarNotificacion(generador.incrementAndGet(), pojoJuego,
                            "Se ha eliminado el juego " + pojoJuego.nombre,
                            "Datos eliminados en la app",
                            VerJuegosActivity::class.java
                        )
                    }

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
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

    private fun generarNotificacion(
        id_noti: Int,
        pojo: Parcelable,
        contenido: String,
        titulo: String,
        destino: Class<*>
    ) {
        val id = "Canal de prueba"
        val actividad = Intent(applicationContext, destino).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //Para evitar bugs en la aplicacion
        }

        actividad.putExtra("juego", pojo)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, actividad, PendingIntent.FLAG_IMMUTABLE)

        val notificacion = NotificationCompat.Builder(this, id)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de informacion")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id_noti, notificacion)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crearCanalNotificaciones() {
        val nombre = "canal_basico"
        val id = "Canal de prueba"
        val descripcion = "Notificacion basica"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id, nombre, importancia).apply {
            description = descripcion
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

}