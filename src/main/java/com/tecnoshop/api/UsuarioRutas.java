package com.tecnoshop.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.tecnoshop.controlador.IUsuarioControlador;
import com.tecnoshop.controlador.UsuarioControlador;
import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;

import io.javalin.Javalin;
import io.javalin.http.util.NaiveRateLimit;

public final class UsuarioRutas {

    public record RegistroPeticion(String nombre, String email, String password) {}
    public record LoginPeticion(String email, String password) {}
    public record ActivoPeticion(boolean activo) {}

    private UsuarioRutas() {
    }

    public static void registrar(Javalin app) {
        IUsuarioControlador UC = UsuarioControlador.get();

        // publica: devuelve el token de sesion. Maximo 10 intentos por minuto por IP, para frenar ataques de fuerza bruta
        app.post("/api/login", ctx -> {
            NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.MINUTES);
            LoginPeticion p = ctx.bodyAsClass(LoginPeticion.class);
            UsuarioDTO usuario = UC.login(p.email(), p.password());
            ctx.json(Map.of("token", Autenticacion.crearToken(usuario), "usuario", usuario));
        });

        // el front lo usa al abrir la pagina para saber si el token guardado sigue siendo valido
        app.get("/api/sesion", ctx -> ctx.json(Autenticacion.usuario(ctx)));

        app.get("/api/usuarios", ctx -> ctx.json(UC.obtenerTodosLosUsuarios()));

        // solo un usuario con sesion puede crear otro: no hay registro abierto al publico
        app.post("/api/usuarios", ctx -> {
            RegistroPeticion p = ctx.bodyAsClass(RegistroPeticion.class);
            UC.registrarUsuario(p.nombre(), p.email(), p.password(), true);
            ctx.status(201);
        });

        // RF02: baja logica
        app.put("/api/usuarios/{id}/activo", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            boolean activo = ctx.bodyAsClass(ActivoPeticion.class).activo();
            // si el unico usuario se desactivara a si mismo, nadie podria volver a entrar
            if (!activo && id == Autenticacion.usuario(ctx).getId()) {
                throw new DatosInvalidosException("No podes desactivar tu propio usuario");
            }
            if (activo) {
                UC.activarUsuario(id);
            } else {
                UC.desactivarUsuario(id);
            }
            ctx.status(204);
        });
    }
}
