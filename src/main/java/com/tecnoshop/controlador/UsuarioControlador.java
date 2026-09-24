package com.tecnoshop.controlador;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.excepciones.CredencialesInvalidasException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
import com.tecnoshop.excepciones.UsuarioInactivoException;
import com.tecnoshop.manejador.ManejadorUsuario;
import com.tecnoshop.model.Usuario;

public class UsuarioControlador implements IUsuarioControlador {
    private static UsuarioControlador instancia= null;

    private UsuarioControlador(){}

    public static synchronized UsuarioControlador get(){
        if(instancia == null){
            instancia = new UsuarioControlador();
        }
        return instancia;
    }

    public void registrarUsuario(String nombre, String email, String password) throws ExisteUsuarioException{
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        boolean existe= MU.existeUsuario(email);
        if(existe){
            throw new ExisteUsuarioException("Ya existe un usuario con el email dado");
        }
        else{
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            Usuario S = new Usuario (nombre,email,hash,true);
            MU.agregarUsuario(S);
        }
    }


    public List<UsuarioDTO> obtenerTodosLosUsuarios(){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        return MU.obtenerTodosLosUsuarios();
    }

    
    public void activarUsuario(int id){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        MU.cambiarEstadoUsuario(id,true);
    }

    public void desactivarUsuario(int id){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        MU.cambiarEstadoUsuario(id,false);
    }

    public void cambiarNombreUsuario(int id, String nombre){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        MU.cambiarNombreUsuario(id, nombre);
    }

    public void cambiarEmailUsuario(int id, String email) throws ExisteUsuarioException{
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        Usuario otro = MU.obtenerUsuarioPorEmail(email);
        if(otro != null && otro.getId() != id){
            throw new ExisteUsuarioException("Ya existe un usuario con el email dado");
        }
        MU.cambiarEmailUsuario(id, email);
    }

    public void cambiarPasswordUsuario(int id, String password){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        MU.cambiarPasswordUsuario(id, hash);
    }

    public UsuarioDTO login(String email, String password) throws CredencialesInvalidasException, UsuarioInactivoException{
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        Usuario usuario = MU.obtenerUsuarioPorEmail(email);
        // mismo mensaje para email inexistente o password incorrecta, asi no se revela que emails estan registrados
        if(usuario == null || !BCrypt.checkpw(password, usuario.getPassword())){
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
        if(!usuario.isActivo()){
            throw new UsuarioInactivoException("El usuario esta inactivo");
        }
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.isActivo());
    }

}
