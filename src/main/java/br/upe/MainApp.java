package br.upe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal da aplicação JavaFX - SysFit
 * Esta classe inicia a interface gráfica do sistema
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o arquivo FXML do Menu Principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();

            // Cria a cena
            Scene scene = new Scene(root, 1000, 700);
            
            // Configura a janela principal
            primaryStage.setTitle("SysFit - Sistema de Gerenciamento de Academia");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Exibe a janela
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a interface: " + e.getMessage());
        }
    }

    /**
     * Método main - ponto de entrada da aplicação JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}
