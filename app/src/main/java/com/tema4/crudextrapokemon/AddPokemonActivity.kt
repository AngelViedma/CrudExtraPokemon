package com.tema4.crudextrapokemon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AddPokemonActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var nombre : EditText
    private lateinit var tipo : EditText
    private lateinit var poder : EditText
    private lateinit var salud: EditText
    private lateinit var img_pokemon: ImageView
    private lateinit var crear: Button
    private lateinit var volver: Button


    private var url_pokemon: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private lateinit var lista_pokemons: MutableList<Pokemon>



    private lateinit var job: Job



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pokemon)


        val this_activity = this
        job = Job()

        nombre = findViewById(R.id.textInputEditTextNombrePokemon)
        tipo = findViewById(R.id.textInputEditTextTipo_pokemon)
        poder = findViewById(R.id.textInputEditTextPuntosPoder_pokemon)
        salud = findViewById(R.id.textInputEditTextPS_pokemon)
        img_pokemon = findViewById(R.id.image_pokemon)
        crear = findViewById(R.id.bt_add_pokemon)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()
        lista_pokemons = Utilidades.obtenerListaPokemons(db_ref)


        crear.setOnClickListener {


            if (nombre.text.toString().trim().isEmpty() ||
                tipo.text.toString().trim().isEmpty() ||
                poder.text.toString().trim().isEmpty() ||
                salud.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formularion", Toast.LENGTH_SHORT
                ).show()

            } else if (url_pokemon == null) {
                Toast.makeText(
                    applicationContext, "Falta seleccionar la " +
                            "foto", Toast.LENGTH_SHORT
                ).show()
            } else if (Utilidades.existePokemon(lista_pokemons, nombre.text.toString().trim())) {
                Toast.makeText(applicationContext, "Ese Pokemon ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                /* var nuevo_club:Club?=null
                val id_club = db_ref.child("nba")
                    .child("clubs")
                    .push().key
                st_ref.child("nba").child("clubs").child("escudos")
                    .child(id_club!!).putFile(url_escudo!!).addOnSuccessListener {
                        uploadTask->
                        st_ref.child("nba").child("clubs")
                            .child("escudos").child(id_club).downloadUrl.addOnSuccessListener {
                                uri: Uri?->
                                nuevo_club = Club(id_club,nombre.text.toString(),ciudad.text.toString(),
                                fundacion.text.toString().toInt(), uri.toString())
                                db_ref.child("nba").child("club").child(id_club)
                                    .setValue(nuevo_club)
                                Toast.makeText(applicationContext,"Club creado con exito",Toast.LENGTH_SHORT)
                                    .show()
                            }

                    }*/

                var id_generado: String? = db_ref.child("pokemons").push().key

                //GlobalScope(Dispatchers.IO)
                launch {
                    val url_pokemon_firebase =
                        Utilidades.guardarImagenPokemon(st_ref, id_generado!!, url_pokemon!!)

                    Utilidades.CrearPokemon(
                        db_ref, id_generado!!,
                        nombre.text.toString().trim(),
                        tipo.text.toString().trim(),
                        poder.text.toString().trim().toDouble(),
                        salud.text.toString().trim().toDouble(),
                        url_pokemon_firebase
                    )
                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Pokemon creado con exito"
                    )
                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }

            }
        }

        img_pokemon.setOnClickListener {
            accesoGaleria.launch("image/*")

        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_pokemon = uri
            img_pokemon.setImageURI(uri)
        }


    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}