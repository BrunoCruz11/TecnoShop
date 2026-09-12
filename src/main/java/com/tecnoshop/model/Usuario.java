package com.tecnoshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {\
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String email;
    private String password;
    private boolean activo;


public Usuario() {
}


public Usuario(int id, String nombre, String email, String password, boolean activo) {
    this.id = id;
    this.nombre = nombre;
    this.email = email;
    this.password = password;
    this.activo = activo;
}

public int getId() {
    return this.id;
}

public void setId(int id) {
    this.id = id;
}


public String getNombre() {
    return this.nombre;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}

public String getEmail() {
    return this.email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getPassword() {
    return this.password;
}

public void setPassword(String password) {
    this.password = password;
}

public boolean isActivo() {
    return this.activo;
}

public void setActivo(boolean activo) {
    this.activo = activo;
}

}

