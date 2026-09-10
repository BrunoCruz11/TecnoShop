package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {
    private int id;
    private int compra_id;
    private int cantidad;
    private double precio_unitario;
    private int subtotal;
    private int producto_id;
}
