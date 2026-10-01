package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProductoCatalogoDTO;
import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.PrecioInvalidoException;

public interface IProductoControlador {
    void registrarProducto(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta, String imagenUrl) throws ExisteProductoException, PrecioInvalidoException, DatosInvalidosException;
    ProductoDTO obtenerProductoPorId(int id);
    List<ProductoDTO> obtenerTodosLosProductos();
    List<ProductoCatalogoDTO> obtenerCatalogo();
    ProductoCatalogoDTO obtenerProductoCatalogo(int id) throws NoExisteProductoException;
    boolean alertaStockMinimo(int id) throws NoExisteProductoException;
    List<ProductoDTO> obtenerProductosPorAcabar();
    void marcarProductoDisponible(int id) throws NoExisteProductoException;
    void marcarProductoNoDisponible(int id) throws NoExisteProductoException;
    void cambiarNombreProducto(int id, String nombre) throws NoExisteProductoException, DatosInvalidosException;
    void cambiarDescripcionProducto(int id, String descripcion) throws NoExisteProductoException, DatosInvalidosException;
    void cambiarCodigoProducto(int id, String codigo) throws NoExisteProductoException, ExisteProductoException, DatosInvalidosException;
    void cambiarStockProducto(int id, int stock) throws NoExisteProductoException, DatosInvalidosException;
    void cambiarStockMinimoProducto(int id, int stockMinimo) throws NoExisteProductoException, DatosInvalidosException;
    void cambiarPrecioCompraProducto(int id, double precioCompra) throws NoExisteProductoException, PrecioInvalidoException, DatosInvalidosException;
    void cambiarPrecioVentaProducto(int id, double precioVenta) throws NoExisteProductoException, PrecioInvalidoException, DatosInvalidosException;
    void modificarProducto(int id, String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta, String imagenUrl) throws NoExisteProductoException, ExisteProductoException, PrecioInvalidoException, DatosInvalidosException;
}
