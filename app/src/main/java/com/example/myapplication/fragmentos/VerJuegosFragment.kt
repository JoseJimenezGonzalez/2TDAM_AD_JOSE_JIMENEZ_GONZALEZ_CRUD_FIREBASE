package com.example.myapplication.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adaptador.AdaptadorJuegoRecyclerView
import com.example.myapplication.adaptador.OnItemClickListener
import com.example.myapplication.databinding.FragmentVerJuegosBinding
import com.google.firebase.database.ValueEventListener
import com.example.myapplication.modelo.Juego
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

@AndroidEntryPoint
class VerJuegosFragment : Fragment() {

    private var _binding: FragmentVerJuegosBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Juego>
    private lateinit var adaptador: AdaptadorJuegoRecyclerView

    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stoRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerJuegosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        configurarSearchView()
        configurarMenuPopup()
        configurarRecyclerView()

    }
    private fun configurarRecyclerView() {
        lista = mutableListOf()

        dbRef.child("PS2").child("juegos").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{ hijo: DataSnapshot? ->
                    val pojoJuego = hijo?.getValue(Juego::class.java)
                    lista.add(pojoJuego!!)
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })

        adaptador = AdaptadorJuegoRecyclerView(lista, findNavController(), binding.lySinJuegos)
        recycler = binding.rvVerJuegos
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)
    }

    private fun configurarMenuPopup() {
        //Boton popup
        binding.ivFiltrar.setOnClickListener {
            showPopupMenu(it)
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
        val popupMenu = view?.let { android.widget.PopupMenu(context, it) }

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
