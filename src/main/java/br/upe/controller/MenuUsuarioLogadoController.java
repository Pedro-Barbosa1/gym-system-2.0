package br.upe.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.ui.util.StyledAlert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuUsuarioLogadoController {

    private static final Logger logger = Logger.getLogger(MenuUsuarioLogadoController.class.getName());

    // --- VARIÁVEIS VINCULADAS AOS COMPONENTES DA TELA ---
    @FXML
    private Button sairB;

    @FXML
    private Button meusTreinosB;

    @FXML
    private Button meusIndicadoresB;

    @FXML
    private Button gerenciarPlanosB;

    @FXML
    private Button gerenciarExerciciosB;

    @FXML
    private Button exportarRelatoriosB;

    /**
     * Inicializa o controller e configura os eventos dos botões
     */
    @FXML
    public void initialize() {
        logger.info("Menu Usuário Logado inicializado com sucesso!");
        configurarAcoes();
    }

    /**
     * Configura as ações dos botões
     */
    private void configurarAcoes() {
        meusTreinosB.setOnAction(e -> abrirMeusTreinos());
        meusIndicadoresB.setOnAction(e -> abrirMeusIndicadores());
        gerenciarPlanosB.setOnAction(e -> abrirGerenciarPlanoDeTreino());
        gerenciarExerciciosB.setOnAction(e -> abrirGerenciarExercicios());
        exportarRelatoriosB.setOnAction(e -> abrirExportarRelatorios());
    }


    // --- MÉTODOS EXECUTADOS PELOS BOTÕES ---

    /**
     * Chamado quando o botão "Meus Treinos" é clicado.
     * Abre a tela de visualização de treinos do usuário.
     */
    private void abrirMeusTreinos() {
        logger.info("Botão 'Meus Treinos' clicado!");
        carregarNovaTela("/fxml/TreinoView.fxml", "Gym System - Meus Treinos");
    }

    /**
     * Chamado quando o botão "Meus Indicadores" é clicado.
     * Abre a tela de indicadores de progresso do usuário.
     */
    private void abrirMeusIndicadores() {
        logger.info("Botão 'Meus Indicadores' clicado!");
        carregarNovaTela("/fxml/IndicadoresView.fxml", "Gym System - Meus Indicadores");
    }

    /**
     * Chamado quando o botão "Gerenciar Plano de Treino" é clicado.
     * Abre a tela para o usuário gerenciar seus planos.
     */
    private void abrirGerenciarPlanoDeTreino() {
        logger.info("Botão 'Gerenciar Plano de Treino' clicado!");
        carregarNovaTela("/fxml/MenuPlanoTreinos.fxml", "Gym System - Planos de Treino");
    }

    /**
     * Chamado quando o botão "Gerenciar Exercícios" é clicado.
     * Abre a tela de gerenciamento de exercícios.
     */
    private void abrirGerenciarExercicios() {
        logger.info("Botão 'Gerenciar Exercícios' clicado!");
        carregarNovaTela("/fxml/ExercicioView.fxml", "Gym System - Gerenciar Exercícios");
    }

    /**
     * Chamado quando o botão "Exportar Relatórios" é clicado.
     * Abre a funcionalidade de exportação de relatórios.
     */
    private void abrirExportarRelatorios() {
        logger.info("Botão 'Exportar Relatórios' clicado!");
        carregarNovaTela("/fxml/MenuRelatorios.fxml", "Gym System - Relatórios");
    }

    /**
     * Chamado quando o botão "Sair" é clicado.
     * Fecha a tela atual e retorna para a tela de login.
     */
    @FXML
    void handleSair() {
        logger.info("Botão 'Sair' clicado! Voltando para a tela de login...");
        carregarNovaTela("/fxml/MenuPrincipal.fxml", "Gym System - Login");
    }


    /**
     * Metodo auxiliar para carregar uma nova tela FXML, fechando a atual.
     *
     * @param fxmlFile O caminho do arquivo FXML a ser carregado.
     * @param titulo   O título da nova janela.
     */
    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            logger.info("Tela carregada com sucesso: " + fxmlFile);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxmlFile, e);
        }
    }

    /**
     * Exibe uma caixa de diálogo para feedback do usuário.
     *
     * @param tipo     Tipo do alerta (INFORMATION, WARNING, ERROR, etc.)
     * @param titulo   Título da janela de alerta
     * @param mensagem Mensagem a ser exibida
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        if (tipo == Alert.AlertType.ERROR) {
            StyledAlert.showErrorAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.INFORMATION) {
            StyledAlert.showInformationAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.WARNING) {
            StyledAlert.showWarningAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.CONFIRMATION) {
            StyledAlert.showConfirmationAndWait(titulo, mensagem);
        }
    }
}