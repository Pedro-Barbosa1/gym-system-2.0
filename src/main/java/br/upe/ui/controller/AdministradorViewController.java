package br.upe.ui.controller;

import br.upe.model.Usuario;
import br.upe.service.IUsuarioService;
import br.upe.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller para a tela de administração
 * Gerencia ações de usuário como listar, promover, rebaixar ou remover
 */
public class AdministradorViewController {

    private static final Logger logger = Logger.getLogger(AdministradorViewController.class.getName());
    
    // Services
    private IUsuarioService usuarioService;

    // --- IMAGENS E LOGO ---
    @FXML
    private ImageView LogoDoApp;

    @FXML
    private ImageView IFavoritos;

    // --- BOTÕES ---
    @FXML
    private Button BSairDoApp;

    @FXML
    private Button BListarUsuarios;

    @FXML
    private Button BPromoverUsuario;

    @FXML
    private Button BRebaixarUsuario;

    @FXML
    private Button BRemoverUsuario;

    /**
     * Inicializa o controller após o carregamento do FXML
     * Este metodo é chamado automaticamente pelo JavaFX
     */
    @FXML
    public void initialize() {
        this.usuarioService = new UsuarioService();
        
        logger.info("AdministradorViewController inicializado com sucesso!");
    }

    /**
     * Manipula o clique no botão "Sair"
     * Fecha a tela atual e retorna ao menu principal
     */
    @FXML
    public void handleSair() {
        logger.info("Botão 'Sair' clicado. Retornando ao menu principal...");
        carregarNovaTela("/fxml/MenuPrincipal.fxml", "Gym System - Menu Principal");
    }

    /**
     * Manipula o clique no botão "Listar Usuários"
     * Exibe todos os usuários cadastrados no sistema em um Dialog
     */
    @FXML
    public void handleListarUsuarios() {
        logger.info("Botão 'Listar Usuários' clicado!");
        
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        
        if (usuarios.isEmpty()) {
            showInfo("Lista de Usuários", "Nenhum usuário cadastrado no sistema.");
            return;
        }
        
        // Criar Dialog com TextArea
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lista de Todos os Usuários");
        dialog.setHeaderText("Usuários cadastrados no sistema:");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        StringBuilder sb = new StringBuilder();
        for (Usuario u : usuarios) {
            sb.append("═══════════════════════════════════════\n");
            sb.append(String.format("ID: %d%n", u.getId()));
            sb.append(String.format("Nome: %s%n", u.getNome()));
            sb.append(String.format("Email: %s%n", u.getEmail()));
            sb.append(String.format("Tipo: %s%n", u.getTipo()));
            sb.append("\n");
        }
        
        textArea.setText(sb.toString());
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }

    /**
     * Manipula o clique no botão "Promover Usuário"
     * Promove um usuário comum para administrador
     */
    @FXML
    public void handlePromoverUsuario() {
        logger.info("Botão 'Promover Usuário' clicado!");
        
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        
        if (usuarios.isEmpty()) {
            showInfo("Promover Usuário", "Nenhum usuário cadastrado no sistema.");
            return;
        }
        
        // Selecionar usuário
        Usuario usuarioSelecionado = exibirDialogSelecaoUsuario(usuarios, "Promover Usuário a Admin");
        
        if (usuarioSelecionado == null) {
            return;
        }
        
        // Confirmação
        if (showConfirmation("Confirmar Promoção", 
            "Deseja realmente promover o usuário '" + usuarioSelecionado.getNome() + "' a Administrador?")) {
            
            try {
                usuarioService.promoverUsuarioAAdmin(usuarioSelecionado.getId());
                showInfo("Sucesso", "Usuário '" + usuarioSelecionado.getNome() + "' promovido a Administrador com sucesso!");
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao promover usuário: " + e.getMessage());
            }
        }
    }

    /**
     * Manipula o clique no botão "Rebaixar Usuário"
     * Rebaixa um administrador para usuário comum
     */
    @FXML
    public void handleRebaixarUsuario() {
        logger.info("Botão 'Rebaixar Usuário' clicado!");
        
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        
        if (usuarios.isEmpty()) {
            showInfo("Rebaixar Usuário", "Nenhum usuário cadastrado no sistema.");
            return;
        }
        
        // Selecionar usuário
        Usuario usuarioSelecionado = exibirDialogSelecaoUsuario(usuarios, "Rebaixar Usuário a Comum");
        
        if (usuarioSelecionado == null) {
            return;
        }
        
        // Confirmação
        if (showConfirmation("Confirmar Rebaixamento", 
            "Deseja realmente rebaixar o usuário '" + usuarioSelecionado.getNome() + "' a Comum?")) {
            
            try {
                usuarioService.rebaixarUsuarioAComum(usuarioSelecionado.getId());
                showInfo("Sucesso", "Usuário '" + usuarioSelecionado.getNome() + "' rebaixado a Comum com sucesso!");
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao rebaixar usuário: " + e.getMessage());
            }
        }
    }

    /**
     * Manipula o clique no botão "Remover Usuário"
     * Remove um usuário do sistema
     */
    @FXML
    public void handleRemoverUsuario() {
        logger.info("Botão 'Remover Usuário' clicado!");
        
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        
        if (usuarios.isEmpty()) {
            showInfo("Remover Usuário", "Nenhum usuário cadastrado no sistema.");
            return;
        }
        
        // Selecionar usuário
        Usuario usuarioSelecionado = exibirDialogSelecaoUsuario(usuarios, "Remover Usuário");
        
        if (usuarioSelecionado == null) {
            return;
        }
        
        // Confirmação
        if (showConfirmation("Confirmar Remoção", 
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
            "Deseja realmente remover o usuário '" + usuarioSelecionado.getNome() + "'?")) {
            
            try {
                usuarioService.removerUsuario(usuarioSelecionado.getId());
                showInfo("Sucesso", "Usuário '" + usuarioSelecionado.getNome() + "' removido com sucesso!");
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao remover usuário: " + e.getMessage());
            }
        }
    }
    
    // --- MÉTODOS AUXILIARES ---
    
    /**
     * Exibe dialog para seleção de usuário de uma lista.
     */
    private Usuario exibirDialogSelecaoUsuario(List<Usuario> usuarios, String titulo) {
        List<String> opcoesUsuarios = new ArrayList<>();
        for (Usuario u : usuarios) {
            opcoesUsuarios.add(String.format("%d - %s (%s) - %s", 
                u.getId(), u.getNome(), u.getEmail(), u.getTipo()));
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoesUsuarios.get(0), opcoesUsuarios);
        dialog.setTitle(titulo);
        dialog.setHeaderText("Selecione o usuário:");
        dialog.setContentText("Usuário:");
        
        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String escolha = resultado.get();
            int idUsuario = Integer.parseInt(escolha.split(" - ")[0].trim());
            return usuarios.stream()
                .filter(u -> u.getId() == idUsuario)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    /**
     * Metodo auxiliar para carregar novas telas
     * @param fxmlFile Caminho do FXML
     * @param titulo Título da janela
     */
    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) BSairDoApp.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            logger.log(Level.INFO, "Tela carregada com sucesso: {0}", fxmlFile);
        } catch (IOException e) {
            showError("Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, () -> "Erro ao carregar tela: " + fxmlFile);
        }
    }

    // --- MÉTODOS AUXILIARES PARA ALERTAS ---
    
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
    
    /**
     * Exibe uma caixa de diálogo de confirmação e retorna a resposta do usuário.
     *
     * @return true se o usuário confirmar, false caso contrário
     */
    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
