package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.ResenaDTO;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.NoExisteProductoException;
import com.tecnoshop.excepciones.NoExisteResenaException;
import com.tecnoshop.excepciones.SinPermisoException;
import com.tecnoshop.manejador.ManejadorProducto;
import com.tecnoshop.manejador.ManejadorResena;
import com.tecnoshop.util.Validar;

public class ResenaControlador implements IResenaControlador {
    private static ResenaControlador instancia= null;

    private ResenaControlador(){}

    public static synchronized ResenaControlador get(){
        if(instancia == null){
            instancia = new ResenaControlador();
        }
        return instancia;
    }

    // si el usuario ya habia opinado sobre el producto, se reemplaza su reseña anterior
    public void publicarResena(int productoId, int usuarioId, int puntaje, String comentario) throws NoExisteProductoException, DatosInvalidosException{
        if(puntaje < 1 || puntaje > 5){
            throw new DatosInvalidosException("El puntaje tiene que ser de 1 a 5 estrellas");
        }
        comentario = Validar.textoOpcional(comentario, "comentario", 1000);
        // solo se opina sobre productos que estan en el catalogo
        if(ManejadorProducto.getInstancia().obtenerProductoCatalogo(productoId) == null){
            throw new NoExisteProductoException("El producto no existe o no esta disponible");
        }
        ManejadorResena.getInstancia().guardarResena(productoId, usuarioId, puntaje, comentario);
    }

    public List<ResenaDTO> obtenerResenasDeProducto(int productoId){
        return ManejadorResena.getInstancia().obtenerResenasDeProducto(productoId);
    }

    // cada uno borra la suya; el admin puede borrar cualquiera (para moderar)
    public void eliminarResena(int id, int usuarioId, boolean esAdmin) throws NoExisteResenaException, SinPermisoException{
        ManejadorResena MR = ManejadorResena.getInstancia();
        ResenaDTO resena = MR.obtenerResenaPorId(id);
        if(resena == null){
            throw new NoExisteResenaException("No existe una reseña con el id dado");
        }
        if(resena.getUsuarioId() != usuarioId && !esAdmin){
            throw new SinPermisoException("Solo podes borrar tus propias reseñas");
        }
        MR.eliminarResena(id);
    }

}
