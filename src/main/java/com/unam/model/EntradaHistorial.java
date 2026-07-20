package com.unam.model;

import java.time.LocalDateTime;

// No es una entidad JPA propia: se arma en memoria a partir de Turnos y
// Vacunaciones ya persistidos, para mostrar la línea de tiempo del
// historial médico sin necesitar su propia tabla.
public record EntradaHistorial(LocalDateTime fecha, String tipo, String descripcion) {}