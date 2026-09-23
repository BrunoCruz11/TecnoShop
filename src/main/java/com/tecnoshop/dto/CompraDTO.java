package com.tecnoshop.dto;

public class CompraDTO {
    private int id;
    private String estado;
    private int usuarioId;
    private int proveedorId;

    public CompraDTO(int id, String estado, int usuarioId, int proveedorId) {
        this.id = id;
        this.estado = estado;
        this.usuarioId = usuarioId;
        this.proveedorId = proveedorId;
    }

    public int getId() {
        return this.id;
    }

    public String getEstado() {
        return this.estado;
    }

    public int getUsuarioId() {
        return this.usuarioId;
    }

    public int getProveedorId() {
        return this.proveedorId;
    }
}
