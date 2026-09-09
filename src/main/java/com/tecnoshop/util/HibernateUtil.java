package com.tecnoshop.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Punto único de acceso al EntityManagerFactory de la persistence-unit
 * "tecnoshopPU" (ver src/main/resources/META-INF/persistence.xml).
 */
public final class HibernateUtil {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("tecnoshopPU");

    private HibernateUtil() {
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        EMF.close();
    }
}
