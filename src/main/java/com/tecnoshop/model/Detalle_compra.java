package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Detalle_compras")
public class Detalle_compra {
    private int id;
    private int compra_id;
    private int cantidad;
    private double precio_unitario;
    private int subtotal;
    private int producto_id;
}

public Detalle_compra(int id, int compra_id, int cantidad, double precio_unitario, int subtotal, int producto_id){
    this.id = id;
    this.compra_id = compra_id;
    this.cantidad = cantidad;
    this.precio_unitario = precio_unitario;
    this.subtotal = subtotal;
    this.producto_id = producto_id;
}


public int getId() {
    return this.id;
}

public void setId(int id) {
    this.id = id;
}

public int getCompra_id() {
    return this.compra_id;
}

public void setCompra_id(int compra_id) {
    this.compra_id = compra_id;
}

public int getCantidad() {
    return this.cantidad;
}

public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
}

public double getPrecio_unitario() {
    return this.precio_unitario;
}

public void setPrecio_unitario(double precio_unitario) {
    this.precio_unitario = precio_unitario;
}

public int getSubtotal() {
    return this.subtotal;
}

public void setSubtotal(int subtotal) {
    this.subtotal = subtotal;
}

public int getProducto_id() {
    return this.producto_id;
}

public void setProducto_id(int producto_id) {
    this.producto_id = producto_id;
}
