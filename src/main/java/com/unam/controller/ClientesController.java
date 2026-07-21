package com.unam.controller;

import com.unam.App;
import com.unam.enums.Especie;
import com.unam.model.Cliente;
import com.unam.model.Mascota;
import com.unam.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ClientesController {

    @FXML private TextField campoBusquedaCliente;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;

    @FXML private TableView<Mascota> tablaMascotas;
    @FXML private TableColumn<Mascota, String> colFicha;
    @FXML private TableColumn<Mascota, String> colNombreMascota;
    @FXML private TableColumn<Mascota, String> colEspecie;
    @FXML private TableColumn<Mascota, String> colRaza;
    @FXML private TableColumn<Mascota, String> colEdad;
    @FXML private TableColumn<Mascota, String> colActiva;

    private final ClienteService clienteService = new ClienteService();

    @FXML
    public void initialize() {
        colDni.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDni()));
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colApellido.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getApellido()));
        colTelefono.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTelefono()));
        colEmail.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail()));

        colFicha.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getNumeroFicha())));
        colNombreMascota.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colEspecie.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEspecie().toString()));
        colRaza.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRaza()));
        colEdad.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getEdadEnAnios() != null ? d.getValue().getEdadEnAnios() + " años" : "-"));
        colActiva.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().isActiva() ? "Sí" : "No"));

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> cargarMascotas(nuevo));

        cargarClientes(null);
    }

    private void cargarClientes(String filtro) {
        List<Cliente> clientes = clienteService.buscarClientes(filtro);
        tablaClientes.setItems(FXCollections.observableArrayList(clientes));
    }

    private void cargarMascotas(Cliente cliente) {
        if (cliente == null) {
            tablaMascotas.setItems(FXCollections.emptyObservableList());
            return;
        }
        tablaMascotas.setItems(FXCollections.observableArrayList(clienteService.listarMascotasDeCliente(cliente.getId())));
    }

    @FXML
    private void buscarClientes() {
        cargarClientes(campoBusquedaCliente.getText());
    }

    @FXML
    private void volverAlMenu() throws IOException {
        App.setRoot("mainmenu");
    }

    @FXML
    private void nuevoCliente() {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Cliente");
        dialog.setHeaderText("Datos del nuevo cliente");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField dni = new TextField();
        TextField nombre = new TextField();
        TextField apellido = new TextField();
        TextField telefono = new TextField();
        TextField email = new TextField();

        GridPane grid = construirGrid(
            "DNI:", dni, "Nombre:", nombre, "Apellido:", apellido, "Teléfono:", telefono, "Email:", email);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                try {
                    return clienteService.registrarCliente(
                        dni.getText(), nombre.getText(), apellido.getText(), telefono.getText(), email.getText());
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        cargarClientes(null);
    }

    @FXML
    private void editarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccioná un cliente para editar.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar Cliente");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nombre = new TextField(seleccionado.getNombre());
        TextField apellido = new TextField(seleccionado.getApellido());
        TextField telefono = new TextField(seleccionado.getTelefono());
        TextField email = new TextField(seleccionado.getEmail());

        GridPane grid = construirGrid("Nombre:", nombre, "Apellido:", apellido, "Teléfono:", telefono, "Email:", email);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                seleccionado.setNombre(nombre.getText());
                seleccionado.setApellido(apellido.getText());
                seleccionado.setTelefono(telefono.getText());
                seleccionado.setEmail(email.getText());
                clienteService.actualizarCliente(seleccionado);
            }
            return null;
        });

        dialog.showAndWait();
        cargarClientes(null);
    }

    @FXML
    private void nuevaMascota() {
        Cliente cliente = tablaClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarError("Seleccioná un cliente para agregarle una mascota.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nueva Mascota");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nombre = new TextField();
        ComboBox<Especie> especie = new ComboBox<>(FXCollections.observableArrayList(Especie.values()));
        especie.getSelectionModel().selectFirst();
        TextField raza = new TextField();
        DatePicker fechaNacimiento = new DatePicker(LocalDate.now());

        GridPane grid = construirGrid(
            "Nombre:", nombre, "Especie:", especie, "Raza:", raza, "Fecha nacimiento:", fechaNacimiento);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                try {
                    clienteService.agregarMascota(
                        cliente.getId(), nombre.getText(), especie.getValue(), raza.getText(), fechaNacimiento.getValue());
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        cargarMascotas(cliente);
    }

    @FXML
    private void editarMascota() {
        Mascota mascota = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascota == null) {
            mostrarError("Seleccioná una mascota para editar.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar Mascota");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nombre = new TextField(mascota.getNombre());
        ComboBox<Especie> especie = new ComboBox<>(FXCollections.observableArrayList(Especie.values()));
        especie.getSelectionModel().select(mascota.getEspecie());
        TextField raza = new TextField(mascota.getRaza());
        DatePicker fechaNacimiento = new DatePicker(mascota.getFechaNacimiento());

        GridPane grid = construirGrid(
            "Nombre:", nombre, "Especie:", especie, "Raza:", raza, "Fecha nacimiento:", fechaNacimiento);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                mascota.setNombre(nombre.getText());
                mascota.setEspecie(especie.getValue());
                mascota.setRaza(raza.getText());
                mascota.setFechaNacimiento(fechaNacimiento.getValue());
                clienteService.actualizarMascota(mascota);
            }
            return null;
        });

        dialog.showAndWait();
        cargarMascotas(tablaClientes.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void toggleBajaMascota() {
        Mascota mascota = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascota == null) {
            mostrarError("Seleccioná una mascota.");
            return;
        }
        if (mascota.isActiva()) {
            clienteService.darDeBajaMascota(mascota);
        } else {
            clienteService.reactivarMascota(mascota);
        }
        cargarMascotas(tablaClientes.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void verHistorial() throws IOException {
        Mascota mascota = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascota == null) {
            mostrarError("Seleccioná una mascota.");
            return;
        }
        SesionUI.setMascotaSeleccionada(mascota.getId());
        App.setRoot("historial");
    }

    @FXML
    private void verTurnos() throws IOException {
        Mascota mascota = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascota == null) {
            mostrarError("Seleccioná una mascota.");
            return;
        }
        SesionUI.setMascotaSeleccionada(mascota.getId());
        App.setRoot("turnos");
    }

    private GridPane construirGrid(Object... etiquetaControl) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        for (int i = 0; i < etiquetaControl.length; i += 2) {
            grid.add(new Label((String) etiquetaControl[i]), 0, i / 2);
            grid.add((javafx.scene.Node) etiquetaControl[i + 1], 1, i / 2);
        }
        return grid;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.showAndWait();
    }
}
