package com.tecnoshop.dto;

import java.util.ArrayList;
import java.util.List;

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
    private String imagenUrl;
    // nombres de los proveedores a los que se le compro el producto (sale de las compras confirmadas)
    private List<String> proveedores = new ArrayList<>();

    public ProductoDTO(int id, String nombre, String descripcion, boolean disponible, String codigo,
            int stockMinimo, int stock, double precioCompra, double precioVenta, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.disponible = disponible;
        this.codigo = codigo;
        this.stockMinimo = stockMinimo;
        this.stock = stock;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.imagenUrl = imagenUrl;
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

    public String getImagenUrl() {
        return this.imagenUrl;
    }

    public List<String> getProveedores() {
        return this.proveedores;
    }

    public void setProveedores(List<String> proveedores) {
        this.proveedores = proveedores;
    }
}
