# Prestamos Bancarios - Sistema de Gestion de Prestamos

Sistema completo para la gestion de prestamos bancarios con arquitectura limpia (Clean Architecture), backend en Spring Boot y frontend en React.

## Tecnologias Utilizadas

### Backend
| Tecnologia | Version | Uso |
|---|---|---|
| Java | 21 | Lenguaje de programacion |
| Spring Boot | 4.1.0 | Framework principal |
| Spring Security | 7.x | Autenticacion JWT y autorizacion |
| Spring Data JPA | - | Persistencia con Hibernate |
| MySQL | - | Base de datos relacional |
| Lombok | - | Reduccion de boilerplate |
| MapStruct | 1.6.3 | Mapeo DTO - Entity |
| JJWT | 0.12.6 | Generacion y validacion de tokens JWT |
| Logback | - | Logging (incluido con Spring Boot) |
| SpringDoc OpenAPI | 2.8.6 | Documentacion Swagger UI |
| Spring Cache | - | Cache de consultas repetitivas |
| BCrypt | - | Encriptacion de contrasenas |
| JUnit 5 + Mockito | - | Pruebas unitarias |
| H2 Database | - | Base de datos en memoria (tests) |
| Maven | - | Gestor de dependencias |

### Frontend
| Tecnologia | Version | Uso |
|---|---|---|
| React | 18.3.1 | Framework de UI |
| Vite | 5.4.21 | Bundler y dev server |
| React Router | 6.28.0 | Enrutamiento SPA |
| Axios | 1.7.9 | Cliente HTTP |

### Arquitectura
```
com.Sistema.PrestamosBancarios/
├── config/          → SecurityConfig, CorsConfig, CacheConfig, OpenApiConfig, DataInitializer
├── controller/      → AuthController, LoanController (REST endpoints)
├── dto/             → request/ y response/ (objetos de transferencia)
├── exception/       → GlobalExceptionHandler y excepciones personalizadas
├── mapper/          → UserMapper, LoanMapper (Entity <-> DTO)
├── model/           → User, Loan, Role, Status (entidades JPA)
├── repository/      → UserRepository, LoanRepository (Spring Data)
├── security/        → JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
├── service/         → AuthService, LoanService (logica de negocio)
└── cache/           → Configuracion de cache
```

## Requisitos Previos

- **Java 21** o superior
- **Node.js 18+** y npm
- **MySQL 8.x** corriendo en `localhost:3306`
- **Maven** (incluido via wrapper `mvnw`)

## Instalacion y Ejecucion

### 1. Base de Datos

Crear la base de datos MySQL:

```sql
CREATE DATABASE prestamos_db;
```

Por defecto la aplicacion se conecta con:
- **Usuario:** `root`
- **Password:** `root`
- **URL:** `jdbc:mysql://localhost:3306/prestamos_db`

Si necesitas cambiar estas credenciales, edita `Backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/prestamos_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

> Las tablas se crean automaticamente al iniciar la aplicacion (`ddl-auto=update`).

### 2. Backend (Spring Boot)

```bash
cd Backend

# Compilar el proyecto
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run
```

El backend estara disponible en: **http://localhost:8080**

**Endpoints:**
| Metodo | URL | Descripcion | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Registrar usuario | No |
| POST | `/api/auth/login` | Iniciar sesion | No |
| GET | `/api/auth/me` | Perfil del usuario actual | Si |
| POST | `/api/loans` | Solicitar prestamo | Si |
| GET | `/api/loans/my-loans` | Mis prestamos | Si |
| GET | `/api/loans/{id}` | Prestamo por ID | Si (propietario/Admin) |
| GET | `/api/loans` | Todos los prestamos | Admin |
| PUT | `/api/loans/{id}/approve` | Aprobar prestamo | Admin |
| PUT | `/api/loans/{id}/reject` | Rechazar prestamo | Admin |

**Swagger UI:** http://localhost:8080/swagger-ui.html

### 3. Frontend (React)

```bash
cd Frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

El frontend estara disponible en: **http://localhost:5173**

> El frontend usa un proxy de Vite que redirige `/api` al backend en `localhost:8080`.

### 4. Credenciales de Prueba

La aplicacion crea dos usuarios automaticamente al iniciar:

| Rol | Email | Contrasena |
|---|---|---|
| **Admin** | `admin@test.com` | `123` |
| **Usuario** | `usuario@test.com` | `123` |

### 5. Pruebas Unitarias

```bash
cd Backend
./mvnw test
```

Las pruebas usan H2 (base de datos en memoria) y no requieren MySQL.

## Funcionalidades

### Usuario
- Registrarse e iniciar sesion con JWT
- Solicitar un prestamo (monto y plazo en meses)
- Ver historial de prestamos con estado (Pendiente/Aprobado/Rechazado)

### Administrador
- Ver todas las solicitudes de prestamos
- Filtrar por estado
- Aprobar o rechazar solicitudes pendientes
- Auditoria de acciones con timestamps

### Seguridad
- Autenticacion stateless con JWT
- BCrypt para encriptacion de contrasenas
- Roles: `USER` y `ADMIN`
- Solo administradores pueden aprobar/rechazar prestamos
- CORS habilitado para el frontend

### Logging
- Logs en consola con nivel DEBUG para: app, Spring Security, SQL de Hibernate
- Cada endpoint, autenticacion y operacion de prestamo queda registrado
- Los errores muestran stack trace completo

## Estructura del Proyecto

```
PrestamosBancarios/
├── Backend/
│   ├── src/main/java/...    → Codigo fuente
│   ├── src/main/resources/  → Configuracion (application.properties, logback-spring.xml)
│   ├── src/test/java/...    → Pruebas unitarias
│   ├── pom.xml              → Dependencias Maven
│   └── mvnw                 → Maven wrapper
├── Frontend/
│   ├── src/
│   │   ├── components/      → Navbar, PrivateRoute, AdminRoute
│   │   ├── context/         → AuthContext (estado global)
│   │   ├── pages/           → Login, Register, UserDashboard, AdminDashboard
│   │   ├── services/        → api.js, authService.js, loanService.js
│   │   └── index.css        → Estilos
│   ├── vite.config.js       → Configuracion Vite + proxy API
│   └── package.json
└── Instructions.md          → Requerimientos del proyecto
```
