package com.tecnoshop.dto;

import java.time.LocalDateTime;

public class ResenaDTO {
    private int id;
    private int productoId;
    private int usuarioId;
    private String usuarioNombre;
    private int puntaje;
    private String comentario;
    private LocalDateTime fecha;

    public ResenaDTO(int id, int productoId, int usuarioId, String usuarioNombre, int puntaje, String comentario, LocalDateTime fecha) {
        this.id = id;
        this.productoId = productoId;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public int getId() {
        return this.id;
    }

    public int getProductoId() {
        return this.productoId;
    }

    public int getUsuarioId() {
        return this.usuarioId;
    }

    public String getUsuarioNombre() {
        return this.usuarioNombre;
    }

    public int getPuntaje() {
        return this.puntaje;
    }

    public String getComentario() {
        return this.comentario;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }
}
