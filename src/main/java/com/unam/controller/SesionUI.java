package com.unam.controller;

/**
 * Dado que App.setRoot reemplaza la vista creando un controlador nuevo cada
 * vez, esta clase guarda en memoria el contexto de navegación mínimo (por
 * ejemplo, qué mascota quedó seleccionada) para que la pantalla de destino
 * pueda precargarlo. No es una entidad ni reemplaza el modelo: es pura
 * plomería de la capa de presentación.
 */
public final class SesionUI {

    private static Long mascotaSeleccionadaId;

    private SesionUI() {}

    public static void setMascotaSeleccionada(Long id) { mascotaSeleccionadaId = id; }

    public static Long getMascotaSeleccionada() { return mascotaSeleccionadaId; }

    public static void limpiar() { mascotaSeleccionadaId = null; }
}
