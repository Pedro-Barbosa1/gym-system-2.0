package br.upe.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.model.SessaoTreino;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.service.IPlanoTreinoService;
import br.upe.service.PlanoTreinoService;
import br.upe.service.SessaoTreinoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controlador da tela de Treinos do usuário.
 * Gerencia a navegação e funcionalidades relacionadas aos planos de treino.
 * Adaptado da classe MenuTreinos para interface gráfica JavaFX.
 */
public class TreinoViewController {

        private static final Logger logger = Logger.getLogger(TreinoViewController.class.getName());

        // --- SERVIÇOS ---
        private final SessaoTreinoService sessaoTreinoService;
        private final IPlanoTreinoService planoTreinoService;
        private final IExercicioService exercicioService;
        
        // ID do usuário logado (será configurado posteriormente com sistema de autenticação)
        private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login

        // --- COMPONENTES VINCULADOS AO FXML ---
        @FXML
        private ImageView ITreino;

        @FXML
        private ImageView IFechar;

        @FXML
        private javafx.scene.control.ButtonBar exitB;

        @FXML
        private Button BListarTR;

        @FXML
        private Button BEditarTR;

        /**
         * Construtor - inicializa os serviços necessários.
         */
        public TreinoViewController() {
                this.sessaoTreinoService = new SessaoTreinoService();
                this.planoTreinoService = new PlanoTreinoService();
                this.exercicioService = new ExercicioService();
        }


        // --- MÉTODOS EXECUTADOS PELOS BOTÕES ---

