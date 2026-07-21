package com.unam.service;

import com.unam.exception.EstadoTurnoInvalidoException;
import com.unam.model.Mascota;
import com.unam.model.Servicio;
import com.unam.model.Turno;
import com.unam.model.Veterinario;
import com.unam.repository.MascotaRepository;
import com.unam.repository.ServicioRepository;
import com.unam.repository.TurnoRepository;
import com.unam.repository.VeterinarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orquesta la creación y el ciclo de vida de los turnos. La validación de
 * solapamiento necesita consultar otros turnos en base de datos (algo que
 * una entidad no debería hacer), pero la comparación en sí (Turno.seSolapaCon)
 * vive en el modelo: aquí solo se decide CONTRA QUÉ turnos comparar.
 */
public class TurnoService {

    private final TurnoRepository turnoRepository = new TurnoRepository();
    private final MascotaRepository mascotaRepository = new MascotaRepository();
    private final VeterinarioRepository veterinarioRepository = new VeterinarioRepository();
    private final ServicioRepository servicioRepository = new ServicioRepository();

    public Turno crearTurno(Long mascotaId, Long veterinarioId, LocalDateTime fechaHora, List<Long> servicioIds) {
        Mascota mascota = mascotaRepository.buscarPorId(mascotaId)
            .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        Veterinario veterinario = veterinarioRepository.buscarPorId(veterinarioId)
            .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));

        Turno turno = new Turno(mascota, veterinario, fechaHora);
        for (Long servicioId : servicioIds) {
            Servicio servicio = servicioRepository.buscarPorId(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
            turno.agregarServicio(servicio);
        }

        validarSinSolapamientos(turno);
        return turnoRepository.guardar(turno);
    }

    private void validarSinSolapamientos(Turno turno) {
        LocalDate fecha = turno.getFechaHora().toLocalDate();

        boolean solapaConVeterinario = turnoRepository
            .buscarPorVeterinarioEnFecha(turno.getVeterinario().getId(), fecha).stream()
            .anyMatch(turno::seSolapaCon);
        if (solapaConVeterinario) {
            throw new EstadoTurnoInvalidoException(
                "El veterinario ya tiene un turno asignado que se superpone con ese horario.");
        }

        boolean solapaConMascota = turnoRepository
            .buscarPorMascotaEnFecha(turno.getMascota().getId(), fecha).stream()
            .anyMatch(turno::seSolapaCon);
        if (solapaConMascota) {
            throw new EstadoTurnoInvalidoException(
                "La mascota ya tiene un turno asignado que se superpone con ese horario.");
        }
    }

    public List<Turno> listarPorFecha(LocalDate fecha) {
        return turnoRepository.buscarPorFecha(fecha);
    }

    public List<Turno> listarPorMascota(Long mascotaId) {
        return turnoRepository.buscarPorMascota(mascotaId);
    }

    public Turno confirmar(Turno turno) {
        turno.confirmar();
        return turnoRepository.actualizar(turno);
    }

    public Turno atender(Turno turno, String diagnostico, String tratamiento) {
        turno.atender();
        turno.getDetalles().stream()
            .map(d -> d.getServicio())
            .filter(s -> s instanceof com.unam.model.Consulta)
            .map(s -> (com.unam.model.Consulta) s)
            .forEach(c -> {
                if (diagnostico != null && !diagnostico.isBlank()) {
                    c.registrarDiagnostico(diagnostico, tratamiento);
                }
            });
        return turnoRepository.actualizar(turno);
    }

    public Turno cancelar(Turno turno) {
        turno.cancelar();
        return turnoRepository.actualizar(turno);
    }

    public List<Servicio> listarServicios() {
        return servicioRepository.listarTodos();
    }

    public List<Veterinario> listarVeterinarios() {
        return veterinarioRepository.listarTodos();
    }
}
