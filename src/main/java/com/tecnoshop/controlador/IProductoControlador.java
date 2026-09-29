package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.PrecioInvalidoException;

public interface IProductoControlador {
    void registrarProducto(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) throws ExisteProductoException, PrecioInvalidoException;
    ProductoDTO obtenerProductoPorId(int id);
    List<ProductoDTO> obtenerTodosLosProductos();
    boolean alertaStockMinimo(int id) throws NoExisteProductoException;
    List<ProductoDTO> obtenerProductosPorAcabar();
    void marcarProductoDisponible(int id);
    void marcarProductoNoDisponible(int id);
    void cambiarNombreProducto(int id, String nombre);
    void cambiarDescripcionProducto(int id, String descripcion);
    void cambiarCodigoProducto(int id, String codigo) throws ExisteProductoException;
    void cambiarStockProducto(int id, int stock);
    void cambiarStockMinimoProducto(int id, int stockMinimo);
    void cambiarPrecioCompraProducto(int id, double precioCompra) throws NoExisteProductoException, PrecioInvalidoException;
    void cambiarPrecioVentaProducto(int id, double precioVenta) throws NoExisteProductoException, PrecioInvalidoException;
}
