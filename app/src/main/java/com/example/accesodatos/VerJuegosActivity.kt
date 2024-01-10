package com.example.accesodatos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.accesodatos.databinding.ActivityVerJuegosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerJuegosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerJuegosBinding

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Juego>
    private lateinit var adaptador: JuegoAdaptador
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerJuegosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lista = mutableListOf()
        dbRef = FirebaseDatabase.getInstance().getReference()

        dbRef.child("PS2").child("juegos").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{hijo: DataSnapshot? ->
                    val pojoJuego = hijo?.getValue(Juego::class.java)
                    lista.add(pojoJuego!!)
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })

        adaptador = JuegoAdaptador(lista)
        recycler = findViewById(R.id.rv)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

        // Configurar el SearchView
        binding.svJuegos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter(newText.orEmpty())
                return true
            }
        })
        //Boton atras
        binding.btnAtras.setOnClickListener {
            val intent = Intent(this@VerJuegosActivity, MainActivity::class.java)
            startActivity(intent)
        }


    }
}