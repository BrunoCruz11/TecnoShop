package com.tecnoshop.api;

import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tecnoshop.excepciones.CredencialesInvalidasException;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
import com.tecnoshop.excepciones.NoExisteCompraException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.NoExisteResenaException;
import com.tecnoshop.excepciones.NoExisteUsuarioException;
import com.tecnoshop.excepciones.ProveedorConComprasException;
import com.tecnoshop.excepciones.SinPermisoException;
import com.tecnoshop.excepciones.UsuarioInactivoException;
import com.tecnoshop.util.Config;
import com.tecnoshop.util.HibernateUtil;

import io.javalin.Javalin;
import io.javalin.http.HttpResponseException;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.ValidationException;
import jakarta.persistence.OptimisticLockException;

/**
 * Levanta la API REST que consume el front (TecnoShop-front).
 * Las rutas solo traducen HTTP <-> controladores: la logica sigue en controlador y manejador.
 */
public final class ApiServidor {

    private static final Logger LOG = LoggerFactory.getLogger(ApiServidor.class);

    // rutas que se pueden usar sin haber iniciado sesion
    private static final Set<String> RUTAS_PUBLICAS = Set.of("/api/login", "/api/registro", "/api/salud");
    // rutas que puede usar cualquier usuario con sesion (la sesion, el catalogo y las reseñas); las demas son solo para ADMIN
    private static boolean esRutaDeUsuario(String ruta) {
        return ruta.equals("/api/sesion") || ruta.equals("/api/catalogo") || ruta.startsWith("/api/catalogo/") || ruta.startsWith("/api/resenas/");
    }

    private ApiServidor() {
    }

