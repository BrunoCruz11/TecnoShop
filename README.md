# TecnoShop

Back de TecnoShop: API REST en Java 21 (Javalin + Hibernate + PostgreSQL) para gestionar productos, proveedores, compras y usuarios. El front está en `../TecnoShop-front`.

## Desarrollo

```
docker compose up -d                 # base de datos en localhost:5433
mvn compile exec:java                # API en http://localhost:8080/api
```

Y el front (desde `../TecnoShop-front`): `python3 -m http.server 5500` → <http://localhost:5500>.

En desarrollo todo tiene valores por defecto:
- Si la base no tiene usuarios, se crea `admin@tecnoshop.com` / `admin1234`.
- Se cargan 12 productos de ejemplo.

## Producción

Se levantan tres contenedores, y solo nginx queda expuesto:

- **db:** PostgreSQL. No publica ningún puerto.
- **backend:** la API. Tampoco publica puertos; corre como usuario sin privilegios.
- **frontend:** nginx. Sirve el front y reenvía `/api` al backend. Es el único que publica un puerto.

```
cp .env.example .env                 # completar: contraseñas, JWT_SECRET (openssl rand -base64 48), admin
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

La app queda en el puerto `PUERTO_WEB` (por defecto 80). El primer ingreso es con `ADMIN_EMAIL` / `ADMIN_PASSWORD`; los demás usuarios se crean desde la sección **Usuarios**.

**HTTPS:** este compose sirve HTTP. En un servidor público hay que ponerle adelante un proxy con certificado (por ejemplo Caddy o el balanceador del proveedor de hosting), porque si no las contraseñas viajan sin cifrar.

**Actualizar:** `git pull` y el mismo comando `up -d --build`. Las migraciones de la base corren solas al arrancar.

**Backup de la base:** `docker compose -f docker-compose.prod.yml exec db pg_dump -U tecnoshop tecnoshop_db > backup.sql`

## Configuración (variables de entorno)

| Variable | Qué hace | Por defecto en desarrollo |
|---|---|---|
| `APP_ENV` | `prod` exige las variables sensibles y apaga los valores de desarrollo | `dev` |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexión a PostgreSQL | la base de `docker-compose.yml` |
| `JWT_SECRET` | Clave que firma las sesiones (mínimo 32 caracteres en prod) | una fija de desarrollo |
| `JWT_HORAS` | Duración de la sesión | `8` |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Primer usuario, solo si no hay ninguno | `admin@tecnoshop.com` / `admin1234` |
| `CARGAR_DATOS` | Carga los productos de ejemplo | `true` (en prod `false`) |
| `CORS_ORIGENES` | Orígenes que pueden llamar a la API desde el navegador | `http://localhost:5500` (en prod ninguno) |
| `CONFIAR_EN_PROXY` | Usar `X-Real-IP` de nginx para el límite de intentos de login | `false` (en prod `true`) |
| `MOSTRAR_SQL` | Loguea las consultas SQL | `false` |
| `PORT` | Puerto de la API | `8080` |

## Base de datos

El esquema lo manejan las migraciones de Flyway en `src/main/resources/db/migration`, y Hibernate solo lo valida. Para cambiar una tabla:

1. Crear un archivo nuevo, por ejemplo `V3__agregar_columna.sql`. Nunca se editan las migraciones que ya corrieron.
2. Ajustar la entidad.

Si entidad y tabla no coinciden, la app no arranca y el log dice qué columna falta.

## API

Todas las rutas empiezan con `/api`. Salvo `POST /login` y `GET /salud`, todas piden el header `Authorization: Bearer <token>`. El token lo devuelve el login.

Los errores vuelven como `{"error": "mensaje"}` con estos códigos:

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos |
| 401 | Sin sesión, o la sesión venció |
| 403 | Usuario inactivo |
| 404 | No existe |
| 409 | Duplicado o conflicto |
| 429 | Demasiados intentos de login |
