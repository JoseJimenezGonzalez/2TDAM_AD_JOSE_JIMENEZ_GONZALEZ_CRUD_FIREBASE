package com.example.accesodatos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.accesodatos.databinding.ActivityVerJuegosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerJuegosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerJuegosBinding

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Juego>
    private lateinit var adaptador: JuegoAdaptador

    @Inject
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerJuegosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSearchView()
        configurarBotonBack()
        configurarMenuPopup()
        configurarRecyclerView()
    }

    private fun configurarRecyclerView() {
        lista = mutableListOf()

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
    }

    private fun configurarMenuPopup() {
        //Boton popup
        binding.ivFiltrar.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun configurarBotonBack() {
        //Boton atras
        binding.btnAtras.setOnClickListener {
            val intent = Intent(this@VerJuegosActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarSearchView() {
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
    }

    private fun showPopupMenu(view: View?) {
        // Crear instancia de PopupMenu
        val popupMenu = view?.let { PopupMenu(this, it) }

        // Inflar el menú desde el archivo XML
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu, popupMenu.menu)

        // Establecer un listener para manejar clics en las opciones del menú
        popupMenu?.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_aph -> {
                    // Lógica para la opción "ordenar alfabeticamente"
                    lista.sortBy { juego->
                        juego.nombre
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_rating -> {
                    // Lógica para la opción "ordenar por puntuacion"
                    lista.sortByDescending { juego->
                        juego.ratingBar
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_date -> {
                    // Lógica para la opción "ordenar por fecha de lanzamiento"
                    lista.sortByDescending { juego->
                        juego.fechaLanzamiento
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                else -> false
            }
        }
        // Mostrar el menú emergente
        popupMenu?.show()
    }
}