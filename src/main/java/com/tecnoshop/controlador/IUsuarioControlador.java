package com.tecnoshop.controlador;

import java.util.List;

import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.enums.Rol;
import com.tecnoshop.excepciones.CredencialesInvalidasException;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
import com.tecnoshop.excepciones.NoExisteUsuarioException;
import com.tecnoshop.excepciones.UsuarioInactivoException;

public interface IUsuarioControlador {
    void registrarUsuario(String nombre, String email, String password, boolean activo, Rol rol) throws ExisteUsuarioException, DatosInvalidosException;
    UsuarioDTO obtenerUsuarioPorId(int id);
    List<UsuarioDTO> obtenerTodosLosUsuarios();
    void activarUsuario(int id) throws NoExisteUsuarioException;
    void desactivarUsuario(int id) throws NoExisteUsuarioException;
    void cambiarRolUsuario(int id, Rol rol) throws NoExisteUsuarioException, DatosInvalidosException;
    void cambiarNombreUsuario(int id, String nombre) throws NoExisteUsuarioException, DatosInvalidosException;
    void cambiarEmailUsuario(int id, String email) throws NoExisteUsuarioException, ExisteUsuarioException, DatosInvalidosException;
    void cambiarPasswordUsuario(int id, String password) throws NoExisteUsuarioException, DatosInvalidosException;
    UsuarioDTO login(String email, String password) throws CredencialesInvalidasException, UsuarioInactivoException;
}
