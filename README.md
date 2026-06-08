# RHApp - Sistema de Control de Recursos Humanos (Android)

Aplicación móvil desarrollada en Kotlin y Jetpack Compose para la gestión integral de recursos humanos. Consume un backend en Django REST Framework conectado a una base de datos PostgreSQL.

## 📱 Descripción de la Aplicación

RHApp es una solución móvil empresarial para gestionar empleados, departamentos, puestos de trabajo, nóminas y asistencias. Ofrece una interfaz premium con soporte para dos perfiles de usuario bien definidos:

- **Usuarios Básicos (Empleados):** Pueden visualizar su perfil, registrar asistencia y consultar su historial de nóminas.
- **Administradores (Staff):** Tienen acceso al Panel de Administración (Dashboard), donde pueden realizar operaciones CRUD completas en las 5 entidades principales del sistema.

---

## 🛠️ Arquitectura y Tecnologías

La aplicación móvil utiliza los estándares modernos de desarrollo de Android:

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Declarativa, moderna y fluida)
- **Arquitectura:** MVVM (Model-View-ViewModel) con Clean Architecture básica
- **Inyección de Dependencias:** Hilt (Dagger Hilt)
- **Red / API REST:** Retrofit 2 + OkHttp 3 con serialización JSON (Gson)
- **Asincronía:** Kotlin Coroutines & Flow para manejo de flujos de datos reactivos
- **Manejo de Estados:** StateFlow para representar estados de Carga, Éxito y Error de forma robusta
- **Almacenamiento Local Seguro:** Jetpack DataStore Preferences para la persistencia del token de sesión (JWT) y el perfil del usuario

---

## 📋 Entidades Implementadas

La aplicación gestiona de forma interactiva 5 entidades del backend:

### 1. Departamentos

Permite organizar la estructura interna de la empresa.

- **Campos:** Código único, Nombre, Descripción, Presupuesto Anual, ID del Jefe (Empleado) y Estado (Activo/Inactivo).
- **Funcionalidades:** Búsqueda local, listado con total de empleados por departamento, activación/desactivación optimista, creación y edición desde Bottom Sheet, y diálogo inteligente de eliminación (sugiere desactivar si tiene empleados vinculados).

### 2. Puestos

Define los cargos y perfiles requeridos.

- **Campos:** Código único, Título del cargo, Descripción, Requisitos, Salario Base, Salario Máximo, Departamento asignado y Estado (Activo/Inactivo).
- **Funcionalidades:** Visualización de rangos salariales, filtro de departamento, creación y actualización con selector dinámico de departamentos activos.

### 3. Empleados

El núcleo de la gestión de personal.

- **Campos:** Cédula, Nombre, Apellido, Email, Teléfono, Dirección, Fecha de Nacimiento, Salario Actual, Tipo de Contrato (Indefinido, Temporal, etc.), Estado, Puesto y Supervisor (opcional).
- **Funcionalidades:** Búsqueda avanzada por nombre/cédula, filtros rápidos por estado y tipo de contrato, formulario completo con validaciones y dropdowns dinámicos.

### 4. Nóminas

Control de pagos mensuales para el personal.

- **Campos:** Empleado asignado, Mes, Año, Salario Base, Bono, Descuento por Aportes, Descuento por Impuestos, Salario Neto, Estado (Pendiente, Pagado, Anulado) y Fechas de registro/pago.
- **Funcionalidades:** Cálculo automático de salario neto, cambio de estado optimista desde la lista, formulario de generación, filtros por mes y año, y vista detallada en tarjeta expandida.

### 5. Asistencias

Registro diario de entradas y salidas.

- **Campos:** Empleado, Fecha, Hora de Entrada, Hora de Salida y Estado de Asistencia (Presente, Tarde, Falta, Licencia).
- **Funcionalidades:** Registro rápido, visualización organizada en tarjeta con colores de estado, filtros por fecha y búsqueda por empleado.

---

## 🖥️ Listado de Pantallas

1.  **Pantalla de Login:** Permite autenticar a los usuarios mediante credenciales seguras (JWT).
2.  **Home / Dashboard del Empleado:** Panel principal para usuarios normales, mostrando accesos a su perfil, asistencias y nóminas.
3.  **Mi Perfil:** Visualización de los datos del usuario logueado con opción de cierre de sesión seguro.
4.  **Panel de Administración (Admin Dashboard):** Estadísticas clave de la aplicación (total de empleados, activos/inactivos, distribución por departamento).
5.  **CRUD Departamentos:** Listado interactivo, búsqueda, formulario flotante y borrado.
6.  **CRUD Puestos:** Listado, formulario con selección de departamento y edición.
7.  **CRUD Empleados:** Gestión detallada de la nómina de empleados, filtrado avanzado por tipo de contrato y estado.
8.  **CRUD Nóminas:** Generación y consulta de nóminas por mes/año y actualización rápida del estado del pago.
9.  **CRUD Asistencias:** Control de entradas, salidas y tardanzas del personal.

