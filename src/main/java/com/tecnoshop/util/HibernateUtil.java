package com.tecnoshop.util;

import java.util.HashMap;
import java.util.Map;

import org.flywaydb.core.Flyway;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Punto único de acceso al EntityManagerFactory de la persistence-unit
 * "tecnoshopPU" (ver src/main/resources/META-INF/persistence.xml).
 *
 * Antes de crear el EntityManagerFactory corre las migraciones de Flyway, asi la base
 * siempre tiene el esquema que esperan las entidades (Hibernate solo lo valida, no lo modifica).
 */
public final class HibernateUtil {

    private static final EntityManagerFactory EMF = crear();

    private HibernateUtil() {
    }

    private static EntityManagerFactory crear() {
        migrar();

        // los datos de conexion vienen de Config (variables de entorno), no del persistence.xml
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", Config.DB_URL);
        propiedades.put("jakarta.persistence.jdbc.user", Config.DB_USER);
        propiedades.put("jakarta.persistence.jdbc.password", Config.DB_PASSWORD);
        propiedades.put("hibernate.show_sql", String.valueOf(Config.MOSTRAR_SQL));
        return Persistence.createEntityManagerFactory("tecnoshopPU", propiedades);
    }

    private static void migrar() {
        Flyway.configure()
                .dataSource(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD)
                // una base que ya tenia tablas (creadas antes por Hibernate) se toma como si ya tuviera la V1
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load()
                .migrate();
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    // la usa /api/salud para saber si la base responde
    public static boolean baseDisponible() {
        EntityManager em = EMF.createEntityManager();
        try {
            em.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (RuntimeException e) {
            return false;
        } finally {
            em.close();
        }
    }

    public static void shutdown() {
        EMF.close();
    }
}
