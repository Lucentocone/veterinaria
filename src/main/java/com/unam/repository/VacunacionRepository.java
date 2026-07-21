package com.unam.repository;

import com.unam.model.Vacunacion;

import java.util.List;

public class VacunacionRepository extends RepositorioBase<Vacunacion, Long> {

    public VacunacionRepository() { super(Vacunacion.class); }

    public List<Vacunacion> buscarPorMascota(Long mascotaId) {
    return ejecutarConsulta(em -> em.createQuery(
            "SELECT v FROM Vacunacion v " +
            "JOIN FETCH v.mascota " +
            "JOIN FETCH v.tipoVacuna " +
            "WHERE v.mascota.id = :mid ORDER BY v.fechaVacunacion DESC",
            Vacunacion.class)
        .setParameter("mid", mascotaId)
        .getResultList());
}
}
