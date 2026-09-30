package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.CompraDTO;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.EstadoCompraInvalidoException;
import com.tecnoshop.excepciones.NoExisteCompraException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.manejador.ManejadorCompra;
import com.tecnoshop.manejador.ManejadorDetalleCompra;
import com.tecnoshop.manejador.ManejadorProducto;
import com.tecnoshop.util.Validar;

public class DetalleCompraControlador implements IDetalleCompraControlador {
    private static DetalleCompraControlador instancia= null;

    private DetalleCompraControlador(){}

    public static synchronized DetalleCompraControlador get(){
        if(instancia == null){
            instancia = new DetalleCompraControlador();
        }
        return instancia;
    }

    public void registrarDetalleCompra(int compraId, int productoId, int cantidad, double precioUnitario) throws NoExisteCompraException, NoExisteProductoException, EstadoCompraInvalidoException, DatosInvalidosException{
        Validar.positivo(cantidad, "cantidad");
        Validar.noNegativo(precioUnitario, "precio unitario");
        CompraDTO compra = ManejadorCompra.getInstancia().obtenerCompraPorId(compraId);
        if(compra == null){
            throw new NoExisteCompraException("No existe una compra con el id dado");
        }
        if(ManejadorProducto.getInstancia().obtenerProductoPorId(productoId) == null){
            throw new NoExisteProductoException("No existe un producto con el id dado");
        }
        ManejadorDetalleCompra MD = ManejadorDetalleCompra.getInstancia();
        for(DetalleCompraDTO existente : MD.obtenerDetallesDeCompra(compraId)){
            if(existente.getProductoId() == productoId){
                throw new DatosInvalidosException("Ese producto ya esta en la compra");
            }
        }
        double subtotal = cantidad * precioUnitario;
        // el manejador vuelve a revisar que siga pendiente con la compra bloqueada:
        // si se confirmo justo ahora, esta linea no sumaria stock (RF17)
        if(!MD.agregarDetalleCompra(compraId, productoId, cantidad, precioUnitario, subtotal)){
            throw new EstadoCompraInvalidoException("Solo se pueden agregar detalles a una compra pendiente");
        }
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
