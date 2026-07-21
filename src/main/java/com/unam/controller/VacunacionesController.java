package com.unam.controller;

import com.unam.App;
import com.unam.model.Mascota;
import com.unam.model.TipoVacuna;
import com.unam.model.Vacunacion;
import com.unam.service.HistorialService;
import com.unam.service.VacunacionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class VacunacionesController {

    @FXML private TableView<Vacunacion> tablaAlertas;
    @FXML private TableColumn<Vacunacion, String> colMascota;
    @FXML private TableColumn<Vacunacion, String> colVacuna;
    @FXML private TableColumn<Vacunacion, String> colUltimaAplicacion;
    @FXML private TableColumn<Vacunacion, String> colVencimiento;
    @FXML private TableColumn<Vacunacion, String> colEstadoVacuna;

    private final VacunacionService vacunacionService = new VacunacionService();
    private final HistorialService historialService = new HistorialService();

    private static final int DIAS_ALERTA_VENCIMIENTO = 30;

    @FXML
    public void initialize() {
        colMascota.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMascota().getNombre()));
        colVacuna.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipoVacuna().getNombreComercial()));
        colUltimaAplicacion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFechaVacunacion().toString()));
        colVencimiento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().calcularFechaVencimiento().toString()));
        colEstadoVacuna.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estaVigente() ? "Vence pronto" : "VENCIDA"));

        actualizarAlertas();
    }

    @FXML
    private void actualizarAlertas() {
        List<Vacunacion> alertas = vacunacionService.ultimasVacunacionesRelevantes(DIAS_ALERTA_VENCIMIENTO);
        tablaAlertas.setItems(FXCollections.observableArrayList(alertas));
    }

    @FXML
    private void volverAlMenu() throws IOException {
        App.setRoot("mainmenu");
    }

    @FXML
    private void registrarVacunacion() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Registrar Vacunación");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Mascota> comboMascota = new ComboBox<>(FXCollections.observableArrayList(historialService.buscarMascotas("")));
        ComboBox<TipoVacuna> comboVacuna = new ComboBox<>(FXCollections.observableArrayList(vacunacionService.listarTiposVacuna()));
        DatePicker fecha = new DatePicker(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.add(new Label("Mascota:"), 0, 0); grid.add(comboMascota, 1, 0);
        grid.add(new Label("Vacuna:"), 0, 1); grid.add(comboVacuna, 1, 1);
        grid.add(new Label("Fecha de aplicación:"), 0, 2); grid.add(fecha, 1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(boton -> {
            if (boton == ButtonType.OK) {
                if (comboMascota.getValue() == null || comboVacuna.getValue() == null || fecha.getValue() == null) {
                    mostrarError("Completá mascota, vacuna y fecha.");
                    return null;
                }
                try {
                    vacunacionService.registrarVacunacion(
                        comboMascota.getValue().getId(), comboVacuna.getValue().getId(), fecha.getValue(), null);
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        actualizarAlertas();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.showAndWait();
    }
}
