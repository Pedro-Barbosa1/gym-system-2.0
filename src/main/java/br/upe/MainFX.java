package br.upe;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static final Logger logger = Logger.getLogger(MainFX.class.getName());

    @Override
    public void start(Stage stage) {
        abrirTela(stage, "ui/MenuPrincipal.fxml", "SysFit - Menu Principal");
    }

    public void abrirTela(Stage stage, String caminhoFXML, String tituloJanela) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + caminhoFXML));
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
