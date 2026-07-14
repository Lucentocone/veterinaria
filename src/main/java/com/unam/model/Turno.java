package com.unam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
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

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estadoDeTurno = EstadoTurno.PENDIENTE;

    // Precio total congelado al confirmar el turno
    private BigDecimal precio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTurno> detalles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "historial_medico_id")
    private HistorialMedico historialMedico;

    public Turno() {}

    public Turno(LocalDateTime fechaHora, Mascota mascota, Veterinario veterinario) {
        if (mascota == null) throw new IllegalArgumentException("La mascota no puede ser nula.");
        if (veterinario == null) throw new IllegalArgumentException("El veterinario no puede ser nulo.");
        this.fechaHora = fechaHora;
        this.mascota = mascota;
        this.veterinario = veterinario;
    }

    public void agregarServicio(Servicio servicio) {
        DetalleTurno detalle = new DetalleTurno(this, servicio);
        detalles.add(detalle);
    }

    public void confirmar() {
        if (estadoDeTurno != EstadoTurno.PENDIENTE) {
            throw new IllegalStateException("Solo se puede confirmar un turno PENDIENTE.");
        }
        if (detalles.isEmpty()) {
            throw new IllegalStateException("El turno debe tener al menos un servicio.");
        }
        this.precio = calcularPrecioTotal();
        this.estadoDeTurno = EstadoTurno.CONFIRMADO;
    }

    public void atender() {
        if (estadoDeTurno != EstadoTurno.CONFIRMADO) {
            throw new IllegalStateException("Solo se puede atender un turno CONFIRMADO.");
        }
        this.estadoDeTurno = EstadoTurno.ATENDIDO;
    }

    public void cancelar() {
        if (estadoDeTurno == EstadoTurno.ATENDIDO) {
            throw new IllegalStateException("No se puede cancelar un turno ya ATENDIDO.");
        }
        if (estadoDeTurno == EstadoTurno.CANCELADO) {
            throw new IllegalStateException("El turno ya está CANCELADO.");
        }
        if (LocalDateTime.now().isAfter(fechaHora.minusHours(24))) {
            throw new IllegalStateException("No se puede cancelar con menos de 24 horas de anticipación.");
        }
        this.estadoDeTurno = EstadoTurno.CANCELADO;
    }

    public int getDuracionTotal() {
        return detalles.stream()
            .mapToInt(d -> d.getServicio().getDuracionMinutos())
            .sum();
    }

    private BigDecimal calcularPrecioTotal() {
        return detalles.stream()
            .map(DetalleTurno::getPrecioCobrado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean seSolapa(LocalDateTime otraFechaHora, int otraDuracion) {
        LocalDateTime esteFin = fechaHora.plusMinutes(getDuracionTotal());
        LocalDateTime otroFin = otraFechaHora.plusMinutes(otraDuracion);
        return fechaHora.isBefore(otroFin) && esteFin.isAfter(otraFechaHora);
    }

    public Long getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public EstadoTurno getEstadoDeTurno() { return estadoDeTurno; }
    public BigDecimal getPrecio() { return precio; }
    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }
    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }
    public List<DetalleTurno> getDetalles() { return Collections.unmodifiableList(detalles); }
    public HistorialMedico getHistorialMedico() { return historialMedico; }
    public void setHistorialMedico(HistorialMedico historialMedico) { this.historialMedico = historialMedico; }
}
