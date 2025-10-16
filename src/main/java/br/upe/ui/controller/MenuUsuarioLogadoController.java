package br.upe.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuUsuarioLogadoController {

    // --- VARIÁVEIS VINCULADAS AOS COMPONENTES DA TELA ---
    // Para que estas variáveis funcionem, você precisa definir o "fx:id"
    // para cada botão correspondente no Scene Builder.
    @FXML
    private Button btnMeusTreinos;

    @FXML
    private Button btnMeusIndicadores;

    @FXML
    private Button btnGerenciarPlanoDeTreino;

    @FXML
    private Button btnGerenciarExercicios;

    @FXML
    private Button btnExportarRelatorios;

    @FXML
    private Button btnSair;


    // --- MÉTODOS EXECUTADOS PELOS BOTÕES (On Mouse Clicked) ---

    /**
     * Chamado quando o botão "Meus Treinos" é clicado.
     * Deve abrir a tela de visualização de treinos do usuário.
     */
    @FXML
    void abrirMeusTreinos(ActionEvent event) {
        System.out.println("Botão 'Meus Treinos' clicado!");
        // Exemplo de como carregar uma nova tela:
        // carregarNovaTela(event, "TelaMeusTreinos.fxml");
    }

    /**
     * Chamado quando o botão "Meus Indicadores" é clicado.
     * Deve abrir a tela de indicadores de progresso do usuário.
     */
    @FXML
    void abrirMeusIndicadores(ActionEvent event) {
        System.out.println("Botão 'Meus Indicadores' clicado!");
        // carregarNovaTela(event, "TelaMeusIndicadores.fxml");
    }

    /**
     * Chamado quando o botão "Gerenciar Plano de Treino" é clicado.
     * Deve abrir a tela para o usuário gerenciar seus planos.
     */
    @FXML
    void abrirGerenciarPlanoDeTreino(ActionEvent event) {
        System.out.println("Botão 'Gerenciar Plano de Treino' clicado!");
        // carregarNovaTela(event, "TelaPlanoTreino.fxml");
    }

    /**
     * Chamado quando o botão "Gerenciar Exercícios" é clicado.
     * Deve abrir a tela de gerenciamento de exercícios.
     */
    @FXML
    void abrirGerenciarExercicios(ActionEvent event) {
        System.out.println("Botão 'Gerenciar Exercícios' clicado!");
        // carregarNovaTela(event, "TelaGerenciarExercicios.fxml");
    }

    /**
     * Chamado quando o botão "Exportar Relatórios" é clicado.
     * Deve abrir a funcionalidade de exportação de relatórios.
     */
    @FXML
    void abrirExportarRelatorios(ActionEvent event) {
        System.out.println("Botão 'Exportar Relatórios' clicado!");
        // carregarNovaTela(event, "TelaExportarRelatorios.fxml");
    }

    /**
     * Chamado quando o botão "Sair" é clicado.
     * Deve fechar a tela atual e retornar para a tela de login.
     */
    @FXML
    void sairDaConta(ActionEvent event) {
        System.out.println("Botão 'Sair' clicado! Voltando para a tela de login...");
        // Exemplo para voltar à tela de login
        // carregarNovaTela(event, "TelaLogin.fxml");
    }


    /**
     * Método auxiliar para carregar uma nova tela FXML, fechando a atual.
     *
     * @param event    O evento de ação que disparou a chamada (necessário para obter o Stage atual).
     * @param fxmlFile O nome do arquivo FXML a ser carregado (ex: "TelaLogin.fxml").
     */
    private void carregarNovaTela(ActionEvent event, String fxmlFile) {
        try {
            // Carrega o recurso FXML da nova tela
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));

            // Obtém o Stage (a janela) atual a partir do evento do botão
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cria a nova cena com o FXML carregado
            Scene scene = new Scene(root);

            // Define a nova cena no Stage
            stage.setScene(scene);

            // Exibe o Stage atualizado
            stage.show();

        } catch (IOException e) {
            System.err.println("Falha ao carregar a tela: " + fxmlFile);
            e.printStackTrace();
        }
    }
}