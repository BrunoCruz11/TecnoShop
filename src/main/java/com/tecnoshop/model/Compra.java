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