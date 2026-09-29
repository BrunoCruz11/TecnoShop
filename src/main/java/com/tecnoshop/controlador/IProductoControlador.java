package com.tecnoshop.controlador;

import com.tecnoshop.excepciones.ExisteProductoException;

public interface IProductoControlador {
    void registrarProducto(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) throws ExisteProductoException;
}
