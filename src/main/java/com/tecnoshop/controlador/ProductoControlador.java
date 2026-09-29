package com.tecnoshop.controlador;

import com.tecnoshop.excepciones.ExisteProductoException;
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

    public void registrarProducto(String nombre, String descripcion, String codigo, int stock, int stockMinimo, double precioCompra, double precioVenta) throws ExisteProductoException{
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        if(MP.existeProducto(codigo)){
            throw new ExisteProductoException("Ya existe un producto con el codigo dado");
        }
        // un producto nuevo arranca disponible; RF09 es el que permite cambiarlo despues
        Producto P = new Producto(nombre, descripcion, true, codigo, stockMinimo, stock, precioCompra, precioVenta);
        MP.agregarProducto(P);
    }

}
