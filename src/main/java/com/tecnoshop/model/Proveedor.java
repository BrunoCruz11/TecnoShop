package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    private int telefono;
    private string nombre;
    private int id;
    private string email;
}