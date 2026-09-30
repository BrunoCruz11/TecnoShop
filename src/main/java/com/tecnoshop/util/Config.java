package com.tecnoshop.util;

import java.util.Arrays;
import java.util.List;

/**
 * Toda la configuracion sale de variables de entorno, asi el mismo .jar sirve para desarrollo y produccion
 * y ninguna contraseña queda escrita en el codigo.
 *
 * En desarrollo (APP_ENV=dev, el valor por defecto) cada variable tiene un valor comodo para trabajar local.
 * En produccion (APP_ENV=prod) las variables sensibles son obligatorias: si faltan, la app no arranca.
 */
public final class Config {

    private Config() {
    }

    public static final boolean PRODUCCION = "prod".equalsIgnoreCase(env("APP_ENV", "dev"));

    public static final int PUERTO = Integer.parseInt(env("PORT", "8080"));

    public static final String DB_URL = requerida("DB_URL", "jdbc:postgresql://localhost:5433/tecnoshop_db");
    public static final String DB_USER = requerida("DB_USER", "tecnoshop");
    public static final String DB_PASSWORD = requerida("DB_PASSWORD", "tecnoshop");

    // clave con la que se firman los tokens de sesion; en produccion tiene que ser larga y secreta
    public static final String JWT_SECRET = requerida("JWT_SECRET", "clave-solo-para-desarrollo-no-usar-en-produccion");
    public static final int JWT_HORAS = Integer.parseInt(env("JWT_HORAS", "8"));

    // origenes que pueden llamar a la API desde el navegador (separados por coma). Vacio = ninguno.
    // En produccion el front y la API se sirven desde el mismo origen (nginx), asi que no hace falta.
    public static final List<String> CORS_ORIGENES = lista(env("CORS_ORIGENES", PRODUCCION ? "" : "http://localhost:5500,http://127.0.0.1:5500"));

    // carga los productos de ejemplo al arrancar
    public static final boolean CARGAR_DATOS = Boolean.parseBoolean(env("CARGAR_DATOS", PRODUCCION ? "false" : "true"));

    // primer usuario: se crea solo si la tabla de usuarios esta vacia
    public static final String ADMIN_EMAIL = env("ADMIN_EMAIL", PRODUCCION ? "" : "admin@tecnoshop.com");
    public static final String ADMIN_PASSWORD = env("ADMIN_PASSWORD", PRODUCCION ? "" : "admin1234");

    public static final boolean MOSTRAR_SQL = Boolean.parseBoolean(env("MOSTRAR_SQL", "false"));

    // detras de nginx, la IP real del cliente llega en el header X-Real-IP (se usa para limitar intentos de login).
    // Solo se activa si la API no esta expuesta directo a internet, porque ese header lo puede inventar cualquiera.
    public static final boolean CONFIAR_EN_PROXY = Boolean.parseBoolean(env("CONFIAR_EN_PROXY", PRODUCCION ? "true" : "false"));

    static {
        // una clave corta se puede adivinar por fuerza bruta y con ella cualquiera podria fabricar tokens
        if (PRODUCCION && JWT_SECRET.length() < 32) {
            throw new IllegalStateException("JWT_SECRET tiene que tener al menos 32 caracteres");
        }
    }

    private static String env(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? porDefecto : valor.trim();
    }

    private static String requerida(String nombre, String valorDesarrollo) {
        String valor = System.getenv(nombre);
        if (valor != null && !valor.isBlank()) {
            return valor.trim();
        }
        if (PRODUCCION) {
            throw new IllegalStateException("Falta la variable de entorno " + nombre + " (obligatoria con APP_ENV=prod)");
        }
        return valorDesarrollo;
    }

    private static List<String> lista(String valor) {
        return Arrays.stream(valor.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
