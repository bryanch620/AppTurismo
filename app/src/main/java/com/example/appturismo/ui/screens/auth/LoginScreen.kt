package com.example.appturismo.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import com.example.appturismo.data.network.LoginRequest
import com.example.appturismo.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val backgroundColor = Color(0xFF0B0D0F)
    val cardBackgroundColor = Color(0xFF16191D)
    val primaryOrange = Color(0xFFFF8A00)
    val textMuted = Color(0xFF8E95A0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Bienvenido a\nHorizonteGPS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Text(
                text = "NAVEGACIÓN SIN LÍMITES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = primaryOrange,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

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

            Spacer(modifier = Modifier.height(16.dp))

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

            Text(
                text = "¿Olvidé mi contraseña?",
                color = primaryOrange,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = 12.dp)
                    .clickable { }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        cargando = true
                        scope.launch {
                            try {
                                val request = LoginRequest(email = email.trim(), password = password)
                                val response = withContext(Dispatchers.IO) {
                                    RetrofitClient.instance.iniciarSesion(request)
                                }

                                cargando = false
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    val nombreReal = body?.usuario?.nombre ?: "Usuario Turista"
                                    val correoReal = body?.usuario?.email ?: email.trim()

                                    // Guardar datos en SharedPreferences para la pantalla de Perfil
                                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    prefs.edit().apply {
                                        putString("user_name", nombreReal)
                                        putString("user_email", correoReal)
                                        apply()
                                    }

                                    Toast.makeText(
                                        context,
                                        "¡Bienvenido $nombreReal!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Correo o contraseña incorrectos",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: Exception) {
                                cargando = false
                                Toast.makeText(
                                    context,
                                    "Error de conexión con el servidor",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
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
                        text = "Iniciar Sesión",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "o continuar con",
                color = textMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = cardBackgroundColor),
                    border = null,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Google", color = Color.White)
                }

                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = cardBackgroundColor),
                    border = null,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Apple", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Text("¿No tienes una cuenta? ", color = textMuted, fontSize = 13.sp)
                Text(
                    text = "Crear cuenta",
                    color = primaryOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}