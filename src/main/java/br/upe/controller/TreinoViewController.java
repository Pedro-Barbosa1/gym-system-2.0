package br.upe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.ItemSessaoTreino;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
<<<<<<< HEAD:src/main/java/br/upe/ui/controller/TreinoViewController.java
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
=======
>>>>>>> 4fbce8a0fac5f0400545d352aee02a99e6ca747f:src/main/java/br/upe/controller/TreinoViewController.java

public class TreinoViewController {

    private static final Logger logger = Logger.getLogger(TreinoViewController.class.getName());

    private final SessaoTreinoService sessaoTreinoService;
    private final IPlanoTreinoService planoTreinoService;
    private final IExercicioService exercicioService;
    
    private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login

    @FXML
    private ImageView ITreino;

    @FXML
    private Button IFechar;

    @FXML
    private javafx.scene.control.ButtonBar exitB;

    @FXML
    private Button BListarTR;

    @FXML
    private Button BEditarTR;

    public TreinoViewController() {
        this.sessaoTreinoService = new SessaoTreinoService();
        this.planoTreinoService = new PlanoTreinoService();
        this.exercicioService = new ExercicioService();
    }


    @FXML
    void iniciarNovaSessao(ActionEvent event) {
        logger.info("Iniciando nova sessão de treino...");
        
        List<PlanoTreino> meusPlanos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

        if (meusPlanos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Planos", 
                "Você não possui planos de treino cadastrados. Crie um plano primeiro.");
            return;
        }

        PlanoTreino planoEscolhido = exibirDialogSelecaoPlano(meusPlanos);
        if (planoEscolhido == null) {
            logger.info("Usuário cancelou a seleção de plano.");
            return;
        }

