package br.upe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principal da aplicação JavaFX - SysFit
 * Esta classe inicia a interface gráfica do sistema
 */
public class MainApp extends Application {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

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

            logger.info("Aplicação SysFit iniciada com sucesso!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar a interface gráfica do sistema.", e);
        }
    }

    /**
     * Metodo main - ponto de entrada da aplicação JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}