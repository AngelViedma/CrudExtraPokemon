package com.tema4.crudextrapokemon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.tema4.crudextrapokemon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplicar el tema oscuro si está habilitado
        sharedPreferences = getSharedPreferences("config_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isDarkThemeOn", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restaurar el título del ActionBar
        supportActionBar?.title = getStoredActionBarName()

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("config_prefs", Context.MODE_PRIVATE)

        binding.btNuevoPokemon.setOnClickListener {
            val activity = Intent(applicationContext, AddPokemonActivity::class.java)
            startActivity(activity)
        }

        binding.btVerPokemon.setOnClickListener {
            val activity = Intent(applicationContext, VerPokemon::class.java)
            startActivity(activity)
        }

        binding.btConfiguracion.setOnClickListener {
            val intent = Intent(applicationContext, Configuracion::class.java)
            startActivityForResult(intent, REQUEST_CODE_CONFIG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CONFIG && resultCode == Activity.RESULT_OK) {
            val newName = data?.getStringExtra("action_bar_name")
            newName?.let {
                supportActionBar?.title = it
                // Opcionalmente, guardar el nuevo nombre en SharedPreferences
                saveActionBarName(it)
            }
        }
    }

    // Método para guardar el nombre del ActionBar en SharedPreferences
    private fun saveActionBarName(name: String) {
        sharedPreferences.edit().putString("action_bar_name", name).apply()
    }

    // Método para obtener el nombre del ActionBar desde SharedPreferences
    private fun getStoredActionBarName(): String {
        return sharedPreferences.getString("action_bar_name", "Nombre Predeterminado") ?: "Nombre Predeterminado"
    }

    companion object {
        const val REQUEST_CODE_CONFIG = 1001
    }
}
