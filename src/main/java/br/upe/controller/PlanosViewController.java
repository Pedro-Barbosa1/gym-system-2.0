package br.upe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.service.IPlanoTreinoService;
import br.upe.service.PlanoTreinoService;
import br.upe.ui.util.StyledAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
// import javafx.scene.control.ListCell; // not used here
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PlanosViewController {
    private static final Logger logger = Logger.getLogger(PlanosViewController.class.getName());

    private IPlanoTreinoService planoTreinoService;
    private IExercicioService exercicioService;
    // usuário logado (temporariamente fixo como 1, consistente com outros controllers)
    private int idUsuarioLogado = 1;

    @FXML
    private Label totalLabel;

    @FXML
    private TableView<PlanoTreino> tablePlanos;

    @FXML
    private TableColumn<PlanoTreino, Integer> colId;

    @FXML
    private TableColumn<PlanoTreino, String> colNome;

    @FXML
    private TableColumn<PlanoTreino, Integer> colExercicios;

    @FXML
    private TableColumn<PlanoTreino, Void> colAcoes;

    @FXML
    private Button BAddPlano;

    @FXML
    private Button BVoltar;

    @FXML
    public void initialize() {
        this.planoTreinoService = new PlanoTreinoService();
        this.exercicioService = new ExercicioService();
        setupTable();
        loadPlanos();
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdPlano()).asObject());
        colId.setPrefWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colNome.setPrefWidth(400);

        colExercicios.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getItensTreino().size()).asObject());
        colExercicios.setPrefWidth(220);
        colExercicios.setStyle("-fx-alignment: CENTER;");

        adicionarColunaAcoes();
        // make columns fit the table width to avoid horizontal scrollbar
        tablePlanos.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        tablePlanos.setFixedCellSize(56);
        aplicarEstiloTableView(tablePlanos);
    }

    private void adicionarColunaAcoes() {
        colAcoes.setCellFactory(tc -> new javafx.scene.control.TableCell<PlanoTreino, Void>() {
            private final Button btnDetalhes = new Button("Detalhes");
            private final Button btnExcluir = new Button("Excluir");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, btnDetalhes, btnExcluir);

            {
                // style buttons similar to ExerciciosViewController
                btnDetalhes.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnExcluir.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");

                btnDetalhes.setPrefWidth(80);
                btnExcluir.setPrefWidth(70);

                // vertical alignment and padding to match row fixed size
                box.setAlignment(javafx.geometry.Pos.CENTER);
                box.setPadding(new Insets(6, 4, 6, 4));
                box.setPrefHeight(48);

                btnDetalhes.setPrefHeight(28);
                btnExcluir.setPrefHeight(28);

                btnDetalhes.setOnAction(e -> {
                    PlanoTreino plano = getTableView().getItems().get(getIndex());
                    showDetalhesPlano(plano);
                });

                btnExcluir.setOnAction(e -> {
                    PlanoTreino plano = getTableView().getItems().get(getIndex());
                    Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacao.setTitle("Confirmar Exclusão");
                    confirmacao.setHeaderText("Deletar: " + plano.getNome());
                    confirmacao.setContentText("Tem certeza que deseja deletar este plano de treino?");
                    confirmacao.showAndWait().ifPresent(resp -> {
                        if (resp == ButtonType.OK) {
                            try {
                                boolean deletado = planoTreinoService.deletarPlano(idUsuarioLogado, plano.getNome());
                                if (deletado) {
                                    getTableView().getItems().remove(plano);
                                    showInfo("Sucesso", "Plano '" + plano.getNome() + "' deletado com sucesso!");
                                } else {
                                    showError("Erro", "Plano não encontrado ou não pertence a você.");
                                }
                            } catch (IllegalArgumentException ex) {
                                showError("Erro", "Erro ao deletar plano: " + ex.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
    }

    private void loadPlanos() {
        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(1);
        tablePlanos.setItems(FXCollections.observableArrayList(planos));
        totalLabel.setText(String.format("Total: %d plano(s)", planos.size()));
        // ajustar altura preferencial da tabela para evitar rolagem vertical desnecessária e manter linhas visíveis
        double fixed = tablePlanos.getFixedCellSize();
        if (fixed <= 0) fixed = 56;
        int rowsToShow = Math.max(6, planos.size());
        double header = 30; // estimativa para a altura do cabeçalho
        tablePlanos.setPrefHeight(fixed * rowsToShow + header);
        aplicarEstiloTableView(tablePlanos);
    }

    @FXML
    private void handleAdicionarPlano() {
        Dialog<ButtonType> dialog = criarDialogPadrao("Criar Novo Plano de Treino", "Preencha o nome do plano:");
        GridPane grid = criarGridPadrao();

        TextField nomeField = criarCampoTexto("Nome do plano");
        grid.add(criarLabel("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String nome = nomeField.getText().trim();

            if (nome.isEmpty()) {
                showError("Erro", "O nome do plano não pode estar vazio.");
                return;
            }

            try {
                PlanoTreino novoPlano = planoTreinoService.criarPlano(1, nome);
                showInfo("Sucesso", "Plano de Treino '" + novoPlano.getNome() + "' criado com sucesso!\nID: " + novoPlano.getIdPlano());
                loadPlanos();
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao criar plano: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleVoltar() {
        // retornar para MenuUsuarioLogado.fxml
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ui/MenuUsuarioLogado.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) BVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao voltar para o menu do usuário", e);
            showError("Erro", "Não foi possível voltar ao menu.");
        }
    }

    // --- Helpers copied/adapted from existing utils ---
    private Dialog<ButtonType> criarDialogPadrao(String titulo, String cabecalho) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecalho);
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node header = dialog.getDialogPane().lookup(".header-panel");
            if (header != null) header.setStyle("-fx-background-color: #1e1e1e;");
            Node label = dialog.getDialogPane().lookup(".header-panel .label");
            if (label != null) label.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
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
        field.setStyle(
            "-fx-text-fill: #ffb300;" +
            "-fx-background-color: #333;" +
            "-fx-border-color: #1e1e1e;" +
            "-fx-prompt-text-fill: #888888;"
        );
        return field;
    }

    private TextArea criarTextArea(int largura, int altura) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(largura);
        textArea.setPrefHeight(altura);
        textArea.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-background-color: #2c2c2c;" +
            "-fx-text-fill: #ffb300;" +
            "-fx-border-color: transparent;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;" +
            "-fx-padding: 0;"
        );
        return textArea;
    }

    private void aplicarEstiloTableView(TableView<?> tableView) {
        // Basic styling (adapted from other controllers)
        tableView.setStyle(
            "-fx-background-color: #2c2c2c; " +
            "-fx-control-inner-background: #2c2c2c; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 0; " +
            "-fx-table-cell-border-color: #333;"
        );

        // Keep styling simpler here to avoid generic/row factory complexity
        // Row-level styling is handled by CSS-like inline styles applied below.

        tableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                tableView.applyCss();
                tableView.layout();

                javafx.scene.Node headerRow = tableView.lookup(".column-header-background");
                if (headerRow != null) headerRow.setStyle("-fx-background-color: #1e1e1e;");

                tableView.lookupAll(".column-header").forEach(node -> {
                    node.setStyle(
                        "-fx-background-color: #1e1e1e; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #333;"
                    );
                });

                tableView.lookupAll(".column-header .label").forEach(node -> {
                    ((javafx.scene.control.Labeled) node).setStyle(
                        "-fx-text-fill: #ffb300; " +
                        "-fx-font-weight: bold;"
                    );
                });
            }
        });
    }

    private void showInfo(String title, String content) {
        StyledAlert.showInformationAndWait(title, content);
    }

    private void showError(String title, String content) {
        StyledAlert.showErrorAndWait(title, content);
    }

    // Detalhes do plano (mostra tabela dos exercícios do plano)
    @SuppressWarnings("unchecked")
    private void showDetalhesPlano(PlanoTreino planoSelecionado) {
        if (planoSelecionado == null) return;

        Dialog<ButtonType> dialog = criarDialogPadrao("Detalhes do Plano", 
            String.format("Plano: %s (ID: %d) | %d exercício(s)", 
                planoSelecionado.getNome(), 
                planoSelecionado.getIdPlano(), 
                planoSelecionado.getItensTreino().size()));

        // Se não houver exercícios, manter a mensagem simples
        if (planoSelecionado.getItensTreino().isEmpty()) {
            TextArea textArea = criarTextArea(650, 200);
            textArea.setText("Nenhum exercício adicionado ainda ao plano \"" + planoSelecionado.getNome() + "\".");
            // Adicionar botão fechar abaixo
            Button btnFecharEmpty = new Button("Fechar");
            btnFecharEmpty.setPrefHeight(36);
            btnFecharEmpty.setPrefWidth(140);
            btnFecharEmpty.setStyle("-fx-background-color: #1e1e1e;");
            btnFecharEmpty.setTextFill(Color.web("#e5a000"));
            btnFecharEmpty.setOnAction(e -> {
                Stage st = (Stage) dialog.getDialogPane().getScene().getWindow();
                st.close();
            });
            javafx.scene.layout.VBox v = new javafx.scene.layout.VBox(10, textArea, btnFecharEmpty);
            v.setPadding(new Insets(10));
            dialog.getDialogPane().setContent(v);
            dialog.showAndWait();
            return;
        }

        // Função auxiliar para rebuild da tabela a partir do plano mais recente do serviço
        java.util.function.Consumer<TableView<ExercicioPlanoData>> refresh = (tableView) -> {
            List<PlanoTreino> planosAtualizados = planoTreinoService.listarMeusPlanos(idUsuarioLogado);
            PlanoTreino atualizado = planosAtualizados.stream()
                    .filter(p -> p.getIdPlano() == planoSelecionado.getIdPlano())
                    .findFirst()
                    .orElse(planoSelecionado);

            List<ExercicioPlanoData> dados = new ArrayList<>();
            for (ItemPlanoTreino item : atualizado.getItensTreino()) {
                Optional<Exercicio> exOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
                String nome = "Desconhecido";
                if (exOpt.isPresent() && exOpt.get().getIdUsuario() == idUsuarioLogado) nome = exOpt.get().getNome();
                dados.add(new ExercicioPlanoData(item.getIdExercicio(), nome, item.getCargaKg(), item.getRepeticoes()));
            }
            tableView.setItems(FXCollections.observableArrayList(dados));
        };

        // Construir tabela
        TableView<ExercicioPlanoData> tableView = new TableView<>();
        tableView.setPrefWidth(650);
        tableView.setPrefHeight(360);

        TableColumn<ExercicioPlanoData, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdExercicio()).asObject());
        colId.setPrefWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<ExercicioPlanoData, String> colNome = new TableColumn<>("Exercício");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colNome.setPrefWidth(300);

        TableColumn<ExercicioPlanoData, Integer> colCarga = new TableColumn<>("Carga (kg)");
        colCarga.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCargaKg()).asObject());
        colCarga.setPrefWidth(120);
        colCarga.setStyle("-fx-alignment: CENTER;");

        TableColumn<ExercicioPlanoData, Integer> colRepeticoes = new TableColumn<>("Repetições");
        colRepeticoes.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRepeticoes()).asObject());
        colRepeticoes.setPrefWidth(120);
        colRepeticoes.setStyle("-fx-alignment: CENTER;");

        TableColumn<ExercicioPlanoData, Void> colAcoes = new TableColumn<>("Ações");
        colAcoes.setPrefWidth(130);
        colAcoes.setCellFactory(tc -> new javafx.scene.control.TableCell<ExercicioPlanoData, Void>() {
            private final Button btnVisualizar = new Button("Visualizar");
            private final Button btnRemover = new Button("Remover");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, btnVisualizar, btnRemover);

            {
                btnVisualizar.setOnAction(e -> {
                    ExercicioPlanoData data = getTableView().getItems().get(getIndex());
                    Optional<Exercicio> exOpt = exercicioService.buscarExercicioPorIdGlobal(data.getIdExercicio());
                    if (exOpt.isPresent()) {
                        Exercicio ex = exOpt.get();
                        try {
                            String caminhoGif = ex.getCaminhoGif();
                            caminhoGif = caminhoGif.replace("\\", "/");
                            caminhoGif = caminhoGif.replaceFirst("^/gif/", "");
                            caminhoGif = caminhoGif.replaceFirst("^gif/", "");
                            caminhoGif = "/gif/" + caminhoGif;

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/visualizador.fxml"));
                            Parent root = loader.load();
                            br.upe.controller.VisualizadorExercicioController controller = loader.getController();
                            controller.exibirExercicio(caminhoGif, ex.getNome(), ex.getDescricao());

                            Stage stage = new Stage();
                            stage.initModality(Modality.WINDOW_MODAL);
                            try { if (BAddPlano != null && BAddPlano.getScene() != null) stage.initOwner(BAddPlano.getScene().getWindow()); } catch (Exception ignore) {}
                            stage.setTitle("Visualizar Exercício - " + ex.getNome());
                            stage.setScene(new Scene(root));
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception exx) {
                            logger.log(Level.SEVERE, "Erro ao abrir visualizador de exercício", exx);
                            showError("Erro", "Não foi possível abrir o visualizador do exercício.");
                        }
                    } else {
                        showError("Erro", "Exercício não encontrado.");
                    }
                });

                btnRemover.setOnAction(e -> {
                    ExercicioPlanoData data = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmar Remoção");
                    confirm.setHeaderText("Remover exercício do plano");
                    confirm.setContentText("Tem certeza que deseja remover este exercício do plano?");
                    confirm.showAndWait().ifPresent(resp -> {
                        if (resp == ButtonType.OK) {
                            try {
                                planoTreinoService.removerExercicioDoPlano(idUsuarioLogado, planoSelecionado.getNome(), data.getIdExercicio());
                                showInfo("Sucesso", "Exercício removido do plano com sucesso.");
                                refresh.accept(tableView);
                            } catch (IllegalArgumentException ex) {
                                showError("Erro", "Erro ao remover exercício: " + ex.getMessage());
                            }
                        }
                    });
                });

                box.setStyle("-fx-alignment: CENTER; -fx-padding: 4;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null); else setGraphic(box);
            }
        });

        tableView.getColumns().addAll(colId, colNome, colCarga, colRepeticoes, colAcoes);
        aplicarEstiloTableView(tableView);

        // Popular inicialmente
        refresh.accept(tableView);

        // Botão adicionar acima da tabela (estilizado como em ExerciciosView.fxml)
        Button btnAdicionar = new Button("Adicionar Exercício");
        btnAdicionar.setPrefHeight(36);
        btnAdicionar.setPrefWidth(140);
        btnAdicionar.setStyle("-fx-background-color: #1e1e1e;");
        btnAdicionar.setTextFill(Color.web("#e5a000"));
        btnAdicionar.setOnAction(e -> {
            // Selecionar exercício do usuário
            List<Exercicio> meusEx = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
            if (meusEx.isEmpty()) {
                showError("Erro", "Você não possui exercícios cadastrados. Cadastre um exercício primeiro.");
                return;
            }

            List<String> op = new ArrayList<>();
            for (Exercicio ex : meusEx) op.add(String.format("%d - %s", ex.getIdExercicio(), ex.getNome()));

            Dialog<ButtonType> sel = criarDialogPadrao("Selecionar Exercício", "Selecione o exercício para adicionar ao plano:");
            GridPane g = criarGridPadrao();
            Label lbl = criarLabel("Exercício:");
            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll(op);
            combo.getSelectionModel().selectFirst();
            g.add(lbl, 0, 0);
            g.add(combo, 1, 0);
            sel.getDialogPane().setContent(g);
            sel.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> res = sel.showAndWait();
            if (!res.isPresent() || res.get() != ButtonType.OK) return;

            int idEx = Integer.parseInt(combo.getValue().split(" - ")[0]);

            // Pedir carga e repetições
            Dialog<ButtonType> cfg = criarDialogPadrao("Adicionar Exercício ao Plano", "Configure o exercício no plano:");
            GridPane gg = criarGridPadrao();
            TextField cargaF = criarCampoTexto("Carga em kg");
            TextField repsF = criarCampoTexto("Número de repetições");
            gg.add(criarLabel("Carga (kg):"), 0, 0);
            gg.add(cargaF, 1, 0);
            gg.add(criarLabel("Repetições:"), 0, 1);
            gg.add(repsF, 1, 1);
            cfg.getDialogPane().setContent(gg);
            cfg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> r2 = cfg.showAndWait();
            if (!r2.isPresent() || r2.get() != ButtonType.OK) return;
            try {
                int carga = Integer.parseInt(cargaF.getText().trim());
                int reps = Integer.parseInt(repsF.getText().trim());
                planoTreinoService.adicionarExercicioAoPlano(idUsuarioLogado, planoSelecionado.getNome(), idEx, carga, reps);
                showInfo("Sucesso", "Exercício adicionado ao plano com sucesso!");
                refresh.accept(tableView);
            } catch (NumberFormatException ex) {
                showError("Erro", "Carga e repetições devem ser números válidos.");
            } catch (IllegalArgumentException ex) {
                showError("Erro", "Erro ao adicionar exercício: " + ex.getMessage());
            }
        });

        // Botão fechar abaixo da tabela (estilizado como em ExerciciosView.fxml)
        Button btnFechar = new Button("Fechar");
        btnFechar.setPrefHeight(36);
        btnFechar.setPrefWidth(140);
        btnFechar.setStyle("-fx-background-color: #1e1e1e;");
        btnFechar.setTextFill(Color.web("#e5a000"));
        btnFechar.setOnAction(e -> {
            Stage st = (Stage) dialog.getDialogPane().getScene().getWindow();
            st.close();
        });

        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(8);
        container.setPadding(new Insets(10));
        container.getChildren().addAll(btnAdicionar, tableView, btnFechar);

        dialog.getDialogPane().setContent(container);
        dialog.showAndWait();
    }

    private static class ExercicioPlanoData {
        private final int idExercicio;
        private final String nome;
        private final int cargaKg;
        private final int repeticoes;

        public ExercicioPlanoData(int idExercicio, String nome, int cargaKg, int repeticoes) {
            this.idExercicio = idExercicio;
            this.nome = nome;
            this.cargaKg = cargaKg;
            this.repeticoes = repeticoes;
        }

        public int getIdExercicio() { return idExercicio; }
        public String getNome() { return nome; }
        public int getCargaKg() { return cargaKg; }
        public int getRepeticoes() { return repeticoes; }
    }
}
