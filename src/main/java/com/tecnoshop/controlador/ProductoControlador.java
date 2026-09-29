package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.PrecioInvalidoException;
import com.tecnoshop.manejador.ManejadorProducto;
import com.tecnoshop.model.Producto;

public class ProductoControlador implements IProductoControlador {
    private static ProductoControlador instancia= null;

    private ProductoControlador(){}

    public static synchronized ProductoControlador get(){
        if(instancia == null){
            instancia = new ProductoControlador();
        }
        return instancia;
    }

    public void registrarProducto(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) throws ExisteProductoException, PrecioInvalidoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        if(MP.existeProducto(codigo)){
            throw new ExisteProductoException("Ya existe un producto con el codigo dado");
        }
        if(precioVenta < precioCompra){
            throw new PrecioInvalidoException("El precio de venta no puede ser menor al precio de compra");
        }
        // un producto nuevo arranca disponible; RF09 es el que permite cambiarlo despues
        Producto P = new Producto(nombre, descripcion, true, codigo, stockMinimo, stock, precioCompra, precioVenta);
        MP.agregarProducto(P);
    }

    public ProductoDTO obtenerProductoPorId(int id){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        return MP.obtenerProductoPorId(id);
    }

    public List<ProductoDTO> obtenerTodosLosProductos(){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        return MP.obtenerTodosLosProductos();
    }

    public boolean alertaStockMinimo(int id) throws NoExisteProductoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        if(MP.obtenerProductoPorId(id) == null){
            throw new NoExisteProductoException("No existe un producto con el id dado");
        }
        return MP.porAcabar(id);
    }

    public List<ProductoDTO> obtenerProductosPorAcabar(){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        return MP.obtenerProductosPorAcabar();
    }

    public void marcarProductoDisponible(int id){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        MP.cambiarEstadoProducto(id, true);
    }

    public void marcarProductoNoDisponible(int id){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        MP.cambiarEstadoProducto(id, false);
    }

    public void cambiarNombreProducto(int id, String nombre){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        MP.cambiarNombreProducto(id, nombre);
    }

    public void cambiarDescripcionProducto(int id, String descripcion){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        MP.cambiarDescripcionProducto(id, descripcion);
    }

    public void cambiarCodigoProducto(int id, String codigo) throws ExisteProductoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        Producto otro = MP.obtenerProductoPorCodigo(codigo);
        if(otro != null && otro.getId() != id){
            throw new ExisteProductoException("Ya existe un producto con el codigo dado");
        }
        MP.cambiarCodigoProducto(id, codigo);
    }

    public void cambiarStockProducto(int id, int stock){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        MP.cambiarStockProducto(id, stock);
    }

    public void cambiarStockMinimoProducto(int id, int stockMinimo){
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        MP.cambiarStockMinimoProducto(id, stockMinimo);
    }

    public void cambiarPrecioCompraProducto(int id, double precioCompra) throws NoExisteProductoException, PrecioInvalidoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        ProductoDTO producto = MP.obtenerProductoPorId(id);
        if(producto == null){
            throw new NoExisteProductoException("No existe un producto con el id dado");
        }
        if(producto.getPrecioVenta() < precioCompra){
            throw new PrecioInvalidoException("El precio de venta no puede ser menor al precio de compra");
        }
        MP.cambiarPrecioCompraProducto(id, precioCompra);
    }

    public void cambiarPrecioVentaProducto(int id, double precioVenta) throws NoExisteProductoException, PrecioInvalidoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        ProductoDTO producto = MP.obtenerProductoPorId(id);
        if(producto == null){
            throw new NoExisteProductoException("No existe un producto con el id dado");
        }
        if(precioVenta < producto.getPrecioCompra()){
            throw new PrecioInvalidoException("El precio de venta no puede ser menor al precio de compra");
        }
        MP.cambiarPrecioVentaProducto(id, precioVenta);
    }

}
