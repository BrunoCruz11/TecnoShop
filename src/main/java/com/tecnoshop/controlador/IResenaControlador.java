package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ResenaDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.NoExisteResenaException;
import com.tecnoshop.excepciones.SinPermisoException;

public interface IResenaControlador {
    void publicarResena(int productoId, int usuarioId, int puntaje, String comentario) throws NoExisteProductoException, DatosInvalidosException;
    List<ResenaDTO> obtenerResenasDeProducto(int productoId);
    void eliminarResena(int id, int usuarioId, boolean esAdmin) throws NoExisteResenaException, SinPermisoException;
}
