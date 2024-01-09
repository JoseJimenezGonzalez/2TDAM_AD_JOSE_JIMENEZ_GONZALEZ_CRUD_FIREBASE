package com.example.accesodatos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        val URL: String? = when(itemActual.imagen){
            "" -> null
            else -> itemActual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.miniatura)
    }

    override fun getItemCount(): Int = listaFiltrada.size

    class JuegoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val miniatura: ImageView = itemView.findViewById(R.id.ivFotoJuego)
        val nombreJuego: TextView = itemView.findViewById(R.id.tvNombreJuego)
        val nombreEstudio: TextView = itemView.findViewById(R.id.tvNombreEstudio)
        val generoJuego: TextView = itemView.findViewById(R.id.tvGeneroJuego)
        val edadRecomendadaJuego: TextView = itemView.findViewById(R.id.tvEdad)
        val fechaSalidaJuego: TextView = itemView.findViewById(R.id.tvFechaSalida)
        //Falta el rating bar que a los demas les ha dado problemas
    }
}