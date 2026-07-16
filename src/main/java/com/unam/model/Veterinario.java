package com.unam.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.unam.enums.Especialidad;

@Entity
@Table(name = "veterinarios")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidad especialidad;

    // Lado inverso: solo lectura, el Veterinario no controla el ciclo de
    // vida de sus turnos (no hay cascade). Sirve para consultar
    // "turnos atendidos por este veterinario" sin duplicar lógica.
    @OneToMany(mappedBy = "veterinario")
    private List<Turno> turnos = new ArrayList<>();

    public Veterinario() {}

    public Veterinario(String nombre, String telefono, String email, Especialidad especialidad) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.especialidad = especialidad;
    }

    public List<Turno> getTurnos() { return Collections.unmodifiableList(turnos); }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Veterinario that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() { return nombre + " (" + especialidad + ")"; }
}