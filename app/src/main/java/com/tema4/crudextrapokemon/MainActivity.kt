package com.tema4.crudextrapokemon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.tema4.crudextrapokemon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bt_nuevo_pokemon: Button
    lateinit var bt_ver_pokemones: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bt_nuevo_pokemon = binding.btNuevoPokemon
        bt_ver_pokemones = binding.btVerPokemon

        bt_nuevo_pokemon.setOnClickListener {
            val activity = Intent(applicationContext, AddPokemonActivity::class.java)
            startActivity(activity)
        }

        bt_ver_pokemones.setOnClickListener {
            val activity = Intent(applicationContext, VerPokemon::class.java)
            startActivity(activity)
        }
    }
}