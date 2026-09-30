package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ProductoDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.ExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.PrecioInvalidoException;
import com.tecnoshop.manejador.ManejadorProducto;
import com.tecnoshop.model.Producto;
import com.tecnoshop.util.Validar;

public class ProductoControlador implements IProductoControlador {
    private static ProductoControlador instancia= null;

    private ProductoControlador(){}

    public static synchronized ProductoControlador get(){
        if(instancia == null){
            instancia = new ProductoControlador();
        }
        return instancia;
    }

    public void registrarProducto(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) throws ExisteProductoException, PrecioInvalidoException, DatosInvalidosException{
        nombre = Validar.texto(nombre, "nombre", 255);
        descripcion = Validar.textoOpcional(descripcion, "descripcion", 255);
        codigo = Validar.texto(codigo, "codigo", 50);
        validarNumeros(stock, stockMinimo, precioCompra, precioVenta);

        ManejadorProducto MP = ManejadorProducto.getInstancia();
        if(MP.existeProducto(codigo)){
            throw new ExisteProductoException("Ya existe un producto con el codigo dado");
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

    public void marcarProductoDisponible(int id) throws NoExisteProductoException{
        ManejadorProducto MP = buscar(id);
        MP.cambiarEstadoProducto(id, true);
    }

    public void marcarProductoNoDisponible(int id) throws NoExisteProductoException{
        ManejadorProducto MP = buscar(id);
        MP.cambiarEstadoProducto(id, false);
    }

    public void cambiarNombreProducto(int id, String nombre) throws NoExisteProductoException, DatosInvalidosException{
        nombre = Validar.texto(nombre, "nombre", 255);
        buscar(id).cambiarNombreProducto(id, nombre);
    }

    public void cambiarDescripcionProducto(int id, String descripcion) throws NoExisteProductoException, DatosInvalidosException{
        descripcion = Validar.textoOpcional(descripcion, "descripcion", 255);
        buscar(id).cambiarDescripcionProducto(id, descripcion);
    }

    public void cambiarCodigoProducto(int id, String codigo) throws NoExisteProductoException, ExisteProductoException, DatosInvalidosException{
        codigo = Validar.texto(codigo, "codigo", 50);
        ManejadorProducto MP = buscar(id);
        Producto otro = MP.obtenerProductoPorCodigo(codigo);
        if(otro != null && otro.getId() != id){
            throw new ExisteProductoException("Ya existe un producto con el codigo dado");
        }
        MP.cambiarCodigoProducto(id, codigo);
    }

    public void cambiarStockProducto(int id, int stock) throws NoExisteProductoException, DatosInvalidosException{
        Validar.noNegativo(stock, "stock");
        buscar(id).cambiarStockProducto(id, stock);
    }

    public void cambiarStockMinimoProducto(int id, int stockMinimo) throws NoExisteProductoException, DatosInvalidosException{
        Validar.noNegativo(stockMinimo, "stock minimo");
        buscar(id).cambiarStockMinimoProducto(id, stockMinimo);
    }

    public void cambiarPrecioCompraProducto(int id, double precioCompra) throws NoExisteProductoException, PrecioInvalidoException, DatosInvalidosException{
        Validar.noNegativo(precioCompra, "precio de compra");
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

    public void cambiarPrecioVentaProducto(int id, double precioVenta) throws NoExisteProductoException, PrecioInvalidoException, DatosInvalidosException{
        Validar.noNegativo(precioVenta, "precio de venta");
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

    // modifica todos los datos juntos (lo usa el formulario de edicion del front); la disponibilidad va por RF09
    public void modificarProducto(int id, String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) throws NoExisteProductoException, ExisteProductoException, PrecioInvalidoException, DatosInvalidosException{
        nombre = Validar.texto(nombre, "nombre", 255);
        descripcion = Validar.textoOpcional(descripcion, "descripcion", 255);
        codigo = Validar.texto(codigo, "codigo", 50);
        validarNumeros(stock, stockMinimo, precioCompra, precioVenta);

        ManejadorProducto MP = buscar(id);
        Producto otro = MP.obtenerProductoPorCodigo(codigo);
        if(otro != null && otro.getId() != id){
            throw new ExisteProductoException("Ya existe un producto con el codigo dado");
        }
        MP.modificarProducto(id, nombre, descripcion, codigo, stock, stockMinimo, precioCompra, precioVenta);
    }

    // devuelve el manejador si el producto existe, asi cada metodo no repite el mismo if
    private ManejadorProducto buscar(int id) throws NoExisteProductoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        if(MP.obtenerProductoPorId(id) == null){
            throw new NoExisteProductoException("No existe un producto con el id dado");
        }
        return MP;
    }

    private void validarNumeros(int stock, int stockMinimo, double precioCompra, double precioVenta) throws DatosInvalidosException, PrecioInvalidoException{
        Validar.noNegativo(stock, "stock");
        Validar.noNegativo(stockMinimo, "stock minimo");
        Validar.noNegativo(precioCompra, "precio de compra");
        Validar.noNegativo(precioVenta, "precio de venta");
        if(precioVenta < precioCompra){
            throw new PrecioInvalidoException("El precio de venta no puede ser menor al precio de compra");
        }
    }

}
