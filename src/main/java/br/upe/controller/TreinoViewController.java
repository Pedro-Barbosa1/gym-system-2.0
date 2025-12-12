package br.upe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import br.upe.ui.util.StyledAlert;
import br.upe.util.UserSession;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TreinoViewController {

    private static final Logger logger = Logger.getLogger(TreinoViewController.class.getName());

    private final SessaoTreinoService sessaoTreinoService;
    private final IPlanoTreinoService planoTreinoService;
    private final IExercicioService exercicioService;
    
    private int idUsuarioLogado = 1; 

    @FXML private TableView<SessaoTreino> tableSessoes;
    @FXML private TableColumn<SessaoTreino, String> colData;
    @FXML private TableColumn<SessaoTreino, String> colPlano;
    @FXML private TableColumn<SessaoTreino, String> colExercicios;
    @FXML private TableColumn<SessaoTreino, Void> colAcoes;
    @FXML private Button BAdicionarSessao;
    @FXML private Button BVoltar;
    @FXML private Label totalLabel;

    public TreinoViewController() {
        this.sessaoTreinoService = new SessaoTreinoService();
        this.planoTreinoService = new PlanoTreinoService();
        this.exercicioService = new ExercicioService();
    }

    @FXML
    private void initialize() {
        // configurar colunas
        colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDataSessao().toString()));
        
        colPlano.setCellValueFactory(data -> {
            int idPlano = data.getValue().getIdPlanoTreino();
            return new SimpleStringProperty(String.valueOf(idPlano));
        });
        
        colExercicios.setCellValueFactory(data -> {
            List<ItemSessaoTreino> itens = data.getValue().getItensExecutados();
            String exercicios = itens.stream()
                .map(item -> {
                    Optional<Exercicio> ex = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
                    return ex.isPresent() ? ex.get().getNome() : "Ex. " + item.getIdExercicio();
                })
                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(exercicios.isEmpty() ? "Nenhum exercício" : exercicios);
        });

        // ações (visualizar / editar / remover)
        adicionarColunaAcoes();

        // forçar ajuste das colunas à largura da tabela
        tableSessoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableSessoes.setFixedCellSize(56);

        // carregar dados
        loadSessoes();
        setupResizeListeners();
    }

    private void adicionarColunaAcoes() {
        colAcoes.setCellFactory(col -> new TableCell<SessaoTreino, Void>() {
            private final Button btnVisualizar = new Button("Visualizar");
            private final Button btnEditar = new Button("Editar");
            private final Button btnRemover = new Button("Remover");
            private final HBox container = new HBox(8, btnVisualizar, btnEditar, btnRemover);

            {
                btnVisualizar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnEditar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnRemover.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");

                btnVisualizar.setPrefWidth(90);
                btnEditar.setPrefWidth(70);
                btnRemover.setPrefWidth(70);

                container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(6, 4, 6, 4));
                container.setPrefHeight(48);
                container.setMinHeight(48);

                btnVisualizar.setPrefHeight(28);
                btnEditar.setPrefHeight(28);
                btnRemover.setPrefHeight(28);

                btnVisualizar.setOnAction(e -> {
                    SessaoTreino sessao = getTableView().getItems().get(getIndex());
                    visualizarSessao(sessao);
                });

                btnEditar.setOnAction(e -> {
                    SessaoTreino sessao = getTableView().getItems().get(getIndex());
                    editarSessao(sessao);
                });

                btnRemover.setOnAction(e -> {
                    SessaoTreino sessao = getTableView().getItems().get(getIndex());
                    removerSessao(sessao);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void loadSessoes() {
        List<SessaoTreino> sessoes = sessaoTreinoService.listarSessoesPorUsuario(idUsuarioLogado);
        tableSessoes.setItems(FXCollections.observableArrayList(sessoes));
        if (totalLabel != null) {
            totalLabel.setText(String.format("Total de %d sessão(ões) de treino", sessoes.size()));
        }
        
        double fixed = tableSessoes.getFixedCellSize();
        if (fixed <= 0) fixed = 56;
        int rowsToShow = Math.max(6, sessoes.size());
        double header = 30;
        tableSessoes.setPrefHeight(fixed * rowsToShow + header);
        aplicarEstiloTableViewSessao(tableSessoes);
    }

    @FXML
    private void handleAdicionarSessao(ActionEvent event) {
        iniciarNovaSessao(event);
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        if (!br.upe.util.NavigationUtil.navigateFrom(BVoltar, "/ui/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar para a tela anterior.");
        }
    }

    private void visualizarSessao(SessaoTreino sessao) {
        Dialog<ButtonType> dialog = criarDialogPadrao("Visualizar Sessão", "Sessão de " + sessao.getDataSessao().toString());
        
        Optional<PlanoTreino> planoOpt = planoTreinoService.buscarPlanoPorId(sessao.getIdPlanoTreino());
        PlanoTreino plano = planoOpt.orElse(null);
        String nomePlano = plano != null ? plano.getNome() : "Plano ID: " + sessao.getIdPlanoTreino();
        
        // Criar TableView com os exercícios executados
        TableView<ItemSessaoTreino> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(sessao.getItensExecutados()));
        tableView.setPrefWidth(600);
        tableView.setPrefHeight(300);

        TableColumn<ItemSessaoTreino, String> colExercicio = new TableColumn<>("Exercício");
        colExercicio.setCellValueFactory(data -> {
            Optional<Exercicio> ex = exercicioService.buscarExercicioPorIdGlobal(data.getValue().getIdExercicio());
            String nome = ex.isPresent() ? ex.get().getNome() : "Ex. " + data.getValue().getIdExercicio();
            return new SimpleStringProperty(nome);
        });
        colExercicio.setPrefWidth(300);

        TableColumn<ItemSessaoTreino, Integer> colRepeticoes = new TableColumn<>("Repetições");
        colRepeticoes.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRepeticoesRealizadas()).asObject());
        colRepeticoes.setPrefWidth(150);
        colRepeticoes.setStyle("-fx-alignment: CENTER;");

        TableColumn<ItemSessaoTreino, String> colCarga = new TableColumn<>("Carga (kg)");
        colCarga.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().getCargaRealizada())));
        colCarga.setPrefWidth(150);
        colCarga.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(colExercicio, colRepeticoes, colCarga);
        aplicarEstiloTableView(tableView);

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #2c2c2c;");
        content.setPadding(new Insets(10));
        
        Label planoLabel = criarLabel("Plano: " + nomePlano);
        Label dataLabel = criarLabel("Data: " + sessao.getDataSessao());
        
        content.getChildren().addAll(planoLabel, dataLabel, tableView);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void editarSessao(SessaoTreino sessao) {
        Dialog<ButtonType> dialog = criarDialogPadrao("Editar Sessão", "Editar sessão de " + sessao.getDataSessao().toString());
        
        // Criar TableView editável com os exercícios
        TableView<ItemSessaoTreino> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(sessao.getItensExecutados()));
        tableView.setEditable(true);
        tableView.setPrefWidth(600);
        tableView.setPrefHeight(300);

        TableColumn<ItemSessaoTreino, String> colExercicio = new TableColumn<>("Exercício");
        colExercicio.setCellValueFactory(data -> {
            Optional<Exercicio> ex = exercicioService.buscarExercicioPorIdGlobal(data.getValue().getIdExercicio());
            String nome = ex.isPresent() ? ex.get().getNome() : "Ex. " + data.getValue().getIdExercicio();
            return new SimpleStringProperty(nome);
        });
        colExercicio.setPrefWidth(250);

        TableColumn<ItemSessaoTreino, String> colRepeticoes = new TableColumn<>("Repetições");
        colRepeticoes.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRepeticoesRealizadas())));
        colRepeticoes.setPrefWidth(150);
        colRepeticoes.setStyle("-fx-alignment: CENTER;");

        TableColumn<ItemSessaoTreino, String> colCarga = new TableColumn<>("Carga (kg)");
        colCarga.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().getCargaRealizada())));
        colCarga.setPrefWidth(150);
        colCarga.setStyle("-fx-alignment: CENTER;");

        TableColumn<ItemSessaoTreino, Void> colAcaoEditar = new TableColumn<>("Ação");
        colAcaoEditar.setCellFactory(col -> new TableCell<ItemSessaoTreino, Void>() {
            private final Button btnEditar = new Button("Editar");
            {
                btnEditar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnEditar.setOnAction(e -> {
                    ItemSessaoTreino item = getTableView().getItems().get(getIndex());
                    editarItemSessao(item, sessao);
                });
            }
            @Override
            protected void updateItem(Void voidItem, boolean empty) {
                super.updateItem(voidItem, empty);
                setGraphic(empty ? null : btnEditar);
            }
        });
        colAcaoEditar.setPrefWidth(100);

        tableView.getColumns().addAll(colExercicio, colRepeticoes, colCarga, colAcaoEditar);
        aplicarEstiloTableView(tableView);

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #2c2c2c;");
        content.setPadding(new Insets(10));
        content.getChildren().add(tableView);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sessaoTreinoService.salvarSessao(sessao);
                loadSessoes();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Sessão atualizada com sucesso!");
            }
        });
    }

    private void editarItemSessao(ItemSessaoTreino item, SessaoTreino sessao) {
        Dialog<ButtonType> dialog = criarDialogPadrao("Editar Item", "Modificar exercício da sessão");
        GridPane grid = criarGridPadrao();

        Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
        String nomeExercicio = exercicioOpt.isPresent() ? exercicioOpt.get().getNome() : "Ex. " + item.getIdExercicio();

        TextField repeticoesField = criarCampoTexto(String.valueOf(item.getRepeticoesRealizadas()));
        TextField cargaField = criarCampoTexto(String.format("%.1f", item.getCargaRealizada()));

        grid.add(criarLabel("Exercício: " + nomeExercicio), 0, 0, 2, 1);
        grid.add(criarLabel("Repetições:"), 0, 1);
        grid.add(repeticoesField, 1, 1);
        grid.add(criarLabel("Carga (kg):"), 0, 2);
        grid.add(cargaField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int novasRepeticoes = Integer.parseInt(repeticoesField.getText().trim());
                    double novaCarga = Double.parseDouble(cargaField.getText().trim());
                    
                    item.setRepeticoesRealizadas(novasRepeticoes);
                    item.setCargaRealizada(novaCarga);
                    
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Item atualizado! Clique em OK na janela anterior para salvar.");
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Valores inválidos. Digite números válidos.");
                }
            }
        });
    }

    private void removerSessao(SessaoTreino sessao) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setHeaderText("Excluir sessão de " + sessao.getDataSessao().toString());
        confirm.setContentText("Tem certeza que deseja excluir esta sessão de treino?");
        
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    sessaoTreinoService.removerSessao(sessao.getIdSessao());
                    loadSessoes();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Sessão removida com sucesso!");
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível remover a sessão: " + e.getMessage());
                    logger.log(Level.SEVERE, "Erro ao remover sessão", e);
                }
            }
        });
    }


    @FXML
    void iniciarNovaSessao(ActionEvent event) {
        logger.info("Iniciando nova sessão de treino...");
        
        List<PlanoTreino> meusPlanos = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());

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
            SessaoTreino sessaoAtual = sessaoTreinoService.iniciarSessao(UserSession.getInstance().getIdUsuarioLogado(), planoEscolhido.getIdPlano());
            logger.info("Sessão iniciada para o plano: " + planoEscolhido.getNome() + " em " + sessaoAtual.getDataSessao());

            boolean concluido = registrarExerciciosComDialog(sessaoAtual, planoEscolhido);
            
            if (!concluido) {
                logger.info("Sessão cancelada pelo usuário.");
                return;
            }

            sessaoTreinoService.salvarSessao(sessaoAtual);
            logger.info("===== FIM DA SESSÃO =====");

            loadSessoes();
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

        List<SessaoTreino> historico = sessaoTreinoService.listarSessoesPorUsuario(UserSession.getInstance().getIdUsuarioLogado());

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
            
            // Estilizar botões
            javafx.application.Platform.runLater(() -> {
                dialog.getDialogPane().lookupAll(".button").forEach(node -> {
                    if (node instanceof javafx.scene.control.ButtonBase) {
                        node.setStyle(
                            "-fx-background-color: #5A189A;" +
                            "-fx-text-fill: #FFFFFF;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 5px;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-padding: 8px 16px 8px 16px;" +
                            "-fx-min-width: 80px;" +
                            "-fx-cursor: hand;"
                        );
                    }
                });
            });
        });

        // Criar lista de dados para a tabela
        List<HistoricoSessaoData> dadosTabela = new ArrayList<>();
        for (SessaoTreino sessao : historico) {
            for (ItemSessaoTreino item : sessao.getItensExecutados()) {
                String nomeExercicio = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio())
                    .map(Exercicio::getNome)
                    .orElse("Exercício Desconhecido");
                
                dadosTabela.add(new HistoricoSessaoData(
                    sessao.getDataSessao().toString(),
                    sessao.getIdPlanoTreino(),
                    nomeExercicio,
                    item.getRepeticoesRealizadas(),
                    item.getCargaRealizada()
                ));
            }
        }

        // Criar TableView
        TableView<HistoricoSessaoData> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(dadosTabela));
        tableView.setPrefWidth(700);
        tableView.setPrefHeight(400);

        // Colunas
        TableColumn<HistoricoSessaoData, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getData()));
        colData.setPrefWidth(120);

        TableColumn<HistoricoSessaoData, Integer> colPlano = new TableColumn<>("Plano ID");
        colPlano.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdPlano()).asObject());
        colPlano.setPrefWidth(80);
        colPlano.setStyle("-fx-alignment: CENTER;");

        TableColumn<HistoricoSessaoData, String> colExercicio = new TableColumn<>("Exercício");
        colExercicio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomeExercicio()));
        colExercicio.setPrefWidth(250);

        TableColumn<HistoricoSessaoData, Integer> colRepeticoes = new TableColumn<>("Repetições");
        colRepeticoes.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRepeticoes()).asObject());
        colRepeticoes.setPrefWidth(120);
        colRepeticoes.setStyle("-fx-alignment: CENTER;");

        TableColumn<HistoricoSessaoData, String> colCarga = new TableColumn<>("Carga (kg)");
        colCarga.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().getCarga())));
        colCarga.setPrefWidth(130);
        colCarga.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(colData, colPlano, colExercicio, colRepeticoes, colCarga);

        // Aplicar estilo
        aplicarEstiloTableViewGenerico(tableView);

        VBox box = new VBox(tableView);
        box.setStyle("-fx-background-color: #2c2c2c;");
        box.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private static class HistoricoSessaoData {
        private final String data;
        private final int idPlano;
        private final String nomeExercicio;
        private final int repeticoes;
        private final double carga;

        public HistoricoSessaoData(String data, int idPlano, String nomeExercicio, int repeticoes, double carga) {
            this.data = data;
            this.idPlano = idPlano;
            this.nomeExercicio = nomeExercicio;
            this.repeticoes = repeticoes;
            this.carga = carga;
        }

        public String getData() { return data; }
        public int getIdPlano() { return idPlano; }
        public String getNomeExercicio() { return nomeExercicio; }
        public int getRepeticoes() { return repeticoes; }
        public double getCarga() { return carga; }
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

            boolean confirmado = StyledAlert.showConfirmationAndWait("Atualizar Plano de Treino", 
                "Sugestão de Atualização\n\n" + mensagem);

            if (confirmado) {
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
        carregarNovaTela("/ui/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
    }

    private void carregarNovaTela(String fxmlFile, String titulo) {
        if (!br.upe.util.NavigationUtil.navigateFrom(BAdicionarSessao, fxmlFile, titulo)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela solicitada.");
        } else {
            logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);
        }
    }

    private Dialog<ButtonType> criarDialogPadrao(String titulo, String cabecalho) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecalho);
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node header = dialog.getDialogPane().lookup(".header-panel");
            if (header != null) header.setStyle("-fx-background-color: #1e1e1e;");
            Node label = dialog.getDialogPane().lookup(".header-panel .label");
            if (label != null) label.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold;");
        });
        return dialog;
    }

    private GridPane criarGridPadrao() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.setStyle("-fx-background-color: #2c2c2c;");
        return grid;
    }

    private Label criarLabel(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
        return label;
    }

    private TextField criarCampoTexto(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle("-fx-text-fill: #ffb300; -fx-background-color: #333; -fx-border-color: #1e1e1e;");
        return field;
    }

    private void setupResizeListeners() {
        Runnable recompute = () -> Platform.runLater(() -> {
            double fixed = tableSessoes.getFixedCellSize();
            if (fixed <= 0) fixed = 56;
            int rowsToShow = Math.max(6, tableSessoes.getItems() == null ? 0 : tableSessoes.getItems().size());
            double header = 30;
            tableSessoes.setPrefHeight(fixed * rowsToShow + header);
            tableSessoes.refresh();
        });

        tableSessoes.widthProperty().addListener((obs, old, nw) -> recompute.run());
        tableSessoes.sceneProperty().addListener((obs, old, nw) -> { if (nw != null) recompute.run(); });
        for (TableColumn<SessaoTreino, ?> c : tableSessoes.getColumns()) {
            c.widthProperty().addListener((obs, old, nw) -> recompute.run());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        StyledAlert.mostrarAlerta(tipo, titulo, mensagem);
    }

    private void aplicarEstiloTableView(TableView<ItemSessaoTreino> tableView) {
        // Estilo simples e limpo, igual à tela de exercícios
        tableView.setStyle("-fx-background-color: #2c2c2c; -fx-control-inner-background: #2c2c2c;");
        tableView.setRowFactory(tv -> new javafx.scene.control.TableRow<ItemSessaoTreino>() {
            {
                // Forçar prefHeight por linha (combinado com fixedCellSize)
                setPrefHeight(56);
                setMinHeight(56);
            }

            @Override
            protected void updateItem(ItemSessaoTreino item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: #2c2c2c; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                } else {
                    setStyle(
                        "-fx-background-color: #2c2c2c; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-border-color: white; -fx-border-width: 0 0 1 0;"
                    );
                }

                // Estilo ao selecionar
                if (isSelected()) {
                    setStyle("-fx-background-color: #5A189A; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                }
            }
        });
    }
    
    private void aplicarEstiloTableViewSessao(TableView<SessaoTreino> tableView) {
        // Estilo simples e limpo, igual à tela de exercícios
        tableView.setStyle("-fx-background-color: #2c2c2c; -fx-control-inner-background: #2c2c2c;");
        tableView.setRowFactory(tv -> new javafx.scene.control.TableRow<SessaoTreino>() {
            {
                // Forçar prefHeight por linha (combinado com fixedCellSize)
                setPrefHeight(56);
                setMinHeight(56);
            }

            @Override
            protected void updateItem(SessaoTreino item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: #2c2c2c; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                } else {
                    setStyle(
                        "-fx-background-color: #2c2c2c; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-border-color: white; -fx-border-width: 0 0 1 0;"
                    );
                }

                // Estilo ao selecionar
                if (isSelected()) {
                    setStyle("-fx-background-color: #5A189A; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                }
            }
        });
    }
    
    private <T> void aplicarEstiloTableViewGenerico(TableView<T> tableView) {
        // Estilo simples e limpo, igual à tela de exercícios
        tableView.setStyle("-fx-background-color: #2c2c2c; -fx-control-inner-background: #2c2c2c;");
        tableView.setRowFactory(tv -> new javafx.scene.control.TableRow<T>() {
            {
                // Forçar prefHeight por linha (combinado com fixedCellSize)
                setPrefHeight(56);
                setMinHeight(56);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: #2c2c2c; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                } else {
                    setStyle(
                        "-fx-background-color: #2c2c2c; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-border-color: white; -fx-border-width: 0 0 1 0;"
                    );
                }

                // Estilo ao selecionar
                if (isSelected()) {
                    setStyle("-fx-background-color: #5A189A; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                }
            }
        });
    }
}
