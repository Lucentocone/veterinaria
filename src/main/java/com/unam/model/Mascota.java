package com.unam.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMascota;

    @Column(nullable = false)
    private String nombre;

    private String raza;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(unique = true, nullable = false)
    private String numeroFicha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    private boolean activa = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    private List<Vacunacion> vacunaciones = new ArrayList<>();

    public Mascota() {}

    public Mascota(String nombre, String raza, LocalDate fechaNacimiento,
                   String numeroFicha, Especie especie, Cliente cliente) {
        this.nombre = nombre;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroFicha = numeroFicha;
        this.especie = especie;
        this.cliente = cliente;
    }

    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public boolean tieneVacunaVigente(TipoVacuna tipoVacuna) {
        return vacunaciones.stream()
            .filter(v -> v.getTipoVacuna().equals(tipoVacuna))
            .anyMatch(Vacunacion::estaVigente);
    }

    public List<Vacunacion> getVacunaciones() {
        return Collections.unmodifiableList(vacunaciones);
    }

    public void agregarVacunacion(Vacunacion vacunacion) {
        vacunaciones.add(vacunacion);
    }

    public Long getIdMascota() { return idMascota; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getNumeroFicha() { return numeroFicha; }
    public void setNumeroFicha(String numeroFicha) { this.numeroFicha = numeroFicha; }
    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public String toString() {
        return nombre + " (" + especie + " - Ficha: " + numeroFicha + ")";
    }
}

