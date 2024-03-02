package com.tema4.crudextrapokemon

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Utilidades {
    companion object {

        fun existePokemon(pokemon: List<Pokemon>, nombre: String): Boolean {
            return pokemon.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        //Obtener lista datos pero no para usar de manera sincrona o sea que algo dependa directamente de el
        fun obtenerListaPokemons(db_ref: DatabaseReference): MutableList<Pokemon> {
            var lista = mutableListOf<Pokemon>()
            //Consulta a la bd
            db_ref.child("pokemons")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //lista.clear()
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val pojo_pokemons = hijo?.getValue(Pokemon::class.java)

                            lista.add(pojo_pokemons!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }
        fun CrearPokemon(db_ref:DatabaseReference,id:String,nombre:String,tipo:String,poder:Double,salud:Double,url_firebase:String)=
            db_ref.child("pokemons").child(id).setValue(Pokemon(
                id,
                nombre,
                tipo,
                poder,
                salud,
                url_firebase
            ))

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto: String) {
            activity.runOnUiThread {
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        suspend fun guardarImagenPokemon(sto_ref: StorageReference, id: String, imagen: Uri): String {
            lateinit var url_pokemon_firebase: Uri

            url_pokemon_firebase = sto_ref.child("pokemons").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_pokemon_firebase.toString()
        }
        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.pokemon_generico)
                .error(R.drawable.error_404)
            return options
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable{
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }

    }
}