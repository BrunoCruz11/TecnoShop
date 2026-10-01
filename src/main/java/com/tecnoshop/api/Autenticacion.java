package com.tecnoshop.api;

import java.time.Duration;
import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tecnoshop.controlador.IUsuarioControlador;
import com.tecnoshop.controlador.UsuarioControlador;
import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.enums.Rol;
import com.tecnoshop.util.Config;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

/**
 * Sesiones con JWT: al hacer login el back devuelve un token firmado con JWT_SECRET que dice
 * "este es el usuario N y vence a tal hora". El front lo manda en cada peticion en el header
 * Authorization: Bearer <token>. Como esta firmado, nadie puede fabricar uno ni cambiarle el usuario.
 */
public final class Autenticacion {

    private static final String EMISOR = "tecnoshop";
    private static final String ATRIBUTO_USUARIO = "usuario";

    private static final Algorithm ALGORITMO = Algorithm.HMAC256(Config.JWT_SECRET);
    private static final JWTVerifier VERIFICADOR = JWT.require(ALGORITMO).withIssuer(EMISOR).build();

    private Autenticacion() {
    }

    public static String crearToken(UsuarioDTO usuario) {
        Instant ahora = Instant.now();
        return JWT.create()
                .withIssuer(EMISOR)
                .withSubject(String.valueOf(usuario.getId()))
                .withIssuedAt(ahora)
                .withExpiresAt(ahora.plus(Duration.ofHours(Config.JWT_HORAS)))
                .sign(ALGORITMO);
    }

    // se ejecuta antes de cada peticion protegida: si el token no es valido corta con 401
    public static void verificar(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedResponse("Tenes que iniciar sesion");
        }
        int usuarioId;
        try {
            usuarioId = Integer.parseInt(VERIFICADOR.verify(header.substring(7)).getSubject());
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new UnauthorizedResponse("La sesion vencio o no es valida, volve a iniciar sesion");
        }
        // se consulta el usuario en cada peticion: si lo desactivaron (RF03), su token deja de servir en el momento
        IUsuarioControlador UC = UsuarioControlador.get();
        UsuarioDTO usuario = UC.obtenerUsuarioPorId(usuarioId);
        if (usuario == null || !usuario.isActivo()) {
            throw new UnauthorizedResponse("El usuario no existe o esta inactivo");
        }
        ctx.attribute(ATRIBUTO_USUARIO, usuario);
    }

    // corta con 403 si el usuario de la sesion no es administrador
    public static void exigirAdmin(Context ctx) {
        if (usuario(ctx).getRol() != Rol.ADMIN) {
            throw new ForbiddenResponse("Solo un administrador puede hacer esto");
        }
    }

    // el usuario que hizo la peticion (lo dejo verificar())
    public static UsuarioDTO usuario(Context ctx) {
        return ctx.attribute(ATRIBUTO_USUARIO);
    }
}
