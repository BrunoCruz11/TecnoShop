package com.tecnoshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecnoshop.api.ApiServidor;
import com.tecnoshop.util.CargaDatos;
import com.tecnoshop.util.Config;

public class Main {

    static {
        // Hibernate loguea con jboss-logging; asi usa SLF4J y todos los logs salen con el mismo formato
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info("Iniciando TecnoShop en modo {}", Config.PRODUCCION ? "produccion" : "desarrollo");
        // el primer acceso a la base corre las migraciones de Flyway (ver HibernateUtil)
        CargaDatos.crearAdministradorSiNoHayUsuarios();
        if (Config.CARGAR_DATOS) {
            CargaDatos.cargarProductos();
        }
        ApiServidor.iniciar(Config.PUERTO);
        LOG.info("API escuchando en el puerto {}", Config.PUERTO);
    }
}
