package com.tecnoshop.api;

import com.tecnoshop.controlador.IProveedorControlador;
import com.tecnoshop.controlador.ProveedorControlador;
import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.excepciones.NoExisteProveedorException;

import io.javalin.Javalin;

public final class ProveedorRutas {

    public record ProveedorPeticion(int telefono, String nombre, String email) {}

    private ProveedorRutas() {
    }

    public static void registrar(Javalin app) {
        IProveedorControlador PC = ProveedorControlador.get();

        app.get("/api/proveedores", ctx -> ctx.json(PC.obtenerTodosLosProveedores()));

        app.get("/api/proveedores/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ProveedorDTO proveedor = PC.obtenerProveedorPorId(id);
            if (proveedor == null) {
                throw new NoExisteProveedorException("No existe un proveedor con el id dado");
            }
            ctx.json(proveedor);
        });

        app.post("/api/proveedores", ctx -> {
            ProveedorPeticion p = ctx.bodyAsClass(ProveedorPeticion.class);
            PC.registrarProveedor(p.telefono(), p.nombre(), p.email());
            ctx.status(201);
        });

        app.put("/api/proveedores/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ProveedorPeticion p = ctx.bodyAsClass(ProveedorPeticion.class);
            PC.cambiarNombreProveedor(id, p.nombre());
            PC.cambiarTelefonoProveedor(id, p.telefono());
            PC.cambiarEmailProveedor(id, p.email());
            ctx.status(204);
        });

        // RF06: el controlador rechaza si tiene compras asociadas
        app.delete("/api/proveedores/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            PC.eliminarProveedor(id);
            ctx.status(204);
        });
    }
}
