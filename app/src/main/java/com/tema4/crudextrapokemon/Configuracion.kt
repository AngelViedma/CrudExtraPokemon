package com.tema4.crudextrapokemon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class Configuracion : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)
        supportActionBar?.title = "Configuración"

        sharedPreferences = getSharedPreferences("config_prefs", Context.MODE_PRIVATE)

        val switchTheme = findViewById<Switch>(R.id.switch_tema_oscuro)

        // Restaurar el estado del switch desde las preferencias compartidas
        switchTheme.isChecked = sharedPreferences.getBoolean("isDarkThemeOn", false)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Cambiar al tema oscuro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Cambiar al tema claro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            // Guardar el estado del switch en las preferencias compartidas
            sharedPreferences.edit().putBoolean("isDarkThemeOn", isChecked).apply()
        }

        // Botón para cambiar el nombre del ActionBar
        findViewById<Button>(R.id.bt_cambiar_nombre_app).setOnClickListener {
            showChangeNameDialog()
        }
    }

    // Método para mostrar el diálogo de cambio de nombre del ActionBar
    private fun showChangeNameDialog() {
        val editText = EditText(this)
        editText.hint = "Nuevo nombre"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Cambiar nombre del ActionBar")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = editText.text.toString()
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra("action_bar_name", newName)
                })
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
}

