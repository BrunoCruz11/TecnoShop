package com.tecnoshop.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tecnoshop.controlador.CompraControlador;
import com.tecnoshop.controlador.DetalleCompraControlador;
import com.tecnoshop.controlador.ICompraControlador;
import com.tecnoshop.controlador.IDetalleCompraControlador;
import com.tecnoshop.dto.CompraDTO;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.excepciones.NoExisteCompraException;

import io.javalin.Javalin;

public final class CompraRutas {

    public record LineaPeticion(int productoId, int cantidad, double precioUnitario) {}
    public record CompraPeticion(int proveedorId, LocalDate fecha, List<LineaPeticion> detalles) {}

    private CompraRutas() {
    }

    public static void registrar(Javalin app) {
        ICompraControlador CC = CompraControlador.get();
        IDetalleCompraControlador DC = DetalleCompraControlador.get();

        // RF13: ?usuarioId=, ?proveedorId= o ?desde=&hasta= (sin filtros devuelve todas)
        app.get("/api/compras", ctx -> {
            if (ctx.queryParam("usuarioId") != null) {
                ctx.json(CC.obtenerComprasPorUsuario(ctx.queryParamAsClass("usuarioId", Integer.class).get()));
            } else if (ctx.queryParam("proveedorId") != null) {
                ctx.json(CC.obtenerComprasPorProveedor(ctx.queryParamAsClass("proveedorId", Integer.class).get()));
            } else if (ctx.queryParam("desde") != null && ctx.queryParam("hasta") != null) {
                ctx.json(CC.obtenerComprasPorFecha(LocalDate.parse(ctx.queryParam("desde")), LocalDate.parse(ctx.queryParam("hasta"))));
            } else {
                ctx.json(CC.obtenerTodasLasCompras());
            }
        });

        app.get("/api/compras/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            CompraDTO compra = CC.obtenerCompraPorId(id);
            if (compra == null) {
                throw new NoExisteCompraException("No existe una compra con el id dado");
            }
            ctx.json(compra);
        });

        app.get("/api/compras/{id}/detalles", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            ctx.json(DC.obtenerDetallesDeCompra(id));
        });

        // RF11 y RF12: la compra se registra junto con sus lineas; si no viene fecha se usa la de hoy.
        // El usuario es el de la sesion (token), no uno que mande el front: nadie puede registrar compras a nombre de otro
        app.post("/api/compras", ctx -> {
            CompraPeticion p = ctx.bodyAsClass(CompraPeticion.class);
            List<DetalleCompraDTO> detalles = new ArrayList<>();
            if (p.detalles() != null) {
                for (LineaPeticion linea : p.detalles()) {
                    detalles.add(new DetalleCompraDTO(linea.productoId(), linea.cantidad(), linea.precioUnitario()));
                }
            }
            LocalDate fecha = p.fecha() != null ? p.fecha() : LocalDate.now();
            int id = CC.registrarCompra(Autenticacion.usuario(ctx).getId(), p.proveedorId(), fecha, detalles);
            ctx.status(201).json(Map.of("id", id));
        });

        // agregar una linea a una compra pendiente
        app.post("/api/compras/{id}/detalles", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            LineaPeticion linea = ctx.bodyAsClass(LineaPeticion.class);
            DC.registrarDetalleCompra(id, linea.productoId(), linea.cantidad(), linea.precioUnitario());
            ctx.status(201);
        });

        // RF14 y RF17
        app.put("/api/compras/{id}/confirmar", ctx -> {
            CC.confirmarCompra(ctx.pathParamAsClass("id", Integer.class).get());
            ctx.status(204);
        });

        app.put("/api/compras/{id}/cancelar", ctx -> {
            CC.cancelarCompra(ctx.pathParamAsClass("id", Integer.class).get());
            ctx.status(204);
        });
    }
}
