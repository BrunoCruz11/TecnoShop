package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.CompraDTO;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.enums.EstadoCompra;
import com.tecnoshop.excepciones.EstadoCompraInvalidoException;
import com.tecnoshop.excepciones.NoExisteCompraException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.manejador.ManejadorCompra;
import com.tecnoshop.manejador.ManejadorDetalleCompra;
import com.tecnoshop.manejador.ManejadorProducto;

public class DetalleCompraControlador implements IDetalleCompraControlador {
    private static DetalleCompraControlador instancia= null;

    private DetalleCompraControlador(){}

    public static synchronized DetalleCompraControlador get(){
        if(instancia == null){
            instancia = new DetalleCompraControlador();
        }
        return instancia;
    }

    public void registrarDetalleCompra(int compraId, int productoId, int cantidad, double precioUnitario) throws NoExisteCompraException, NoExisteProductoException, EstadoCompraInvalidoException{
        CompraDTO compra = ManejadorCompra.getInstancia().obtenerCompraPorId(compraId);
        if(compra == null){
            throw new NoExisteCompraException("No existe una compra con el id dado");
        }
        // si la compra ya se confirmo, esta linea no sumaria stock (RF17)
        if(compra.getEstado() != EstadoCompra.PENDIENTE){
            throw new EstadoCompraInvalidoException("Solo se pueden agregar detalles a una compra pendiente");
        }
        if(ManejadorProducto.getInstancia().obtenerProductoPorId(productoId) == null){
            throw new NoExisteProductoException("No existe un producto con el id dado");
        }
        double subtotal = cantidad * precioUnitario;
        ManejadorDetalleCompra MD = ManejadorDetalleCompra.getInstancia();
        MD.agregarDetalleCompra(compraId, productoId, cantidad, precioUnitario, subtotal);
    }

    public DetalleCompraDTO obtenerDetalleCompraPorId(int id){
        ManejadorDetalleCompra MD = ManejadorDetalleCompra.getInstancia();
        return MD.obtenerDetalleCompraPorId(id);
    }

    public List<DetalleCompraDTO> obtenerDetallesDeCompra(int compraId){
        ManejadorDetalleCompra MD = ManejadorDetalleCompra.getInstancia();
        return MD.obtenerDetallesDeCompra(compraId);
    }

}
