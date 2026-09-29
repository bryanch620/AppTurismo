# Plan de Implementación: HorizonteGPS - App de Turismo

Este plan detalla la creación de la aplicación "HorizonteGPS", una plataforma de turismo que permite a los usuarios descubrir lugares, crear álbumes de fotos (públicos o privados) y acceder a un plan premium para mayor capacidad.

## Resumen del Proyecto
La aplicación se construirá usando **Jetpack Compose** para la interfaz de usuario, siguiendo una arquitectura **MVVM (Model-View-ViewModel)** y utilizando **Firebase** para el backend (Autenticación, Base de Datos y Almacenamiento).

## Revisión del Usuario Requerida
> [!IMPORTANT]
> Se requiere la configuración de un proyecto en **Firebase Console** para obtener el archivo `google-services.json`.
> También se necesitará una **API Key de Google Maps** habilitada en la consola de Google Cloud.

## Preguntas Abiertas
- ¿Deseas usar **Hilt** o **Koin** para la inyección de dependencias? (He propuesto Hilt por ser el estándar).
- ¿Tienes algún esquema de colores específico o seguimos los de la imagen (predominantemente oscuro con acentos naranja)?

## Cambios Propuestos

---

### Infraestructura y Configuración

#### [MODIFY] [build.gradle.kts (Proyecto)](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/build.gradle.kts)
- Añadir plugins de Hilt y Google Services.

#### [MODIFY] [build.gradle.kts (App)](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/build.gradle.kts)
- Añadir dependencias:
    - Navigation Compose
    - Hilt (Dagger/Hilt)
    - Firebase (Auth, Firestore, Storage)
    - Google Maps Compose
    - Play Billing Library
    - Coil (Carga de imágenes)

---

### Módulo de Autenticación

#### [NEW] [LoginScreen.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/auth/LoginScreen.kt)
- Pantalla de inicio de sesión con Correo/Contraseña y opciones de Google/Apple.

#### [NEW] [AuthViewModel.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/auth/AuthViewModel.kt)
- Lógica de autenticación vinculada a Firebase Auth.

---

### Módulo de Exploración y Mapas

#### [NEW] [ExploreScreen.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/explore/ExploreScreen.kt)
- Integración de Google Maps con marcadores personalizados.
- Lista inferior de "Lugares Recomendados".

#### [NEW] [PlaceDetailScreen.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/details/PlaceDetailScreen.kt)
- Información detallada del lugar, opiniones y galería de fotos de usuarios.

---

### Módulo de Álbumes y Perfil

#### [NEW] [ProfileScreen.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/profile/ProfileScreen.kt)
- Vista del perfil del usuario, estadísticas (lugares visitados, álbumes creados).
- Cuadrícula de álbumes.

#### [NEW] [AlbumCreateScreen.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/album/AlbumCreateScreen.kt)
- Formulario para crear álbumes con selector de privacidad (Público/Privado).

---

### Módulo de Pagos (Premium)

#### [NEW] [SubscriptionManager.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/billing/SubscriptionManager.kt)
- Integración con Play Billing Library para manejar la suscripción de $5.99.

#### [NEW] [PremiumUpsellScreen.kt](file:///C:/Users/User/AndroidStudioProjects/AppTurismo/app/src/main/java/com/example/appturismo/ui/premium/PremiumUpsellScreen.kt)
- Pantalla informativa sobre los beneficios del plan premium.

## Plan de Verificación

### Pruebas Automatizadas
- Pruebas unitarias para los ViewModels (Auth, Albums).
- Pruebas de UI con Compose Test Rule para el flujo de navegación.

### Verificación Manual
- Flujo completo desde Login hasta creación de un álbum privado.
- Simulación de compra en Google Play (Sandbox) para verificar el estado Premium.
