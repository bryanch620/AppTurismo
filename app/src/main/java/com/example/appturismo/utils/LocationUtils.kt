package com.example.appturismo.utils

import android.location.Location

object LocationUtils {
    /**
     * Calcula la distancia en kilómetros entre dos coordenadas GPS (Latitud y Longitud)
     */
    fun calcularDistanciaKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val resultado = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, resultado)
        return (resultado[0] / 1000.0) // Convierte metros a kilómetros
    }
}