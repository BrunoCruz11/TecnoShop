package com.tecnoshop.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.tecnoshop.controlador.IUsuarioControlador;
import com.tecnoshop.controlador.UsuarioControlador;
import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.enums.Rol;
import com.tecnoshop.excepciones.DatosInvalidosException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.util.NaiveRateLimit;

public final class UsuarioRutas {

    public record RegistroPeticion(String nombre, String email, String password) {}
    public record NuevoUsuarioPeticion(String nombre, String email, String password, Rol rol) {}
    public record LoginPeticion(String email, String password) {}
    public record ActivoPeticion(boolean activo) {}
    public record RolPeticion(Rol rol) {}

    private UsuarioRutas() {
    }

    public static void registrar(Javalin app) {
        IUsuarioControlador UC = UsuarioControlador.get();

        // publica: devuelve el token de sesion. Maximo 10 intentos por minuto por IP, para frenar ataques de fuerza bruta
        app.post("/api/login", ctx -> {
            NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);
            LoginPeticion p = ctx.bodyAsClass(LoginPeticion.class);
            responderConSesion(ctx, UC.login(p.email(), p.password()));
        });

        // publica: cualquiera puede crear su cuenta, pero siempre queda como USUARIO (solo ve el catalogo)
        app.post("/api/registro", ctx -> {
            NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);
            RegistroPeticion p = ctx.bodyAsClass(RegistroPeticion.class);
            UC.registrarUsuario(p.nombre(), p.email(), p.password(), true, Rol.USUARIO);
            // queda con la sesion iniciada, sin tener que volver a escribir la contraseña
            responderConSesion(ctx.status(201), UC.login(p.email(), p.password()));
        });

        // el front lo usa al abrir la pagina para saber si el token guardado sigue siendo valido
        app.get("/api/sesion", ctx -> ctx.json(Autenticacion.usuario(ctx)));

        // de aca para abajo, solo ADMIN (lo controla el filtro de ApiServidor)
        app.get("/api/usuarios", ctx -> ctx.json(UC.obtenerTodosLosUsuarios()));

        // el admin elige el rol; si no manda ninguno, queda como USUARIO
        app.post("/api/usuarios", ctx -> {
            NuevoUsuarioPeticion p = ctx.bodyAsClass(NuevoUsuarioPeticion.class);
            UC.registrarUsuario(p.nombre(), p.email(), p.password(), true, p.rol() != null ? p.rol() : Rol.USUARIO);
            ctx.status(201);
        });

        // RF02: baja logica
        app.put("/api/usuarios/{id}/activo", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            boolean activo = ctx.bodyAsClass(ActivoPeticion.class).activo();
            // si el unico admin se desactivara a si mismo, nadie podria volver a administrar
            if (!activo && esUnoMismo(ctx, id)) {
                throw new DatosInvalidosException("No podes desactivar tu propio usuario");
            }
            if (activo) {
                UC.activarUsuario(id);
            } else {
                UC.desactivarUsuario(id);
            }
            ctx.status(204);
        });

        app.put("/api/usuarios/{id}/rol", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            // mismo motivo: un admin no se puede quitar el rol a si mismo
            if (esUnoMismo(ctx, id)) {
                throw new DatosInvalidosException("No podes cambiar tu propio rol");
            }
            UC.cambiarRolUsuario(id, ctx.bodyAsClass(RolPeticion.class).rol());
            ctx.status(204);
        });
    }

    private static void responderConSesion(Context ctx, UsuarioDTO usuario) {
        ctx.json(Map.of("token", Autenticacion.crearToken(usuario), "usuario", usuario));
    }

    private static boolean esUnoMismo(Context ctx, int id) {
        return id == Autenticacion.usuario(ctx).getId();
    }
}
