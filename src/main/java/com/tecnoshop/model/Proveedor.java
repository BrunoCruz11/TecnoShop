package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    private int telefono;
    private String nombre;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;

public Proveedor() {
}


public Proveedor(int telefono, String nombre, int id, String email) {
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

public void setNombre(String nombre) {
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

public void setEmail(String email) {
    this.email = email;
}

}
