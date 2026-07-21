package com.unam.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String telefono;

    private String email;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();

    public Cliente() {}

    public Cliente(String dni, String nombre, String apellido, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    public void agregarMascota(Mascota mascota) {
        mascota.setCliente(this);
        mascotas.add(mascota);
    }

    public void quitarMascota(Mascota mascota) {
        mascotas.remove(mascota);
        mascota.setCliente(null);
    }

    public List<Mascota> getMascotas() {
        return Collections.unmodifiableList(mascotas);
    }

    public Long getId() { return id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        // Usamos el DNI como clave de negocio: a diferencia del id (que puede
        // ser null antes de persistir), el DNI identifica al cliente desde
        // el momento en que existe el objeto, incluso en memoria.
        return dni != null && dni.equals(cliente.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dni);
    }

    @Override
    public String toString() {
        return apellido + ", " + nombre + " (DNI: " + dni + ")";
    }
}