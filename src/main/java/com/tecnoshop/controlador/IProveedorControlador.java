package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.ProveedorConComprasException;

public interface IProveedorControlador {
    void registrarProveedor(int telefono, String nombre, String email);
    ProveedorDTO obtenerProveedorPorId(int id);
    List<ProveedorDTO> obtenerTodosLosProveedores();
    void cambiarNombreProveedor(int id, String nombre);
    void cambiarTelefonoProveedor(int id, int telefono);
    void cambiarEmailProveedor(int id, String email);
    void eliminarProveedor(int id) throws NoExisteProveedorException, ProveedorConComprasException;
}
