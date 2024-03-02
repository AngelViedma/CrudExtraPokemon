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
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditarPokemon : AppCompatActivity(), CoroutineScope {

    private lateinit var nombre : EditText
    private lateinit var tipo : EditText
    private lateinit var poder : EditText
    private lateinit var salud : EditText
    private lateinit var img_pokemon: ImageView
    private lateinit var modificar: Button
    private lateinit var volver: Button


    private var url_pokemon: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private  lateinit var  pojo_pokemon:Pokemon
    private lateinit var lista_pokemons: MutableList<Pokemon>



    private lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_pokemon)

        supportActionBar?.title = "Editar Pokemon"


        val this_activity = this
        job = Job()

        pojo_pokemon = intent.getParcelableExtra<Pokemon>("pokemon")!!


        nombre = findViewById(R.id.textInputEditTextNombrePokemon)
        tipo = findViewById(R.id.textInputEditTextTipo_pokemon)
        poder = findViewById(R.id.textInputEditTextPuntosPoder_pokemon)
        salud=findViewById(R.id.textInputEditTextPS_pokemon)
        img_pokemon = findViewById(R.id.image_pokemon)
        modificar = findViewById(R.id.bt_modificar_pokemon)

        nombre.setText(pojo_pokemon.nombre)
        tipo.setText(pojo_pokemon.tipo)
        poder.setText(pojo_pokemon.poder.toString())
        salud.setText(pojo_pokemon.salud.toString())


        Glide.with(applicationContext)
            .load(pojo_pokemon.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(img_pokemon)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        lista_pokemons = Utilidades.obtenerListaPokemons(db_ref)

        modificar.setOnClickListener {

            if (nombre.text.toString().trim().isEmpty() ||
                tipo.text.toString().trim().isEmpty() ||
                poder.text.toString().trim().isEmpty() ||
                salud.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formularion", Toast.LENGTH_SHORT
                ).show()

            } else if (Utilidades.existePokemon(lista_pokemons, nombre.text.toString().trim())) {
                Toast.makeText(applicationContext, "Ese Pokemon ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                //GlobalScope(Dispatchers.IO)
                var url_pokemon_firebase = String()
                launch {
                    if(url_pokemon == null){
                        url_pokemon_firebase = pojo_pokemon.imagen!!
                    }else{
                        val url_escudo_firebase =
                            Utilidades.guardarImagenPokemon(st_ref, pojo_pokemon.id!!, url_pokemon!!)
                    }


                    Utilidades.CrearPokemon(
                        db_ref, pojo_pokemon.id!!,
                        nombre.text.toString().trim(),
                        tipo.text.toString().trim(),
                        poder.text.toString().trim().toDouble(),
                        salud.text.toString().trim().toDouble(),
                        url_pokemon_firebase
                    )
                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Pokemon modificado con exito"
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