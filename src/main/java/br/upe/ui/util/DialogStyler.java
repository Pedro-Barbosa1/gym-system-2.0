package br.upe.ui.util;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Classe utilitária para aplicar estilos consistentes em Dialogs.
 * Centraliza a lógica de estilização para evitar duplicação de código.
 */
public class DialogStyler {
    
    // Cores do sistema
    private static final String COR_FUNDO_DIALOG = "#1e1e1e";
    private static final String COR_FUNDO_GRID = "#2c2c2c";
    private static final String COR_FUNDO_CAMPO = "#222";
    private static final String COR_TEXTO = "#ffb300";
    
    private DialogStyler() {
        // Classe utilitária - não deve ser instanciada
    }
    
    /**
     * Aplica estilo padrão em um Dialog.
     * @param dialog Dialog a ser estilizado
     */
    public static void aplicarEstilo(Dialog<?> dialog) {
        dialog.getDialogPane().setStyle("-fx-background-color: " + COR_FUNDO_DIALOG + ";");
        
        Node header = dialog.getDialogPane().lookup(".header-panel");
        if (header != null) {
            header.setStyle("-fx-background-color: " + COR_FUNDO_DIALOG + ";");
        }
    }
    
    /**
     * Aplica estilo em um GridPane de formulário.
     * @param grid GridPane a ser estilizado
     */
    public static void aplicarEstiloGrid(GridPane grid) {
        grid.setStyle("-fx-background-color: " + COR_FUNDO_GRID + ";");
    }
    
    /**
     * Aplica estilo padrão em um TextField.
     * @param field TextField a ser estilizado
     */
    public static void aplicarEstiloCampo(TextField field) {
        field.setStyle("-fx-background-color: " + COR_FUNDO_CAMPO + "; " +
                      "-fx-text-fill: " + COR_TEXTO + "; " +
                      "-fx-border-radius: 4;");
    }
    
    /**
     * Aplica estilo padrão em um ComboBox com células personalizadas.
     * @param comboBox ComboBox a ser estilizado
     * @param <T> Tipo dos itens do ComboBox
     */
    public static <T> void aplicarEstiloComboBox(ComboBox<T> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-background-color: " + COR_FUNDO_CAMPO + ";");
            }
        });
        
        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-background-color: " + COR_FUNDO_CAMPO + ";");
            }
        });
        
        comboBox.setStyle("-fx-background-color: " + COR_FUNDO_CAMPO + "; -fx-text-fill: " + COR_TEXTO + ";");
    }
}
