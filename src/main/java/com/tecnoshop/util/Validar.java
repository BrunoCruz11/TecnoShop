package com.tecnoshop.util;

import java.util.regex.Pattern;

import com.tecnoshop.excepciones.DatosInvalidosException;

/**
 * Validaciones de formato que usan todos los controladores.
 * Los metodos que reciben texto devuelven el valor limpio (sin espacios de mas) para guardar ese.
 */
public final class Validar {

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private Validar() {
    }

    public static String texto(String valor, String campo, int maximo) throws DatosInvalidosException {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new DatosInvalidosException("El campo " + campo + " no puede tener mas de " + maximo + " caracteres");
        }
        return limpio;
    }

    public static String textoOpcional(String valor, String campo, int maximo) throws DatosInvalidosException {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        return texto(valor, campo, maximo);
    }

    // el email se guarda en minusculas para que "Ana@Mail.com" y "ana@mail.com" sean el mismo usuario
    public static String email(String valor) throws DatosInvalidosException {
        String limpio = texto(valor, "email", 255).toLowerCase();
        if (!EMAIL.matcher(limpio).matches()) {
            throw new DatosInvalidosException("El email no tiene un formato valido");
        }
        return limpio;
    }

    // BCrypt solo usa los primeros 72 bytes, por eso el maximo
    public static void password(String valor) throws DatosInvalidosException {
        if (valor == null || valor.length() < 8) {
            throw new DatosInvalidosException("La contraseña tiene que tener al menos 8 caracteres");
        }
        if (valor.length() > 72) {
            throw new DatosInvalidosException("La contraseña no puede tener mas de 72 caracteres");
        }
    }

    // imagen opcional: una URL http(s) o una ruta del front (img/...). Nada de "javascript:" ni otros esquemas
    public static String imagenUrl(String valor) throws DatosInvalidosException {
        String limpio = textoOpcional(valor, "imagen", 500);
        if (limpio.isEmpty()) {
            return null;
        }
        if (!limpio.startsWith("https://") && !limpio.startsWith("http://") && !limpio.startsWith("img/")) {
            throw new DatosInvalidosException("La imagen tiene que ser una URL que empiece con http:// o https://");
        }
        return limpio;
    }

    public static void noNegativo(double numero, String campo) throws DatosInvalidosException {
        if (!Double.isFinite(numero) || numero < 0) {
            throw new DatosInvalidosException("El campo " + campo + " no puede ser negativo");
        }
    }

    public static void positivo(double numero, String campo) throws DatosInvalidosException {
        if (!Double.isFinite(numero) || numero <= 0) {
            throw new DatosInvalidosException("El campo " + campo + " tiene que ser mayor a 0");
        }
    }

    public static void obligatorio(Object valor, String campo) throws DatosInvalidosException {
        if (valor == null) {
            throw new DatosInvalidosException("El campo " + campo + " es obligatorio");
        }
    }
}
