package com.unam.init;

import com.unam.enums.Especialidad;
import com.unam.model.Consulta;
import com.unam.model.Guarderia;
import com.unam.model.Peluqueria;
import com.unam.model.TipoVacuna;
import com.unam.model.Veterinario;
import com.unam.persistence.PersistenceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;

/**
 * Carga datos de prueba (veterinarios, servicios y tipos de vacuna) la
 * primera vez que se ejecuta la aplicación, tal como permite la consigna
 * para la pantalla opcional de "Gestión de Veterinarios y Servicios".
 */
public final class DataInitializer {

    private DataInitializer() {}

    public static void inicializarSiCorresponde() {
        EntityManager em = PersistenceManager.crearEntityManager();
        try {
            Long cantidadVeterinarios = em.createQuery("SELECT COUNT(v) FROM Veterinario v", Long.class)
                .getSingleResult();
            if (cantidadVeterinarios > 0) {
                return; // ya hay datos, no se vuelve a sembrar
            }

            EntityTransaction tx = em.getTransaction();
            tx.begin();

            em.persist(new Veterinario("Dra. Lucía Fernández", "3764-111111", "lucia.fernandez@vet.com", Especialidad.VETERINARIO_GENERAL));
            em.persist(new Veterinario("Dr. Martín Gómez", "3764-222222", "martin.gomez@vet.com", Especialidad.VETERINARIO_PREVENTIVO));
            em.persist(new Veterinario("Dra. Ana Torres", "3764-333333", "ana.torres@vet.com", Especialidad.VETERINARIO_AUXILIAR));

            em.persist(new Consulta("Consulta general", new BigDecimal("15000"), 30));
            em.persist(new Consulta("Consulta de urgencia", new BigDecimal("25000"), 45));
            em.persist(new Peluqueria("Baño y corte", new BigDecimal("8000"), 60));
            em.persist(new Guarderia("Guardería día completo", new BigDecimal("12000"), 480, 10));

            em.persist(new TipoVacuna("Quíntuple canina", "Moquillo, hepatitis, parvovirus, parainfluenza, leptospirosis", 12));
            em.persist(new TipoVacuna("Antirrábica", "Rabia", 12));
            em.persist(new TipoVacuna("Triple felina", "Panleucopenia, rinotraqueítis, calicivirus", 12));

            tx.commit();
        } finally {
            em.close();
        }
    }
}
