package com.tecnoshop.model;

import java.util.ArrayList;
import java.util.List;

import com.tecnoshop.enums.Rol;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(mappedBy = "usuario")
    private List<Compra> compras = new ArrayList<>();
    private String nombre;
    private String email;
    private String password;
    private boolean activo;
    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.USUARIO;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String password, boolean activo) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.activo = activo;
    }

    public Usuario(String nombre, String email, String password, boolean activo, Rol rol) {
        this(nombre, email, password, activo);
        this.rol = rol;
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

    public Rol getRol() {
        return this.rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Compra> getCompras() {
        return this.compras;
    }

}
