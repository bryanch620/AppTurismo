package com.example.appturismo.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appturismo.data.network.LugarDto
import com.example.appturismo.data.network.RetrofitClient
import com.example.appturismo.utils.LocationUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Todos") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(1) }

    var lugaresList by remember { mutableStateOf<List<LugarDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val defaultLocation = LatLng(13.6989, -89.1914)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    userLocation = currentLatLng
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.instance.obtenerLugares()
            }
            lugaresList = response
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val lugaresConDistancia = remember(lugaresList, userLocation) {
        val uLoc = userLocation
        if (uLoc != null) {
            lugaresList.map { lugar ->
                val dist = LocationUtils.calcularDistanciaKm(
                    uLoc.latitude,
                    uLoc.longitude,
                    lugar.latitud,
                    lugar.longitud
                )
                lugar.copy(distanciaKm = dist)
            }.sortedBy { it.distanciaKm }
        } else {
            lugaresList
        }
    }

    val lugaresFiltrados = remember(lugaresConDistancia, searchQuery, selectedCategory) {
        lugaresConDistancia.filter { lugar ->
            val nombre = lugar.nombre ?: ""
            val descripcion = lugar.descripcion ?: ""
            val categoria = lugar.categoria ?: ""

            val coincideBusqueda = nombre.contains(searchQuery, ignoreCase = true) ||
                    descripcion.contains(searchQuery, ignoreCase = true)
            val coincideCategoria = if (selectedCategory == "Todos") {
                true
            } else {
                categoria.equals(selectedCategory, ignoreCase = true)
            }
            coincideBusqueda && coincideCategoria
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explorar") },
                    label = { Text("Explorar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00C853),
                        selectedTextColor = Color(0xFF00C853)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onNavigateToFavorites()
                    },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        onNavigateToProfile()
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = true
                ),
                properties = MapProperties(isMyLocationEnabled = userLocation != null)
            ) {
                lugaresFiltrados.forEach { lugar ->
                    Marker(
                        state = MarkerState(position = LatLng(lugar.latitud, lugar.longitud)),
                        title = lugar.nombre ?: "",
                        snippet = if (userLocation != null) lugar.distanciaFormateada else (lugar.categoria ?: "")
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar lugares turísticos...", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F5E9),
                        shadowElevation = 2.dp
                    ) {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = Color(0xFF00C853))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        text = "Todos",
                        icon = Icons.Default.GridView,
                        isSelected = selectedCategory == "Todos",
                        onClick = { selectedCategory = "Todos" }
                    )
                    FilterChipItem(
                        text = "Monumentos",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedCategory == "Monumentos",
                        onClick = { selectedCategory = "Monumentos" }
                    )
                    FilterChipItem(
                        text = "Parques",
                        icon = Icons.Default.Park,
                        isSelected = selectedCategory == "Parques",
                        onClick = { selectedCategory = "Parques" }
                    )
                    FilterChipItem(
                        text = "Museos",
                        icon = Icons.Default.Museum,
                        isSelected = selectedCategory == "Museos",
                        onClick = { selectedCategory = "Museos" }
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color(0xFF121212),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (userLocation != null) "Lugares más cercanos" else "Lugares recomendados",
                            color = Color(0xFF00C853),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Ver todos (${lugaresFiltrados.size})",
                            color = Color(0xFF00C853),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00C853))
                        }
                    } else if (lugaresFiltrados.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron lugares cercanos", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(lugaresFiltrados) { lugar ->
                                RecommendedSpotCard(
                                    spot = lugar,
                                    mostrarDistancia = userLocation != null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF00A859) else Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Black,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RecommendedSpotCard(
    spot: LugarDto,
    mostrarDistancia: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (spot.categoria ?: "").uppercase(),
                        color = Color(0xFF7C4DFF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (mostrarDistancia) {
                            Text(
                                text = spot.distanciaFormateada,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00C853)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = spot.calificacion,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                Text(
                    text = spot.nombre ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = spot.descripcion ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}