        /**
         * Inicia uma nova sessão de treino através de dialogs interativos.
         * Vinculado ao botão "Nova Sessão de Treino".
         * Adaptado do método iniciarNovaSessao() da classe MenuTreinos.
         */
        @FXML
        void iniciarNovaSessao(ActionEvent event) {
                logger.info("Iniciando nova sessão de treino...");
                
                // 1. Buscar planos do usuário
                List<PlanoTreino> meusPlanos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

                if (meusPlanos.isEmpty()) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Sem Planos", 
                                "Você não possui planos de treino cadastrados. Crie um plano primeiro.");
                        return;
                }

                // 2. Exibir dialog de seleção de plano
                PlanoTreino planoEscolhido = exibirDialogSelecaoPlano(meusPlanos);
                if (planoEscolhido == null) {
                        logger.info("Usuário cancelou a seleção de plano.");
                        return;
                }

                // 3. Validar plano
                if (planoEscolhido.getItensTreino().isEmpty()) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Plano Vazio", 
                                "Este plano não possui exercícios. Adicione exercícios ao plano antes de iniciar uma sessão.");
                        return;
                }

                try {
                        // 4. Iniciar sessão
                        SessaoTreino sessaoAtual = sessaoTreinoService.iniciarSessao(idUsuarioLogado, planoEscolhido.getIdPlano());
                        logger.info("Sessão iniciada para o plano: " + planoEscolhido.getNome() + " em " + sessaoAtual.getDataSessao());

                        // 5. Registrar execução de cada exercício através de dialogs
                        boolean concluido = registrarExerciciosComDialog(sessaoAtual, planoEscolhido);
                        
                        if (!concluido) {
                                logger.info("Sessão cancelada pelo usuário.");
                                return;
                        }

                        // 6. Salvar sessão
                        sessaoTreinoService.salvarSessao(sessaoAtual);
                        logger.info("===== FIM DA SESSÃO =====");

                        // 7. Verificar e exibir sugestões de atualização
                        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
                                sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessaoAtual);
                        tratarSugestoesComDialog(sugestoes, planoEscolhido);

                        // 8. Mensagem de sucesso
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Sessão Concluída", 
                                "Sessão de treino registrada com sucesso!");

                } catch (NumberFormatException e) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Erro de Entrada", 
                                "Entrada inválida para repetições ou carga. Por favor, digite um número válido.");
                        logger.log(Level.SEVERE, "Erro de formato numérico", e);
                } catch (IllegalArgumentException e) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                                "Erro ao iniciar ou registrar sessão: " + e.getMessage());
                        logger.log(Level.SEVERE, "Erro ao processar sessão", e);
                }
        }

        /**
         * Exibe o histórico de sessões de treino do usuário.
         * Vinculado ao botão "Ver Histórico de Sessões".
         */
        @FXML
        void verHistoricoSessoes(ActionEvent event) {
                logger.info("Botão 'Ver Histórico de Sessões' clicado!");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Em Desenvolvimento", 
                        "A funcionalidade de histórico de sessões será implementada em breve.");
        }


        // --- LÓGICA DE NEGÓCIO ADAPTADA DE MenuTreinos ---

        /**
         * Exibe dialog para seleção de plano de treino.
         * Adaptado do método exibirPlanos() e solicitarPlanoId() do MenuTreinos.
         */
        private PlanoTreino exibirDialogSelecaoPlano(List<PlanoTreino> planos) {
                List<String> opcoesPlanos = new ArrayList<>();
                for (PlanoTreino p : planos) {
                        opcoesPlanos.add(p.getIdPlano() + " - " + p.getNome());
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoesPlanos.get(0), opcoesPlanos);
                dialog.setTitle("Selecionar Plano de Treino");
                dialog.setHeaderText("Escolha o plano para esta sessão:");
                dialog.setContentText("Plano:");

                Optional<String> resultado = dialog.showAndWait();
                if (resultado.isPresent()) {
                        String escolha = resultado.get();
                        int idPlano = Integer.parseInt(escolha.split(" - ")[0].trim());
                        return planos.stream()
                                .filter(p -> p.getIdPlano() == idPlano)
                                .findFirst()
                                .orElse(null);
                }
                return null;
        }

        /**
         * Registra a execução de exercícios através de dialogs customizados.
         * Adaptado do método registrarExercicios() do MenuTreinos.
         */
        private boolean registrarExerciciosComDialog(SessaoTreino sessaoAtual, PlanoTreino planoBase) {
                logger.info("--- Registrando Exercícios ---");

                for (ItemPlanoTreino itemPlanejado : planoBase.getItensTreino()) {
                        Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(itemPlanejado.getIdExercicio());
                        String nomeExercicio = exercicioOpt.isPresent() ? exercicioOpt.get().getNome() : "Exercício Desconhecido";

                        // Dialog customizado para registrar repetições e carga
                        Dialog<ButtonType> dialog = new Dialog<>();
                        dialog.setTitle("Registrar Exercício");
                        dialog.setHeaderText("Exercício: " + nomeExercicio);

                        // Criar grid com campos de entrada
                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        Label infoLabel = new Label(String.format("Planejado: Carga %dkg, Repetições %d", 
                                itemPlanejado.getCargaKg(), itemPlanejado.getRepeticoes()));
                        Label repLabel = new Label("Repetições realizadas:");
                        TextField repField = new TextField(String.valueOf(itemPlanejado.getRepeticoes()));
                        Label cargaLabel = new Label("Carga utilizada (kg):");
                        TextField cargaField = new TextField(String.valueOf(itemPlanejado.getCargaKg()));

                        grid.add(infoLabel, 0, 0, 2, 1);
                        grid.add(repLabel, 0, 1);
                        grid.add(repField, 1, 1);
                        grid.add(cargaLabel, 0, 2);
                        grid.add(cargaField, 1, 2);

                        dialog.getDialogPane().setContent(grid);
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                        Optional<ButtonType> resultado = dialog.showAndWait();
                        if (resultado.isEmpty() || resultado.get() == ButtonType.CANCEL) {
                                logger.info("Registro de exercício cancelado pelo usuário.");
                                return false;
                        }

                        try {
                                int repRealizadas = Integer.parseInt(repField.getText());
                                double cargaRealizada = Double.parseDouble(cargaField.getText());
                                sessaoTreinoService.registrarExecucao(sessaoAtual, itemPlanejado.getIdExercicio(), 
                                        repRealizadas, cargaRealizada);
                        } catch (NumberFormatException e) {
                                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Valores inválidos. Usando valores planejados.");
                                sessaoTreinoService.registrarExecucao(sessaoAtual, itemPlanejado.getIdExercicio(), 
                                        (int)itemPlanejado.getRepeticoes(), itemPlanejado.getCargaKg());
                        }
                }
                return true;
        }

        /**
         * Trata sugestões de atualização do plano através de dialogs de confirmação.
         * Adaptado do método tratarSugestoes() do MenuTreinos.
         */
        private void tratarSugestoesComDialog(List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes, 
                                              PlanoTreino planoBase) {
                if (sugestoes.isEmpty()) {
                        logger.info("Nenhuma alteração significativa nos exercícios para sugerir atualização do plano.");
                        return;
                }

                logger.info("--- Sugestões de Atualização do Plano ---");
                for (SessaoTreinoService.SugestaoAtualizacaoPlano sugestao : sugestoes) {
                        String mensagem = String.format(
                                "O exercício '%s' teve alterações:\n\n" +
                                "Repetições: Planejado %d → Realizado %d\n" +
                                "Carga: Planejado %.0fkg → Realizado %.0fkg\n\n" +
                                "Deseja atualizar o plano com os novos valores?",
                                sugestao.nomeExercicio(),
                                sugestao.repPlanejadas(), sugestao.repRealizadas(),
                                sugestao.cargaPlanejada(), sugestao.cargaRealizada()
                        );

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Atualizar Plano de Treino");
                        alert.setHeaderText("Sugestão de Atualização");
                        alert.setContentText(mensagem);

                        Optional<ButtonType> resultado = alert.showAndWait();
                        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                                sessaoTreinoService.aplicarAtualizacoesNoPlano(planoBase.getIdPlano(), 
                                        sugestao.idExercicio(), sugestao.repRealizadas(), sugestao.cargaRealizada());
                                logger.info("Plano atualizado para " + sugestao.nomeExercicio());
                                mostrarAlerta(Alert.AlertType.INFORMATION, "Atualizado", 
                                        "Plano atualizado com sucesso para " + sugestao.nomeExercicio() + "!");
                        }
                }
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


        /**
         * Método auxiliar para carregar uma nova tela FXML, fechando a atual.
         *
         * @param fxmlFile O caminho do arquivo FXML a ser carregado.
         * @param titulo   O título da nova janela.
         */
        private void carregarNovaTela(String fxmlFile, String titulo) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                        Parent root = loader.load();

                        // Obtém o stage atual através de qualquer botão (usa BListarTR como referência)
                        Stage stage = (Stage) BListarTR.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle(titulo);
                        stage.show();

                        logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);

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
                Alert alert = new Alert(tipo);
                alert.setTitle(titulo);
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }
}
