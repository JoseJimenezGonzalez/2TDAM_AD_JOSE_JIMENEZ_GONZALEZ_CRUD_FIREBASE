package com.example.myapplication.adaptador

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.fragmentos.EditarJuegoFragment
import com.example.myapplication.modelo.Juego
import com.example.myapplication.utilidades.Utilidades
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorJuegoRecyclerView(private val listaJuegos: MutableList<Juego>, private val navController: NavController, private val linearLayout: LinearLayout):RecyclerView.Adapter<AdaptadorJuegoRecyclerView.JuegoViewHolder>(), Filterable{

    private lateinit var contexto: Context
    private var listaFiltrada = listaJuegos

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JuegoViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_juego, parent, false)
        contexto = parent.context
        return JuegoViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: JuegoViewHolder, position: Int) {

        val itemActual = listaFiltrada[position]

        holder.nombreJuego.text = itemActual.nombre
        holder.nombreEstudio.text = itemActual.estudio
        holder.generoJuego.text = itemActual.genero
        holder.edadRecomendadaJuego.text = itemActual.edad
        holder.fechaSalidaJuego.text = itemActual.fechaLanzamiento
        holder.puntuacion.rating = itemActual.ratingBar?.toFloat() ?: 0.0f

        val URL: String? = when(itemActual.imagen){
            "" -> null
            else -> itemActual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.miniatura)


        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("juego", itemActual)
            val fragment = EditarJuegoFragment()
            fragment.arguments = bundle
            Log.d("juego bundle", bundle.toString())
            navController.navigate(R.id.action_verJuegosFragment_to_editarJuegoFragment, bundle)
        }

        holder.itemView.setOnLongClickListener {
            Log.d("long click", "Se ha registrado el long click")

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar juego")
            builder.setMessage("¿Estás seguro de que deseas eliminar este juego?")

            builder.setPositiveButton("Sí") { _, _ ->
                // Acción para long click
                val dbRef = FirebaseDatabase.getInstance().reference
                val stoRef = FirebaseStorage.getInstance().reference
                listaFiltrada.remove(itemActual)
                stoRef.child("PS2").child("covers").child(itemActual.id!!).delete()
                dbRef.child("PS2").child("juegos").child(itemActual.id!!).removeValue()

                Toast.makeText(contexto,"Juego borrado con éxito", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("No") { _, _ ->
                // No hacer nada si el usuario elige no eliminar
            }

            val dialog = builder.create()
            dialog.show()

            true // Importante devolver true para indicar que se manejo el evento long click
        }

    }

    override fun getItemCount(): Int = listaFiltrada.size

    class JuegoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val miniatura: ImageView = itemView.findViewById(R.id.ivFotoJuego)
        val nombreJuego: TextView = itemView.findViewById(R.id.tvNombreJuego)
        val nombreEstudio: TextView = itemView.findViewById(R.id.tvNombreEstudio)
        val generoJuego: TextView = itemView.findViewById(R.id.tvGeneroJuego)
        val edadRecomendadaJuego: TextView = itemView.findViewById(R.id.tvEdad)
        val fechaSalidaJuego: TextView = itemView.findViewById(R.id.tvFechaSalida)
        var puntuacion: RatingBar = itemView.findViewById(R.id.rbPuntuacion)
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()){
                    listaFiltrada = listaJuegos
                }else {
                    listaFiltrada = (listaJuegos.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Juego>
                }

                val filterResults = FilterResults()
                filterResults.values = listaFiltrada
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    // Método para filtrar la lista
    fun filter(newText: String) {
        val busqueda = newText.lowercase()
        if (busqueda.isEmpty()) {
            listaFiltrada = listaJuegos
        } else {
            listaFiltrada = listaJuegos.filter {
                it.nombre.toString().lowercase().contains(busqueda)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}