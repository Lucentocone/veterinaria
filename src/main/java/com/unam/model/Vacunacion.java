package com.unam.model;

import com.unam.exception.VacunaVigenteException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vacunaciones")
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_vacuna_id", nullable = false)
    private TipoVacuna tipoVacuna;

    @Column(nullable = false)
    private LocalDate fechaVacunacion;

    // Puede registrarse en el marco de un turno o directamente a la
    // mascota (según pide la pantalla de Control de Vacunaciones).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id")
    private Turno turno;

    public Vacunacion() {}

    public Vacunacion(Mascota mascota, TipoVacuna tipoVacuna, LocalDate fechaVacunacion, Turno turno) {
        this.mascota = mascota;
        this.tipoVacuna = tipoVacuna;
        this.fechaVacunacion = fechaVacunacion;
        this.turno = turno;
    }

    // --- Reglas de negocio ---

    public LocalDate calcularFechaVencimiento() {
        return fechaVacunacion.plusMonths(tipoVacuna.getPeriodicidad());
    }

    public boolean estaVigente() {
        return !calcularFechaVencimiento().isBefore(LocalDate.now());
    }

    public boolean venceEnLosProximosDias(int dias) {
        long restantes = ChronoUnit.DAYS.between(LocalDate.now(), calcularFechaVencimiento());
        return restantes >= 0 && restantes <= dias;
    }

    // Valida que se pueda aplicar una nueva vacuna de este tipo, dado el
    // historial de vacunaciones previas de la mascota (ya consultado por
    // la capa de repositorio). Si la última aplicación de ese tipo sigue
    // vigente, no debería volver a aplicarse.
    public static void validarPuedeAplicarse(TipoVacuna tipoVacuna, List<Vacunacion> vacunacionesPrevias) {
        vacunacionesPrevias.stream()
            .filter(v -> Objects.equals(v.getTipoVacuna().getId(), tipoVacuna.getId()))
            .max(Comparator.comparing(Vacunacion::getFechaVacunacion))
            .filter(Vacunacion::estaVigente)
            .ifPresent(ultima -> {
                throw new VacunaVigenteException(
                    "La mascota ya tiene la vacuna " + tipoVacuna.getNombreComercial()
                        + " vigente hasta " + ultima.calcularFechaVencimiento());
            });
    }

    public Long getId() { return id; }
    public Mascota getMascota() { return mascota; }
    public TipoVacuna getTipoVacuna() { return tipoVacuna; }
    public LocalDate getFechaVacunacion() { return fechaVacunacion; }
    public Turno getTurno() { return turno; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vacunacion that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return tipoVacuna.getNombreComercial() + " - " + fechaVacunacion
            + (estaVigente() ? " (vigente)" : " (vencida)");
    }
}