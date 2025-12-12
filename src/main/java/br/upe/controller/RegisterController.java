package br.upe.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.service.UsuarioService;
import br.upe.ui.util.StyledAlert;
import br.upe.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    private static final Logger logger = Logger.getLogger(RegisterController.class.getName());

    @FXML
    private TextField nomeCompletoTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField setSenhaField;

    @FXML
    private PasswordField senhaTextField;

    @FXML
    private Button cadastroBotao;

    @FXML
    private Button voltarBotao;

    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Metodo chamado quando o botão Cadastrar é clicado
     */
    @FXML
    void onCadastrar(ActionEvent event) {
        try {
            String nome = nomeCompletoTextField.getText();
            String email = emailTextField.getText();
            String senha = setSenhaField.getText();
            String confirmaSenha = senhaTextField.getText();

            if (!senha.equals(confirmaSenha)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Senhas não conferem", "As senhas informadas são diferentes.");
                logger.warning(() -> "Tentativa de cadastro com senhas diferentes para email: " + email);
                return;
            }

            Usuario novoUsuario = usuarioService.cadastrarUsuario(nome, email, senha, TipoUsuario.COMUM);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso!",
                    "Usuário cadastrado com sucesso:\n" + novoUsuario.getNome() + "\nPor favor, retome para a tela de login.");

            logger.info(() -> "Usuário cadastrado: " + novoUsuario.getEmail());
            limparCampos();

            // Navega para a tela principal mantendo dimensões
            if (!NavigationUtil.navigateFrom(voltarBotao, "/ui/MenuPrincipal.fxml", "Gym System - Tela Inicial")) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar à tela inicial.");
            } else {
                logger.info("Retornando para a tela de login.");
            }

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro no cadastro", e.getMessage());
            logger.log(Level.WARNING, () -> "Erro de validação ao cadastrar: " + e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro inesperado", "Ocorreu um erro: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro inesperado no onCadastrar.", e);
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
     * Exibe uma caixa de diálogo de alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        StyledAlert.mostrarAlerta(tipo, titulo, mensagem);
    }

    /**
     * Limpa os campos de texto após o cadastro
     */
    private void limparCampos() {
        nomeCompletoTextField.clear();
        emailTextField.clear();
        setSenhaField.clear();
        senhaTextField.clear();
    }
}
