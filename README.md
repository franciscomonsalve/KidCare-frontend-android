# KidCare — Android App

Aplicación móvil Android del sistema KidCare. Permite a tutores y apoderados registrar observaciones de salud pediátrica, gestionar perfiles de menores y compartir información con médicos de forma segura.

## Equipo

| Nombre | Rol |
|---|---|
| Génesis Rojas | Líder de Proyecto / DBA / Analista Funcional |
| Francisco Monsalve | Frontend Mobile / QA |
| Benjamín Peña | Backend / Integración IA / DevOps |

## Stack técnico

- Kotlin + Jetpack Compose (Material3)
- Navigation Compose
- Retrofit 2 + OkHttp3 (cliente HTTP)
- Gson (serialización JSON)
- ViewModel + StateFlow (gestión de estado)
- SharedPreferences (sesión persistente)

## Prerrequisitos

- Android Studio Hedgehog o superior
- JDK 17+
- Emulador Android con API 26+ o dispositivo físico
- Los 4 microservicios backend corriendo en el mismo equipo

## Configuración

La app usa `10.0.2.2` como dirección IP del backend (equivale a `localhost` desde el emulador Android).
Si usas un dispositivo físico, reemplaza esa IP por la IP real de tu máquina en la red local dentro de [ApiClient.kt](app/src/main/java/com/example/kidcare/data/network/ApiClient.kt).

## Microservicios necesarios

| Servicio | Puerto | Repositorio |
|---|---|---|
| usuario-service | 8081 | KidCare_Usuario_Backend |
| acceso-service | 8082 | KidCare_Acceso_Backend |
| chatbot-service | 8083 | KidCare_Chatbot_Backend |
| historial-service | 8084 | KidCare_Historial_Backend |

## Estructura de pantallas

```
SplashScreen
├── LoginScreen
│   ├── RegistroScreen
│   └── RecuperarPasswordScreen
│       └── RestablecerPasswordScreen
└── HomeScreen (bottom nav)
    ├── Inicio — lista de menores, acciones rápidas
    ├── Bitácora — historial de observaciones del menor
    ├── Chatbot — registro de nuevas observaciones
    └── Perfil — datos de sesión, cerrar sesión
        └── InvitarApoderadoScreen (solo TUTOR)
```

## Roles de usuario

| Rol | Puede agregar menores | Puede eliminar menores | Puede invitar apoderados | Ve menores |
|---|---|---|---|---|
| TUTOR | Sí | Sí | Sí | Sus propios menores |
| DELEGADO | No | No | No | Menores asignados por el tutor |
| ADMIN | Sí | Sí | Sí | Todos |

## Flujo de recuperación de contraseña

1. Usuario pulsa "¿Olvidaste tu contraseña?" en LoginScreen
2. Ingresa su email en RecuperarPasswordScreen → backend envía un código UUID al correo
3. Usuario ingresa el código + nueva contraseña en RestablecerPasswordScreen
4. El código se invalida tras el uso (no reutilizable)
