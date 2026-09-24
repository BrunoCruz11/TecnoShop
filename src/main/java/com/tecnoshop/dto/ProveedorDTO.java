package com.tecnoshop.dto;

public class ProveedorDTO {
    private int id;
    private int telefono;
    private String nombre;
    private String email;

    public ProveedorDTO(int id, int telefono, String nombre, String email) {
        this.id = id;
        this.telefono = telefono;
        this.nombre = nombre;
        this.email = email;
    }

    public int getId() {
        return this.id;
    }

    public int getTelefono() {
        return this.telefono;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getEmail() {
        return this.email;
    }
}
