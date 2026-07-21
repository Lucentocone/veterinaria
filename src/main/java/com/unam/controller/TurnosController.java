package com.unam.controller;

import com.unam.App;
import com.unam.model.*;
import com.unam.service.ClienteService;
import com.unam.service.TurnoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TurnosController {

    @FXML private DatePicker selectorFecha;
    @FXML private TableView<Turno> tablaTurnos;
    @FXML private TableColumn<Turno, String> colHora;
    @FXML private TableColumn<Turno, String> colMascota;
    @FXML private TableColumn<Turno, String> colVeterinario;
    @FXML private TableColumn<Turno, String> colServicios;
    @FXML private TableColumn<Turno, String> colEstado;
    @FXML private TableColumn<Turno, String> colPrecio;

    private final TurnoService turnoService = new TurnoService();
    private final ClienteService clienteService = new ClienteService();

    @FXML
    public void initialize() {
        colHora.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFechaHora().toString().replace("T", " ")));
        colMascota.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMascota().getNombre()));
        colVeterinario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVeterinario().getNombre()));
        colServicios.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getDetalles().stream().map(det -> det.getServicio().getNombre()).collect(Collectors.joining(", "))));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado().toString()));
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty("$" + d.getValue().getPrecio()));

        selectorFecha.setValue(fechaInicialSegunMascotaSeleccionada());
        cargarTurnosDeFecha();
    }

    // Si venimos de "Ver turnos" de una mascota puntual (ClientesController),
    // arranca en la fecha de su próximo turno para no obligar a buscarla a
    // mano en el calendario. Si esa mascota no tiene turnos próximos, o si
    // se entró directamente a esta pantalla sin mascota seleccionada, se
    // usa la fecha de hoy.
    private LocalDate fechaInicialSegunMascotaSeleccionada() {
        Long mascotaSeleccionada = SesionUI.getMascotaSeleccionada();
        if (mascotaSeleccionada == null) {
            return LocalDate.now();
        }
        return turnoService.obtenerProximaFechaTurno(mascotaSeleccionada).orElse(LocalDate.now());
    }

    @FXML
    private void cargarTurnosDeFecha() {
        LocalDate fecha = selectorFecha.getValue() != null ? selectorFecha.getValue() : LocalDate.now();
        List<Turno> turnos = turnoService.listarPorFecha(fecha);
        Long mascotaFiltro = SesionUI.getMascotaSeleccionada();
        if (mascotaFiltro != null) {
            turnos = turnos.stream().filter(t -> t.getMascota().getId().equals(mascotaFiltro)).toList();
        }
        tablaTurnos.setItems(FXCollections.observableArrayList(turnos));
    }

    @FXML
    private void volverAlMenu() throws IOException {
        SesionUI.limpiar();
        App.setRoot("mainmenu");
    }

    @FXML
    private void nuevoTurno() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Turno");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Mascota> comboMascota = new ComboBox<>(FXCollections.observableArrayList(clienteService.buscarMascotas("")));
        ComboBox<Veterinario> comboVeterinario = new ComboBox<>(FXCollections.observableArrayList(turnoService.listarVeterinarios()));
        DatePicker fecha = new DatePicker(selectorFecha.getValue() != null ? selectorFecha.getValue() : LocalDate.now());
        ComboBox<String> hora = new ComboBox<>(FXCollections.observableArrayList(horasDisponibles()));
        hora.getSelectionModel().select("09:00");

        ListView<Servicio> listaServicios = new ListView<>(FXCollections.observableArrayList(turnoService.listarServicios()));
        listaServicios.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        listaServicios.setPrefHeight(140);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.add(new Label("Mascota:"), 0, 0); grid.add(comboMascota, 1, 0);
        grid.add(new Label("Veterinario:"), 0, 1); grid.add(comboVeterinario, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2); grid.add(fecha, 1, 2);
        grid.add(new Label("Hora:"), 0, 3); grid.add(hora, 1, 3);
        grid.add(new Label("Servicios:"), 0, 4); grid.add(listaServicios, 1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                try {
                    Mascota mascota = comboMascota.getValue();
                    Veterinario veterinario = comboVeterinario.getValue();
                    if (mascota == null || veterinario == null || hora.getValue() == null || listaServicios.getSelectionModel().getSelectedItems().isEmpty()) {
                        mostrarError("Completá mascota, veterinario, hora y al menos un servicio.");
                        return null;
                    }
                    String[] partes = hora.getValue().split(":");
                    LocalDateTime fechaHora = fecha.getValue().atTime(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                    List<Long> servicioIds = listaServicios.getSelectionModel().getSelectedItems().stream()
                        .map(Servicio::getId).toList();
                    turnoService.crearTurno(mascota.getId(), veterinario.getId(), fechaHora, servicioIds);
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        cargarTurnosDeFecha();
    }

    @FXML
    private void confirmarTurno() {
        conTurnoSeleccionado(turno -> {
            try {
                turnoService.confirmar(turno);
            } catch (RuntimeException e) {
                mostrarError(e.getMessage());
            }
        });
    }

    @FXML
    private void atenderTurno() {
        Turno turno = tablaTurnos.getSelectionModel().getSelectedItem();
        if (turno == null) {
            mostrarError("Seleccioná un turno.");
            return;
        }
        boolean tieneConsulta = turno.getDetalles().stream().anyMatch(d -> d.getServicio() instanceof Consulta);

        if (tieneConsulta) {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Atender Turno");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            TextArea diagnostico = new TextArea();
            diagnostico.setPrefRowCount(3);
            TextArea tratamiento = new TextArea();
            tratamiento.setPrefRowCount(3);
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 10, 10, 10));
            grid.add(new Label("Diagnóstico:"), 0, 0); grid.add(diagnostico, 1, 0);
            grid.add(new Label("Tratamiento:"), 0, 1); grid.add(tratamiento, 1, 1);
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(boton -> {
                if (boton == ButtonType.OK) {
                    try {
                        turnoService.atender(turno, diagnostico.getText(), tratamiento.getText());
                    } catch (RuntimeException e) {
                        mostrarError(e.getMessage());
                    }
                }
                return null;
            });
            dialog.showAndWait();
            cargarTurnosDeFecha();
        } else {
            conTurnoSeleccionado(t -> {
                try {
                    turnoService.atender(t, null, null);
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            });
        }
    }

    @FXML
    private void cancelarTurno() {
        conTurnoSeleccionado(turno -> {
            try {
                turnoService.cancelar(turno);
            } catch (RuntimeException e) {
                mostrarError(e.getMessage());
            }
        });
    }

    private void conTurnoSeleccionado(java.util.function.Consumer<Turno> accion) {
        Turno turno = tablaTurnos.getSelectionModel().getSelectedItem();
        if (turno == null) {
            mostrarError("Seleccioná un turno.");
            return;
        }
        accion.accept(turno);
        cargarTurnosDeFecha();
    }

    private List<String> horasDisponibles() {
        List<String> horas = new java.util.ArrayList<>();
        for (int h = 8; h <= 19; h++) {
            horas.add(String.format("%02d:00", h));
            horas.add(String.format("%02d:30", h));
        }
        return horas;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.showAndWait();
    }
}