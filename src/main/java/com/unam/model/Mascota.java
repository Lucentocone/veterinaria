package com.unam.model;

import com.unam.enums.Especie;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    private String raza;

    private LocalDate fechaNacimiento;

    // Baja lógica: la mascota nunca se borra físicamente (conserva turnos e
    // historial), pero deja de aparecer en las pantallas de gestión activa.
    @Column(nullable = false)
    private boolean activa = true;

    // Lado inverso de la relación. LAZY: no queremos traer el Cliente
    // completo cada vez que consultamos una Mascota si no hace falta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Mascota() {}

    public Mascota(String nombre, Especie especie, String raza, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
    }

    // El número de ficha único que pide la consigna es, en este diseño,
    // el propio id generado por la base de datos: ya es único y estable
    // desde el momento en que la mascota se persiste.
    public Long getNumeroFicha() { return id; }

    public void darDeBaja() { this.activa = false; }
    public void reactivar() { this.activa = true; }
    public boolean isActiva() { return activa; }

    public Integer getEdadEnAnios() {
        if (fechaNacimiento == null) return null;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }
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