---

## ⚙️ Configuración y Requisitos de Instalación

### Requisitos Previos

- **Android Studio:** Jellyfish / Koala o superior.
- **JDK:** Versión 17 configurada en el proyecto.
- **SDK de Android:** API Nivel 26 (Android 8.0) como mínimo, API Nivel 35 como target.
- **Conectividad:** Acceso de red al backend Django REST Framework.

### Configuración de la URL Base del Backend

La URL del backend se define mediante la clave `API_BASE_URL` en el archivo `local.properties` ubicado en la raíz del proyecto.

1.  Crea o edita el archivo `local.properties` en la raíz del proyecto:

    ```properties
    # local.properties
    sdk.dir=C\:\\Users\\Usuario\\AppData\\Local\\Android\\Sdk
    API_BASE_URL=http://10.0.2.2:8000/api/
    ```

    > [!NOTE]
    > La dirección IP `10.0.2.2` es el alias que utiliza el Emulador de Android para referirse a la máquina host (`localhost` de tu computadora donde corre el backend). Si pruebas en un dispositivo físico, cambia esta dirección por la IP de tu red local (ej. `http://192.168.1.100:8000/api/`).

2.  Sincroniza el proyecto con los archivos de Gradle. El script `app/build.gradle.kts` cargará este valor de forma dinámica a `BuildConfig.API_BASE_URL`.

---

## 🚀 Instrucciones para Ejecutar la Aplicación

### Opción A: Desde Android Studio (Recomendado)

1.  Abre el proyecto `RHApp` en **Android Studio**.
2.  Inicia un dispositivo virtual (Emulador) desde el Device Manager, o conecta un teléfono físico con la depuración USB habilitada.
3.  Presiona el botón **Run** ▶️ (o la combinación `Shift + F10`).

### Opción B: Desde la Terminal (Compilación manual del APK)

1.  Abre una terminal (PowerShell o CMD) en la raíz del proyecto.
2.  Ejecuta el siguiente comando para compilar el APK en modo debug:
    ```bash
    ./gradlew assembleDebug
    ```
3.  Una vez finalizada la compilación, el archivo APK generado se encontrará en la siguiente ruta:
    `app/build/outputs/apk/debug/app-debug.apk`
4.  Instálalo en tu emulador o dispositivo físico arrastrándolo o usando ADB:
    ```bash
    adb install app/build/outputs/apk/debug/app-debug.apk
    ```

---

## 🔐 Credenciales de Prueba

Para validar las funciones y el control de accesos basados en roles, utiliza los siguientes usuarios de prueba:

| Rol                 | Usuario (Username) | Contraseña                              | Permisos                                                                                         |
| ------------------- | ------------------ | --------------------------------------- | ------------------------------------------------------------------------------------------------ |
| **Administrador**   | `admin2`           | `admin123` _(o contraseña configurada)_ | Acceso al panel de administración, CRUD de las 5 entidades.                                      |
| **Empleado Básico** | `empleado`         | `empleado123`                           | Acceso a consulta de perfil propio, registro de asistencias, visualización de nóminas asignadas. |

---

## 🌐 Ejemplos de Consumo de la API con Token

### 1. Autenticación (Login)

- **Endpoint:** `POST /api/auth/login/`
- **Request:**
  ```bash
  curl -X POST http://10.0.2.2:8000/api/auth/login/ \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "admin123"}'
  ```
- **Response:**
  ```json
  {
    "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@empresa.com",
      "is_staff": true
    }
  }
  ```

### 2. Consulta Protegida (Listar Empleados)

- **Endpoint:** `GET /api/empleados/`
- **Request:**
  ```bash
  curl -X GET http://10.0.2.2:8000/api/empleados/ \
    -H "Authorization: Bearer <ACCESS_TOKEN>"
  ```

### 3. Refrescar Token

- **Endpoint:** `POST /api/auth/token/refresh/`
- **Request:**
  ```bash
  curl -X POST http://10.0.2.2:8000/api/auth/token/refresh/ \
    -H "Content-Type: application/json" \
    -d '{"refresh": "<REFRESH_TOKEN>"}'
  ```

---

## 📸 Capturas de Pantalla y Demostración

> [!TIP]
> Puedes almacenar tus capturas en una carpeta llamada `/screenshots` en la raíz del proyecto para vincularlas de la siguiente forma:

- **Pantalla de Login:**
  `![Login](screenshots/login.png)`
- **Panel de Administración (Stats):**
  `![Admin Dashboard](screenshots/admin_dashboard.png)`
- **Listado de Departamentos y Formulario:**
  `![Departamentos CRUD](screenshots/departamentos.png)`
- **Listado de Empleados:**
  `![Empleados CRUD](screenshots/empleados.png)`
- **Gestión de Nóminas:**
  `![Nóminas CRUD](screenshots/nominas.png)`
