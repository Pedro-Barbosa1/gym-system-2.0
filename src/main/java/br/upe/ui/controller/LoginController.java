package br.upe.ui.controller;

import br.upe.model.Usuario;
import br.upe.service.UsuarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField setSenhaField;

    @FXML
    private Button entrarBotao;

    @FXML
    private Button cadastreseBotao;

    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Metodo chamado ao clicar no botão "Entrar"
     */
    @FXML
    void onEntrar(ActionEvent event) {
        String email = emailTextField.getText();
        String senha = setSenhaField.getText();

        try {
            Usuario usuario = usuarioService.autenticarUsuario(email, senha);
            if (usuario != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Login realizado com sucesso",
                        "Bem-vindo, " + usuario.getNome() + "!");

                logger.info("Usuário autenticado com sucesso: " + usuario.getEmail());

                // Carregar tela principal ou home
                FXMLLoader loader = new FXMLLoader(getClass().getResource("")); // path do home
                Parent root = loader.load();

                Stage stage = (Stage) entrarBotao.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Gym System - Início");
                stage.show();

            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Falha no login", "Email ou senha incorretos.");
                logger.warning(() -> "Tentativa de login falhou para o email: " + email);
            }
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos inválidos", e.getMessage());
            logger.log(Level.WARNING, () -> "Erro de validação no login: " + e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro inesperado", "Erro ao tentar fazer login.");
            logger.log(Level.SEVERE, "Erro inesperado no login.", e);
        }
    }

    /**
     * Metodo chamado ao clicar no botão "Cadastre-se"
     */
    @FXML
    void onCadastrase(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/auth/Signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cadastreseBotao.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym System - Cadastro");
            stage.show();

            logger.info("Tela de cadastro carregada com sucesso.");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de cadastro.");
            logger.log(Level.SEVERE, "Erro ao carregar tela de cadastro.", e);
        }
    }

    /**
     * Exibe uma caixa de diálogo para feedback do usuário
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
