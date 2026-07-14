package com.unam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarios")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVeterinario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String matricula;

    @Enumerated(EnumType.STRING)
    private Especialidad especialidad;

    @OneToMany(mappedBy = "veterinario")
    private List<Turno> turnos = new ArrayList<>();

    public Veterinario() {}

    public Veterinario(String nombre, String apellido, String matricula, Especialidad especialidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
        this.especialidad = especialidad;
    }

    public boolean tieneTurnoSolapado(LocalDateTime nuevaFechaHora, int duracionMinutos) {
        LocalDateTime nuevoFin = nuevaFechaHora.plusMinutes(duracionMinutos);
        return turnos.stream()
            .filter(t -> t.getEstadoDeTurno() != EstadoTurno.CANCELADO)
            .anyMatch(t -> {
                LocalDateTime inicio = t.getFechaHora();
                LocalDateTime fin = inicio.plusMinutes(t.getDuracionTotal());
                return nuevaFechaHora.isBefore(fin) && nuevoFin.isAfter(inicio);
            });
    }

    public Long getIdVeterinario() { return idVeterinario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }
    public List<Turno> getTurnos() { return turnos; }

    @Override
    public String toString() {
        return "Dr/a. " + apellido + ", " + nombre + " - Mat: " + matricula;
    }
}