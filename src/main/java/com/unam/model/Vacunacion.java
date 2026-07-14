ackage com.unam.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vacunaciones")
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVacuna;

    @Column(nullable = false)
    private LocalDate fechaVacunacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_vacuna_id", nullable = false)
    private TipoVacuna tipoVacuna;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;

    public Vacunacion() {}

    public Vacunacion(LocalDate fechaVacunacion, TipoVacuna tipoVacuna, Mascota mascota, Turno turno) {
        this.fechaVacunacion = fechaVacunacion;
        this.tipoVacuna = tipoVacuna;
        this.mascota = mascota;
        this.turno = turno;
    }

    public boolean estaVigente() {
        LocalDate vencimiento = fechaVacunacion.plusMonths(tipoVacuna.getPeriodicidad());
        return LocalDate.now().isBefore(vencimiento);
    }

    public LocalDate proximoVencimiento() {
        return fechaVacunacion.plusMonths(tipoVacuna.getPeriodicidad());
    }

    public Long getIdVacuna() { return idVacuna; }
    public LocalDate getFechaVacunacion() { return fechaVacunacion; }
    public void setFechaVacunacion(LocalDate fechaVacunacion) { this.fechaVacunacion = fechaVacunacion; }
    public TipoVacuna getTipoVacuna() { return tipoVacuna; }
    public void setTipoVacuna(TipoVacuna tipoVacuna) { this.tipoVacuna = tipoVacuna; }
    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
}
