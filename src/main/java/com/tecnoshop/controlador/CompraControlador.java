package com.tecnoshop.controlador;

import java.time.LocalDate;
import java.util.List;

import com.tecnoshop.dto.CompraDTO;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.enums.EstadoCompra;
import com.tecnoshop.excepciones.CompraVaciaException;
import com.tecnoshop.excepciones.EstadoCompraInvalidoException;
import com.tecnoshop.excepciones.NoExisteCompraException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.NoExisteUsuarioException;
import com.tecnoshop.excepciones.RangoFechasInvalidoException;
import com.tecnoshop.manejador.ManejadorCompra;
import com.tecnoshop.manejador.ManejadorProducto;
import com.tecnoshop.manejador.ManejadorProveedor;
import com.tecnoshop.manejador.ManejadorUsuario;

public class CompraControlador implements ICompraControlador {
    private static CompraControlador instancia= null;

    private CompraControlador(){}

    public static synchronized CompraControlador get(){
        if(instancia == null){
            instancia = new CompraControlador();
        }
        return instancia;
    }

    public int registrarCompra(int usuarioId, int proveedorId, LocalDate fecha, List<DetalleCompraDTO> detalles) throws CompraVaciaException, NoExisteUsuarioException, NoExisteProveedorException, NoExisteProductoException{
        if(detalles == null || detalles.isEmpty()){
            throw new CompraVaciaException("Una compra debe tener al menos un detalle");
        }
        if(ManejadorUsuario.getInstancia().obtenerUsuarioPorId(usuarioId) == null){
            throw new NoExisteUsuarioException("No existe un usuario con el id dado");
        }
        if(ManejadorProveedor.getInstancia().obtenerProveedorPorId(proveedorId) == null){
            throw new NoExisteProveedorException("No existe un proveedor con el id dado");
        }
        ManejadorProducto MP = ManejadorProducto.getInstancia();
        for(DetalleCompraDTO detalle : detalles){
            if(MP.obtenerProductoPorId(detalle.getProductoId()) == null){
                throw new NoExisteProductoException("No existe un producto con el id " + detalle.getProductoId());
            }
        }
        // una compra nueva arranca pendiente (RF14)
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        return MC.agregarCompra(fecha, usuarioId, proveedorId, detalles);
    }

    public CompraDTO obtenerCompraPorId(int id){
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        return MC.obtenerCompraPorId(id);
    }

    public List<CompraDTO> obtenerTodasLasCompras(){
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        return MC.obtenerTodasLasCompras();
    }

    public List<CompraDTO> obtenerComprasPorUsuario(int usuarioId){
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        return MC.obtenerComprasPorUsuario(usuarioId);
    }

    public List<CompraDTO> obtenerComprasPorProveedor(int proveedorId){
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        return MC.obtenerComprasPorProveedor(proveedorId);
    }

    public List<CompraDTO> obtenerComprasPorFecha(LocalDate desde, LocalDate hasta) throws RangoFechasInvalidoException{
        if(desde.isAfter(hasta)){
            throw new RangoFechasInvalidoException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        return MC.obtenerComprasPorFecha(desde, hasta);
    }

    // flujo valido (RF14): pendiente -> confirmada -> cancelada, o pendiente -> cancelada
    public void confirmarCompra(int id) throws NoExisteCompraException, EstadoCompraInvalidoException{
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        CompraDTO compra = MC.obtenerCompraPorId(id);
        if(compra == null){
            throw new NoExisteCompraException("No existe una compra con el id dado");
        }
        if(compra.getEstado() != EstadoCompra.PENDIENTE){
            throw new EstadoCompraInvalidoException("Solo se puede confirmar una compra pendiente");
        }
        MC.confirmarCompra(id);
    }

    public void cancelarCompra(int id) throws NoExisteCompraException, EstadoCompraInvalidoException{
        ManejadorCompra MC = ManejadorCompra.getInstancia();
        CompraDTO compra = MC.obtenerCompraPorId(id);
        if(compra == null){
            throw new NoExisteCompraException("No existe una compra con el id dado");
        }
        if(compra.getEstado() == EstadoCompra.CANCELADA){
            throw new EstadoCompraInvalidoException("La compra ya esta cancelada");
        }
        boolean estabaConfirmada = compra.getEstado() == EstadoCompra.CONFIRMADA;
        MC.cancelarCompra(id, estabaConfirmada);
    }

}
