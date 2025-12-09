package br.upe.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.service.UsuarioService;
import br.upe.ui.util.StyledAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML
    private Button voltarBotao;

    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Metodo chamado ao clicar no botão "Entrar".
     * Redireciona para a tela conforme o tipo do usuário (ADMIN ou COMUM).
     */

    @FXML
    public void initialize() {
        entrarBotao.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.setMinWidth(900);
                stage.setMinHeight(700);
            }
        });
    }

    @FXML
    void onEntrar(ActionEvent event) {
        String email = emailTextField.getText();
        String senha = setSenhaField.getText();

        try {
            Usuario usuario = usuarioService.autenticarUsuario(email, senha);

            if (usuario != null) {
                logger.info(() -> "Usuário autenticado: " + usuario.getEmail() +
                        " (tipo: " + usuario.getTipo() + ")");

                // Verifica o tipo de usuário e abre a tela correspondente
                String caminhoFXML;
                String titulo;

                if (usuario.getTipo() == TipoUsuario.ADMIN) {
                    caminhoFXML = "/fxml/AdministradorView.fxml";
                    titulo = "Gym System - Painel do Administrador";
                } else {
                    caminhoFXML = "/fxml/MenuUsuarioLogado.fxml";
                    titulo = "Gym System - Painel do Usuário";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
                Parent root = loader.load();

                Stage stage = (Stage) entrarBotao.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(titulo);
                stage.show();

            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Falha no login", "Email ou senha incorretos.");
                logger.warning(() -> "Tentativa de login falhou para o email: " + email);
            }

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos inválidos", e.getMessage());
            logger.log(Level.WARNING, () -> "Erro de validação no login: " + e.getMessage());

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao carregar a tela principal.");
            logger.log(Level.SEVERE, "Erro ao carregar FXML da próxima tela.", e);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro inesperado", "Erro ao tentar fazer login.");
            logger.log(Level.SEVERE, "Erro inesperado no login.", e);
        }
    }

    /**
     * Metodo chamado ao clicar no botão "Cadastre-se".
     */
    @FXML
    void onCadastrase(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
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

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) voltarBotao.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym System - Tela Inicial");
            stage.show();

            logger.info("Retornando para a tela de menu principal.");

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar à tela inicial.");
            logger.log(Level.SEVERE, "Erro ao carregar a tela inicial no onVoltar.", e);
        }
    }

    /**
     * Exibe uma caixa de diálogo para feedback do usuário.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        if (tipo == Alert.AlertType.ERROR) {
            StyledAlert.showErrorAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.INFORMATION) {
            StyledAlert.showInformationAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.WARNING) {
            StyledAlert.showWarningAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.CONFIRMATION) {
            StyledAlert.showConfirmationAndWait(titulo, mensagem);
        }
    }
}
