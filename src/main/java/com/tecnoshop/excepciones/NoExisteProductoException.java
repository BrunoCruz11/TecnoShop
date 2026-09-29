package com.tecnoshop.excepciones;

public class NoExisteProductoException extends Exception{
    public NoExisteProductoException(String string){
        super(string);
    }

}
