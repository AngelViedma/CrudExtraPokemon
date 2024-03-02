package com.tema4.crudextrapokemon

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class PokemonAdaptador(private val lista_pokemon: MutableList<Pokemon>):
    RecyclerView.Adapter<PokemonAdaptador.PokemonViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_pokemon



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonAdaptador.PokemonViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon,parent,false)
        contexto = parent.context
        return PokemonViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: PokemonAdaptador.PokemonViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]
        holder.nombre.text = item_actual.nombre
        holder.tipo.text=item_actual.tipo
        holder.poder.text=item_actual.poder.toString()
        holder.salud.text=item_actual.salud.toString()

        val URL:String? = when(item_actual.imagen){
            ""-> null
            else -> item_actual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.miniatura)

        holder.editar.setOnClickListener {
            val activity = Intent(contexto,EditarPokemon::class.java)
            activity.putExtra("pokemon", item_actual)
            contexto.startActivity(activity)
        }

        holder.eliminar.setOnClickListener {
            val  db_ref = FirebaseDatabase.getInstance().getReference()
            val sto_ref = FirebaseStorage.getInstance().getReference()
            lista_filtrada.remove(item_actual)
            sto_ref.child("pokemons").child(item_actual.id!!).delete()
            db_ref.child("pokemons").child(item_actual.id!!).removeValue()

            Toast.makeText(contexto,"Pokemon borrado con exito", Toast.LENGTH_SHORT).show()
        }




    }

    override fun getItemCount(): Int = lista_filtrada.size

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre_edit)
        val tipo: TextView = itemView.findViewById(R.id.item_tipo_edit)
        val poder: TextView = itemView.findViewById(R.id.item_poder_edit)
        val salud: TextView = itemView.findViewById(R.id.item_salud_edit)
        val editar: ImageView = itemView.findViewById(R.id.item_editar)
        val eliminar: ImageView = itemView.findViewById(R.id.item_borrar)
    }

    override fun getFilter(): Filter {
        return  object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()){
                    lista_filtrada = lista_pokemon
                }else {
                    lista_filtrada = (lista_pokemon.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Pokemon>
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


}