package br.upe.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller para a tela de administração
 * Gerencia ações de usuário como listar, promover, rebaixar ou remover
 */
public class AdministradorViewController {

    private static final Logger logger = Logger.getLogger(AdministradorViewController.class.getName());

    // --- IMAGENS E LOGO ---
    @FXML
    private ImageView LogoDoApp;

    @FXML
    private ImageView IFavoritos;

    // --- BOTÕES ---
    @FXML
    private Button BSairDoApp;

    @FXML
    private Button BListarUsuarios;

    @FXML
    private Button BPromoverUsuario;

    @FXML
    private Button BRebaixarUsuario;

    @FXML
    private Button BRemoverUsuario;

    /**
     * Inicializa o controller após o carregamento do FXML
     * Este metodo é chamado automaticamente pelo JavaFX
     */
    @FXML
    public void initialize() {
        logger.info(() -> "AdministradorViewController inicializado com sucesso!");
        configurarAcoes();
    }

    /**
     * Configura as ações dos botões da tela
     */
    @FXML
    private void configurarAcoes() {
        BSairDoApp.setOnAction(e -> handleSair());
        BListarUsuarios.setOnAction(e -> handleListarUsuarios());
        BPromoverUsuario.setOnAction(e -> handlePromoverUsuario());
        BRebaixarUsuario.setOnAction(e -> handleRebaixarUsuario());
        BRemoverUsuario.setOnAction(e -> handleRemoverUsuario());
    }

    /**
     * Manipula o clique no botão "Sair"
     * Fecha a tela atual e retorna ao menu principal
     */
    @FXML
    private void handleSair() {
        logger.info(() -> "Botão 'Sair' clicado. Retornando ao menu principal...");
        carregarNovaTela("/fxml/Login.fxml", "Gym System - Login");
    }

    /**
     * Manipula o clique no botão "Listar Usuários"
     */
    @FXML
    private void handleListarUsuarios() {
        logger.info(() -> "Botão 'Listar Usuários' clicado!");
        showInfo("Listar Usuários", "Funcionalidade de listar usuários será implementada em breve.");
    }

    /**
     * Manipula o clique no botão "Promover Usuário"
     */
    @FXML
    private void handlePromoverUsuario() {
        logger.info(() -> "Botão 'Promover Usuário' clicado!");
        showInfo("Promover Usuário", "Funcionalidade de promover usuário a admin será implementada em breve.");
    }

    /**
     * Manipula o clique no botão "Rebaixar Usuário"
     */
    @FXML
    private void handleRebaixarUsuario() {
        logger.info(() -> "Botão 'Rebaixar Usuário' clicado!");
        showInfo("Rebaixar Usuário", "Funcionalidade de rebaixar usuário será implementada em breve.");
    }

    /**
     * Manipula o clique no botão "Remover Usuário"
     */
    @FXML
    private void handleRemoverUsuario() {
        logger.info(() -> "Botão 'Remover Usuário' clicado!");
        showInfo("Remover Usuário", "Funcionalidade de remover usuário será implementada em breve.");
    }

    /**
     * Metodo auxiliar para carregar novas telas
     * @param fxmlFile Caminho do FXML
     * @param titulo Título da janela
     */
    @FXML
    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) BSairDoApp.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);
        } catch (IOException e) {
            showError("Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxmlFile, e);
        }
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
