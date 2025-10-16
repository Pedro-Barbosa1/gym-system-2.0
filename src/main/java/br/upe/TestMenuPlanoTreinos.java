package br.upe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe de teste para a tela de Menu Plano Treinos
 */
public class TestMenuPlanoTreinos extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o arquivo FXML do Menu Plano Treinos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPlanoTreinos.fxml"));
            Parent root = loader.load();

            // Cria a cena
            Scene scene = new Scene(root, 900, 700);
            
            // Configura a janela principal
            primaryStage.setTitle("SysFit - Menu Plano de Treinos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(700);
            
            // Exibe a janela
            primaryStage.show();
            
            System.out.println("Tela Menu Plano Treinos carregada com sucesso!");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a interface: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
