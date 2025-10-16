package br.upe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFX extends Application {

    private static final Logger logger = Logger.getLogger(MainFX.class.getName());

    @Override
    public void start(Stage stage) {
        abrirTela(stage, "auth/Login.fxml", "GymSystem - Login");
    }

    public void abrirTela(Stage stage, String caminhoFXML, String tituloJanela) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/" + caminhoFXML));
            Parent root = loader.load();

            stage.setTitle(tituloJanela);
            stage.setScene(new Scene(root));
            stage.show();

            logger.info(() -> "Tela carregada com sucesso: " + caminhoFXML);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar a tela: " + caminhoFXML, e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
