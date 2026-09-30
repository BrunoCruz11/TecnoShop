package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProveedorDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.ProveedorConComprasException;

public interface IProveedorControlador {
    void registrarProveedor(int telefono, String nombre, String email) throws DatosInvalidosException;
    ProveedorDTO obtenerProveedorPorId(int id);
    List<ProveedorDTO> obtenerTodosLosProveedores();
    void cambiarNombreProveedor(int id, String nombre) throws NoExisteProveedorException, DatosInvalidosException;
    void cambiarTelefonoProveedor(int id, int telefono) throws NoExisteProveedorException, DatosInvalidosException;
    void cambiarEmailProveedor(int id, String email) throws NoExisteProveedorException, DatosInvalidosException;
    void eliminarProveedor(int id) throws NoExisteProveedorException, ProveedorConComprasException;
}
