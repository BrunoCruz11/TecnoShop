package com.tecnoshop.excepciones;

public class NoExisteUsuarioException extends Exception{
    public NoExisteUsuarioException(String string){
        super(string);
    }

}
