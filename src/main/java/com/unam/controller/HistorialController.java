package com.unam.controller;

import com.unam.App;
import com.unam.model.EntradaHistorial;
import com.unam.model.Mascota;
import com.unam.service.HistorialService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HistorialController {

    @FXML private TextField campoBusqueda;
    @FXML private ComboBox<Mascota> comboMascotas;
    @FXML private ComboBox<String> comboTipoFiltro;
    @FXML private DatePicker fechaDesde;
    @FXML private DatePicker fechaHasta;

    @FXML private TableView<EntradaHistorial> tablaHistorial;
    @FXML private TableColumn<EntradaHistorial, String> colFecha;
    @FXML private TableColumn<EntradaHistorial, String> colTipo;
    @FXML private TableColumn<EntradaHistorial, String> colDescripcion;

    private final HistorialService historialService = new HistorialService();
    private List<EntradaHistorial> historialCompleto = List.of();

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha().toString().replace("T", " ")));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo()));
        colDescripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion()));

        comboTipoFiltro.setItems(FXCollections.observableArrayList("TODOS", "TURNO", "VACUNA"));
        comboTipoFiltro.getSelectionModel().select("TODOS");

        buscarMascotas();

        Long mascotaPrecargada = SesionUI.getMascotaSeleccionada();
        if (mascotaPrecargada != null) {
            comboMascotas.getItems().stream()
                .filter(m -> m.getId().equals(mascotaPrecargada))
                .findFirst()
                .ifPresent(m -> {
                    comboMascotas.getSelectionModel().select(m);
                    cargarHistorial(m);
                });
        }
    }

    @FXML
    private void buscarMascotas() {
        List<Mascota> resultado = historialService.buscarMascotas(campoBusqueda.getText());
        comboMascotas.setItems(FXCollections.observableArrayList(resultado));
    }

    @FXML
    private void verHistorialDeMascota() {
        Mascota mascota = comboMascotas.getValue();
        if (mascota == null) {
            mostrarError("Seleccioná una mascota de la lista de resultados.");
            return;
        }
        cargarHistorial(mascota);
    }

    private void cargarHistorial(Mascota mascota) {
        historialCompleto = historialService.obtenerHistorial(mascota.getId());
        aplicarFiltro();
    }

    @FXML
    private void aplicarFiltro() {
        String tipo = comboTipoFiltro.getValue();
        LocalDate desde = fechaDesde.getValue();
        LocalDate hasta = fechaHasta.getValue();

        List<EntradaHistorial> filtrado = historialCompleto.stream()
            .filter(e -> tipo == null || tipo.equals("TODOS") || e.tipo().equals(tipo))
            .filter(e -> desde == null || !e.fecha().toLocalDate().isBefore(desde))
            .filter(e -> hasta == null || !e.fecha().toLocalDate().isAfter(hasta))
            .toList();

        tablaHistorial.setItems(FXCollections.observableArrayList(filtrado));
    }

    @FXML
    private void volverAlMenu() throws IOException {
        SesionUI.limpiar();
        App.setRoot("mainmenu");
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.showAndWait();
    }
}