    public static Javalin iniciar(int puerto) {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            // solo los origenes configurados pueden llamar a la API desde un navegador
            if (!Config.CORS_ORIGENES.isEmpty()) {
                config.bundledPlugins.enableCors(cors -> cors.addRule(regla -> {
                    Config.CORS_ORIGENES.forEach(regla::allowHost);
                }));
            }
            if (Config.CONFIAR_EN_PROXY) {
                config.contextResolver.ip = ctx -> {
                    String ipReal = ctx.header("X-Real-IP");
                    return ipReal != null ? ipReal : ctx.req().getRemoteAddr();
                };
            }
            // LocalDate se envia como "2026-09-29" y no como un arreglo de numeros
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                // si el front manda un campo de mas (ej. usuarioId en una compra) se ignora en vez de fallar
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }));
        });

        app.before("/api/*", ctx -> {
            ctx.header("X-Content-Type-Options", "nosniff");
            ctx.header("Cache-Control", "no-store");
            // el navegador manda un OPTIONS antes de cada peticion con otro origen (CORS): no lleva token
            if (ctx.method().name().equals("OPTIONS") || RUTAS_PUBLICAS.contains(ctx.path())) {
                return;
            }
            Autenticacion.verificar(ctx);
            if (!esRutaDeUsuario(ctx.path())) {
                Autenticacion.exigirAdmin(ctx);
            }
        });

        // para que docker / el balanceador sepan si la app esta viva y la base responde
        app.get("/api/salud", ctx -> {
            boolean baseOk = HibernateUtil.baseDisponible();
            ctx.status(baseOk ? 200 : 503).json(Map.of("estado", baseOk ? "ok" : "sin base de datos"));
        });

        ProductoRutas.registrar(app);
        ProveedorRutas.registrar(app);
        UsuarioRutas.registrar(app);
        CompraRutas.registrar(app);
        ResenaRutas.registrar(app);

        registrarManejoDeErrores(app);

        app.events(eventos -> eventos.serverStopped(HibernateUtil::shutdown));
        // al apagar el contenedor (SIGTERM) se terminan las peticiones en curso y se cierra el pool de conexiones
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        return app.start(puerto);
    }

    // cada excepcion del dominio se devuelve como {"error": "mensaje"} con el codigo HTTP que corresponde
    private static void registrarManejoDeErrores(Javalin app) {
        app.exception(HttpResponseException.class, (e, ctx) -> {
            // 429 lo tira el limite de intentos de login, con un mensaje en ingles
            String mensaje = e.getStatus() == 429 ? "Demasiados intentos. Espera un minuto y volve a intentar" : e.getMessage();
            ctx.status(e.getStatus()).json(Map.of("error", mensaje));
        });

        app.exception(JacksonException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", "El cuerpo de la peticion no tiene el formato esperado")));

        // un id que no es numero (/api/productos/abc) o una fecha mal escrita (?desde=ayer)
        app.exception(ValidationException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", "Hay un parametro con formato invalido")));
        app.exception(DateTimeParseException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("error", "Las fechas tienen que tener el formato aaaa-mm-dd")));

        app.exception(Exception.class, (e, ctx) -> {
            Respuesta r = traducir(e);
            if (r.codigo() == 500) {
                // el detalle queda en el log del servidor; al cliente nunca se le muestra el stack trace
                LOG.error("Error no controlado en {} {}", ctx.method(), ctx.path(), e);
            }
            ctx.status(r.codigo()).json(Map.of("error", r.mensaje()));
        });
    }

    private record Respuesta(int codigo, String mensaje) {}

    private static Respuesta traducir(Exception e) {
        if (e instanceof NoExisteProductoException || e instanceof NoExisteProveedorException
                || e instanceof NoExisteUsuarioException || e instanceof NoExisteCompraException
                || e instanceof NoExisteResenaException) {
            return new Respuesta(404, e.getMessage());
        }
        if (e instanceof ExisteProductoException || e instanceof ExisteUsuarioException || e instanceof ProveedorConComprasException) {
            return new Respuesta(409, e.getMessage());
        }
        if (e instanceof CredencialesInvalidasException) {
            return new Respuesta(401, e.getMessage());
        }
        if (e instanceof UsuarioInactivoException || e instanceof SinPermisoException) {
            return new Respuesta(403, e.getMessage());
        }
        // el resto de las excepciones del dominio son datos invalidos (precio, compra vacia, estado, fechas...)
        if (e.getClass().getPackageName().equals("com.tecnoshop.excepciones")) {
            return new Respuesta(400, e.getMessage());
        }
        // errores de la base: vienen envueltos en otras excepciones, por eso se recorre la cadena de causas
        for (Throwable causa = e; causa != null; causa = causa.getCause()) {
            if (causa instanceof OptimisticLockException || causa instanceof org.hibernate.StaleStateException) {
                return new Respuesta(409, "Otra persona modifico estos datos al mismo tiempo. Recarga e intenta de nuevo");
            }
            if (causa instanceof org.hibernate.exception.ConstraintViolationException cv) {
                return traducirRestriccion(cv);
            }
        }
        return new Respuesta(500, "Error interno del servidor");
    }

    // las restricciones de V2__restricciones.sql, traducidas a un mensaje para el usuario
    private static Respuesta traducirRestriccion(org.hibernate.exception.ConstraintViolationException cv) {
        String nombre = cv.getConstraintName() == null ? "" : cv.getConstraintName().toLowerCase();
        if (nombre.contains("productos_codigo_unico")) return new Respuesta(409, "Ya existe un producto con el codigo dado");
        if (nombre.contains("usuarios_email_unico")) return new Respuesta(409, "Ya existe un usuario con el email dado");
        if (nombre.contains("detalle_producto_unico")) return new Respuesta(409, "Ese producto ya esta en la compra");
        if (nombre.contains("resenas_una_por_usuario")) return new Respuesta(409, "Ya publicaste una reseña de este producto");
        if (nombre.contains("productos_stock_check")) return new Respuesta(409, "La operacion dejaria el stock de un producto en negativo");
        if (nombre.contains("productos_precios_check")) return new Respuesta(400, "El precio de venta no puede ser menor al precio de compra");
        SQLException sql = cv.getSQLException();
        if (sql != null && "23503".equals(sql.getSQLState())) {
            return new Respuesta(409, "No se puede completar: el registro esta siendo usado por otros datos");
        }
        return new Respuesta(409, "Los datos no cumplen una restriccion de la base");
    }
}
