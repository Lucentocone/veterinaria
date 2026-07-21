package com.unam.repository;

import com.unam.model.Turno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TurnoRepository extends RepositorioBase<Turno, Long> {

    private static final String PARAM_INICIO = "inicio";
    private static final String PARAM_FIN = "fin";

    public TurnoRepository() { super(Turno.class); }

    public List<Turno> buscarPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT DISTINCT t FROM Turno t " +
                "JOIN FETCH t.mascota " +
                "JOIN FETCH t.veterinario " +
                "LEFT JOIN FETCH t.detalles d " +
                "LEFT JOIN FETCH d.servicio " +
                "WHERE t.fechaHora >= :inicio AND t.fechaHora < :fin ORDER BY t.fechaHora",
                Turno.class)
            .setParameter(PARAM_INICIO, inicio)
            .setParameter(PARAM_FIN, fin)
            .getResultList());
    }

    public List<Turno> buscarPorVeterinarioEnFecha(Long veterinarioId, LocalDate fecha) {
        LocalDateTime inicio = fecha.minusDays(1).atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT DISTINCT t FROM Turno t " +
                "LEFT JOIN FETCH t.detalles d " +
                "LEFT JOIN FETCH d.servicio " +
                "WHERE t.veterinario.id = :vid AND t.fechaHora >= :inicio AND t.fechaHora < :fin",
                Turno.class)
            .setParameter("vid", veterinarioId)
            .setParameter(PARAM_INICIO, inicio)
            .setParameter(PARAM_FIN, fin)
            .getResultList());
    }

    public List<Turno> buscarPorMascotaEnFecha(Long mascotaId, LocalDate fecha) {
        LocalDateTime inicio = fecha.minusDays(1).atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT DISTINCT t FROM Turno t " +
                "LEFT JOIN FETCH t.detalles d " +
                "LEFT JOIN FETCH d.servicio " +
                "WHERE t.mascota.id = :mid AND t.fechaHora >= :inicio AND t.fechaHora < :fin",
                Turno.class)
            .setParameter("mid", mascotaId)
            .setParameter(PARAM_INICIO, inicio)
            .setParameter(PARAM_FIN, fin)
            .getResultList());
    }

    public List<Turno> buscarPorMascota(Long mascotaId) {
        return ejecutarConsulta(em -> em.createQuery(
                "SELECT DISTINCT t FROM Turno t " +
                "JOIN FETCH t.mascota " +
                "JOIN FETCH t.veterinario " +
                "LEFT JOIN FETCH t.detalles d " +
                "LEFT JOIN FETCH d.servicio " +
                "WHERE t.mascota.id = :mid ORDER BY t.fechaHora", Turno.class)
            .setParameter("mid", mascotaId)
            .getResultList());
    }
}