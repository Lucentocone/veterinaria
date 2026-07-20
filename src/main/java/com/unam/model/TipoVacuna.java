package com.unam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_vacuna")
public class TipoVacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreComercial;

    @Column(nullable = false)
    private String previene;

    @Column(nullable = false)
    private int periodicidad; // en meses

    public TipoVacuna() {}

    public TipoVacuna(String nombreComercial, String previene, int periodicidad) {
        this.nombreComercial = nombreComercial;
        this.previene = previene;
        this.periodicidad = periodicidad;
    }

    public Long getId() { return id; }
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getPreviene() { return previene; }
    public void setPreviene(String previene) { this.previene = previene; }
    public int getPeriodicidad() { return periodicidad; }
    public void setPeriodicidad(int periodicidad) { this.periodicidad = periodicidad; }

    @Override
    public String toString() {
        return nombreComercial + " (cada " + periodicidad + " meses)";
    }
}