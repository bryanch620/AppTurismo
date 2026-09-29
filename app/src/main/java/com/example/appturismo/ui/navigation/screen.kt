package com.example.appturismo.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Favorites : Screen("favorites") // <- Agregar
    object Profile : Screen("profile")     // <- Agregar
}