package br.upe.ui.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class MenuPrincipalController {

    @FXML
    private Button loginB;

    @FXML
    private Button registerB;

    @FXML
    private Button exitB;

   
    @FXML
    public void initialize() {
        System.out.println("Menu Principal inicializado com sucesso!");
        
       
        configurarAcoes();
    }

    
    private void configurarAcoes() {
        loginB.setOnAction(e -> handleLogin());
        registerB.setOnAction(e -> handleRegister());
        exitB.setOnAction(e -> handleExit());
    }


    @FXML
    private void handleLogin() {
        System.out.println("Botão ENTRAR clicado!");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginB.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SysFit - Login");
        } catch (IOException e) {
            showError("Erro", "Não foi possível abrir a tela de login.");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRegister() {
        System.out.println("Botão CADASTRAR clicado!");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerB.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SysFit - Cadastro");
        } catch (IOException e) {
            showError("Erro", "Não foi possível abrir a tela de cadastro.");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleExit() {
        System.out.println("Botão SAIR clicado - Encerrando aplicação...");
        

        Platform.exit();
        System.exit(0);
    }

    /**
     * Exibe um alerta de informação
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Exibe um alerta de erro
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
