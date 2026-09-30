package com.tecnoshop.controlador;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.tecnoshop.dto.UsuarioDTO;
import com.tecnoshop.excepciones.CredencialesInvalidasException;
import com.tecnoshop.excepciones.DatosInvalidosException;
import com.tecnoshop.excepciones.ExisteUsuarioException;
import com.tecnoshop.excepciones.NoExisteUsuarioException;
import com.tecnoshop.excepciones.UsuarioInactivoException;
import com.tecnoshop.manejador.ManejadorUsuario;
import com.tecnoshop.model.Usuario;
import com.tecnoshop.util.Validar;

public class UsuarioControlador implements IUsuarioControlador {
    private static UsuarioControlador instancia= null;

    // hash de una contraseña cualquiera, para que el login tarde lo mismo exista o no el email
    private static final String HASH_FALSO = BCrypt.hashpw("contraseña-que-no-es-de-nadie", BCrypt.gensalt());

    private UsuarioControlador(){}

    public static synchronized UsuarioControlador get(){
        if(instancia == null){
            instancia = new UsuarioControlador();
        }
        return instancia;
    }

    public void registrarUsuario(String nombre, String email, String password, boolean activo) throws ExisteUsuarioException, DatosInvalidosException{
        nombre = Validar.texto(nombre, "nombre", 255);
        email = Validar.email(email);
        Validar.password(password);
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        if(MU.existeUsuario(email)){
            throw new ExisteUsuarioException("Ya existe un usuario con el email dado");
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        Usuario S = new Usuario (nombre,email,hash,activo);
        MU.agregarUsuario(S);
    }


    public UsuarioDTO obtenerUsuarioPorId(int id){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        return MU.obtenerUsuarioPorId(id);
    }

    public List<UsuarioDTO> obtenerTodosLosUsuarios(){
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        return MU.obtenerTodosLosUsuarios();
    }


    public void activarUsuario(int id) throws NoExisteUsuarioException{
        buscar(id).cambiarEstadoUsuario(id,true);
    }

    public void desactivarUsuario(int id) throws NoExisteUsuarioException{
        buscar(id).cambiarEstadoUsuario(id,false);
    }

    public void cambiarNombreUsuario(int id, String nombre) throws NoExisteUsuarioException, DatosInvalidosException{
        nombre = Validar.texto(nombre, "nombre", 255);
        buscar(id).cambiarNombreUsuario(id, nombre);
    }

    public void cambiarEmailUsuario(int id, String email) throws NoExisteUsuarioException, ExisteUsuarioException, DatosInvalidosException{
        email = Validar.email(email);
        ManejadorUsuario MU = buscar(id);
        Usuario otro = MU.obtenerUsuarioPorEmail(email);
        if(otro != null && otro.getId() != id){
            throw new ExisteUsuarioException("Ya existe un usuario con el email dado");
        }
        MU.cambiarEmailUsuario(id, email);
    }

    public void cambiarPasswordUsuario(int id, String password) throws NoExisteUsuarioException, DatosInvalidosException{
        Validar.password(password);
        ManejadorUsuario MU = buscar(id);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        MU.cambiarPasswordUsuario(id, hash);
    }

    public UsuarioDTO login(String email, String password) throws CredencialesInvalidasException, UsuarioInactivoException{
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        Usuario usuario = email == null ? null : MU.obtenerUsuarioPorEmail(email.trim());
        // si el email no existe igual se compara contra un hash, asi por el tiempo de respuesta no se puede saber que emails estan registrados
        String hash = usuario != null ? usuario.getPassword() : HASH_FALSO;
        boolean passwordCorrecta = password != null && BCrypt.checkpw(password, hash);
        // mismo mensaje para email inexistente o password incorrecta, asi no se revela que emails estan registrados
        if(usuario == null || !passwordCorrecta){
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
        if(!usuario.isActivo()){
            throw new UsuarioInactivoException("El usuario esta inactivo");
        }
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.isActivo());
    }

    // devuelve el manejador si el usuario existe, asi cada metodo no repite el mismo if
    private ManejadorUsuario buscar(int id) throws NoExisteUsuarioException{
        ManejadorUsuario MU = ManejadorUsuario.getInstancia();
        if(MU.obtenerUsuarioPorId(id) == null){
            throw new NoExisteUsuarioException("No existe un usuario con el id dado");
        }
        return MU;
    }

}
