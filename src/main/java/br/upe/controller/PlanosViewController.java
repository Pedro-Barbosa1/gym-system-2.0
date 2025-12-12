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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
    }

    private void adicionarColunaAcoes() {
        colAcoes.setCellFactory(tc -> new javafx.scene.control.TableCell<PlanoTreino, Void>() {
            private final Button btnDetalhes = new Button("Detalhes");
            private final Button btnExcluir = new Button("Excluir");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(8, btnDetalhes, btnExcluir);

            {
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
                                boolean deletado = planoTreinoService.deletarPlano(1, plano.getNome());
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
                box.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 4;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    private void loadPlanos() {
        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(1);
        tablePlanos.setItems(FXCollections.observableArrayList(planos));
        totalLabel.setText(String.format("Total: %d plano(s)", planos.size()));
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

    private void showInfo(String title, String content) {
        StyledAlert.showInformationAndWait(title, content);
    }

    private void showError(String title, String content) {
        StyledAlert.showErrorAndWait(title, content);
    }

    // Detalhes do plano (mostra tabela dos exercícios do plano)
    private void showDetalhesPlano(PlanoTreino planoSelecionado) {
        if (planoSelecionado == null) return;

        Dialog<ButtonType> dialog = criarDialogPadrao("Detalhes do Plano", 
            String.format("Plano: %s (ID: %d) | %d exercício(s)", 
                planoSelecionado.getNome(), 
                planoSelecionado.getIdPlano(), 
                planoSelecionado.getItensTreino().size()));

        if (planoSelecionado.getItensTreino().isEmpty()) {
            TextArea textArea = criarTextArea(650, 420);
            textArea.setText("Nenhum exercício adicionado ainda ao plano \"" + planoSelecionado.getNome() + "\".");
            dialog.getDialogPane().setContent(textArea);
        } else {
            List<ExercicioPlanoData> dadosTabela = new ArrayList<>();
            for (ItemPlanoTreino item : planoSelecionado.getItensTreino()) {
                Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
                String nomeExercicio = "Desconhecido";
                if (exercicioOpt.isPresent() && exercicioOpt.get().getIdUsuario() == 1) {
                    nomeExercicio = exercicioOpt.get().getNome();
                }
                dadosTabela.add(new ExercicioPlanoData(
                    item.getIdExercicio(),
                    nomeExercicio,
                    item.getCargaKg(),
                    item.getRepeticoes()
                ));
            }

            TableView<ExercicioPlanoData> tableView = new TableView<>();
            tableView.setItems(FXCollections.observableArrayList(dadosTabela));
            tableView.setPrefWidth(650);
            tableView.setPrefHeight(420);

            TableColumn<ExercicioPlanoData, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdExercicio()).asObject());
            colId.setPrefWidth(80);
            colId.setStyle("-fx-alignment: CENTER;");

            TableColumn<ExercicioPlanoData, String> colNome = new TableColumn<>("Exercício");
            colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
            colNome.setPrefWidth(320);

            TableColumn<ExercicioPlanoData, Integer> colCarga = new TableColumn<>("Carga (kg)");
            colCarga.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCargaKg()).asObject());
            colCarga.setPrefWidth(120);
            colCarga.setStyle("-fx-alignment: CENTER;");

            TableColumn<ExercicioPlanoData, Integer> colRepeticoes = new TableColumn<>("Repetições");
            colRepeticoes.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRepeticoes()).asObject());
            colRepeticoes.setPrefWidth(130);
            colRepeticoes.setStyle("-fx-alignment: CENTER;");

            tableView.getColumns().addAll(colId, colNome, colCarga, colRepeticoes);

            dialog.getDialogPane().setContent(tableView);
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
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
