# KidCare Frontend Android

Aplicación móvil Android desarrollada con Kotlin y Jetpack Compose para la plataforma KidCare.

---

# Descripción

KidCare es una aplicación enfocada en la gestión y seguimiento infantil.

Este frontend Android permite:

- Registro de usuarios
- Inicio de sesión
- Persistencia de sesión JWT
- Navegación por roles
- Conexión con backend REST API

---

# Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Navigation Compose
- Retrofit 2
- Gson Converter
- OkHttp Logging Interceptor
- Android Studio
- Docker
- MySQL

---

# Estructura del proyecto

```text
app/
│
├── data/
│   ├── api/
│   │   ├── KidCareApi.kt
│   │   └── RetrofitClient.kt
│   │
│   ├── model/
│   │   └── model.kt
│   │
│   ├── AuthRepository.kt
│   └── SessionManager.kt
│
├── navigation/
│   └── Rutas.kt
│
├── ui/
│   └── screens/
│       ├── LoginScreen.kt
│       ├── RegistroScreen.kt
│       ├── HomeScreen.kt
│       └── HomeDelegadoScreen.kt
│
├── res/
│   └── xml/
│       └── network_security_config.xml
│
└── AndroidManifest.xml
```

---

# ⚙️ Requisitos

Antes de ejecutar el proyecto necesitas:

- Android Studio Hedgehog o superior
- JDK 17
- Emulador Android o dispositivo físico
- Backend KidCare funcionando en puerto `8081`

---

# Clonar repositorio

```bash
git clone https://github.com/franciscomonsalve/KidCare-frontend-android.git
```

---

# Ejecutar proyecto

## 1. Abrir Android Studio

Abrir el proyecto desde:

```text
KidCare-frontend-android
```

---

## 2. Sincronizar Gradle

Esperar que Android Studio descargue dependencias automáticamente.

---

## 3. Ejecutar backend

El backend debe estar funcionando en:

```text
http://localhost:8081
```

---

## 4. Ejecutar emulador Android

Iniciar un dispositivo virtual Android desde Android Studio.

---

## 5. Ejecutar aplicación

Presionar:

```text
Run 
```

o usar:

```bash
Shift + F10
```

---

# Configuración API

El proyecto utiliza Retrofit apuntando a:

```kotlin
http://10.0.2.2:8081/
```

## Importante

`10.0.2.2` corresponde al `localhost` del computador host desde el emulador Android.

---

# Funcionalidades implementadas

## Autenticación

- Login
- Registro
- JWT
- Persistencia de sesión

---

## Manejo de roles

- Tutor
- Delegado
- Admin

---

## Integración Backend

Endpoints integrados:

```text
POST /api/auth/login
POST /api/auth/registro
```

---

# Dependencias principales

```kotlin
implementation("androidx.navigation:navigation-compose:2.7.7")

implementation("com.squareup.retrofit2:retrofit:2.11.0")

implementation("com.squareup.retrofit2:converter-gson:2.11.0")

implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

---

# Estado del proyecto

Proyecto actualmente en desarrollo 

---

# Autor

Francisco Monsalve

GitHub:
https://github.com/franciscomonsalve
