package com.tecnoshop.api;

import com.tecnoshop.controlador.IProductoControlador;
import com.tecnoshop.controlador.ProductoControlador;
import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.excepciones.NoExisteProductoException;

import io.javalin.Javalin;

public final class ProductoRutas {

    // lo que manda el front al crear o editar un producto
    public record ProductoPeticion(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta, String imagenUrl) {}
    public record DisponiblePeticion(boolean disponible) {}

    private ProductoRutas() {
    }

    public static void registrar(Javalin app) {
        IProductoControlador PC = ProductoControlador.get();

        // catalogo: lo puede ver cualquier usuario con sesion (sin precio de compra ni stock exacto)
        app.get("/api/catalogo", ctx -> ctx.json(PC.obtenerCatalogo()));

        app.get("/api/productos", ctx -> ctx.json(PC.obtenerTodosLosProductos()));

        // RF08: va antes de /{id} para que "por-acabar" no se tome como un id
        app.get("/api/productos/por-acabar", ctx -> ctx.json(PC.obtenerProductosPorAcabar()));

        app.get("/api/productos/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ProductoDTO producto = PC.obtenerProductoPorId(id);
            if (producto == null) {
                throw new NoExisteProductoException("No existe un producto con el id dado");
            }
            ctx.json(producto);
        });

        app.post("/api/productos", ctx -> {
            ProductoPeticion p = ctx.bodyAsClass(ProductoPeticion.class);
            PC.registrarProducto(p.nombre(), p.descripcion(), p.codigo(), p.stock(), p.stockMinimo(), p.precioCompra(), p.precioVenta(), p.imagenUrl());
            ctx.status(201);
        });

        app.put("/api/productos/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ProductoPeticion p = ctx.bodyAsClass(ProductoPeticion.class);
            PC.modificarProducto(id, p.nombre(), p.descripcion(), p.codigo(), p.stock(), p.stockMinimo(), p.precioCompra(), p.precioVenta(), p.imagenUrl());
            ctx.status(204);
        });

        // RF09
        app.put("/api/productos/{id}/disponible", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            if (ctx.bodyAsClass(DisponiblePeticion.class).disponible()) {
                PC.marcarProductoDisponible(id);
            } else {
                PC.marcarProductoNoDisponible(id);
            }
            ctx.status(204);
        });
    }
}
