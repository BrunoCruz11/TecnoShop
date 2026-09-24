package com.tecnoshop.dto;

public class UsuarioDTO {
    private int id;
    private String nombre;
    private String email;
    private boolean activo;

    public UsuarioDTO(int id, String nombre, String email, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
    }

    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean isActivo() {
        return this.activo;
    }
}
