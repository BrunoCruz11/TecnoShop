# TecnoShop — Back

API REST para gestionar el stock y las compras de una tienda de tecnología: productos, proveedores, compras y usuarios, más un catálogo con reseñas para los clientes.

Hecha en **Java 21** con **Javalin** (servidor web), **Hibernate** (acceso a la base) y **PostgreSQL**.

El front está en otro repositorio: [TecnoShop-front](https://github.com/BrunoCruz11/TecnoShop-front).

---

## 1. Qué necesitás instalar

| Programa | Para qué | Cómo verificar que lo tenés |
|---|---|---|
| [Git](https://git-scm.com/downloads) | Descargar el código | `git --version` |
| [Java 21 (JDK)](https://adoptium.net/) | Compilar y correr el back | `java -version` (tiene que decir 21) |
| [Maven](https://maven.apache.org/download.cgi) | Compilar el proyecto y bajar las librerías | `mvn -version` |
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Correr la base de datos | `docker --version` |
| Python 3 | Servir el front en desarrollo (en Mac y Linux ya viene) | `python3 --version` |

---

## 2. Descargar el proyecto

El back y el front van **en la misma carpeta, uno al lado del otro**:

```
mkdir tecnoshop && cd tecnoshop
git clone https://github.com/BrunoCruz11/TecnoShop.git
git clone https://github.com/BrunoCruz11/TecnoShop-front.git
```

Queda así:

```
tecnoshop/
├── TecnoShop/          ← este repo (back)
└── TecnoShop-front/    ← el front
```

---

## 3. Levantar todo en tu compu (desarrollo)

Se usan **dos terminales**, una para el back y otra para el front.

### Paso 1: abrir Docker Desktop

Abrí la aplicación y esperá a que diga que está corriendo (*Engine running*).

### Paso 2: levantar la base de datos

```
cd TecnoShop
docker compose up -d
```

Queda corriendo en segundo plano, en `localhost:5433`. La primera vez tarda un poco porque descarga la imagen de PostgreSQL.

### Paso 3: levantar el back (terminal 1)

```
cd TecnoShop
mvn compile exec:java
```

La primera vez Maven descarga las librerías y tarda un par de minutos. Está listo cuando aparece:

```
API escuchando en el puerto 8080
```

**Dejá esta terminal abierta**: si la cerrás, se apaga el back.

Al arrancar por primera vez, el back hace esto solo:
- crea las tablas de la base;
- crea el usuario administrador `admin@tecnoshop.com` con contraseña `admin1234`;
- carga datos de ejemplo: 12 productos con imagen, 5 proveedores y 7 compras (5 confirmadas, 1 pendiente y 1 cancelada).

Las compras de ejemplo solo se cargan si la base no tiene ninguna compra, porque confirmarlas suma stock.

### Paso 4: levantar el front (terminal 2)

```
cd TecnoShop-front
python3 -m http.server 5500
```

### Paso 5: entrar

Abrí **http://localhost:5500** e ingresá con:

- **Email:** `admin@tecnoshop.com`
- **Contraseña:** `admin1234`

Para probar como cliente, tocá **Crear cuenta** en el login: esas cuentas son de usuario normal y solo ven el catálogo.

### Para apagar todo

- **Back y front:** `Ctrl + C` en cada terminal.
- **Base de datos:** `docker compose down`. Los datos quedan guardados para la próxima vez.

> Ojo: `docker compose down -v` **borra la base de datos**. Usalo solo si querés empezar de cero.

---

## 4. Ver la base de datos

Para entrar a la consola de PostgreSQL:

```
docker exec -it tecnoshop-postgres psql -U tecnoshop -d tecnoshop_db
```

Ahí escribís SQL (siempre terminado en `;`):

```sql
SELECT * FROM productos;
SELECT * FROM compras;
```

Para salir: `\q`.

Las tablas son `usuarios`, `proveedores`, `productos`, `compras`, `detalle_compras` y `resenas`. Las columnas van en minúscula, por ejemplo `stockminimo`.

---

## 5. Problemas comunes

| Problema | Solución |
|---|---|
| `Cannot connect to the Docker daemon` | Docker Desktop no está abierto. Abrilo y esperá a que arranque. |
| `Address already in use` / puerto 8080 o 5500 ocupado | Ya tenés el back o el front corriendo en otra terminal. Cerralo con `Ctrl + C`. |
| El back no arranca y dice `Connection refused` | La base no está levantada: `docker compose up -d`. |
| El front dice "No se pudo conectar con el servidor" | El back no está corriendo (paso 3). |
| Me olvidé la contraseña del admin | Solo se crea si la base no tiene usuarios. Para empezar de cero: `docker compose down -v` y `docker compose up -d` (se borra todo). |
| `java -version` no dice 21 | Instalá el JDK 21 y revisá que sea el que usa la terminal. |

---

## 6. Cómo está organizado el código

```
src/main/java/com/tecnoshop/
├── Main.java          arranca todo
├── api/               rutas HTTP: reciben el JSON, llaman al controlador y devuelven la respuesta
├── controlador/       reglas de negocio y validaciones (RF01 – RF17)
├── manejador/         acceso a la base de datos (Hibernate)
├── model/             entidades (tablas)
├── dto/               datos que se devuelven al front
├── enums/             EstadoCompra (PENDIENTE, CONFIRMADA, CANCELADA) y Rol (ADMIN, USUARIO)
├── excepciones/       errores del dominio (NoExisteProducto, PrecioInvalido, ...)
└── util/              configuración, validaciones, conexión y datos iniciales

src/main/resources/db/migration/   scripts SQL que crean y actualizan las tablas (Flyway)
```

Una petición recorre este camino:

**front → `api/` → `controlador/` → `manejador/` → base de datos**

Los requisitos funcionales están en [REQUISITOS_FUNCIONALES.md](REQUISITOS_FUNCIONALES.md).

---

## 7. La API

Todas las rutas empiezan con `/api`. Hay que **iniciar sesión** para usarlas:

1. `POST /api/login` con `{"email": "...", "password": "..."}` devuelve un `token`. `POST /api/registro` crea una cuenta y también devuelve un token.
2. En cada petición se manda el header `Authorization: Bearer <token>`.

Hay dos roles, y un admin puede cambiar el rol de los demás desde la sección Usuarios:

| Rol | Qué puede usar |
|---|---|
| Cualquiera, sin sesión | `POST /login`, `POST /registro`, `GET /salud` |
| **USUARIO** (cuentas creadas con "Crear cuenta") | `GET /sesion`, el catálogo y las reseñas. El catálogo no muestra el precio de compra ni el stock exacto. |
| **ADMIN** | Todas las rutas |

| Recurso | Rutas |
|---|---|
| Productos | `GET /productos` (incluye a qué proveedores se le compró cada producto, según las compras confirmadas), `GET /productos/por-acabar`, `GET /productos/{id}`, `POST /productos`, `PUT /productos/{id}`, `PUT /productos/{id}/disponible` |
| Proveedores | `GET /proveedores`, `GET /proveedores/{id}`, `POST /proveedores`, `PUT /proveedores/{id}`, `DELETE /proveedores/{id}` |
| Compras | `GET /compras` (filtros `?usuarioId=`, `?proveedorId=`, `?desde=&hasta=`), `GET /compras/{id}`, `GET /compras/{id}/detalles`, `POST /compras`, `POST /compras/{id}/detalles`, `PUT /compras/{id}/confirmar`, `PUT /compras/{id}/cancelar` |
| Catálogo | `GET /catalogo`, `GET /catalogo/{id}` (ficha con reseñas), `POST /catalogo/{id}/resenas` (`{"puntaje": 1-5, "comentario": "..."}`; una por usuario: si ya tenía una, se reemplaza), `DELETE /resenas/{id}` (la propia; el admin puede borrar cualquiera) |
| Usuarios | `GET /usuarios`, `POST /usuarios`, `PUT /usuarios/{id}/activo`, `PUT /usuarios/{id}/rol`, `GET /sesion` |

Los errores vuelven como `{"error": "mensaje"}` con este código HTTP:

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos |
| 401 | Sin sesión, o la sesión venció |
| 403 | Usuario inactivo, o la acción es solo para administradores |
| 404 | No existe |
| 409 | Duplicado o conflicto |
| 429 | Demasiados intentos de login |

---

## 8. Subirlo a un servidor (producción)

En producción se levantan tres contenedores con un solo comando:

- **db:** PostgreSQL. No queda expuesta a internet.
- **backend:** la API. Tampoco queda expuesta; corre sin permisos de administrador.
- **frontend:** nginx. Es lo único expuesto: sirve el front y reenvía `/api` al back.

### Paso 1: clonar los dos repos en el servidor, uno al lado del otro (igual que en el punto 2)

### Paso 2: crear el archivo de configuración

```
cd TecnoShop
cp .env.example .env
```

Abrí `.env` y completá:

- **`DB_PASSWORD`:** una contraseña larga para la base.
- **`JWT_SECRET`:** la clave que firma las sesiones. Generala con `openssl rand -base64 48`.
- **`ADMIN_EMAIL` y `ADMIN_PASSWORD`:** el primer usuario, para poder entrar.

> El archivo `.env` tiene contraseñas: **nunca lo subas a git** (ya está en `.gitignore`).

### Paso 3: levantar

```
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

La app queda en el puerto 80 del servidor, o en el que pongas en `PUERTO_WEB`.

**Importante: HTTPS.** Este compose sirve HTTP. Si el servidor es público, ponele adelante un proxy con certificado, por ejemplo [Caddy](https://caddyserver.com/). Sin eso, las contraseñas viajan sin cifrar.

### Actualizar a una versión nueva

```
git pull
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

Si la versión nueva trae cambios en las tablas, se aplican solos al arrancar.

### Backup de la base

```
docker compose -f docker-compose.prod.yml exec db pg_dump -U tecnoshop tecnoshop_db > backup.sql
```

---

## 9. Configuración (variables de entorno)

En desarrollo no hace falta configurar nada. En producción se definen en el archivo `.env`.

| Variable | Qué hace | En desarrollo |
|---|---|---|
| `APP_ENV` | Con `prod`, exige las variables sensibles | `dev` |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexión a PostgreSQL | la base de `docker-compose.yml` |
| `JWT_SECRET` | Clave que firma las sesiones (mínimo 32 caracteres en prod) | una fija de desarrollo |
| `JWT_HORAS` | Cuánto dura la sesión | `8` |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Primer usuario (solo si no hay ninguno) | `admin@tecnoshop.com` / `admin1234` |
| `CARGAR_DATOS` | Cargar los productos, proveedores y compras de ejemplo | `true` |
| `CORS_ORIGENES` | Qué sitios pueden llamar a la API desde el navegador | `http://localhost:5500` |
| `CONFIAR_EN_PROXY` | Usar la IP real que manda nginx (para el límite de intentos de login) | `false` |
| `MOSTRAR_SQL` | Mostrar las consultas SQL en la consola | `false` |
| `PORT` | Puerto de la API | `8080` |

---

## 10. Cambiar las tablas de la base

Las tablas las crean los scripts de `src/main/resources/db/migration`, que se ejecutan solos al arrancar. Hibernate **no** modifica las tablas; solo revisa que coincidan con las entidades.

Para agregar o cambiar una columna:

1. Creá un archivo nuevo con el número siguiente, por ejemplo `V5__agregar_columna.sql`, con el `ALTER TABLE`.
2. Actualizá la entidad en `model/`.

**Nunca edites un script que ya corrió**: siempre se crea uno nuevo.

Si la entidad y la tabla no coinciden, el back no arranca y el error dice qué columna falta.
