package com.example.accesodatos

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class JuegoAdaptador(private val listaJuegos: MutableList<Juego>):RecyclerView.Adapter<JuegoAdaptador.JuegoViewHolder>(), Filterable{


    private lateinit var contexto: Context
    private var listaFiltrada = listaJuegos

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JuegoAdaptador.JuegoViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_juego, parent, false)
        contexto = parent.context
        return JuegoViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: JuegoAdaptador.JuegoViewHolder, position: Int) {
        val itemActual = listaFiltrada[position]
        //Falta
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

        holder.editar.setOnClickListener {
            val activity = Intent(contexto, EditarJuegoActivity::class.java)
            activity.putExtra("juego", itemActual)
            contexto.startActivity(activity)
        }

        holder.eliminar.setOnClickListener {

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar juego")
            builder.setMessage("¿Estás seguro de que deseas eliminar este juego?")

            builder.setPositiveButton("Sí"){_, _ ->

                val dbRef = FirebaseDatabase.getInstance().reference
                val stRef = FirebaseStorage.getInstance().reference

                val androidId =
                    Settings.Secure.getString(contexto.contentResolver, Settings.Secure.ANDROID_ID)

                listaFiltrada.remove(itemActual)
                stRef.child("PS2").child("covers").child(itemActual.id!!).delete()
                dbRef.child("PS2").child("juegos").child(itemActual.id!!).child("user_notification").setValue(androidId)

                dbRef.child("PS2").child("juegos").child(itemActual.id!!).removeValue()

                Toast.makeText(contexto,"Juego borrado con exito", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("No") { _, _ ->
                // No hacer nada si el usuario elige no eliminar
            }

            val dialog = builder.create()
            dialog.show()

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
        val editar: ImageView = itemView.findViewById(R.id.ivEditar)
        val eliminar: ImageView = itemView.findViewById(R.id.ivBorrar)
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