package com.tecnoshop.controlador;

import java.time.LocalDate;
import java.util.List;

import com.tecnoshop.dto.CompraDTO;
import com.tecnoshop.dto.DetalleCompraDTO;
import com.tecnoshop.excepciones.CompraVaciaException;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.EstadoCompraInvalidoException;
import com.tecnoshop.excepciones.NoExisteCompraException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.NoExisteProveedorException;
import com.tecnoshop.excepciones.NoExisteUsuarioException;
import com.tecnoshop.excepciones.RangoFechasInvalidoException;

public interface ICompraControlador {
    int registrarCompra(int usuarioId, int proveedorId, LocalDate fecha, List<DetalleCompraDTO> detalles) throws CompraVaciaException, NoExisteUsuarioException, NoExisteProveedorException, NoExisteProductoException, DatosInvalidosException;
    CompraDTO obtenerCompraPorId(int id);
    List<CompraDTO> obtenerTodasLasCompras();
    List<CompraDTO> obtenerComprasPorUsuario(int usuarioId);
    List<CompraDTO> obtenerComprasPorProveedor(int proveedorId);
    List<CompraDTO> obtenerComprasPorFecha(LocalDate desde, LocalDate hasta) throws RangoFechasInvalidoException, DatosInvalidosException;
    void confirmarCompra(int id) throws NoExisteCompraException, EstadoCompraInvalidoException;
    void cancelarCompra(int id) throws NoExisteCompraException, EstadoCompraInvalidoException;
}
