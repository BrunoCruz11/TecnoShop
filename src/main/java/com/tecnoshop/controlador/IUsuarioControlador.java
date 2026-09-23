package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.excepciones.ExisteUsuarioException;

public interface IUsuarioControlador {
    void registrarUsuario(String nombre, String email, String password) throws ExisteUsuarioException;
    List<UsuarioDTO> obtenerTodosLosUsuarios();
}
