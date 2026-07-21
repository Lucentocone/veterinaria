package com.unam.repository;

import com.unam.persistence.PersistenceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Optional;
import java.util.function.Function;

/**
 * Repositorio base genérico: encapsula el ciclo de vida del EntityManager y
 * las transacciones para que las clases concretas (y sobre todo la capa de
 * servicio) no tengan que repetir ese código ni hablar con EntityManager
 * directamente.
 */
public abstract class RepositorioBase<T, ID> {

    private final Class<T> tipo;

    protected RepositorioBase(Class<T> tipo) {
        this.tipo = tipo;
    }

    public T guardar(T entidad) {
        return ejecutarEnTransaccion(em -> {
            em.persist(entidad);
            return entidad;
        });
    }

    public T actualizar(T entidad) {
        return ejecutarEnTransaccion(em -> em.merge(entidad));
    }

    public Optional<T> buscarPorId(ID id) {
        try (EntityManager em = PersistenceManager.crearEntityManager()) {
            return Optional.ofNullable(em.find(tipo, id));
        }
    }

    public java.util.List<T> listarTodos() {
        try (EntityManager em = PersistenceManager.crearEntityManager()) {
            return em.createQuery("SELECT e FROM " + tipo.getSimpleName() + " e", tipo).getResultList();
        }
    }

    /**
     * Ejecuta una operación de lectura dentro de un EntityManager, delegando
     * la construcción de la consulta a las subclases.
     */
    protected <R> R ejecutarConsulta(Function<EntityManager, R> operacion) {
        try (EntityManager em = PersistenceManager.crearEntityManager()) {
            return operacion.apply(em);
        }
    }

    /**
     * Ejecuta una operación de escritura dentro de una transacción,
     * garantizando commit/rollback consistente.
     */
    protected <R> R ejecutarEnTransaccion(Function<EntityManager, R> operacion) {
        EntityManager em = PersistenceManager.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = operacion.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
