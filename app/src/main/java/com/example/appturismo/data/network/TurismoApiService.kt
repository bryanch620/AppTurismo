package com.example.appturismo.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 1. Modelo de Datos para los Lugares
data class LugarDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val departamento: String,
    val latitud: Double,
    val longitud: Double,
    @SerializedName("calificacion_promedio") val calificacionPromedio: Double,
    val categoria: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    // Campo dinámico para almacenar la distancia calculada según el GPS del usuario
    var distanciaKm: Double = 0.0
) {
    // Propiedad derivada segura para mostrar la calificación formateada como String
    val calificacion: String
        get() = String.format("%.1f", calificacionPromedio)

    // Propiedad para mostrar la distancia en un formato claro en la UI
    val distanciaFormateada: String
        get() = if (distanciaKm < 1.0) {
            "${(distanciaKm * 1000).toInt()} m"
        } else {
            String.format("%.1f km", distanciaKm)
        }
}

// 2. Modelos para el Registro de Usuario
data class RegistroRequest(
    val nombre: String,
    val apellidos: String? = null,
    val email: String,
    val password: String,
    val telefono: String? = null
)

data class RegistroResponse(
    val mensaje: String,
    val usuarioId: Int?
)

// 3. Modelos para el Inicio de Sesión (Login)
data class LoginRequest(
    val email: String,
    val password: String
)

data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val email: String
)

data class LoginResponse(
    val mensaje: String,
    val usuario: UsuarioDto?
)

// 4. Interfaz de Endpoints de Retrofit
interface TurismoApiService {

    @GET("api/lugares")
    suspend fun obtenerLugares(): List<LugarDto>

    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<RegistroResponse>

    @POST("api/usuarios/login")
    suspend fun iniciarSesion(@Body request: LoginRequest): Response<LoginResponse>
}

// 5. Cliente singleton para realizar las llamadas
object RetrofitClient {
    // 10.0.2.2 es la IP especial del emulador para acceder al localhost de tu PC
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: TurismoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TurismoApiService::class.java)
    }
}