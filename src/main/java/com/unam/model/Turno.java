package com.unam.model;

import com.unam.enums.EstadoTurno;
import com.unam.exception.CancelacionFueraDeTerminoException;
import com.unam.exception.EstadoTurnoInvalidoException;
import com.unam.exception.TurnoSinServiciosException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado;

    @Column(nullable = false)
    private BigDecimal precio = BigDecimal.ZERO;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTurno> detalles = new ArrayList<>();

    public Turno() {}

    public Turno(Mascota mascota, Veterinario veterinario, LocalDateTime fechaHora) {
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.fechaHora = fechaHora;
        this.estado = EstadoTurno.PENDIENTE;
    }

    // --- Servicios y precio ---

    // Agrega un servicio, delega en él su propia validación específica
    // (ej: cupo de guardería) y congela su precio actual en un DetalleTurno.
    public void agregarServicio(Servicio servicio) {
        if (estado != EstadoTurno.PENDIENTE) {
            throw new EstadoTurnoInvalidoException(
                "Solo se pueden agregar servicios a un turno PENDIENTE");
        }
        servicio.validarParaMascota(mascota);
        detalles.add(new DetalleTurno(this, servicio));
        recalcularPrecio();
    }

    private void recalcularPrecio() {
        precio = detalles.stream()
            .map(DetalleTurno::getPrecioCobrado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Duration calcularDuracionTotal() {
        int minutos = detalles.stream()
            .mapToInt(d -> d.getServicio().getDuracionMinutos())
            .sum();
        return Duration.ofMinutes(minutos);
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHora.plus(calcularDuracionTotal());
    }

    // Compara este turno contra OTRO puntual. Decidir contra qué turnos
    // comparar (los del mismo veterinario o mascota) es tarea de la capa
    // de servicio, que consulta la base de datos; esta entidad no debe
    // llamar a JPA directamente.
    public boolean seSolapaCon(Turno otro) {
        if (otro == null || otro == this || otro.estado == EstadoTurno.CANCELADO) {
            return false;
        }
        return fechaHora.isBefore(otro.getFechaHoraFin())
            && otro.getFechaHora().isBefore(getFechaHoraFin());
    }

    // --- Máquina de estados ---

    public void confirmar() {
        if (estado != EstadoTurno.PENDIENTE) {
            throw new EstadoTurnoInvalidoException(
                "Solo un turno PENDIENTE puede confirmarse (estado actual: " + estado + ")");
        }
        if (detalles.isEmpty()) {
            throw new TurnoSinServiciosException(
                "No se puede confirmar un turno sin servicios asociados");
        }
        estado = EstadoTurno.CONFIRMADO;
    }

    public void atender() {
        if (estado != EstadoTurno.CONFIRMADO) {
            throw new EstadoTurnoInvalidoException(
                "Solo un turno CONFIRMADO puede pasar a ATENDIDO (estado actual: " + estado + ")");
        }
        estado = EstadoTurno.ATENDIDO;
    }

    public boolean puedeCancelarse() {
        if (estado == EstadoTurno.ATENDIDO || estado == EstadoTurno.CANCELADO) {
            return false;
        }
        return Duration.between(LocalDateTime.now(), fechaHora).toHours() >= 24;
    }

    public void cancelar() {
        if (estado == EstadoTurno.ATENDIDO) {
            throw new EstadoTurnoInvalidoException("Un turno ATENDIDO no puede cancelarse");
        }
        if (estado == EstadoTurno.CANCELADO) {
            throw new EstadoTurnoInvalidoException("El turno ya está cancelado");
        }
        if (!puedeCancelarse()) {
            throw new CancelacionFueraDeTerminoException(
                "La cancelación requiere al menos 24hs de anticipación");
        }
        estado = EstadoTurno.CANCELADO;
    }

    // --- Getters ---

    public Long getId() { return id; }
    public Mascota getMascota() { return mascota; }
    public Veterinario getVeterinario() { return veterinario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoTurno getEstado() { return estado; }
    public BigDecimal getPrecio() { return precio; }
    public List<DetalleTurno> getDetalles() { return Collections.unmodifiableList(detalles); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Turno turno)) return false;
        return id != null && id.equals(turno.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return fechaHora + " - " + mascota.getNombre() + " con " + veterinario.getNombre() + " (" + estado + ")";
    }
}