package br.upe.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.IndicadorBiomedico;
import br.upe.service.IIndicadorBiomedicoService;
import br.upe.service.IndicadorBiomedicoService;
import br.upe.ui.util.StyledAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class IndicadoresViewController {

    private static final Logger logger = Logger.getLogger(IndicadoresViewController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final IIndicadorBiomedicoService indicadorService;
    
    private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login

    @FXML
    private Button BCadastrarIN;

    @FXML
    private Button BListarIN;

    @FXML
    private Button sairB;

    @FXML
    private ImageView IFavoritos;

    @FXML
    private ImageView IFechar;

    public IndicadoresViewController() {
        this.indicadorService = new IndicadorBiomedicoService();
    }


    @FXML
    void cadastrarNovoIndicador(ActionEvent event) {
    logger.info("Abrindo dialog para cadastrar novo indicador...");
    
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Cadastrar Novo Indicador");
    dialog.setHeaderText("Preencha os dados do indicador biomédico");
    dialog.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #1e1e1e;");
    dialog.getDialogPane().lookup(".header-panel .label").setStyle("-fx-text-fill: #ffb300ff; -fx-font-size: 16px; -fx-font-weight: bold;");

    // Criar grid para os campos
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    
    // Aplicar estilo ao GridPane
    grid.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #1e1e1e; -fx-border-width: 1; -fx-border-radius: 8;");

    // Campos do formulário
    TextField dataField = new TextField(LocalDate.now().format(DATE_FORMATTER));
    TextField pesoField = new TextField();
    TextField alturaField = new TextField();
    TextField gorduraField = new TextField();
    TextField massaMagraField = new TextField();

    // Aplicar estilo aos campos de texto
    String textFieldStyle = "-fx-text-fill: #ffb300;-fx-background-color: dark gray; -fx-border-color: #1e1e1e; -fx-border-width: 1; -fx-border-radius: 4;";
    dataField.setStyle(textFieldStyle);
    pesoField.setStyle(textFieldStyle);
    alturaField.setStyle(textFieldStyle);
    gorduraField.setStyle(textFieldStyle);
    massaMagraField.setStyle(textFieldStyle);

    // Adicionar labels e campos com estilo
    Label[] labels = {
        new Label("Data (AAAA-MM-DD):"),
        new Label("Peso (kg):"),
        new Label("Altura (cm):"),
        new Label("% Gordura:"),
        new Label("% Massa Magra:")
    };
    
    // Aplicar estilo aos labels
    String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #ffb300ff;";
    for (Label label : labels) {
        label.setStyle(labelStyle);
    }
    
    grid.add(labels[0], 0, 0);
    grid.add(dataField, 1, 0);
    grid.add(labels[1], 0, 1);
    grid.add(pesoField, 1, 1);
    grid.add(labels[2], 0, 2);
    grid.add(alturaField, 1, 2);
    grid.add(labels[3], 0, 3);
    grid.add(gorduraField, 1, 3);
    grid.add(labels[4], 0, 4);
    grid.add(massaMagraField, 1, 4);

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
    // Aplicar estilo geral ao DialogPane
    dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");

    dialog.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            try {
                    // Parse data
                    LocalDate data = LocalDate.now();
                    String dataStr = dataField.getText().trim();
                    if (!dataStr.isEmpty()) {
                        data = LocalDate.parse(dataStr, DATE_FORMATTER);
                    }

                    // Parse valores
                    double peso = Double.parseDouble(pesoField.getText().trim());
                    double altura = Double.parseDouble(alturaField.getText().trim());
                    double gordura = Double.parseDouble(gorduraField.getText().trim());
                    double massaMagra = Double.parseDouble(massaMagraField.getText().trim());

                    // Cadastrar indicador
                    IndicadorBiomedico novo = indicadorService.cadastrarIndicador(
                        idUsuarioLogado, data, peso, altura, gordura, massaMagra
                    );

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                        String.format("Indicador cadastrado com sucesso!\nIMC: %.1f", novo.getImc()));
                    logger.info("Indicador cadastrado: " + novo);

                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", 
                        "Por favor, digite valores numéricos válidos.");
                    logger.log(Level.WARNING, "Erro de formato ao cadastrar indicador", e);
                } catch (DateTimeParseException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro de Data", 
                        "Formato de data inválido. Use AAAA-MM-DD (ex: 2025-10-17)");
                    logger.log(Level.WARNING, "Erro ao parsear data", e);
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                        "Erro ao cadastrar indicador: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro de validação ao cadastrar indicador", e);
                }
            }
        });
    }

    @FXML
    void listarMeusIndicadores(ActionEvent event) {
    logger.info("Listando indicadores do usuário...");
        
        List<IndicadorBiomedico> meusIndicadores = indicadorService.listarTodosDoUsuario(idUsuarioLogado);
        
        if (meusIndicadores.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sem Indicadores", 
                "Você ainda não possui indicadores registrados.");
            return;
        }

        // Criar o Dialog customizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Meus Indicadores Biomédicos");
        dialog.setHeaderText(String.format("Total de %d indicador(es) registrado(s)", meusIndicadores.size()));

        // Estilo para dialog e header
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // TextArea estilizada
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);
        textArea.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-text-fill: #ffb300;" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        // Montar o conteúdo
        StringBuilder sb = new StringBuilder();
        for (IndicadorBiomedico ind : meusIndicadores) {
            sb.append("═══════════════════════════════════════\n");
            sb.append(String.format("Data: %s\n", ind.getData().format(DATE_FORMATTER)));
            sb.append(String.format("Peso: %.1f kg | Altura: %.1f cm\n", ind.getPesoKg(), ind.getAlturaCm()));
            sb.append(String.format("IMC: %.1f\n", ind.getImc()));
            sb.append(String.format("Gordura: %.1f%% | Massa Magra: %.1f%%\n", 
                ind.getPercentualGordura(), ind.getPercentualMassaMagra()));
            sb.append("\n");
        }
        textArea.setText(sb.toString());

        // Um VBox para aplicar fundo igual ao GridPane das outras telas
        VBox vbox = new VBox(textArea);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #2c2c2c;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Chamado ao clicar no botão sair.
     * Retorna ao menu principal do usuário logado.
     */
    @FXML
    void handleSair(ActionEvent event) {
        logger.info("Botão 'Sair' clicado! Retornando ao menu principal...");
        carregarNovaTela("/fxml/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
    }

    /**
     * Chamado ao clicar no ícone IFechar.
     * Retorna ao menu principal do usuário logado.
     */
    @FXML
    void voltar(MouseEvent event) {
        logger.info("Ícone 'Fechar' clicado! Retornando ao menu principal...");
        carregarNovaTela("/fxml/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
    }

    /**
     * Placeholder para funcionalidade de favoritos (implementação futura).
     */
    @FXML
    void abrirFavoritos(MouseEvent event) {
        logger.info("Ícone 'Favoritos' clicado!");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Em Desenvolvimento", 
            "A funcionalidade de favoritos será implementada em breve.");
    }


    // --- MÉTODOS AUXILIARES ---

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

            // Obtém o stage atual através do botão BCadastrarIN
            Stage stage = (Stage) BCadastrarIN.getScene().getWindow();
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