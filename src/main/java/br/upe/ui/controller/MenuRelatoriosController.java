package br.upe.ui.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

/**
 * Controller para a tela do Menu de Relatórios
 * Gerencia a exportação de relatórios do sistema
 */
public class MenuRelatoriosController {

    // Referências aos botões do FXML
    @FXML
    private Button sairB;

    @FXML
    private Button exportarRelatorioB;

    @FXML
    private Button exportarRelatorioDeDiferencaB;

    /**
     * Inicializa o controller após o carregamento do FXML
     * Este método é chamado automaticamente pelo JavaFX
     */
    @FXML
    public void initialize() {
        System.out.println("Menu Relatórios inicializado com sucesso!");
        
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
        System.out.println("Exportar Relatório Por Data clicado!");
        
        // Solicita a data ao usuário
        Optional<String> dataInput = solicitarData("Exportar Relatório", 
            "Digite a data para o relatório (formato: dd/MM/yyyy):");
        
        if (dataInput.isPresent() && !dataInput.get().trim().isEmpty()) {
            String dataStr = dataInput.get().trim();
            
            // Valida o formato da data
            if (validarFormatoData(dataStr)) {
                showInfo("Exportação Iniciada", 
                    "Gerando relatório para a data: " + dataStr + "\n\n" +
                    "O arquivo será salvo na pasta de relatórios.");
                
                // TODO: Implementar exportação de relatório por data
                /*
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate data = LocalDate.parse(dataStr, formatter);
                    
                    // Buscar dados do repositório
                    List<SessaoTreino> sessoes = sessaoTreinoRepository.findByData(data);
                    List<IndicadorBiomedico> indicadores = indicadorRepository.findByData(data);
                    
                    // Gerar relatório usando JasperReports
                    RelatorioService relatorioService = new RelatorioService();
                    String caminhoArquivo = relatorioService.gerarRelatorioPorData(data, sessoes, indicadores);
                    
                    showInfo("Sucesso", "Relatório exportado com sucesso!\nArquivo: " + caminhoArquivo);
                } catch (Exception ex) {
                    showError("Erro", "Erro ao exportar relatório: " + ex.getMessage());
                    ex.printStackTrace();
                }
                */
            } else {
                showError("Data Inválida", 
                    "Formato de data inválido!\n" +
                    "Use o formato: dd/MM/yyyy\n" +
                    "Exemplo: 15/10/2025");
            }
        }
    }

    /**
     * Manipula o clique no botão "Exportar Relatório De Diferença"
     * Exporta relatório comparando indicadores entre duas datas
     */
    @FXML
    private void handleExportarRelatorioDeDiferenca() {
        System.out.println("Exportar Relatório De Diferença clicado!");
        
        // Solicita a primeira data
        Optional<String> dataInicial = solicitarData("Relatório de Diferença", 
            "Digite a DATA INICIAL (formato: dd/MM/yyyy):");
        
        if (dataInicial.isPresent() && !dataInicial.get().trim().isEmpty()) {
            String dataInicialStr = dataInicial.get().trim();
            
            if (!validarFormatoData(dataInicialStr)) {
                showError("Data Inválida", 
                    "Formato de data inicial inválido!\n" +
                    "Use o formato: dd/MM/yyyy");
                return;
            }
            
            // Solicita a segunda data
            Optional<String> dataFinal = solicitarData("Relatório de Diferença", 
                "Digite a DATA FINAL (formato: dd/MM/yyyy):");
            
            if (dataFinal.isPresent() && !dataFinal.get().trim().isEmpty()) {
                String dataFinalStr = dataFinal.get().trim();
                
                if (!validarFormatoData(dataFinalStr)) {
                    showError("Data Inválida", 
                        "Formato de data final inválido!\n" +
                        "Use o formato: dd/MM/yyyy");
                    return;
                }
                
                showInfo("Exportação Iniciada", 
                    "Gerando relatório de diferença:\n" +
                    "Data Inicial: " + dataInicialStr + "\n" +
                    "Data Final: " + dataFinalStr + "\n\n" +
                    "O arquivo será salvo na pasta de relatórios.");
                
                // TODO: Implementar exportação de relatório de diferença
                /*
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dtInicial = LocalDate.parse(dataInicialStr, formatter);
                    LocalDate dtFinal = LocalDate.parse(dataFinalStr, formatter);
                    
                    // Verificar se a data inicial é anterior à final
                    if (dtInicial.isAfter(dtFinal)) {
                        showError("Erro de Data", "A data inicial deve ser anterior à data final!");
                        return;
                    }
                    
                    // Buscar indicadores das duas datas
                    List<IndicadorBiomedico> indicadoresInicial = indicadorRepository.findByData(dtInicial);
                    List<IndicadorBiomedico> indicadoresFinal = indicadorRepository.findByData(dtFinal);
                    
                    // Gerar relatório de diferença
                    RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
                    String caminhoArquivo = relatorio.gerar(dtInicial, dtFinal, 
                        indicadoresInicial, indicadoresFinal);
                    
                    showInfo("Sucesso", "Relatório de diferença exportado com sucesso!\n" +
                        "Arquivo: " + caminhoArquivo);
                } catch (Exception ex) {
                    showError("Erro", "Erro ao exportar relatório: " + ex.getMessage());
                    ex.printStackTrace();
                }
                */
            }
        }
    }

    /**
     * Manipula o clique no botão "Sair"
     * Fecha a aplicação ou volta para o menu anterior
     */
    @FXML
    private void handleSair() {
        System.out.println("Botão SAIR clicado - Encerrando aplicação...");
        
        // Opção 1: Fechar a aplicação completamente
        Platform.exit();
        
        // Opção 2: Voltar para o menu principal (quando implementado)
        // TODO: Implementar navegação para menu anterior
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erro", "Não foi possível voltar ao menu.");
        }
        */
    }

    /**
     * Solicita uma data ao usuário através de um diálogo
     * @param titulo Título do diálogo
     * @param mensagem Mensagem a ser exibida
     * @return Optional contendo a data digitada
     */
    private Optional<String> solicitarData(String titulo, String mensagem) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(mensagem);
        
        return dialog.showAndWait();
    }

    /**
     * Valida se a string está no formato de data dd/MM/yyyy
     * @param data String a ser validada
     * @return true se o formato é válido, false caso contrário
     */
    private boolean validarFormatoData(String data) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(data, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

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
