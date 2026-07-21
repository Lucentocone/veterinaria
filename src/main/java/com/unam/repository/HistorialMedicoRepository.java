package com.unam.repository;

import com.unam.model.HistorialMedico;

import java.util.Optional;

public class HistorialMedicoRepository extends RepositorioBase<HistorialMedico, Long> {

    public HistorialMedicoRepository() { super(HistorialMedico.class); }

    public Optional<HistorialMedico> buscarPorMascota(Long mascotaId) {
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT h FROM HistorialMedico h WHERE h.mascota.id = :mid", HistorialMedico.class)
            .setParameter("mid", mascotaId)
            .getResultStream().findFirst());
    }
}
