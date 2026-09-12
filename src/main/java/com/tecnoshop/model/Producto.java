package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String descripcion;
    private boolean disponible;
    private String codigo;
    private int stockMinimo;
    private int stock;
    private double precioCompra;
    private double precioVenta;

public Producto() {
}


public Producto(int id, String nombre, String descripcion, boolean disponible, String codigo, int stockMinimo, int stock, double precioCompra, double precioVenta) {
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


public void setId(int id) {
    this.id = id;
}


public String getNombre() {
    return this.nombre;
}


public void setNombre(String nombre) {
    this.nombre = nombre;
}

public String getDescripcion() {
    return this.descripcion;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}

public boolean isDisponible() {
    return this.disponible;
}

public void setDisponible(boolean disponible) {
    this.disponible = disponible;
}

public String getCodigo() {
    return this.codigo;
}

public void setCodigo(String codigo) {
    this.codigo = codigo;
}

public int getStockMinimo() {
    return this.stockMinimo;
}

public void setStockMinimo(int stockMinimo) {
    this.stockMinimo = stockMinimo;
}

public int getStock() {
    return this.stock;
}

public void setStock(int stock) {
    this.stock = stock;
}

public double getPrecioCompra() {
    return this.precioCompra;
}

public void setPrecioCompra(double precioCompra) {
    this.precioCompra = precioCompra;
}

public double getPrecioVenta() {
    return this.precioVenta;
}

public void setPrecioVenta(double precioVenta) {
    this.precioVenta = precioVenta;
}


}


