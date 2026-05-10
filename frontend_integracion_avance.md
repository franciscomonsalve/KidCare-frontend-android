# Avance: Integración Frontend Android ↔ Backend Usuario

**Fecha:** 2026-05-09 | **Estado:** Funcional

## Qué se hizo

Conectar las pantallas de Login y Registro del frontend Android al backend de usuario (`localhost:8081`) que estaba corriendo con Docker + MySQL.

## Incompatibilidades corregidas

| Frontend (antes) | Backend (esperaba) |
|---|---|
| `correo`, `contrasena`, `nombre` | `email`, `password`, `nombreCompleto` |
| `LoginResponse` con `userId`, `nombre` | `AuthResponse` con solo `token`, `email`, `rol` |
| Campo `rol` en RegistroRequest | No existe (backend asigna TUTOR) |
| Sin campo `aceptaTerminos` | Requerido por backend |

## Archivos creados/modificados

| Archivo | Acción |
|---|---|
| `app/build.gradle.kts` | + Retrofit 2.11, Gson converter, OkHttp logging |
| `data/model/model.kt` | Modelos auth reescritos para coincidir con backend |
| `data/api/KidCareApi.kt` | Interfaz Retrofit (`/api/auth/registro`, `/api/auth/login`) |
| `data/api/RetrofitClient.kt` | Singleton Retrofit → `http://10.0.2.2:8081/` |
| `data/SessionManager.kt` | Persistencia de `token`, `rol`, `email` en SharedPreferences |
| `data/AuthRepository.kt` | Capa de acceso con `Result<AuthResponse>` |
| `ui/screens/LoginScreen.kt` | Reemplazado mock por llamada real + spinner + error |
| `ui/screens/RegistroScreen.kt` | Reemplazado mock por llamada real + validación + spinner |
| `AndroidManifest.xml` | + permiso INTERNET + networkSecurityConfig |
| `res/xml/network_security_config.xml` | Permite HTTP cleartext a `10.0.2.2` (fix Android 9+) |

## Flujo resultante

1. Usuario llena Registro → POST `/api/auth/registro` → JWT guardado en SharedPreferences → navega a HOME
2. Usuario hace Login → POST `/api/auth/login` → JWT guardado → navega a HOME o HOME_DELEGADO según rol
3. Errores del backend se muestran en pantalla

## Notas técnicas

- `10.0.2.2` = `localhost` del host desde el emulador Android
- JWT se guarda en SharedPreferences (`kidcare_prefs`) y está disponible via `SessionManager`
- El backend asigna rol `TUTOR` por defecto al registrar; `DELEGADO` debe ser vinculado por un tutor
