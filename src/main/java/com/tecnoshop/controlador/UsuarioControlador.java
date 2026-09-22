package com.tecnoshop.controlador;

import com.tecnoshop.excepciones.ExisteUsuarioException;
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
            Usuario S = new Usuario (nombre,email,password,true);
            MU.agregarUsuario(S);
        }
    }

}
