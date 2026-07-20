package com.unam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_turno")
public class DetalleTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(nullable = false)
    private BigDecimal precioCobrado;

    public DetalleTurno() {}

    // El precio se "congela" acá copiando el precioBase del servicio en
    // este instante. Si el precio del servicio cambia después, este turno
    // ya registrado no se ve afectado (precios históricos consistentes).
    public DetalleTurno(Turno turno, Servicio servicio) {
        this.turno = turno;
        this.servicio = servicio;
        this.precioCobrado = servicio.getPrecioBase();
    }

    public Long getId() { return id; }
    public Turno getTurno() { return turno; }
    public Servicio getServicio() { return servicio; }
    public BigDecimal getPrecioCobrado() { return precioCobrado; }
    // Sin setter para precioCobrado: es un dato histórico, no editable.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleTurno that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return servicio.getNombre() + " ($" + precioCobrado + ")";
    }
}