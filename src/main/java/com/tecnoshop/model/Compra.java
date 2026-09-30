package com.tecnoshop.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.enums.EstadoCompra;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "compras")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private EstadoCompra estado;
    private LocalDate fecha;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "compra")
    private List<Detalle_compra> detalles = new ArrayList<>();

    public Compra() {
    }

    public Compra(EstadoCompra estado, LocalDate fecha, Usuario usuario, Proveedor proveedor) {
        this.estado = estado;
        this.fecha = fecha;
        this.usuario = usuario;
        this.proveedor = proveedor;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EstadoCompra getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Proveedor getProveedor() {
        return this.proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<Detalle_compra> getDetalles() {
        return this.detalles;
    }

    // el total no se guarda, se calcula a partir de los subtotales de cada linea (RF16)
    public double getTotal() {
        double total = 0;
        for (Detalle_compra detalle : this.detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }

}
