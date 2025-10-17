package br.upe.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller para a tela do Menu de Planos de Treino.
 *
 * Responsabilidades:
 * - Gerenciar ações dos botões da tela de planos de treino.
 * - Abrir telas correspondentes para criar, listar, editar, deletar planos.
 * - Exibir mensagens de informação, erro ou confirmação ao usuário.
 */
public class MenuPlanoTreinosController {

    // Logger para registrar informações e erros do controller
    private static final Logger logger = Logger.getLogger(MenuPlanoTreinosController.class.getName());

    // --- VARIÁVEIS VINCULADAS AOS BOTÕES DO FXML ---
    @FXML
    private Button sairB; // Botão para voltar ao menu anterior

    @FXML
    private Button criarPlanoB; // Botão para criar novo plano de treino

    @FXML
    private Button ListarPlanoB; // Botão para listar planos existentes

    @FXML
    private Button editarPlanoB; // Botão para editar um plano existente

    @FXML
    private Button deletarPlanoB; // Botão para deletar um plano existente

    @FXML
    private Button adicionarExercicioAoPlanoB; // Botão para adicionar exercício a um plano

    @FXML
    private Button removerExercíciodoPlanoB; // Botão para remover exercício de um plano

    @FXML
    private Button verDetalhesDePlanoB; // Botão para visualizar detalhes de um plano

    // --- MÉTODO DE INICIALIZAÇÃO ---
    /**
     * Chamado automaticamente pelo JavaFX após o carregamento do FXML.
     * Aqui configuramos as ações dos botões.
     */
    @FXML
    public void initialize() {
        logger.info("Menu Plano Treinos inicializado com sucesso!");
        configurarAcoes(); // Associa métodos aos botões
    }

    // --- MÉTODO AUXILIAR PARA CONFIGURAR BOTÕES ---
    private void configurarAcoes() {
        criarPlanoB.setOnAction(e -> handleCriarPlano());
        ListarPlanoB.setOnAction(e -> handleListarPlanos());
        editarPlanoB.setOnAction(e -> handleEditarPlano());
        deletarPlanoB.setOnAction(e -> handleDeletarPlano());
        adicionarExercicioAoPlanoB.setOnAction(e -> handleAdicionarExercicio());
        removerExercíciodoPlanoB.setOnAction(e -> handleRemoverExercicio());
        verDetalhesDePlanoB.setOnAction(e -> handleVerDetalhes());
        sairB.setOnAction(e -> handleSair());
    }

    // --- MÉTODOS DE TRATAMENTO DOS BOTÕES ---
    /**
     * Executado ao clicar em "Criar Novo Plano de Treino".
     * Exibe alerta informando que a funcionalidade será implementada.
     */
    @FXML
    private void handleCriarPlano() {
        logger.info("Criar Novo Plano de Treino clicado!");
        showInfo("Criar Plano", "Funcionalidade de criar plano será implementada em breve.");
    }

    @FXML
    private void handleListarPlanos() {
        logger.info("Listar Meus Planos de Treino clicado!");
        showInfo("Listar Planos", "Funcionalidade de listar planos será implementada em breve.");
    }

    @FXML
    private void handleEditarPlano() {
        logger.info("Editar Plano de Treino clicado!");
        showInfo("Editar Plano", "Funcionalidade de editar plano será implementada em breve.");
    }

    @FXML
    private void handleDeletarPlano() {
        logger.info("Deletar Plano de Treino clicado!");
        showInfo("Deletar Plano", "Funcionalidade de deletar plano será implementada em breve.");
    }

    @FXML
    private void handleAdicionarExercicio() {
        logger.info("Adicionar Exercício ao Plano clicado!");
        showInfo("Adicionar Exercício", "Funcionalidade de adicionar exercício será implementada em breve.");
    }

    @FXML
    private void handleRemoverExercicio() {
        logger.info("Remover Exercício do Plano clicado!");
        showInfo("Remover Exercício", "Funcionalidade de remover exercício será implementada em breve.");
    }

    @FXML
    private void handleVerDetalhes() {
        logger.info("Ver Detalhes do Plano clicado!");
        showInfo("Ver Detalhes", "Funcionalidade de ver detalhes será implementada em breve.");
    }

    /**
     * Executado ao clicar no botão "Sair".
     * Tenta carregar a tela do menu do usuário logado.
     */
    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuUsuarioLogado.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Não foi possível voltar ao menu.", e);
            showError("Erro", "Não foi possível voltar ao menu.");
        }
    }

    // --- MÉTODOS AUXILIARES PARA ALERTAS ---
    /**
     * Exibe uma caixa de diálogo de informação.
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Exibe uma caixa de diálogo de erro.
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Exibe uma caixa de diálogo de confirmação e retorna a resposta do usuário.
     *
     * @return true se o usuário confirmar, false caso contrário
     */
    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        return alert.showAndWait().get() == javafx.scene.control.ButtonType.OK;
    }
}
