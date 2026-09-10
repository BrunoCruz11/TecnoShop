package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private boolean disponible;
    private String codigo;
    private int stockMinimo;
    private int stock;
    private double precioCompra;
    private double precioVenta;
}