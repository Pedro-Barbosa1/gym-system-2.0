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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller responsável pela tela de Indicadores.
 * Permite navegação entre as funcionalidades principais da tela de Indicadores.
 */
public class IndicadoresViewController {

    private static final Logger logger = Logger.getLogger(IndicadoresViewController.class.getName());

    @FXML
    private Button BCadastrarIN;

    @FXML
    private Button BListarIN;

    @FXML
    private ImageView IFavoritos;

    @FXML
    private ImageView IFechar;

    /**
     * Abre a tela de "Cadastrar novo indicador".
     * TODO: implementar cadastro de indicador
     */
    @FXML
    private void abrirCadastrarIndicador() {
        abrirTela("/fxml/", "SysFit - Cadastrar Indicador", BCadastrarIN);
    }

    /**
     * Abre a tela de "Lista indicadores".
     * TODO: implementar listagem de indicador
     */
    @FXML
    private void abrirListarIndicadores() {
        abrirTela("/fxml/", "SysFit - Meus Indicadores", BListarIN);
    }

    /**
     * Abre a tela de "Favoritos".
     * TODO: implementar listagem de favoritos
     */
    @FXML
    private void abrirFavoritos() {
        abrirTela("/fxml/", "SysFit - Favoritos", IFavoritos);
    }

    /**
     * Sai da conta e retorna para o menu principal.
     */
    @FXML
    private void voltar() {
        abrirTela("/fxml/MenuUsuarioLogado.fxml", "SysFit - Menu Usuario", IFechar);
    }

    /**
     * Metodo genérico para abrir uma nova tela.
     * Aceita qualquer Node (Button ou ImageView) para obter a Stage.
     */
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
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao carregar tela",
                    "Não foi possível abrir a tela: " + tituloJanela);
            logger.log(Level.SEVERE, "Erro ao carregar " + caminhoFXML, e);
        }
    }

    /**
     * Exibe alertas para o usuário.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
