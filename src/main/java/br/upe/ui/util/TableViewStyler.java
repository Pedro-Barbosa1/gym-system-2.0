package br.upe.ui.util;

import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.Labeled;
import javafx.scene.Node;

/**
 * Classe utilitária para aplicar estilos consistentes em TableViews.
 * Centraliza a lógica de estilização para evitar duplicação de código.
 */
public class TableViewStyler {
    
    // Cores do sistema
    private static final String COR_FUNDO = "#2c2c2c";
    private static final String COR_TEXTO = "#ffb300";
    private static final String COR_BORDA = "#333";
    private static final String COR_SELECAO = "#5A189A";
    private static final String COR_HEADER = "#1e1e1e";
    
    private TableViewStyler() {
        // Classe utilitária - não deve ser instanciada
    }
    
    /**
     * Aplica o estilo padrão do sistema em uma TableView.
     * @param tableView TableView a ser estilizada
     * @param <T> Tipo dos itens da tabela
     */
    public static <T> void aplicarEstilo(TableView<T> tableView) {
        aplicarEstiloBase(tableView);
        aplicarEstiloLinhas(tableView);
        aplicarEstiloHeaders(tableView);
    }
    
    private static <T> void aplicarEstiloBase(TableView<T> tableView) {
        tableView.setStyle(
            "-fx-background-color: " + COR_FUNDO + "; " +
            "-fx-control-inner-background: " + COR_FUNDO + "; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 0; " +
            "-fx-table-cell-border-color: " + COR_BORDA + ";"
        );
    }
    
    private static <T> void aplicarEstiloLinhas(TableView<T> tableView) {
        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            
            String estiloNormal = 
                "-fx-background-color: " + COR_FUNDO + "; " +
                "-fx-text-fill: " + COR_TEXTO + "; " +
                "-fx-border-color: " + COR_BORDA + ";";
            
            String estiloSelecionado = 
                "-fx-background-color: " + COR_SELECAO + "; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: " + COR_BORDA + ";";
            
            row.setStyle(estiloNormal);
            
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && !row.isSelected()) {
                    row.setStyle(estiloNormal);
                }
            });
            
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                row.setStyle(isSelected ? estiloSelecionado : estiloNormal);
            });
            
            return row;
        });
    }
    
    private static <T> void aplicarEstiloHeaders(TableView<T> tableView) {
        tableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                tableView.applyCss();
                tableView.layout();
                
                Node headerRow = tableView.lookup(".column-header-background");
                if (headerRow != null) {
                    headerRow.setStyle("-fx-background-color: " + COR_HEADER + ";");
                }
                
                tableView.lookupAll(".column-header").forEach(node -> 
                    node.setStyle(
                        "-fx-background-color: " + COR_HEADER + "; " +
                        "-fx-text-fill: " + COR_TEXTO + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: " + COR_BORDA + ";"
                    )
                );
                
                tableView.lookupAll(".column-header .label").forEach(node -> {
                    if (node instanceof Labeled) {
                        ((Labeled) node).setStyle(
                            "-fx-text-fill: " + COR_TEXTO + "; " +
                            "-fx-font-weight: bold;"
                        );
                    }
                });
            }
        });
    }
}
