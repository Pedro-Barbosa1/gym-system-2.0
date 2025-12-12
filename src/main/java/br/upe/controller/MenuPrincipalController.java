package br.upe.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.ui.util.StyledAlert;
import br.upe.util.NavigationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
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
        if (!NavigationUtil.navigateFrom(loginB, "/ui/Login.fxml", "SysFit - Login")) {
            showError("Erro", "Não foi possível abrir a tela de login.");
        }
    }

    @FXML
    private void handleRegister() {
        logger.info("Botão CADASTRAR clicado!");
        if (!NavigationUtil.navigateFrom(registerB, "/ui/Signup.fxml", "SysFit - Cadastro")) {
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
        StyledAlert.showErrorAndWait(title, content);
    }
}
