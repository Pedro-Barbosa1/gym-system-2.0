package br.upe.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.IndicadorBiomedico;
import br.upe.service.IIndicadorBiomedicoService;
import br.upe.service.IndicadorBiomedicoService;
import br.upe.ui.util.StyledAlert;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class IndicadoresViewController {

    private static final Logger logger =
            Logger.getLogger(IndicadoresViewController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private final IIndicadorBiomedicoService indicadorService;
    private int idUsuarioLogado = 1;

    // elementos principais da tela
    @FXML private TableView<IndicadorBiomedico> tableIndicadores;
    @FXML private TableColumn<IndicadorBiomedico, String> colData;
    @FXML private TableColumn<IndicadorBiomedico, String> colPeso;
    @FXML private TableColumn<IndicadorBiomedico, String> colAltura;
    @FXML private TableColumn<IndicadorBiomedico, String> colImc;
    @FXML private TableColumn<IndicadorBiomedico, String> colGordura;
    @FXML private TableColumn<IndicadorBiomedico, String> colMassaMagra;
    @FXML private TableColumn<IndicadorBiomedico, Void> colAcoes;

    @FXML private Button BAddIndicador;
    @FXML private Button BVoltar;
    @FXML private Label totalLabel;

    public IndicadoresViewController() {
        this.indicadorService = new IndicadorBiomedicoService();
    }

    // -------- ciclo de vida --------

    @FXML
    private void initialize() {
        logger.info("IndicadoresViewController inicializado");

        colData.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getData().format(DATE_FORMATTER)));
        colPeso.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f", d.getValue().getPesoKg())));
        colAltura.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f", d.getValue().getAlturaCm())));
        colImc.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f", d.getValue().getImc())));
        colGordura.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f", d.getValue().getPercentualGordura())));
        colMassaMagra.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f", d.getValue().getPercentualMassaMagra())));

        adicionarColunaAcoes();

        tableIndicadores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndicadores.setFixedCellSize(56);

        loadIndicadores();
        setupResizeListeners();
    }

    // -------- tabela --------

    private void loadIndicadores() {
        List<IndicadorBiomedico> lista =
                indicadorService.listarTodosDoUsuario(idUsuarioLogado);
        tableIndicadores.setItems(FXCollections.observableArrayList(lista));
        if (totalLabel != null) {
            totalLabel.setText(
                    String.format("Total de %d indicador(es) cadastrados", lista.size()));
        }

        double fixed = tableIndicadores.getFixedCellSize();
        if (fixed <= 0) fixed = 56;
        int rowsToShow = Math.max(6, lista.size());
        double header = 30;
        tableIndicadores.setPrefHeight(fixed * rowsToShow + header);

        aplicarEstiloTableView(tableIndicadores);
    }

    private void adicionarColunaAcoes() {
        colAcoes.setCellFactory(col -> new TableCell<IndicadorBiomedico, Void>() {
            private final Button btnDetalhes = new Button("Detalhes");
            private final Button btnRemover = new Button("Remover");
            private final HBox container = new HBox(8, btnDetalhes, btnRemover);

            {
                btnDetalhes.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnRemover.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");

                btnDetalhes.setPrefWidth(90);
                btnRemover.setPrefWidth(70);

                container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(6, 4, 6, 4));
                container.setPrefHeight(48);
                container.setMinHeight(48);
                btnDetalhes.setPrefHeight(28);
                btnRemover.setPrefHeight(28);

                btnDetalhes.setOnAction(e -> {
                    IndicadorBiomedico ind =
                            getTableView().getItems().get(getIndex());
                    abrirDialogDetalhes(ind);
                });

                btnRemover.setOnAction(e -> {
                    IndicadorBiomedico ind =
                            getTableView().getItems().get(getIndex());
                    confirmarRemocao(ind);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    // -------- ações principais --------

    @FXML
    private void handleAdicionarIndicador(ActionEvent event) {
        logger.info("Abrindo dialog para cadastrar novo indicador...");

        Dialog<ButtonType> dialog =
                criarDialogPadrao("Cadastrar Novo Indicador",
                        "Preencha os dados do indicador biomédico");
        GridPane grid = criarGridPadrao();

        TextField dataField =
                new TextField(LocalDate.now().format(DATE_FORMATTER));
        TextField pesoField = new TextField();
        TextField alturaField = new TextField();
        TextField gorduraField = new TextField();
        TextField massaMagraField = new TextField();

        String textFieldStyle =
                "-fx-text-fill: #ffb300; -fx-background-color: darkgray; " +
                "-fx-border-color: #1e1e1e; -fx-border-width: 1; -fx-border-radius: 4;";
        dataField.setStyle(textFieldStyle);
        pesoField.setStyle(textFieldStyle);
        alturaField.setStyle(textFieldStyle);
        gorduraField.setStyle(textFieldStyle);
        massaMagraField.setStyle(textFieldStyle);

        grid.add(criarLabel("Data (AAAA-MM-DD):"), 0, 0);
        grid.add(dataField, 1, 0);
        grid.add(criarLabel("Peso (kg):"), 0, 1);
        grid.add(pesoField, 1, 1);
        grid.add(criarLabel("Altura (cm):"), 0, 2);
        grid.add(alturaField, 1, 2);
        grid.add(criarLabel("% Gordura:"), 0, 3);
        grid.add(gorduraField, 1, 3);
        grid.add(criarLabel("% Massa Magra:"), 0, 4);
        grid.add(massaMagraField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes()
              .setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> opt = dialog.showAndWait();
        if (opt.isEmpty() || opt.get() == ButtonType.CANCEL) {
            return;
        }

        try {
            LocalDate data = LocalDate.now();
            String dataStr = dataField.getText().trim();
            if (!dataStr.isEmpty()) {
                data = LocalDate.parse(dataStr, DATE_FORMATTER);
            }

            double peso = Double.parseDouble(pesoField.getText().trim());
            double altura = Double.parseDouble(alturaField.getText().trim());
            double gordura = Double.parseDouble(gorduraField.getText().trim());
            double massaMagra =
                    Double.parseDouble(massaMagraField.getText().trim());

            IndicadorBiomedico novo =
                    indicadorService.cadastrarIndicador(
                            idUsuarioLogado, data, peso, altura,
                            gordura, massaMagra);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    String.format("Indicador cadastrado com sucesso!\nIMC: %.1f",
                            novo.getImc()));
            logger.info("Indicador cadastrado: " + novo);
            loadIndicadores();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato",
                    "Por favor, digite valores numéricos válidos.");
            logger.log(Level.WARNING,
                    "Erro de formato ao cadastrar indicador", e);
        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Data",
                    "Formato de data inválido. Use AAAA-MM-DD (ex: 2025-10-17)");
            logger.log(Level.WARNING, "Erro ao parsear data", e);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro",
                    "Erro ao cadastrar indicador: " + e.getMessage());
            logger.log(Level.WARNING,
                    "Erro de validação ao cadastrar indicador", e);
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        carregarNovaTela("/ui/MenuUsuarioLogado.fxml",
                "Gym System - Menu do Usuário");
    }

    // -------- dialogs de detalhes / remoção --------

    private void abrirDialogDetalhes(IndicadorBiomedico ind) {
        Dialog<ButtonType> dialog =
                criarDialogPadrao("Detalhes do Indicador",
                        "Indicador em " + ind.getData().format(DATE_FORMATTER));

        GridPane grid = criarGridPadrao();

        // Criar campos editáveis
        TextField dataField = new TextField(ind.getData().format(DATE_FORMATTER));
        TextField pesoField = new TextField(String.format("%.1f", ind.getPesoKg()));
        TextField alturaField = new TextField(String.format("%.1f", ind.getAlturaCm()));
        TextField gorduraField = new TextField(String.format("%.1f", ind.getPercentualGordura()));
        TextField massaMagraField = new TextField(String.format("%.1f", ind.getPercentualMassaMagra()));

        // Campo IMC somente leitura (calculado automaticamente)
        Label imcLabel = new Label(String.format("%.1f", ind.getImc()));
        imcLabel.setStyle("-fx-text-fill: #ffb300;");

        String textFieldStyle =
                "-fx-text-fill: #ffb300; -fx-background-color: darkgray; " +
                "-fx-border-color: #1e1e1e; -fx-border-width: 1; -fx-border-radius: 4;";
        dataField.setStyle(textFieldStyle);
        pesoField.setStyle(textFieldStyle);
        alturaField.setStyle(textFieldStyle);
        gorduraField.setStyle(textFieldStyle);
        massaMagraField.setStyle(textFieldStyle);

        grid.add(criarLabel("Data (AAAA-MM-DD):"), 0, 0);
        grid.add(dataField, 1, 0);
        grid.add(criarLabel("Peso (kg):"), 0, 1);
        grid.add(pesoField, 1, 1);
        grid.add(criarLabel("Altura (cm):"), 0, 2);
        grid.add(alturaField, 1, 2);
        grid.add(criarLabel("Gordura (%):"), 0, 3);
        grid.add(gorduraField, 1, 3);
        grid.add(criarLabel("Massa Magra (%):"), 0, 4);
        grid.add(massaMagraField, 1, 4);
        grid.add(criarLabel("IMC:"), 0, 5);
        grid.add(imcLabel, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LocalDate novaData = LocalDate.parse(dataField.getText().trim(), DATE_FORMATTER);
                double novoPeso = Double.parseDouble(pesoField.getText().trim());
                double novaAltura = Double.parseDouble(alturaField.getText().trim());
                double novaGordura = Double.parseDouble(gorduraField.getText().trim());
                double novaMassaMagra = Double.parseDouble(massaMagraField.getText().trim());

                indicadorService.editarIndicador(
                        ind.getIdIndicador(),
                        novaData,
                        novoPeso,
                        novaAltura,
                        novaGordura,
                        novaMassaMagra
                );

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                        "Indicador atualizado com sucesso!");
                logger.info("Indicador editado: ID=" + ind.getIdIndicador());
                loadIndicadores();

            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato",
                        "Por favor, digite valores numéricos válidos.");
                logger.log(Level.WARNING, "Erro de formato ao editar indicador", e);
            } catch (DateTimeParseException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Data",
                        "Formato de data inválido. Use AAAA-MM-DD (ex: 2025-12-12)");
                logger.log(Level.WARNING, "Erro ao parsear data", e);
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro",
                        "Erro ao editar indicador: " + e.getMessage());
                logger.log(Level.WARNING, "Erro de validação ao editar indicador", e);
            }
        }
    }

    private void confirmarRemocao(IndicadorBiomedico ind) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setHeaderText("Excluir indicador de " +
                ind.getData().format(DATE_FORMATTER));
        confirm.setContentText("Tem certeza que deseja excluir este indicador?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    indicadorService.deletarIndicador(ind.getIdIndicador());
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Sucesso", "Indicador excluído com sucesso!");
                    loadIndicadores();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR,
                            "Erro", "Erro ao excluir indicador: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao excluir indicador", e);
                }
            }
        });
    }

    // -------- navegação / helpers visuais --------

    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            br.upe.util.NavigationUtil.navigateTo(tableIndicadores, root, titulo);

            logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro",
                    "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE,
                    "Erro ao carregar tela: " + fxmlFile, e);
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
            if (label != null) {
                label.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
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

    private void aplicarEstiloTableView(TableView<IndicadorBiomedico> tableView) {
        tableView.setStyle("-fx-background-color: #2c2c2c; -fx-control-inner-background: #2c2c2c;");
        tableView.setRowFactory(tv -> new TableRow<IndicadorBiomedico>() {
            {
                setPrefHeight(56);
                setMinHeight(56);
            }

            @Override
            protected void updateItem(IndicadorBiomedico item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setStyle("");
                } else {
                    setStyle(
                            "-fx-background-color: #2c2c2c; " +
                            "-fx-text-fill: #ffb300; " +
                            "-fx-border-color: transparent transparent #e6e6e6 transparent; " +
                            "-fx-border-width: 0 0 1 0; " +
                            "-fx-border-style: solid; " +
                            "-fx-border-insets: 0;");
                }
            }
        });
    }

    private void setupResizeListeners() {
        Runnable recompute = () -> Platform.runLater(() -> {
            double fixed = tableIndicadores.getFixedCellSize();
            if (fixed <= 0) fixed = 56;
            int rowsToShow = Math.max(6,
                    tableIndicadores.getItems() == null
                            ? 0 : tableIndicadores.getItems().size());
            double header = 30;
            tableIndicadores.setPrefHeight(fixed * rowsToShow + header);
            tableIndicadores.refresh();
        });

        tableIndicadores.widthProperty().addListener((obs, oldV, nw) -> recompute.run());
        tableIndicadores.sceneProperty().addListener((obs, oldV, nw) -> {
            if (nw != null) recompute.run();
        });
        for (TableColumn<IndicadorBiomedico, ?> c : tableIndicadores.getColumns()) {
            c.widthProperty().addListener((obs, oldV, nw) -> recompute.run());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        StyledAlert.mostrarAlerta(tipo, titulo, mensagem);
    }
}
