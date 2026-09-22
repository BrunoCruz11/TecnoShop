package com.tecnoshop.excepciones;

public class ExisteUsuarioException extends Exception{
    public ExisteUsuarioException(String string){
        super(string);
    }

}