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
 * Controller da tela de Exercícios.
 *
 * Responsabilidades:
 * - Gerenciar ações dos botões da tela de exercícios.
 * - Abrir telas correspondentes para cadastrar, listar, editar, excluir e ver detalhes dos exercícios.
 * - Fornecer feedback ao usuário via alertas.
 */
public class ExercicioViewController {

    private static final Logger logger = Logger.getLogger(ExercicioViewController.class.getName());

    // --- BOTÕES DA TELA ---
    @FXML
    private Button BCadastrarEX;      // Botão para cadastrar novo exercício

    @FXML
    private Button BDetalhesEX;       // Botão para visualizar detalhes de um exercício

    @FXML
    private Button BEditarEX;         // Botão para editar um exercício existente

    @FXML
    private Button BExcluirEX;        // Botão para excluir um exercício

    @FXML
    private Button BListarEX;         // Botão para listar todos os exercícios

    // --- ICONES/IMAGENS ---
    @FXML
    private ImageView IFechar;        // Ícone para fechar a tela

    @FXML
    private ImageView IMenu;          // Ícone para abrir o menu principal

    // --- METODO DE INICIALIZAÇÃO ---
    @FXML
    public void initialize() {
        logger.info("Tela de Exercícios inicializada com sucesso!");
        configurarAcoes();
    }

    // --- METODO AUXILIAR PARA CONFIGURAR BOTÕES ---
    private void configurarAcoes() {
        BCadastrarEX.setOnAction(e -> handleCadastrarExercicio());
        BListarEX.setOnAction(e -> handleListarExercicios());
        BEditarEX.setOnAction(e -> handleEditarExercicio());
        BExcluirEX.setOnAction(e -> handleExcluirExercicio());
        BDetalhesEX.setOnAction(e -> handleVerDetalhesExercicio());
        IFechar.setOnMouseClicked(e -> handleAbrirMenu());
    }

    // --- METODOS DE TRATAMENTO DOS BOTÕES ---
    @FXML
    private void handleCadastrarExercicio() {
        logger.info("Botão 'Cadastrar Exercício' clicado!");
        showInfo("Cadastrar Exercício", "Funcionalidade de cadastrar exercício será implementada em breve.");
    }

    @FXML
    private void handleListarExercicios() {
        logger.info("Botão 'Listar Exercícios' clicado!");
        showInfo("Listar Exercícios", "Funcionalidade de listar exercícios será implementada em breve.");
    }

    @FXML
    private void handleEditarExercicio() {
        logger.info("Botão 'Editar Exercício' clicado!");
        showInfo("Editar Exercício", "Funcionalidade de editar exercício será implementada em breve.");
    }

    @FXML
    private void handleExcluirExercicio() {
        logger.info("Botão 'Excluir Exercício' clicado!");
        showInfo("Excluir Exercício", "Funcionalidade de excluir exercício será implementada em breve.");
    }

    @FXML
    private void handleVerDetalhesExercicio() {
        logger.info("Botão 'Ver Detalhes Exercício' clicado!");
        showInfo("Detalhes do Exercício", "Funcionalidade de ver detalhes será implementada em breve.");
    }

    @FXML
    private void handleAbrirMenu() {
        logger.info("Ícone 'Menu' clicado! Voltando ao menu principal...");
        carregarNovaTela("/fxml/MenuUsuarioLogado.fxml", "Gym System - Menu Principal");
    }

    // --- METODO AUXILIAR PARA CARREGAR NOVAS TELAS ---
    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) IMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxmlFile, e);
        }
    }

    // --- METODOS AUXILIARES DE ALERTA ---
    private void showInfo(String title, String content) {
        mostrarAlerta(Alert.AlertType.INFORMATION, title, content);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
