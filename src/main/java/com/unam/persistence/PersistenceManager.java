package com.unam.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Punto único de acceso a JPA. Crea un solo EntityManagerFactory para toda
 * la aplicación (es costoso de crear, pero seguro para compartir) y entrega
 * un EntityManager nuevo por operación, que es liviano y no thread-safe.
 */
public final class PersistenceManager {

    private static final String UNIT_NAME = "veterinariaPU";
    private static EntityManagerFactory factory;

    private PersistenceManager() {}

    public static synchronized EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(UNIT_NAME);
        }
        return factory;
    }

    public static EntityManager crearEntityManager() {
        return getFactory().createEntityManager();
    }

    public static synchronized void cerrar() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
