package com.tecnoshop.dto;

public class DetalleCompraDTO {
    private int id;
    private int compraId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleCompraDTO(int id, int compraId, int productoId, int cantidad, double precioUnitario,
            double subtotal) {
        this.id = id;
        this.compraId = compraId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public int getId() {
        return this.id;
    }

    public int getCompraId() {
        return this.compraId;
    }

    public int getProductoId() {
        return this.productoId;
    }

    public int getCantidad() {
        return this.cantidad;
    }

    public double getPrecioUnitario() {
        return this.precioUnitario;
    }

    public double getSubtotal() {
        return this.subtotal;
    }
}
