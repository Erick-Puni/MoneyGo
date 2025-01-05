package com.example.gestorpresupuesto

data class Movimiento(
    val monto: Double,
    val descripcion: String,
    val tipo: String // "Ingreso" o "Gasto"
)