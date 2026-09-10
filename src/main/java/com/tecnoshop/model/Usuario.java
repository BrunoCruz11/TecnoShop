package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private boolean activo;
}
