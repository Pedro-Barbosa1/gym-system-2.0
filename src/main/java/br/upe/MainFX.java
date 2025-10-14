package br.upe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFX extends Application {

    // Cria o logger da classe
    private static final Logger logger = Logger.getLogger(MainFX.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            // Carrega o arquivo FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/auth/Signup.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Gym System - Cadastro");
            stage.setScene(scene);
            stage.show();

            logger.info("Tela de cadastro carregada com sucesso.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar a tela de cadastro.", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
