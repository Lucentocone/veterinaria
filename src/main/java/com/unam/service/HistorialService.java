package com.unam.service;

import com.unam.enums.EstadoTurno;
import com.unam.model.EntradaHistorial;
import com.unam.model.HistorialMedico;
import com.unam.model.Mascota;
import com.unam.model.Turno;
import com.unam.model.Vacunacion;
import com.unam.repository.MascotaRepository;
import com.unam.repository.TurnoRepository;
import com.unam.repository.VacunacionRepository;

import java.util.List;

/**
 * Arma la línea de tiempo del historial médico de una mascota. La
 * construcción de la línea de tiempo en sí es responsabilidad de
 * HistorialMedico (modelo rico); esta capa solo junta los datos que
 * necesita desde los repositorios.
 */
public class HistorialService {

    private final MascotaRepository mascotaRepository = new MascotaRepository();
    private final TurnoRepository turnoRepository = new TurnoRepository();
    private final VacunacionRepository vacunacionRepository = new VacunacionRepository();

    public List<EntradaHistorial> obtenerHistorial(Long mascotaId) {
        Mascota mascota = mascotaRepository.buscarPorId(mascotaId)
            .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

        List<Turno> turnosAtendidos = turnoRepository.buscarPorMascota(mascotaId).stream()
            .filter(t -> t.getEstado() == EstadoTurno.ATENDIDO)
            .toList();
        List<Vacunacion> vacunaciones = vacunacionRepository.buscarPorMascota(mascotaId);

        HistorialMedico historial = new HistorialMedico(mascota);
        return historial.construirLineaDeTiempo(turnosAtendidos, vacunaciones);
    }

    public List<Mascota> buscarMascotas(String texto) {
        return new MascotaRepository().buscarPorNombreOFicha(texto == null ? "" : texto);
    }
}
