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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller para a tela do Menu de Relatórios
 * Gerencia a exportação de relatórios do sistema
 */
public class MenuRelatoriosController {

    private static final Logger logger = Logger.getLogger(MenuRelatoriosController.class.getName());
    
    // Services
    private IndicadorBiomedicoService indicadorService;
    
    // ID do usuário logado
    private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login
    
    // Formatter para datas
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Referências aos botões do FXML
    @FXML
    private Button sairB;

    @FXML
    private Button exportarRelatorioB;

    @FXML
    private Button exportarRelatorioDeDiferencaB;

    /**
     * Inicializa o controller após o carregamento do FXML
     * Este metodo é chamado automaticamente pelo JavaFX
     */
    @FXML
    public void initialize() {
        this.indicadorService = new IndicadorBiomedicoService();
        
        logger.info("Menu Relatórios inicializado com sucesso!");

        // Configura as ações dos botões
        configurarAcoes();
    }

    /**
     * Configura as ações dos botões
     */
    private void configurarAcoes() {
        exportarRelatorioB.setOnAction(e -> handleExportarRelatorioPorData());
        exportarRelatorioDeDiferencaB.setOnAction(e -> handleExportarRelatorioDeDiferenca());
        sairB.setOnAction(e -> handleSair());
    }

    /**
     * Manipula o clique no botão "Exportar Relatório Por Data"
     * Exporta relatório de treinos/indicadores de uma data específica
     */
    @FXML
    private void handleExportarRelatorioPorData() {
        logger.info("Exportar Relatório Por Data clicado!");

        // Criar Dialog com GridPane para duas datas
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Exportar Relatório Por Data");
        dialog.setHeaderText("Informe o período para o relatório:");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField dataInicioField = new TextField();
        dataInicioField.setPromptText("AAAA-MM-DD");
        TextField dataFimField = new TextField();
        dataFimField.setPromptText("AAAA-MM-DD");
        
        grid.add(new Label("Data Início:"), 0, 0);
        grid.add(dataInicioField, 1, 0);
        grid.add(new Label("Data Fim:"), 0, 1);
        grid.add(dataFimField, 1, 1);
        grid.add(new Label("Formato: AAAA-MM-DD"), 0, 2, 2, 1);
        grid.add(new Label("Exemplo: 2025-10-15"), 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> resultado = dialog.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String dataInicioStr = dataInicioField.getText().trim();
            String dataFimStr = dataFimField.getText().trim();
            
            if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                showError("Erro", "As datas de início e fim não podem estar vazias.");
                return;
            }
            
            try {
                LocalDate dataInicio = LocalDate.parse(dataInicioStr, FORMATTER_ISO);
                LocalDate dataFim = LocalDate.parse(dataFimStr, FORMATTER_ISO);
                
                // Verificar se data início é anterior à data fim
                if (dataInicio.isAfter(dataFim)) {
                    showError("Erro de Data", "A data inicial deve ser anterior ou igual à data final!");
                    return;
                }
                
                // Exportar relatório
                String caminho = "src/main/resources/relatorios/relatorio_por_data_" + idUsuarioLogado + ".csv";
                indicadorService.exportarRelatorioPorDataParaCsv(idUsuarioLogado, dataInicio, dataFim, caminho);
                
                showInfo("Sucesso", 
                    "Relatório por data exportado com sucesso!\n\n" +
                    "Período: " + dataInicio.format(FORMATTER_ISO) + " a " + dataFim.format(FORMATTER_ISO) + "\n" +
                    "Arquivo: " + caminho);
                    
            } catch (DateTimeParseException e) {
                showError("Data Inválida", 
                    "Formato de data inválido!\n" +
                    "Use o formato: AAAA-MM-DD\n" +
                    "Exemplo: 2025-10-15");
            } catch (Exception e) {
                showError("Erro", "Erro ao exportar relatório: " + e.getMessage());
                logger.log(Level.SEVERE, "Erro ao exportar relatório por data", e);
            }
        }
    }

    /**
     * Manipula o clique no botão "Exportar Relatório De Diferença"
     * Exporta relatório comparando indicadores entre duas datas
     */
    @FXML
    private void handleExportarRelatorioDeDiferenca() {
        logger.info("Exportar Relatório De Diferença clicado!");

        // Criar Dialog com GridPane para duas datas
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Relatório de Diferença");
        dialog.setHeaderText("Informe as datas para comparação:");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField dataInicioField = new TextField();
        dataInicioField.setPromptText("AAAA-MM-DD");
        TextField dataFimField = new TextField();
        dataFimField.setPromptText("AAAA-MM-DD");
        
        grid.add(new Label("Data Início:"), 0, 0);
        grid.add(dataInicioField, 1, 0);
        grid.add(new Label("Data Fim:"), 0, 1);
        grid.add(dataFimField, 1, 1);
        grid.add(new Label("Formato: AAAA-MM-DD"), 0, 2, 2, 1);
        grid.add(new Label("Exemplo: 2025-10-15"), 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> resultado = dialog.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String dataInicioStr = dataInicioField.getText().trim();
            String dataFimStr = dataFimField.getText().trim();
            
            if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                showError("Erro", "As datas de início e fim não podem estar vazias.");
                return;
            }
            
            try {
                LocalDate dataInicio = LocalDate.parse(dataInicioStr, FORMATTER_ISO);
                LocalDate dataFim = LocalDate.parse(dataFimStr, FORMATTER_ISO);
                
                // Verificar se data início é anterior à data fim
                if (dataInicio.isAfter(dataFim)) {
                    showError("Erro de Data", "A data inicial deve ser anterior ou igual à data final!");
                    return;
                }
                
                // Gerar relatório de diferença
                RelatorioDiferencaIndicadores relatorio = indicadorService.gerarRelatorioDiferenca(
                    idUsuarioLogado, dataInicio, dataFim);
                
                String caminho = "src/main/resources/relatorios/relatorio_diferenca_" + idUsuarioLogado + ".csv";
                relatorio.exportarParaCsv(caminho);
                
                showInfo("Sucesso", 
                    "Relatório de diferença exportado com sucesso!\n\n" +
                    "Data Inicial: " + dataInicio.format(FORMATTER_ISO) + "\n" +
                    "Data Final: " + dataFim.format(FORMATTER_ISO) + "\n\n" +
                    "Arquivo: " + caminho);
                    
            } catch (DateTimeParseException e) {
                showError("Data Inválida", 
                    "Formato de data inválido!\n" +
                    "Use o formato: AAAA-MM-DD\n" +
                    "Exemplo: 2025-10-15");
            } catch (Exception e) {
                showError("Erro", "Erro ao exportar relatório: " + e.getMessage());
                logger.log(Level.SEVERE, "Erro ao exportar relatório de diferença", e);
            }
        }
    }

    /**
     * Manipula o clique no botão "Sair"
     * Fecha a aplicação ou volta para o menu anterior
     */
    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuUsuarioLogado.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erro", "Não foi possível voltar ao menu.");
            logger.log(Level.SEVERE, "Erro ao retornar para o menu", e);
        }
    }

    // --- MÉTODOS AUXILIARES PARA ALERTAS ---
    /**
     * Exibe um alerta de informação
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Exibe um alerta de erro
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
