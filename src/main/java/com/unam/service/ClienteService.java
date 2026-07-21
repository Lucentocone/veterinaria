package com.unam.service;

import com.unam.enums.Especie;
import com.unam.model.Cliente;
import com.unam.model.Mascota;
import com.unam.repository.ClienteRepository;
import com.unam.repository.MascotaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Coordina la persistencia de Clientes y Mascotas. Las reglas de negocio
 * (por ejemplo, que una mascota no exista sin dueño) están garantizadas por
 * el propio modelo -- Mascota exige un Cliente en su asociación -- esta
 * capa solo orquesta las llamadas a los repositorios.
 */
public class ClienteService {

    private final ClienteRepository clienteRepository = new ClienteRepository();
    private final MascotaRepository mascotaRepository = new MascotaRepository();

    public Cliente registrarCliente(String dni, String nombre, String apellido, String telefono, String email) {
        if (clienteRepository.buscarPorDni(dni).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con DNI " + dni);
        }
        Cliente cliente = new Cliente(dni, nombre, apellido, telefono, email);
        return clienteRepository.guardar(cliente);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        return clienteRepository.actualizar(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.listarTodos();
    }

    public List<Cliente> buscarClientes(String texto) {
        return (texto == null || texto.isBlank())
            ? clienteRepository.listarTodos()
            : clienteRepository.buscarPorNombreOApellido(texto);
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.buscarPorId(id);
    }

    public Mascota agregarMascota(Long clienteId, String nombre, Especie especie, String raza, LocalDate fechaNacimiento) {
        Mascota mascota = new Mascota(nombre, especie, raza, fechaNacimiento);
        return clienteRepository.agregarMascota(clienteId, mascota);
    }

    public Mascota actualizarMascota(Mascota mascota) {
        return mascotaRepository.actualizar(mascota);
    }

    public void darDeBajaMascota(Mascota mascota) {
        mascota.darDeBaja();
        mascotaRepository.actualizar(mascota);
    }

    public void reactivarMascota(Mascota mascota) {
        mascota.reactivar();
        mascotaRepository.actualizar(mascota);
    }

    public List<Mascota> listarMascotasDeCliente(Long clienteId) {
        return mascotaRepository.buscarPorCliente(clienteId);
    }

    public List<Mascota> buscarMascotas(String texto) {
        return (texto == null || texto.isBlank())
            ? mascotaRepository.listarActivas()
            : mascotaRepository.buscarPorNombreOFicha(texto);
    }
}
