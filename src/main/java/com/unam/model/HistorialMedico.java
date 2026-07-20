package com.unam.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observaciones;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false, unique = true)
    private Mascota mascota;

    public HistorialMedico() {}

    public HistorialMedico(Mascota mascota) {
        this.mascota = mascota;
        this.observaciones = "";
    }

    public void agregarObservacion(String observacion) {
        if (observacion == null || observacion.isBlank()) return;
        observaciones = (observaciones == null || observaciones.isBlank())
            ? observacion
            : observaciones + "\n" + observacion;
    }

    // Arma la línea de tiempo cronológica combinando turnos atendidos y
    // vacunaciones aplicadas. Recibe ambas listas ya resueltas por el
    // repositorio (esta entidad no consulta la base de datos).
    public List<EntradaHistorial> construirLineaDeTiempo(List<Turno> turnosAtendidos, List<Vacunacion> vacunaciones) {
        List<EntradaHistorial> entradas = new ArrayList<>();

        for (Turno turno : turnosAtendidos) {
            String detalle = turno.getDetalles().stream()
                .map(d -> d.getServicio().getNombre())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin servicios");
            entradas.add(new EntradaHistorial(
                turno.getFechaHora(), "TURNO",
                detalle + " - Dr. " + turno.getVeterinario().getNombre()));
        }

        for (Vacunacion vacunacion : vacunaciones) {
            entradas.add(new EntradaHistorial(
                vacunacion.getFechaVacunacion().atStartOfDay(), "VACUNA",
                vacunacion.getTipoVacuna().getNombreComercial()));
        }

        entradas.sort(Comparator.comparing(EntradaHistorial::fecha));
        return entradas;
    }

    public Long getId() { return id; }
    public String getObservaciones() { return observaciones; }
    public Mascota getMascota() { return mascota; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistorialMedico that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}