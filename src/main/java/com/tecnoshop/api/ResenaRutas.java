package com.tecnoshop.api;

import java.util.Map;

import com.tecnoshop.controlador.IProductoControlador;
import com.tecnoshop.controlador.IResenaControlador;
import com.tecnoshop.controlador.ProductoControlador;
import com.tecnoshop.controlador.ResenaControlador;
import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.enums.Rol;

import io.javalin.Javalin;

// ficha de un producto del catalogo y sus reseñas: lo usa cualquier usuario con sesion
public final class ResenaRutas {

    public record ResenaPeticion(int puntaje, String comentario) {}

    private ResenaRutas() {
    }

    public static void registrar(Javalin app) {
        IProductoControlador PC = ProductoControlador.get();
        IResenaControlador RC = ResenaControlador.get();

        app.get("/api/catalogo/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ctx.json(Map.of("producto", PC.obtenerProductoCatalogo(id), "resenas", RC.obtenerResenasDeProducto(id)));
        });

        // la reseña queda a nombre del usuario de la sesion
        app.post("/api/catalogo/{id}/resenas", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ResenaPeticion p = ctx.bodyAsClass(ResenaPeticion.class);
            RC.publicarResena(id, Autenticacion.usuario(ctx).getId(), p.puntaje(), p.comentario());
            ctx.status(201);
        });

        app.delete("/api/resenas/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            UsuarioDTO usuario = Autenticacion.usuario(ctx);
            RC.eliminarResena(id, usuario.getId(), usuario.getRol() == Rol.ADMIN);
            ctx.status(204);
        });
    }
}
