package com.tecnoshop.dto;

public class ProductoDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private boolean disponible;
    private String codigo;
    private int stockMinimo;
    private int stock;
    private double precioCompra;
    private double precioVenta;

    public ProductoDTO(int id, String nombre, String descripcion, boolean disponible, String codigo,
            int stockMinimo, int stock, double precioCompra, double precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.disponible = disponible;
        this.codigo = codigo;
        this.stockMinimo = stockMinimo;
        this.stock = stock;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
    }

    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public boolean isDisponible() {
        return this.disponible;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public int getStockMinimo() {
        return this.stockMinimo;
    }

    public int getStock() {
        return this.stock;
    }

    public double getPrecioCompra() {
        return this.precioCompra;
    }

    public double getPrecioVenta() {
        return this.precioVenta;
    }
}
