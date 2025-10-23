package br.upe.ui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/**
 * Classe utilitária para criar Alerts estilizados com as cores do sistema
 * Cores do sistema:
 * - Roxo principal: #5A189A
 * - Laranja/Amarelo: #e5a000
 * - Cinza escuro: #2C2C2C
 * - Branco: #FFFFFF
 */
public class StyledAlert {
    
    // Cores do sistema
    private static final String COR_ROXA = "#5A189A";
    private static final String COR_LARANJA = "#e5a000";
    private static final String COR_CINZA_ESCURO = "#2C2C2C";
    private static final String COR_BRANCA = "#FFFFFF";
    private static final String COR_VERDE = "#28a745";
    private static final String COR_VERMELHA = "#dc3545";
    
    /**
     * Estiliza um Alert com as cores do sistema
     * @param alert Alert a ser estilizado
     * @param tipo Tipo do alert (INFORMATION, ERROR, CONFIRMATION, WARNING)
     */
    private static void estilizarAlert(Alert alert, Alert.AlertType tipo) {
        DialogPane dialogPane = alert.getDialogPane();
        
        // Determinar cor do header baseado no tipo
        String corHeader;
        switch (tipo) {
            case INFORMATION:
                corHeader = COR_ROXA;
                break;
            case ERROR:
                corHeader = COR_VERMELHA;
                break;
            case CONFIRMATION:
            case WARNING:
                corHeader = COR_LARANJA;
                break;
            default:
                corHeader = COR_CINZA_ESCURO;
        }
        
        // Aplicar estilos inline diretamente no DialogPane
        dialogPane.setStyle(
            "-fx-background-color: " + COR_BRANCA + ";" +
            "-fx-font-family: 'System';" +
            "-fx-font-size: 14px;"
        );
        
        // Aplicar estilização após o dialog ser mostrado
        final String finalCorHeader = corHeader;
        alert.setOnShown(event -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    // Estilizar header panel - aplicar em múltiplas regiões para garantir
                    javafx.scene.Node headerPanel = dialogPane.lookup(".header-panel");
                    if (headerPanel != null) {
                        // Aplicar estilo com múltiplas propriedades para garantir override
                        headerPanel.setStyle(
                            "-fx-background-color: " + finalCorHeader + ";" +
                            "-fx-background: " + finalCorHeader + ";" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 15px;"
                        );
                        
                        // Se for uma região, forçar a cor de fundo
                        if (headerPanel instanceof javafx.scene.layout.Region) {
                            javafx.scene.layout.Region region = (javafx.scene.layout.Region) headerPanel;
                            region.setBackground(
                                new javafx.scene.layout.Background(
                                    new javafx.scene.layout.BackgroundFill(
                                        javafx.scene.paint.Color.web(finalCorHeader),
                                        javafx.scene.layout.CornerRadii.EMPTY,
                                        javafx.geometry.Insets.EMPTY
                                    )
                                )
                            );
                        }
                        
                        // Estilizar label do header
                        javafx.scene.Node headerLabel = headerPanel.lookup(".label");
                        if (headerLabel != null) {
                            headerLabel.setStyle(
                                "-fx-text-fill: " + COR_BRANCA + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;"
                            );
                        }
                    }
                    
                    // Estilizar content
                    javafx.scene.Node content = dialogPane.lookup(".content");
                    if (content != null) {
                        content.setStyle(
                            "-fx-background-color: " + COR_BRANCA + ";" +
                            "-fx-padding: 20px;"
                        );
                        
                        // Estilizar label do content
                        javafx.scene.Node contentLabel = content.lookup(".label");
                        if (contentLabel != null) {
                            contentLabel.setStyle(
                                "-fx-text-fill: " + COR_CINZA_ESCURO + ";" +
                                "-fx-font-size: 14px;"
                            );
                        }
                    }
                    
                    // Estilizar botões
                    dialogPane.lookupAll(".button").forEach(node -> {
                        if (node instanceof javafx.scene.control.ButtonBase) {
                            javafx.scene.control.ButtonBase button = (javafx.scene.control.ButtonBase) node;
                            String text = button.getText();
                            
                            // Botões de cancelamento/negação em cinza
                            boolean isCancelButton = text != null && 
                                (text.equalsIgnoreCase("Cancelar") || 
                                 text.equalsIgnoreCase("Não") ||
                                 text.equalsIgnoreCase("Cancel") ||
                                 text.equalsIgnoreCase("No"));
                            
                            String corBotao = isCancelButton ? "#6c757d" : COR_ROXA;
                            
                            String estiloBotao = 
                                "-fx-background-color: " + corBotao + ";" +
                                "-fx-text-fill: " + COR_BRANCA + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5px;" +
                                "-fx-border-radius: 5px;" +
                                "-fx-padding: 8px 16px 8px 16px;" +
                                "-fx-min-width: 80px;" +
                                "-fx-cursor: hand;";
                            
                            node.setStyle(estiloBotao);
                            
                            // Efeito hover
                            final String estiloBase = estiloBotao;
                            node.setOnMouseEntered(e -> {
                                node.setStyle(estiloBase + "-fx-opacity: 0.85;");
                            });
                            node.setOnMouseExited(e -> {
                                node.setStyle(estiloBase);
                            });
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println("Erro ao estilizar Alert: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        });
    }
    
    /**
     * Cria um Alert de informação estilizado
     * @param title Título do alert
     * @param header Cabeçalho do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        estilizarAlert(alert, Alert.AlertType.INFORMATION);
        return alert;
    }
    
    /**
     * Cria um Alert de informação estilizado (sem header)
     * @param title Título do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showInformation(String title, String content) {
        return showInformation(title, null, content);
    }
    
    /**
     * Cria um Alert de erro estilizado
     * @param title Título do alert
     * @param header Cabeçalho do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        estilizarAlert(alert, Alert.AlertType.ERROR);
        return alert;
    }
    
    /**
     * Cria um Alert de erro estilizado (sem header)
     * @param title Título do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showError(String title, String content) {
        return showError(title, null, content);
    }
    
    /**
     * Cria um Alert de confirmação estilizado
     * @param title Título do alert
     * @param header Cabeçalho do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        estilizarAlert(alert, Alert.AlertType.CONFIRMATION);
        return alert;
    }
    
    /**
     * Cria um Alert de confirmação estilizado (sem header)
     * @param title Título do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showConfirmation(String title, String content) {
        return showConfirmation(title, null, content);
    }
    
    /**
     * Cria um Alert de aviso estilizado
     * @param title Título do alert
     * @param header Cabeçalho do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        estilizarAlert(alert, Alert.AlertType.WARNING);
        return alert;
    }
    
    /**
     * Cria um Alert de aviso estilizado (sem header)
     * @param title Título do alert
     * @param content Conteúdo do alert
     * @return Alert configurado
     */
    public static Alert showWarning(String title, String content) {
        return showWarning(title, null, content);
    }
    
    /**
     * Exibe um Alert de informação e aguarda resposta
     * @param title Título do alert
     * @param content Conteúdo do alert
     */
    public static void showInformationAndWait(String title, String content) {
        showInformation(title, content).showAndWait();
    }
    
    /**
     * Exibe um Alert de erro e aguarda resposta
     * @param title Título do alert
     * @param content Conteúdo do alert
     */
    public static void showErrorAndWait(String title, String content) {
        showError(title, content).showAndWait();
    }
    
    /**
     * Exibe um Alert de aviso e aguarda resposta
     * @param title Título do alert
     * @param content Conteúdo do alert
     */
    public static void showWarningAndWait(String title, String content) {
        showWarning(title, content).showAndWait();
    }
    
    /**
     * Exibe um Alert de confirmação e retorna se o usuário confirmou
     * @param title Título do alert
     * @param content Conteúdo do alert
     * @return true se confirmado, false caso contrário
     */
    public static boolean showConfirmationAndWait(String title, String content) {
        Optional<ButtonType> result = showConfirmation(title, content).showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
