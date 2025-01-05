package com.example.gestorpresupuesto

import android.content.Context
import com.google.gson.Gson

class GestorPreferencias(private val context: Context) {

    companion object {
        private const val CLAVE_MOVIMIENTOS = "movimientos"
    }

    private val sharedPreferences = context.getSharedPreferences("GestorPresupuesto", Context.MODE_PRIVATE)

    // Guardar lista de movimientos
    fun guardarMovimientos(listaMovimientos: List<Movimiento>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(listaMovimientos)
        editor.putString(CLAVE_MOVIMIENTOS, json)
        editor.apply()
    }

    // Cargar lista de movimientos
    fun cargarMovimientos(): List<Movimiento> {
        val json = sharedPreferences.getString(CLAVE_MOVIMIENTOS, null)
        return if (!json.isNullOrEmpty()) {
            try {
                val type = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<List<Movimiento>>() {}.type
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}
