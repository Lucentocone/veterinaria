package com.unam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private int duracionMinutos;

    public Servicio() {}

    public Servicio(String nombre, BigDecimal precioBase, int duracionMinutos) {
    if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("El precio base debe ser positivo");
    }
    if (duracionMinutos <= 0) {
        throw new IllegalArgumentException("La duración debe ser positiva");
    }
    this.nombre = nombre;
    this.precioBase = precioBase;
    this.duracionMinutos = duracionMinutos;
}

    // Cada subclase decide si genera entrada en el historial médico
    public abstract boolean generaHistorialMedico();

    // Validación específica por tipo de servicio (puede sobreescribirse)
    public void validarParaMascota(Mascota mascota) {
        // por defecto no hace nada; las subclases agregan sus propias reglas
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    @Override
    public String toString() {
        return nombre + " ($" + precioBase + " - " + duracionMinutos + " min)";
    }
}