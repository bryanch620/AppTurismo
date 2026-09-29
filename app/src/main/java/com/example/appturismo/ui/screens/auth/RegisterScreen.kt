package com.example.appturismo.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appturismo.data.network.RegistroRequest
import com.example.appturismo.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val backgroundColor = Color(0xFF0B0D0F)
    val cardBackgroundColor = Color(0xFF16191D)
    val primaryOrange = Color(0xFFFF8A00)
    val textMuted = Color(0xFF8E95A0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp)
        ) {
            Text(
                text = "Crea tu Cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "COMPLETA TUS DATOS DE EXPLORADOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = primaryOrange,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres", color = textMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = textMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBackgroundColor,
                        unfocusedContainerColor = cardBackgroundColor,
                        focusedBorderColor = primaryOrange,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos", color = textMuted, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBackgroundColor,
                        unfocusedContainerColor = cardBackgroundColor,
                        focusedBorderColor = primaryOrange,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono / Móvil", color = textMuted) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = textMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico", color = textMuted) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = textMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = textMuted) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = textMuted) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña", color = textMuted) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = textMuted) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedBorderColor = primaryOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val nombreCompleto = "${nombres.trim()} ${apellidos.trim()}".trim()

                    when {
                        nombres.isBlank() || apellidos.isBlank() -> {
                            Toast.makeText(context, "Por favor ingresa tus nombres y apellidos", Toast.LENGTH_SHORT).show()
                        }
                        telefono.isBlank() -> {
                            Toast.makeText(context, "Por favor ingresa tu número de teléfono", Toast.LENGTH_SHORT).show()
                        }
                        email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                            Toast.makeText(context, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            cargando = true
                            scope.launch {
                                try {
                                    val request = RegistroRequest(
                                        nombre = nombreCompleto,
                                        email = email.trim(),
                                        password = password,
                                        telefono = telefono.trim()
                                    )
                                    val response = withContext(Dispatchers.IO) {
                                        RetrofitClient.instance.registrarUsuario(request)
                                    }

                                    cargando = false
                                    if (response.isSuccessful) {
                                        // Guardar datos ingresados en SharedPreferences
                                        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        prefs.edit().apply {
                                            putString("user_name", nombreCompleto)
                                            putString("user_email", email.trim())
                                            apply()
                                        }

                                        Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    } else {
                                        Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    cargando = false
                                    Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
                shape = RoundedCornerShape(12.dp),
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Registrarse",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("¿Ya tienes una cuenta? ", color = textMuted, fontSize = 13.sp)
                Text(
                    text = "Iniciar sesión",
                    color = primaryOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }
        }
    }
}