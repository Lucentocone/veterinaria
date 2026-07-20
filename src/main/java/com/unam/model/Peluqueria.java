package com.unam.model;

import java.math.BigDecimal;

public class Peluqueria extends Servicio {

    public Peluqueria() {}

    public Peluqueria(String nombre, BigDecimal precioBase, int duracionMinutos) {
        super(nombre, precioBase, duracionMinutos);
    }

    // La peluquería es un servicio estético, no clínico: no deja
    // registro en el historial médico de la mascota.
    @Override
    public boolean generaHistorialMedico() {
        return false;
    }

    // Sin restricciones propias por ahora: usa la validación vacía
    // heredada de Servicio. Si surge una regla (ej. especie/tamaño
    // permitido), se sobreescribe acá.
}