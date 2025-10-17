package br.upe.ui.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class IndicadoresViewController {

    private static final Logger logger = Logger.getLogger(IndicadoresViewController.class.getName());

    @FXML
    private Button BCadastrarIN;

    @FXML
    private Button BListarIN;

    @FXML
    private Button sairB;

    @FXML
    private ImageView IFavoritos;

    @FXML
    private ImageView IFechar;

    
    @FXML
    private void abrirCadastrarIndicador() {
        abrirTela("/fxml/", "SysFit - Cadastrar Indicador", BCadastrarIN);
    }

    @FXML
    private void abrirListarIndicadores() {
        abrirTela("/fxml/", "SysFit - Meus Indicadores", BListarIN);
    }

    @FXML
    private void abrirFavoritos() {
        abrirTela("/fxml/", "SysFit - Favoritos", IFavoritos);
    }

    @FXML
    private void voltar() {
        abrirTela("/fxml/MenuUsuarioLogado.fxml", "SysFit - Menu Usuario", IFechar);
    }

    private void abrirTela(String caminhoFXML, String tituloJanela, javafx.scene.Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloJanela);
            stage.show();

            logger.info(() -> "Tela carregada: " + tituloJanela);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxmlFile, e);
        }
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}