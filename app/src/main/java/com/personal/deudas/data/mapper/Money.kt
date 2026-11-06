package com.personal.deudas.data.mapper

import java.math.BigDecimal
import java.math.RoundingMode

// La API maneja dinero en centavos enteros para evitar errores de redondeo.
// La app trabaja con BigDecimal en pesos (nunca Double).

/** 150000 -> 1500.00 */
fun Long.centavosToPesos(): BigDecimal = BigDecimal.valueOf(this, 2)

/** 1500.5 -> 150050 (redondea a 2 decimales). */
fun BigDecimal.pesosToCentavos(): Long =
    setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact()