        if (planoEscolhido.getItensTreino().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Plano Vazio", 
                "Este plano não possui exercícios. Adicione exercícios ao plano antes de iniciar uma sessão.");
            return;
        }

        try {
            SessaoTreino sessaoAtual = sessaoTreinoService.iniciarSessao(idUsuarioLogado, planoEscolhido.getIdPlano());
            logger.info("Sessão iniciada para o plano: " + planoEscolhido.getNome() + " em " + sessaoAtual.getDataSessao());

            boolean concluido = registrarExerciciosComDialog(sessaoAtual, planoEscolhido);
            
            if (!concluido) {
                logger.info("Sessão cancelada pelo usuário.");
                return;
            }

            sessaoTreinoService.salvarSessao(sessaoAtual);
            logger.info("===== FIM DA SESSÃO =====");

            List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
                sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessaoAtual);
            tratarSugestoesComDialog(sugestoes, planoEscolhido);

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

    @FXML
    void verHistoricoSessoes(ActionEvent event) {
        logger.info("Botão 'Ver Histórico de Sessões' clicado!");

        List<SessaoTreino> historico = sessaoTreinoService.listarSessoesPorUsuario(idUsuarioLogado);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Meu Histórico de Treinos");
        dialog.setHeaderText("Total de " + historico.size() + " sessão(ões) registrada(s)");

        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            javafx.scene.Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null)
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            javafx.scene.Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null)
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
        });

        StringBuilder historicoTxt = new StringBuilder();
        for (SessaoTreino sessao : historico) {
            historicoTxt.append("======================================\n");
            historicoTxt.append("Data: ").append(sessao.getDataSessao()).append("\n");
            historicoTxt.append("Plano ID: ").append(sessao.getIdPlanoTreino()).append("\n");
            for (ItemSessaoTreino item : sessao.getItensExecutados()) {
                String nomeExercicio = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio())
                    .map(Exercicio::getNome)
                    .orElse("Exercício Desconhecido");
                historicoTxt.append(String.format(
                    "Exercício: %s | Repetições: %d | Carga: %.1f kg\n",
                    nomeExercicio,
                    item.getRepeticoesRealizadas(),
                    item.getCargaRealizada()
                ));
            }
            historicoTxt.append("\n");
        }
    TextArea conteudo = new TextArea(historicoTxt.toString());
    conteudo.setFont(Font.font("Consolas", 14));
    conteudo.setStyle("-fx-text-fill: #ffb300; -fx-control-inner-background: #2c2c2c;");
    conteudo.setEditable(false);
    conteudo.setWrapText(false);
    conteudo.setPrefWidth(600);
    conteudo.setPrefHeight(400);

    VBox box = new VBox(conteudo);
    box.setStyle("-fx-background-color: #2c2c2c;");
    box.setPadding(new Insets(10));

    dialog.setTitle("Meu Histórico de Treinos");
    dialog.setHeaderText("Total de " + historico.size() + " sessão(ões) registrada(s)");
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    dialog.getDialogPane().setContent(box);

    dialog.showAndWait();


        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }


    private PlanoTreino exibirDialogSelecaoPlano(List<PlanoTreino> planos) {
        List<String> opcoesPlanos = new ArrayList<>();
        for (PlanoTreino p : planos) {
            opcoesPlanos.add(p.getIdPlano() + " - " + p.getNome());
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Selecionar Plano de Treino");
        dialog.setHeaderText("Escolha o plano para esta sessão:");

        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            javafx.scene.Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null)
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            javafx.scene.Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null)
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
        });

        Label planoLabel = new Label("Plano:");
        planoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
        javafx.scene.control.ComboBox<String> planoCombo = new javafx.scene.control.ComboBox<>();

        planoCombo.setCellFactory(lv -> new ListCell<String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : item);
                        setTextFill(javafx.scene.paint.Color.web("#ffb300"));  // texto laranja
                        setStyle("-fx-background-color: #222;"); // fundo escuro para os itens da lista
                }
                });

                // Para o texto mostrado quando um item é selecionado
                planoCombo.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : item);
                        setTextFill(javafx.scene.paint.Color.web("#ffb300"));  // texto laranja
                        setStyle("-fx-background-color: #222;"); // fundo do campo selecionado
                }
                });

        planoCombo.getItems().addAll(opcoesPlanos);
        planoCombo.getSelectionModel().selectFirst();
        planoCombo.setStyle("-fx-background-color: #222; -fx-text-fill: #ffb300;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));
        grid.setStyle("-fx-background-color: #2c2c2c;");
        grid.add(planoLabel, 0, 0);
        grid.add(planoCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            int idPlano = Integer.parseInt(planoCombo.getValue().split(" - ")[0].trim());
            return planos.stream().filter(p -> p.getIdPlano() == idPlano).findFirst().orElse(null);
        }
        return null;
    }

    // NOVO METODO DE REGISTRO DOS EXERCÍCIOS
    private boolean registrarExerciciosComDialog(SessaoTreino sessaoAtual, PlanoTreino planoBase) {
        logger.info("--- Registrando Exercícios ---");

        for (ItemPlanoTreino itemPlanejado : planoBase.getItensTreino()) {
            Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(itemPlanejado.getIdExercicio());
            String nomeExercicio = exercicioOpt.isPresent() ? exercicioOpt.get().getNome() : "Exercício Desconhecido";

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Registrar Exercício");
            dialog.setHeaderText("Exercício: " + nomeExercicio);

            dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
            dialog.setOnShown(e -> {
                javafx.scene.Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
                if (headerPanel != null)
                    headerPanel.setStyle("-fx-background-color: #1e1e1e;");
                javafx.scene.Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
                if (headerLabel != null)
                    headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            });

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 80, 10, 10));
            grid.setStyle("-fx-background-color: #2c2c2c;");

            Label infoLabel = new Label(String.format("Planejado: Carga %dkg, Repetições %d", itemPlanejado.getCargaKg(), itemPlanejado.getRepeticoes()));
            infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
            Label repLabel = new Label("Repetições realizadas:");
            repLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
            TextField repField = new TextField(String.valueOf(itemPlanejado.getRepeticoes()));
            repField.setStyle("-fx-background-color: #222; -fx-text-fill: #ffb300; -fx-border-radius: 4;");
            Label cargaLabel = new Label("Carga utilizada (kg):");
            cargaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
            TextField cargaField = new TextField(String.valueOf(itemPlanejado.getCargaKg()));
            cargaField.setStyle("-fx-background-color: #222; -fx-text-fill: #ffb300; -fx-border-radius: 4;");

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
                sessaoTreinoService.registrarExecucao(sessaoAtual, itemPlanejado.getIdExercicio(), repRealizadas, cargaRealizada);
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Valores inválidos. Usando valores planejados.");
                sessaoTreinoService.registrarExecucao(sessaoAtual, itemPlanejado.getIdExercicio(), (int) itemPlanejado.getRepeticoes(), itemPlanejado.getCargaKg());
            }
        }
        return true;
    }

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

    @FXML
    void fecharTela(ActionEvent event) {
        logger.info("Ícone 'Fechar' clicado! Retornando ao menu principal...");
        carregarNovaTela("/fxml/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
    }

    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
