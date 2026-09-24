package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.excepciones.CredencialesInvalidasException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
import com.tecnoshop.excepciones.UsuarioInactivoException;

public interface IUsuarioControlador {
    void registrarUsuario(String nombre, String email, String password, boolean activo) throws ExisteUsuarioException;
    UsuarioDTO obtenerUsuarioPorId(int id);
    List<UsuarioDTO> obtenerTodosLosUsuarios();
    void activarUsuario(int id);
    void desactivarUsuario(int id);
    void cambiarNombreUsuario(int id, String nombre);
    void cambiarEmailUsuario(int id, String email) throws ExisteUsuarioException;
    void cambiarPasswordUsuario(int id, String password);
    UsuarioDTO login(String email, String password) throws CredencialesInvalidasException, UsuarioInactivoException;
}
