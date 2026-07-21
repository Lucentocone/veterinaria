package com.unam.controller;

import com.unam.App;

import java.io.IOException;

public class MainMenuController {

    @javafx.fxml.FXML
    private void irAClientes() throws IOException { App.setRoot("clientes"); }

    @javafx.fxml.FXML
    private void irATurnos() throws IOException { App.setRoot("turnos"); }

    @javafx.fxml.FXML
    private void irAHistorial() throws IOException { App.setRoot("historial"); }

    @javafx.fxml.FXML
    private void irAVacunaciones() throws IOException { App.setRoot("vacunaciones"); }
}
