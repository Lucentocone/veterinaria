package com.unam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "consultas")
@PrimaryKeyJoinColumn(name = "servicio_id")
public class Consulta extends Servicio {

    private String diagnostico;
    private String tratamiento;

    public Consulta() {}

    public Consulta(String nombre, BigDecimal precioBase, int duracionMinutos) {
        super(nombre, precioBase, duracionMinutos);
    }

    @Override
    public boolean generaHistorialMedico() {
        return true;
    }

    public void registrarDiagnostico(String diagnostico, String tratamiento) {
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
    }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
}