package br.upe.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.service.IndicadorBiomedicoService;
import br.upe.service.RelatorioDiferencaIndicadores;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller para o menu de relatórios.
 * Mantém o mesmo estilo visual dos pop-ups do IndicadoresViewController.
 */
public class MenuRelatoriosController {

    private static final Logger logger = Logger.getLogger(MenuRelatoriosController.class.getName());
    private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private IndicadorBiomedicoService indicadorService;
    private int idUsuarioLogado = 1; // TODO: Integrar com login

    @FXML private Button sairB;
    @FXML private Button exportarRelatorioB;
    @FXML private Button exportarRelatorioDeDiferencaB;

    @FXML
    public void initialize() {
        this.indicadorService = new IndicadorBiomedicoService();
        logger.info("Menu Relatórios inicializado.");
        configurarAcoes();
    }

    private void configurarAcoes() {
        exportarRelatorioB.setOnAction(e -> handleExportarRelatorioPorData());
        exportarRelatorioDeDiferencaB.setOnAction(e -> handleExportarRelatorioDeDiferenca());
        sairB.setOnAction(e -> handleSair());
    }

    @FXML
    private void handleExportarRelatorioPorData() {
        logger.info("Abrindo diálogo para exportar relatório por data...");

        Dialog<ButtonType> dialog = criarDialogPadrao("Exportar Relatório Por Data", "Informe o período para o relatório:");

        // Criação do conteúdo
        GridPane grid = criarGridPadrao();

        TextField dataInicioField = new TextField();
        TextField dataFimField = new TextField();
        dataInicioField.setPromptText("AAAA-MM-DD");
        dataFimField.setPromptText("AAAA-MM-DD");

        aplicarEstiloCampo(dataInicioField);
        aplicarEstiloCampo(dataFimField);

        Label l1 = criarLabel("Data Início:");
        Label l2 = criarLabel("Data Fim:");
        Label l3 = criarLabel("Formato: AAAA-MM-DD");
        Label l4 = criarLabel("Exemplo: 2025-10-15");

        grid.add(l1, 0, 0);
        grid.add(dataInicioField, 1, 0);
        grid.add(l2, 0, 1);
        grid.add(dataFimField, 1, 1);
        grid.add(l3, 0, 2, 2, 1);
        grid.add(l4, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                processarExportacaoPorData(dataInicioField.getText(), dataFimField.getText());
            }
        });
    }

    private void processarExportacaoPorData(String inicio, String fim) {
        if (inicio.isEmpty() || fim.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "As datas de início e fim não podem estar vazias.");
            return;
        }

        try {
            LocalDate dataInicio = LocalDate.parse(inicio, FORMATTER_ISO);
            LocalDate dataFim = LocalDate.parse(fim, FORMATTER_ISO);

            if (dataInicio.isAfter(dataFim)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Data", "A data inicial deve ser anterior ou igual à data final!");
                return;
            }

            String caminho = "src/main/resources/relatorios/relatorio_por_data_" + idUsuarioLogado + ".csv";
            indicadorService.exportarRelatorioPorDataParaCsv(idUsuarioLogado, dataInicio, dataFim, caminho);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Relatório exportado com sucesso!\n\nPeríodo: " + inicio + " a " + fim + "\nArquivo: " + caminho);

        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Data Inválida", "Formato incorreto! Use AAAA-MM-DD (ex: 2025-10-15)");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao exportar relatório: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao exportar relatório por data", e);
        }
    }

    @FXML
    private void handleExportarRelatorioDeDiferenca() {
        logger.info("Abrindo diálogo para exportar relatório de diferença...");

        Dialog<ButtonType> dialog = criarDialogPadrao("Relatório de Diferença", "Informe as datas para comparação:");
        GridPane grid = criarGridPadrao();

        TextField dataInicioField = new TextField();
        TextField dataFimField = new TextField();
        dataInicioField.setPromptText("AAAA-MM-DD");
        dataFimField.setPromptText("AAAA-MM-DD");
        aplicarEstiloCampo(dataInicioField);
        aplicarEstiloCampo(dataFimField);

        Label l1 = criarLabel("Data Início:");
        Label l2 = criarLabel("Data Fim:");
        Label l3 = criarLabel("Formato: AAAA-MM-DD");
        Label l4 = criarLabel("Exemplo: 2025-10-15");

        grid.add(l1, 0, 0);
        grid.add(dataInicioField, 1, 0);
        grid.add(l2, 0, 1);
        grid.add(dataFimField, 1, 1);
        grid.add(l3, 0, 2, 2, 1);
        grid.add(l4, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                processarRelatorioDiferenca(dataInicioField.getText(), dataFimField.getText());
            }
        });
    }

    private void processarRelatorioDiferenca(String inicio, String fim) {
        if (inicio.isEmpty() || fim.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "As datas de início e fim não podem estar vazias.");
            return;
        }

        try {
            LocalDate dataInicio = LocalDate.parse(inicio, FORMATTER_ISO);
            LocalDate dataFim = LocalDate.parse(fim, FORMATTER_ISO);

            if (dataInicio.isAfter(dataFim)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Data", "A data inicial deve ser anterior ou igual à data final!");
                return;
            }

            RelatorioDiferencaIndicadores relatorio =
                    indicadorService.gerarRelatorioDiferenca(idUsuarioLogado, dataInicio, dataFim);

            String caminho = "src/main/resources/relatorios/relatorio_diferenca_" + idUsuarioLogado + ".csv";
            relatorio.exportarParaCsv(caminho);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Relatório de diferença exportado!\n\nDe: " + inicio + "\nAté: " + fim + "\n\nArquivo: " + caminho);

        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Data Inválida", "Formato incorreto! Use AAAA-MM-DD (ex: 2025-10-15)");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao exportar relatório: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao exportar relatório de diferença", e);
        }
    }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuUsuarioLogado.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar ao menu.");
            logger.log(Level.SEVERE, "Erro ao retornar ao menu", e);
        }
    }

    // === MÉTODOS AUXILIARES DE ESTILO ===

    /** Cria um diálogo com estilo escuro e cabeçalho dourado. */
    private Dialog<ButtonType> criarDialogPadrao(String titulo, String cabecalho) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecalho);
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node header = dialog.getDialogPane().lookup(".header-panel");
            if (header != null) header.setStyle("-fx-background-color: #1e1e1e;");
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
        });
        return dialog;
    }

    /** Cria um GridPane com o mesmo estilo dos pop-ups escuros. */
    private GridPane criarGridPadrao() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #1e1e1e; -fx-border-width: 1; -fx-border-radius: 8;");
        return grid;
    }

    /** Cria um label com texto dourado e negrito. */
    private Label criarLabel(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
        return label;
    }

    /** Aplica o estilo padrão aos campos de texto. */
    private void aplicarEstiloCampo(TextField campo) {
        campo.setStyle("-fx-text-fill: #ffb300;-fx-background-color: dark gray; -fx-border-color: #1e1e1e; -fx-border-width: 1; -fx-border-radius: 4;");
    }

    /** Mostra alertas com o mesmo tema visual. */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        alert.setOnShown(e -> {
            alert.getDialogPane().lookupAll(".label").forEach(node ->
                    node.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 14px;"));
        });
        alert.showAndWait();
    }
}
