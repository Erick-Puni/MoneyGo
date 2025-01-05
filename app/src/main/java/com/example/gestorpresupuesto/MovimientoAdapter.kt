package com.example.gestorpresupuesto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovimientoAdapter(
    private val listaMovimientos: List<Movimiento>,
    private val onItemLongClick: (Int) -> Unit, // Callback para manejar selecciones
    private val onDeleteClick: (Int) -> Unit // Callback para manejar la eliminación
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    // Mantener la referencia de elementos seleccionados
    private val elementosSeleccionados = mutableSetOf<Int>()

    class MovimientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        val itemLayout: View = itemView.findViewById(R.id.itemLayout)
        val btnEliminar: View = itemView.findViewById(R.id.btnEliminar) // Botón de eliminación
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovimientoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movimiento, parent, false)
        return MovimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovimientoViewHolder, position: Int) {
        val movimiento = listaMovimientos[position]
        holder.tvDescripcion.text = movimiento.descripcion
        holder.tvMonto.text = "$${movimiento.monto}" // Mostrar monto con formato de moneda
        holder.tvTipo.text = movimiento.tipo

        // Cambiar colores según el tipo de movimiento
        val color = if (movimiento.tipo == MainActivity.TIPO_INGRESO) {
            holder.itemView.context.getColor(R.color.colorIngreso)
        } else {
            holder.itemView.context.getColor(R.color.colorGasto)
        }
        holder.tvTipo.setTextColor(color)

        // Cambiar fondo para los elementos seleccionados
        holder.itemLayout.setBackgroundColor(
            if (elementosSeleccionados.contains(position))
                holder.itemView.context.getColor(R.color.colorSeleccionado)
            else
                holder.itemView.context.getColor(android.R.color.transparent)
        )

        // Mostrar el botón de eliminar solo si el elemento está seleccionado
        holder.btnEliminar.visibility = if (elementosSeleccionados.contains(position)) View.VISIBLE else View.GONE

        // Configurar listener para manejar clics largos
        holder.itemView.setOnLongClickListener {
            toggleSeleccion(position)  // Al hacer clic largo, se selecciona el item
            onItemLongClick(position)   // También se llama al callback de la actividad
            true
        }

        // Configurar el listener del botón de eliminar
        holder.btnEliminar.setOnClickListener {
            onDeleteClick(position) // Eliminar el item al hacer clic en el botón de eliminar
        }
    }

    override fun getItemCount(): Int {
        return listaMovimientos.size
    }

    // Actualizar lista de seleccionados
    private fun toggleSeleccion(position: Int) {
        if (elementosSeleccionados.contains(position)) {
            elementosSeleccionados.remove(position)
        } else {
            elementosSeleccionados.add(position)
        }
        notifyItemChanged(position)  // Actualizar solo el ítem que ha cambiado
    }

    // Limpiar selección
    fun limpiarSeleccion() {
        elementosSeleccionados.clear()
        notifyDataSetChanged()  // Actualizar toda la lista de elementos
    }

    // Obtener los elementos seleccionados
    fun getElementosSeleccionados(): Set<Int> {
        return elementosSeleccionados
    }
}
