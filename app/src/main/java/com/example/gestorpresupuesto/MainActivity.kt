package com.example.gestorpresupuesto

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        // Constantes para identificar el tipo de movimiento
        const val TIPO_INGRESO = "Ingreso"
        const val TIPO_GASTO = "Gasto"
    }

    // Declaración de vistas y variables
    private lateinit var etMonto: EditText // Campo para ingresar el monto
    private lateinit var etDescripcion: EditText // Campo para ingresar la descripción
    private lateinit var spTipo: Spinner // Selector de tipo de movimiento
    private lateinit var btnRegistrar: Button // Botón para registrar un movimiento
    private lateinit var rvMovimientos: RecyclerView // Lista para mostrar movimientos
    private val listaMovimientos = mutableListOf<Movimiento>() // Lista de movimientos registrados
    private lateinit var adaptador: MovimientoAdapter // Adaptador para el RecyclerView
    private var saldoTotal: Double = 0.0 // Saldo total acumulado
    private lateinit var tvSaldoTotal: TextView // Vista para mostrar el saldo total
    private lateinit var gestorPreferencias: GestorPreferencias // Clase para gestionar almacenamiento
    private val elementosSeleccionados = mutableSetOf<Int>() // Almacena posiciones seleccionadas en la lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        spTipo = findViewById(R.id.spTipo)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        rvMovimientos = findViewById(R.id.rvMovimientos)
        tvSaldoTotal = findViewById(R.id.tvSaldoTotal)
        gestorPreferencias = GestorPreferencias(this)

        // Configurar opciones del Spinner
        val opciones = arrayOf(TIPO_INGRESO, TIPO_GASTO)
        spTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)

        // Configurar RecyclerView con un adaptador y manejo de selección/eliminación
        adaptador = MovimientoAdapter(
            listaMovimientos,
            { posicion -> toggleSeleccion(posicion) }, // Callback para alternar selección
            { posicion -> eliminarMovimiento(posicion) } // Callback para eliminar un movimiento
        )
        rvMovimientos.layoutManager = LinearLayoutManager(this)
        rvMovimientos.adapter = adaptador

        // Cargar movimientos previamente guardados
        cargarDatos()

        // Configurar botón para registrar movimientos
        btnRegistrar.setOnClickListener {
            registrarMovimiento()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflar el menú en la barra de opciones
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Mostrar el botón de eliminar solo si hay elementos seleccionados
        val btnEliminar = menu.findItem(R.id.action_eliminar)
        btnEliminar.isVisible = elementosSeleccionados.isNotEmpty()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejo de opciones de menú
        return when (item.itemId) {
            R.id.action_eliminar -> {
                eliminarSeleccionados() // Eliminar elementos seleccionados
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun registrarMovimiento() {
        // Obtener valores ingresados
        val montoTexto = etMonto.text.toString()
        val descripcion = etDescripcion.text.toString()
        val tipo = spTipo.selectedItem.toString()

        // Validar campos vacíos
        if (montoTexto.isBlank() || descripcion.isBlank()) {
            Snackbar.make(btnRegistrar, "Por favor, completa todos los campos.", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Validar que el monto sea válido
        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Snackbar.make(btnRegistrar, "Ingresa un monto válido.", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Crear y añadir movimiento a la lista
        val movimiento = Movimiento(monto, descripcion, tipo)
        listaMovimientos.add(movimiento)

        // Notificar cambios al adaptador
        adaptador.notifyDataSetChanged()

        // Limpiar campos
        etMonto.text.clear()
        etDescripcion.text.clear()
        spTipo.setSelection(0)

        // Actualizar saldo total y guardar datos
        actualizarSaldoTotal()
        guardarDatos()
    }

    private fun actualizarSaldoTotal() {
        // Calcular saldo total basado en los movimientos
        saldoTotal = listaMovimientos.sumOf { if (it.tipo == TIPO_INGRESO) it.monto else -it.monto }
        tvSaldoTotal.text = "Saldo total: $${String.format("%.2f", saldoTotal)}"
    }

    private fun cargarDatos() {
        // Cargar movimientos desde preferencias y actualizar la vista
        listaMovimientos.clear()
        listaMovimientos.addAll(gestorPreferencias.cargarMovimientos())
        adaptador.notifyDataSetChanged()
        actualizarSaldoTotal()
    }

    private fun guardarDatos() {
        // Guardar lista de movimientos en preferencias
        gestorPreferencias.guardarMovimientos(listaMovimientos)
    }

    private fun toggleSeleccion(posicion: Int) {
        // Alternar selección de un elemento
        if (elementosSeleccionados.contains(posicion)) {
            elementosSeleccionados.remove(posicion)
        } else {
            elementosSeleccionados.add(posicion)
        }
        adaptador.notifyItemChanged(posicion)
        invalidateOptionsMenu() // Refrescar el menú de opciones
    }

    private fun eliminarSeleccionados() {
        // Eliminar elementos seleccionados de la lista
        val listaAEliminar = elementosSeleccionados.sortedDescending() // Ordenar para evitar errores al eliminar
        for (posicion in listaAEliminar) {
            listaMovimientos.removeAt(posicion)
        }
        elementosSeleccionados.clear()
        adaptador.notifyDataSetChanged()
        actualizarSaldoTotal()
        guardarDatos()

        Snackbar.make(rvMovimientos, "Elementos eliminados", Snackbar.LENGTH_SHORT).show()
        invalidateOptionsMenu()
    }

    private fun eliminarMovimiento(posicion: Int) {
        // Eliminar un movimiento individual
        listaMovimientos.removeAt(posicion)
        adaptador.notifyItemRemoved(posicion)
        actualizarSaldoTotal()
        guardarDatos()
    }
}
