package com.tecnoshop.dto;

import java.time.LocalDate;

import com.tecnoshop.enums.EstadoCompra;

public class CompraDTO {
    private int id;
    private EstadoCompra estado;
    private LocalDate fecha;
    private int usuarioId;
    private int proveedorId;
    private double total;

    public CompraDTO(int id, EstadoCompra estado, LocalDate fecha, int usuarioId, int proveedorId, double total) {
        this.id = id;
        this.estado = estado;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.proveedorId = proveedorId;
        this.total = total;
    }

    public int getId() {
        return this.id;
    }

    public EstadoCompra getEstado() {
        return this.estado;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public int getUsuarioId() {
        return this.usuarioId;
    }

    public int getProveedorId() {
        return this.proveedorId;
    }

    public double getTotal() {
        return this.total;
    }
}
