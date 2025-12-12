package br.upe.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.service.UsuarioService;
import br.upe.ui.util.StyledAlert;
import br.upe.util.NavigationUtil;
import br.upe.util.UserSession;
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
     * Redireciona para a tela conforme o tipo do usuário (ADMIN ou COMUM)
     * e passa o ID do usuário logado para o próximo Controller.
     */
    @FXML
    void onEntrar(ActionEvent event) {
        String email = emailTextField.getText();
        String senha = setSenhaField.getText();

        try {
            Usuario usuario = usuarioService.autenticarUsuario(email, senha);

            if (usuario != null) {
                // Define o usuário na sessão global
                UserSession.getInstance().setUsuarioLogado(usuario);

                String caminhoFXML;
                String titulo;

                if (usuario.getTipo() == TipoUsuario.ADMIN) {
                    caminhoFXML = "/ui/AdministradorView.fxml";
                    titulo = "Gym System - Painel do Administrador";
                } else {
                    caminhoFXML = "/ui/MenuUsuarioLogado.fxml";
                    titulo = "Gym System - Painel do Usuário";
                }

                // Usa NavigationUtil para manter dimensões da janela
                if (!NavigationUtil.navigateFrom(entrarBotao, caminhoFXML, titulo)) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar a tela.");
                }

            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Falha no login", "Email ou senha incorretos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro inesperado", "Erro ao tentar fazer login.");
        }
    }


    /**
     * Metodo chamado ao clicar no botão "Cadastre-se".
     */
    @FXML
    void onCadastrase(ActionEvent event) {
        if (!NavigationUtil.navigateFrom(cadastreseBotao, "/ui/Signup.fxml", "Gym System - Cadastro")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de cadastro.");
        } else {
            logger.info("Tela de cadastro carregada com sucesso.");
        }
    }

    @FXML
    void onVoltar(ActionEvent event) {
        if (!NavigationUtil.navigateFrom(voltarBotao, "/ui/MenuPrincipal.fxml", "Gym System - Tela Inicial")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar à tela inicial.");
        } else {
            logger.info("Retornando para a tela de menu principal.");
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