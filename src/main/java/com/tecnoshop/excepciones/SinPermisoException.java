package com.tecnoshop.excepciones;

// el usuario tiene sesion pero no puede hacer esa accion (ej. borrar la reseña de otro)
public class SinPermisoException extends Exception{
    public SinPermisoException(String string){
        super(string);
    }

}
