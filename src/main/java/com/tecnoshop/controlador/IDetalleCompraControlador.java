package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.excepciones.EstadoCompraInvalidoException;
import com.tecnoshop.excepciones.NoExisteCompraException;
import com.tecnoshop.excepciones.NoExisteProductoException;

public interface IDetalleCompraControlador {
    void registrarDetalleCompra(int compraId, int productoId, int cantidad, double precioUnitario) throws NoExisteCompraException, NoExisteProductoException, EstadoCompraInvalidoException;
    DetalleCompraDTO obtenerDetalleCompraPorId(int id);
    List<DetalleCompraDTO> obtenerDetallesDeCompra(int compraId);
}
