package com.unam.service;

import com.unam.model.Mascota;
import com.unam.model.TipoVacuna;
import com.unam.model.Turno;
import com.unam.model.Vacunacion;
import com.unam.repository.MascotaRepository;
import com.unam.repository.TipoVacunaRepository;
import com.unam.repository.VacunacionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Coordina el control de vacunaciones. La regla de "no reaplicar una vacuna
 * vigente" vive en Vacunacion.validarPuedeAplicarse (modelo); esta capa solo
 * consulta el historial previo de la mascota para poder invocarla.
 */
public class VacunacionService {

    private final VacunacionRepository vacunacionRepository = new VacunacionRepository();
    private final MascotaRepository mascotaRepository = new MascotaRepository();
    private final TipoVacunaRepository tipoVacunaRepository = new TipoVacunaRepository();

    public Vacunacion registrarVacunacion(Long mascotaId, Long tipoVacunaId, LocalDate fecha, Turno turnoAsociado) {
        Mascota mascota = mascotaRepository.buscarPorId(mascotaId)
            .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        TipoVacuna tipoVacuna = tipoVacunaRepository.buscarPorId(tipoVacunaId)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de vacuna no encontrado"));

        List<Vacunacion> previas = vacunacionRepository.buscarPorMascota(mascotaId);
        Vacunacion.validarPuedeAplicarse(tipoVacuna, previas);

        Vacunacion vacunacion = new Vacunacion(mascota, tipoVacuna, fecha, turnoAsociado);
        return vacunacionRepository.guardar(vacunacion);
    }

    public List<TipoVacuna> listarTiposVacuna() {
        return tipoVacunaRepository.listarTodos();
    }

    public List<Vacunacion> historialDeMascota(Long mascotaId) {
        return vacunacionRepository.buscarPorMascota(mascotaId);
    }

    /** Última vacunación de cada tipo, por mascota activa, para detectar vencidas/próximas a vencer. */
    public List<Vacunacion> ultimasVacunacionesRelevantes(int diasProximoVencimiento) {
        List<Vacunacion> resultado = new ArrayList<>();
        for (Mascota mascota : mascotaRepository.listarActivas()) {
            List<Vacunacion> previas = vacunacionRepository.buscarPorMascota(mascota.getId());
            Map<Long, Vacunacion> ultimaPorTipo = previas.stream()
                .collect(Collectors.toMap(
                    v -> v.getTipoVacuna().getId(),
                    v -> v,
                    (a, b) -> a.getFechaVacunacion().isAfter(b.getFechaVacunacion()) ? a : b));
            for (Vacunacion v : ultimaPorTipo.values()) {
                if (!v.estaVigente() || v.venceEnLosProximosDias(diasProximoVencimiento)) {
                    resultado.add(v);
                }
            }
        }
        return resultado;
    }
}
