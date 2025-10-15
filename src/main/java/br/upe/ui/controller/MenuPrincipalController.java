package br.upe.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Controller para a tela do Menu Principal
 * Gerencia as ações dos botões: Entrar, Cadastrar e Sair
 */
public class MenuPrincipalController {

    // Referências aos botões do FXML (usando os fx:id definidos)
    @FXML
    private Button loginB;

    @FXML
    private Button registerB;

    @FXML
    private Button exitB;

    /**
     * Inicializa o controller após o carregamento do FXML
     * Este método é chamado automaticamente pelo JavaFX
     */
    @FXML
    public void initialize() {
        System.out.println("Menu Principal inicializado com sucesso!");
        
        // Aqui você pode adicionar configurações iniciais se necessário
        // Exemplo: desabilitar botões, carregar dados, etc.
    }

    /**
     * Manipula o clique no botão "Entrar" (Login)
     * TODO: Implementar navegação para tela de login
     */
    @FXML
    private void handleLogin() {
        System.out.println("Botão ENTRAR clicado!");
        
        // Por enquanto, mostra uma mensagem
        showInfo("Login", "Funcionalidade de login será implementada em breve.");
        
        // TODO: Navegar para tela de login
        // Exemplo de como será:
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erro", "Não foi possível abrir a tela de login.");
        }
        */
    }

    /**
     * Manipula o clique no botão "Cadastrar"
     * TODO: Implementar navegação para tela de cadastro
     */
    @FXML
    private void handleRegister() {
        System.out.println("Botão CADASTRAR clicado!");
        
        // Por enquanto, mostra uma mensagem
        showInfo("Cadastro", "Funcionalidade de cadastro será implementada em breve.");
        
        // TODO: Navegar para tela de cadastro
    }

    /**
     * Manipula o clique no botão "Sair"
     * Fecha a aplicação
     */
    @FXML
    private void handleExit() {
        System.out.println("Botão SAIR clicado - Encerrando aplicação...");
        
        // Fecha a aplicação JavaFX
        Platform.exit();
        System.exit(0);
    }

    /**
     * Exibe um alerta de informação
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Exibe um alerta de erro
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
