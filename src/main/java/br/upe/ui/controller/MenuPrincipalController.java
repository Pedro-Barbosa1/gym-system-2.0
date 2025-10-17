package br.upe.ui.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuPrincipalController {

    private static final Logger logger = Logger.getLogger(MenuPrincipalController.class.getName());

    @FXML
    private Button loginB;

    @FXML
    private Button registerB;

    @FXML
    private Button exitB;

    @FXML
    public void initialize() {
        logger.info("Menu Principal inicializado com sucesso!");
        configurarAcoes();
    }

    private void configurarAcoes() {
        loginB.setOnAction(e -> handleLogin());
        registerB.setOnAction(e -> handleRegister());
        exitB.setOnAction(e -> handleExit());
    }

    @FXML
    private void handleLogin() {
        logger.info("Botão ENTRAR clicado!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginB.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SysFit - Login");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao abrir a tela de login", e);
            showError("Erro", "Não foi possível abrir a tela de login.");
        }
    }

    @FXML
    private void handleRegister() {
        logger.info("Botão CADASTRAR clicado!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerB.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SysFit - Cadastro");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao abrir a tela de cadastro", e);
            showError("Erro", "Não foi possível abrir a tela de cadastro.");
        }
    }

    @FXML
    private void handleExit() {
        logger.info("Botão SAIR clicado - Encerrando aplicação...");
        Platform.exit();
        System.exit(0);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
