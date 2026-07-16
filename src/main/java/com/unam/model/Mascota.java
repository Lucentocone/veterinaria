package com.unam.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String especie;

    private String raza;

    private LocalDate fechaNacimiento;

    // Lado inverso de la relación. LAZY: no queremos traer el Cliente
    // completo cada vez que consultamos una Mascota si no hace falta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Mascota() {}

    public Mascota(String nombre, String especie, String raza, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Cliente getCliente() { return cliente; }
    void setCliente(Cliente cliente) { this.cliente = cliente; } // package-private: solo Cliente debe llamarlo

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mascota mascota)) return false;
        // Con id null (entidad no persistida todavía) dos instancias distintas
        // nunca son iguales; una vez persistidas, se comparan por id.
        return id != null && id.equals(mascota.id);
    }

    @Override
    public int hashCode() {
        // Constante en vez de Objects.hash(id): si usáramos el id, el hashCode
        // cambiaría al persistir (pasa de null a un valor), rompiendo colecciones
        // hash (HashSet/HashMap) que ya contenían el objeto.
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        // OJO: no incluir "cliente" acá. Si el campo es LAZY y la sesión ya
        // cerró, dispara LazyInitializationException. Además evita un
        // toString ↔ toString infinito con Cliente.
        return nombre + " (" + especie + (raza != null ? ", " + raza : "") + ")";
    }
}