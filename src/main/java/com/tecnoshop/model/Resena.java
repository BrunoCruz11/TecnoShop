package com.tecnoshop.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Reseña de un usuario sobre un producto: puntaje de 1 a 5 y un comentario (una por usuario y producto)
@Entity
@Table(name = "resenas")
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private int puntaje;
    private String comentario;
    private LocalDateTime fecha;

    public Resena() {
    }

    public Resena(Producto producto, Usuario usuario, int puntaje, String comentario, LocalDateTime fecha) {
        this.producto = producto;
        this.usuario = usuario;
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public int getId() {
        return this.id;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public int getPuntaje() {
        return this.puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public String getComentario() {
        return this.comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
