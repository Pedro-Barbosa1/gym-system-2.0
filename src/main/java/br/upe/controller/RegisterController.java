package br.upe.controller;

import br.upe.model.TipoUsuario;
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

public class RegisterController {

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
                return;
            }

            Usuario novoUsuario = usuarioService.cadastrarUsuario(nome, email, senha, TipoUsuario.COMUM);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso!",
                    "Usuário cadastrado com sucesso:\n" + novoUsuario.getNome());

            limparCampos();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro no cadastro", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro inesperado", "Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            // Carrega a tela inicial (ajuste o caminho do FXML conforme o seu projeto)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("")); // colocar o path da pagina land/home
            Parent root = loader.load();

            // Obtém a janela atual e substitui a cena
            Stage stage = (Stage) voltarBotao.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym System - Tela Inicial");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar à tela inicial.");
        }
    }

    /**
     * Exibe uma caixa de diálogo de alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
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
