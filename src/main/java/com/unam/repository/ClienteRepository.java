package com.unam.repository;

import com.unam.model.Cliente;
import com.unam.model.Mascota;

import java.util.List;
import java.util.Optional;

public class ClienteRepository extends RepositorioBase<Cliente, Long> {

    public ClienteRepository() { super(Cliente.class); }

    public Optional<Cliente> buscarPorDni(String dni) {
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT c FROM Cliente c WHERE c.dni = :dni", Cliente.class)
            .setParameter("dni", dni)
            .getResultStream().findFirst());
    }

    public List<Cliente> buscarPorNombreOApellido(String texto) {
        String like = "%" + texto.toLowerCase() + "%";
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE :t OR LOWER(c.apellido) LIKE :t OR LOWER(c.dni) LIKE :t",
                Cliente.class)
            .setParameter("t", like)
            .getResultList());
    }

    /**
     * Agrega la mascota al cliente y persiste todo dentro de UNA sola
     * transacción: buscamos el Cliente ya "managed" en este mismo
     * EntityManager (no uno detached de otra consulta) para poder tocar
     * su colección de mascotas sin disparar LazyInitializationException.
     */
    public Mascota agregarMascota(Long clienteId, Mascota mascota) {
        return ejecutarEnTransaccion(em -> {
            Cliente cliente = em.find(Cliente.class, clienteId);
            if (cliente == null) {
                throw new IllegalArgumentException("Cliente no encontrado");
            }
            cliente.agregarMascota(mascota);
            em.persist(mascota);
            return mascota;
        });
    }
}

