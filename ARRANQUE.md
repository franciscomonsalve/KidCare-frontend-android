# ARRANQUE — KidCare Android App

Guía paso a paso para iniciar la aplicación móvil en un equipo nuevo.
Sigue los pasos en orden, sin saltarte ninguno.

---

## Antes de empezar — verifica que tienes todo instalado

### Android Studio

Descarga e instala Android Studio desde: https://developer.android.com/studio

Versión mínima recomendada: **Hedgehog (2023.1.1)** o superior.

Durante la instalación, asegúrate de que se instale también:
- Android SDK
- Android Virtual Device (AVD)

### JDK

Android Studio incluye su propio JDK. Si usas el JDK del sistema, debe ser versión **17 o superior**.

### Git

```bash
git --version
```

Cualquier versión sirve. Si no lo tienes: https://git-scm.com

---

## Paso 1 — Obtener el código

Si ya tienes el repositorio clonado:

```bash
cd KidCare-frontend-android
git fetch origin
git checkout benja
git pull origin benja
```

Si es la primera vez:

```bash
git clone https://github.com/franciscomonsalve/KidCare-frontend-android.git
cd KidCare-frontend-android
git checkout benja
```

---

## Paso 2 — Verificar que los 4 microservicios están corriendo

La app necesita los 4 backends activos. Antes de abrir la app, confirma en una terminal:

**Windows PowerShell:**
```powershell
# Cada uno debe responder (aunque sea con error 403 — eso significa que está vivo)
Invoke-WebRequest -Uri "http://localhost:8081/api/auth/login" -Method POST -ErrorAction SilentlyContinue
Invoke-WebRequest -Uri "http://localhost:8082/api/acceso" -ErrorAction SilentlyContinue
Invoke-WebRequest -Uri "http://localhost:8083/api/interacciones/menor/1" -ErrorAction SilentlyContinue
Invoke-WebRequest -Uri "http://localhost:8084/api/historial/menor/1" -ErrorAction SilentlyContinue
```

**Mac / Linux:**
```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api/auth/login -X POST
curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/api/acceso
curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/api/interacciones/menor/1
curl -s -o /dev/null -w "%{http_code}" http://localhost:8084/api/historial/menor/1
```

Si alguno no responde, inicia ese microservicio primero siguiendo su `ARRANQUE.md`.

---

## Paso 3 — Abrir el proyecto en Android Studio

1. Abre Android Studio.
2. Selecciona **Open** (no "New Project").
3. Navega hasta la carpeta `KidCare-frontend-android` y selecciónala.
4. Espera a que Gradle sincronice el proyecto (puede tardar 2–5 minutos la primera vez). Verás una barra de progreso en la parte inferior.
5. Cuando aparezca **"Gradle sync finished"** en la barra inferior, el proyecto está listo.

---

## Paso 4 — Configurar la dirección IP del backend

Abre el archivo `app/src/main/java/com/example/kidcare/data/network/ApiClient.kt`.

Busca la línea con la IP del backend:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8081/"
```

- Si usas el **emulador de Android Studio**: deja `10.0.2.2` — esta IP apunta al `localhost` de tu computadora desde el emulador.
- Si usas un **dispositivo físico (celular real)**: reemplaza `10.0.2.2` por la IP de tu computadora en la red local (por ejemplo `192.168.1.100`). Para conocer tu IP: en Windows ejecuta `ipconfig`, en Mac/Linux ejecuta `ifconfig`.

---

## Paso 5 — Crear el emulador (si no tienes uno)

1. En Android Studio, ve al menú **Tools → Device Manager**.
2. Haz clic en **Create Virtual Device**.
3. Selecciona **Phone → Pixel 6** (o cualquier teléfono de la lista).
4. Haz clic en **Next**.
5. Selecciona la imagen del sistema: **API 33 (Android 13)** o superior. Si no está descargada, haz clic en el ícono de descarga junto a ella y espera.
6. Haz clic en **Next → Finish**.

---

## Paso 6 — Iniciar la app en el emulador

1. En la barra superior de Android Studio, selecciona el emulador que creaste en el selector de dispositivos (al lado del botón verde de play).
2. Haz clic en el botón **Run ▶** (ícono de triángulo verde) o presiona **Shift+F10**.
3. Android Studio compilará la app e instalará el APK en el emulador (puede tardar 1–3 minutos la primera vez).
4. El emulador se abrirá y la app KidCare aparecerá en pantalla.

---

## Paso 7 — Probar el flujo básico

### Registro
1. En la pantalla de login, toca **"¿No tienes cuenta? Regístrate"**.
2. Completa el formulario con nombre, email y contraseña.
3. Selecciona el rol **TUTOR**.
4. Acepta los términos y toca **Registrarse**.
5. Si el registro es exitoso, verás la pantalla principal (Home).

### Login
1. En la pantalla de login, ingresa el email y contraseña del usuario registrado.
2. Toca **Iniciar sesión**.
3. Deberías ver la pantalla principal con el menú inferior.

### Recuperar contraseña
1. En la pantalla de login, toca **"¿Olvidaste tu contraseña?"**.
2. Ingresa tu email y toca **Enviar código**.
3. El token aparecerá en la **consola del servidor** del usuario-service (modo desarrollo).
4. Copia ese token, ingrésalo en la app junto con tu nueva contraseña y toca **Restablecer**.

---

## Solución de problemas frecuentes

### La app muestra "Error de conexión" o no carga datos
- Verifica que los 4 microservicios están corriendo (Paso 2).
- Si usas emulador, confirma que la IP es `10.0.2.2` en `ApiClient.kt`.
- Si usas dispositivo físico, confirma que el celular y la computadora están en la misma red WiFi y que la IP en `ApiClient.kt` es la IP de la computadora.

### Gradle sync falla al abrir el proyecto
- Verifica tu conexión a internet (Gradle descarga dependencias la primera vez).
- Ve a **File → Invalidate Caches → Invalidate and Restart**.

### El emulador no aparece o no arranca
- Verifica que tienes habilitada la virtualización en tu BIOS (Intel VT-x o AMD-V).
- En Windows: abre **Task Manager → Performance → CPU** y verifica que "Virtualization: Enabled".

### Error de compilación en Android Studio
- Ve a **Build → Clean Project** y luego **Build → Rebuild Project**.
- Si el error persiste, revisa la pestaña **Build** en la parte inferior para ver el detalle.

### La pantalla queda en blanco o carga infinita
- Abre **Logcat** en Android Studio (pestaña inferior) y filtra por `KidCare` o `ERROR` para ver qué está fallando.
