package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "compras")
public class Compra{
    private string estado;
    private int id;
    private int usuario_id;
    private int proveedor_id;
    //private Dtfecha fecha; //checkeable

}


public Compra(string estado, int id, int usuario_id, int proveedor_id) {
    this.estado = estado;
    this.id = id;
    this.usuario_id = usuario_id;
    this.proveedor_id = proveedor_id;
}

public string getEstado() {
    return this.estado;
}

public void setEstado(string estado) {
    this.estado = estado;
}

public int getId() {
    return this.id;
}

public void setId(int id) {
    this.id = id;
}

public int getUsuario_id() {
    return this.usuario_id;
}

public void setUsuario_id(int usuario_id) {
    this.usuario_id = usuario_id;
}

public int getProveedor_id() {
    return this.proveedor_id;
}

public void setProveedor_id(int proveedor_id) {
    this.proveedor_id = proveedor_id;
}