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

public Proveedor(int telefono, string nombre, int id, string email) {
    this.telefono = telefono;
    this.nombre = nombre;
    this.id = id;
    this.email = email;
}

public int getTelefono() {
    return this.telefono;
}

public void setTelefono(int telefono) {
    this.telefono = telefono;
}

public string getNombre() {
    return this.nombre;
}

public void setNombre(string nombre) {
    this.nombre = nombre;
}

public int getId() {
    return this.id;
}

public void setId(int id) {
    this.id = id;
}

public string getEmail() {
    return this.email;
}

public void setEmail(string email) {
    this.email = email;
}
