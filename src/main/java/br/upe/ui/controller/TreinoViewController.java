package br.upe.ui.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controlador da tela de Treinos do usuário.
 * Permite listar, editar, deletar, adicionar e visualizar detalhes dos planos de treino.
 */
public class TreinoViewController {

        private static final Logger logger = Logger.getLogger(TreinoViewController.class.getName());

        // --- COMPONENTES VINCULADOS AO FXML ---
        @FXML
        private ImageView ITreino;

        @FXML
        private ImageView IFechar;

        @FXML
        private Button BListarTR;

        @FXML
        private Button BEditarTR;

        @FXML
        private Button BDeletarTR;

        @FXML
        private Button BAdicionarTR;

        @FXML
        private Button BRemoverTR;

        @FXML
        private Button BDetalhesTR;


        // --- MÉTODOS EXECUTADOS PELOS BOTÕES ---

        /**
         * Chamado ao clicar em "Listar meus planos de treino".
         * TODO: implementar tela lista de planos de treino
         */
        @FXML
        void listarPlanosDeTreino(MouseEvent event) {
                logger.info("Botão 'Listar meus planos de treino' clicado!");
                carregarNovaTela("/fxml/ListarTreinosView.fxml", "Gym System - Meus Planos de Treino");
        }

        /**
         * Chamado ao clicar em "Editar plano de treino".
         * TODO: implementar tela editar plano de treino
         */
        @FXML
        void editarPlanoDeTreino(MouseEvent event) {
                logger.info("Botão 'Editar plano de treino' clicado!");
                carregarNovaTela("/fxml/EditarTreinoView.fxml", "Gym System - Editar Plano de Treino");
        }

        /**
         * Chamado ao clicar em "Deletar plano de treino".
         * TODO: implementar tela deletar plano de treino
         */
        @FXML
        void deletarPlanoDeTreino(MouseEvent event) {
                logger.info("Botão 'Deletar plano de treino' clicado!");
                carregarNovaTela("/fxml/DeletarTreinoView.fxml", "Gym System - Deletar Plano de Treino");
        }

        /**
         * Chamado ao clicar em "Adicionar plano de treino".
         * TODO: implementar tela criar plano de treino
         */
        @FXML
        void adicionarPlanoDeTreino(MouseEvent event) {
                logger.info("Botão 'Adicionar plano de treino' clicado!");
                carregarNovaTela("/fxml/AdicionarTreinoView.fxml", "Gym System - Adicionar Plano de Treino");
        }

        /**
         * Chamado ao clicar em "Remover plano de treino".
         * está duplicado?
         */
        @FXML
        void removerPlanoDeTreino(MouseEvent event) {
                logger.info("Botão 'Remover plano de treino' clicado!");
                carregarNovaTela("/fxml/RemoverTreinoView.fxml", "Gym System - Remover Plano de Treino");
        }

        /**
         * Chamado ao clicar em "Ver detalhes".
         * TODO: implementar tela ver detalhes do plano de treino
         */
        @FXML
        void verDetalhesTreino(MouseEvent event) {
                logger.info("Botão 'Ver detalhes' clicado!");
                carregarNovaTela("/fxml/DetalhesTreinoView.fxml", "Gym System - Detalhes do Plano de Treino");
        }

        /**
         * Chamado ao clicar no ícone de fechar (IFechar).
         * Retorna ao menu principal do usuário logado.
         */
        @FXML
        void fecharTela(MouseEvent event) {
                logger.info("Ícone 'Fechar' clicado! Retornando ao menu principal...");
                carregarNovaTela("/fxml/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
        }


        // --- MÉTODOS AUXILIARES ---

        /**
         * Metodo para carregar uma nova tela FXML, fechando a atual.
         *
         * @param fxmlFile caminho do arquivo FXML
         * @param titulo   título da nova janela
         */
        private void carregarNovaTela(String fxmlFile, String titulo) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                        Parent root = loader.load();

                        // obtém o stage atual através de qualquer botão (usa BListarTR como referência)
                        Stage stage = (Stage) BListarTR.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle(titulo);
                        stage.show();

                        logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);

                } catch (IOException e) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir tela",
                                "Não foi possível carregar a tela solicitada. Verifique o caminho FXML.");
                        logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxmlFile, e);
                }
        }

        /**
         * Exibe uma caixa de diálogo de alerta para o usuário.
         *
         * @param tipo     Tipo do alerta (INFORMATION, WARNING, ERROR)
         * @param titulo   Título do alerta
         * @param mensagem Mensagem exibida
         */
        private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
                Alert alert = new Alert(tipo);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }
}
