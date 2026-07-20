package com.unam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "guarderias")
@PrimaryKeyJoinColumn(name = "servicio_id")
public class Guarderia extends Servicio {

    @Column(nullable = false)
    private int cuposDisponibles;

    public Guarderia() {}

    public Guarderia(String nombre, BigDecimal precioBase, int duracionMinutos, int cuposDisponibles) {
        super(nombre, precioBase, duracionMinutos);
        this.cuposDisponibles = cuposDisponibles;
    }

    @Override
    public boolean generaHistorialMedico() {
        return false;
    }

    @Override
    public void validarParaMascota(Mascota mascota) {
        if (cuposDisponibles <= 0) {
            throw new IllegalStateException("No hay cupos disponibles en la guardería.");
        }
    }

    public void ocuparCupo() {
        if (cuposDisponibles <= 0) {
            throw new IllegalStateException("No hay cupos disponibles.");
        }
        cuposDisponibles--;
    }

    public void liberarCupo() {
        cuposDisponibles++;
    }

    public int getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(int cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
}