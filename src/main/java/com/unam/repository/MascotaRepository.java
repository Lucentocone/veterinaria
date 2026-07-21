package com.unam.repository;

import com.unam.model.Mascota;

import java.util.List;

public class MascotaRepository extends RepositorioBase<Mascota, Long> {

    public MascotaRepository() { super(Mascota.class); }

    public List<Mascota> buscarPorCliente(Long clienteId) {
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT m FROM Mascota m WHERE m.cliente.id = :cid ORDER BY m.nombre", Mascota.class)
            .setParameter("cid", clienteId)
            .getResultList());
    }

    public List<Mascota> buscarPorNombreOFicha(String texto) {
        String like = "%" + texto.toLowerCase() + "%";
        return ejecutarConsulta(em -> {
            var query = em.createQuery(
                "SELECT m FROM Mascota m WHERE LOWER(m.nombre) LIKE :t OR CAST(m.id AS string) = :exacto ORDER BY m.nombre",
                Mascota.class);
            query.setParameter("t", like);
            query.setParameter("exacto", texto.trim());
            return query.getResultList();
        });
    }

    public List<Mascota> listarActivas() {
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT m FROM Mascota m WHERE m.activa = true ORDER BY m.nombre", Mascota.class)
            .getResultList());
    }
}
