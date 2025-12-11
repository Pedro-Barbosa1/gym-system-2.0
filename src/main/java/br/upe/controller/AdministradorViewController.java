package br.upe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Usuario;
import br.upe.service.IUsuarioService;
import br.upe.service.UsuarioService;
import br.upe.ui.util.StyledAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        carregarNovaTela("/ui/MenuPrincipal.fxml", "Gym System - Menu Principal");
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
        
        // Criar Dialog
        Dialog<ButtonType> dialog = criarDialogPadrao("Lista de Todos os Usuários", 
            String.format("Total de %d usuário(s) cadastrados", usuarios.size()));
        
        // Criar TableView
        TableView<Usuario> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(usuarios));
        tableView.setPrefWidth(700);
        tableView.setPrefHeight(400);

        // Colunas
        TableColumn<Usuario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colId.setPrefWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colNome.setPrefWidth(200);

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colEmail.setPrefWidth(250);

        TableColumn<Usuario, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipo().toString()));
        colTipo.setPrefWidth(170);
        colTipo.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(colId, colNome, colEmail, colTipo);

        // Aplicar estilo
        aplicarEstiloTableView(tableView);

        dialog.getDialogPane().setContent(tableView);
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
        
        // Confirmação usando Alert puro (igual ExercicioViewController)
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Promoção");
        confirmacao.setHeaderText("Promover: " + usuarioSelecionado.getNome());
        confirmacao.setContentText("Tem certeza que deseja promover este usuário a Administrador?");
        confirmacao.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    usuarioService.promoverUsuarioAAdmin(usuarioSelecionado.getId());
                    showInfo("Sucesso", "Usuário '" + usuarioSelecionado.getNome() + "' promovido a Administrador com sucesso!");
                } catch (IllegalArgumentException e) {
                    showError("Erro", "Erro ao promover usuário: " + e.getMessage());
                }
            }
        });
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
        
        // Confirmação usando Alert puro (igual ExercicioViewController)
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Rebaixamento");
        confirmacao.setHeaderText("Rebaixar: " + usuarioSelecionado.getNome());
        confirmacao.setContentText("Tem certeza que deseja rebaixar este usuário a Comum?");
        confirmacao.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    usuarioService.rebaixarUsuarioAComum(usuarioSelecionado.getId());
                    showInfo("Sucesso", "Usuário '" + usuarioSelecionado.getNome() + "' rebaixado a Comum com sucesso!");
                } catch (IllegalArgumentException e) {
                    showError("Erro", "Erro ao rebaixar usuário: " + e.getMessage());
                }
            }
        });
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
        
        // Confirmação usando Alert puro (igual ExercicioViewController)
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover: " + usuarioSelecionado.getNome());
        confirmacao.setContentText("ATENÇÃO: Esta ação não pode ser desfeita!\n\nTem certeza que deseja remover este usuário?");
        confirmacao.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    usuarioService.removerUsuario(usuarioSelecionado.getId());
                    showInfo("Sucesso", "Usuário '" + usuarioSelecionado.getNome() + "' removido com sucesso!");
                } catch (IllegalArgumentException e) {
                    showError("Erro", "Erro ao remover usuário: " + e.getMessage());
                }
            }
        });
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
        
        Dialog<ButtonType> dialog = criarDialogPadrao(titulo, "Selecione o usuário:");
        GridPane grid = criarGridPadrao();

        Label label = criarLabel("Usuário:");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(opcoesUsuarios);
        comboBox.getSelectionModel().selectFirst();

        // Estilo do ComboBox
        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(javafx.scene.paint.Color.web("#ffb300"));
                setStyle("-fx-background-color: #222;");
            }
        });
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(javafx.scene.paint.Color.web("#ffb300"));
                setStyle("-fx-background-color: #222;");
            }
        });
        comboBox.setStyle("-fx-background-color: #222; -fx-text-fill: #ffb300;");

        grid.add(label, 0, 0);
        grid.add(comboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            int idUsuario = Integer.parseInt(comboBox.getValue().split(" - ")[0].trim());
            return usuarios.stream()
                .filter(u -> u.getId() == idUsuario)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    /**
     * Cria um Dialog padronizado com estilo escuro.
     */
    private Dialog<ButtonType> criarDialogPadrao(String titulo, String cabecalho) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecalho);
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node header = dialog.getDialogPane().lookup(".header-panel");
            if (header != null) header.setStyle("-fx-background-color: #1e1e1e;");
            Node label = dialog.getDialogPane().lookup(".header-panel .label");
            if (label != null) label.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
        });
        return dialog;
    }

    private GridPane criarGridPadrao() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.setStyle("-fx-background-color: #2c2c2c;");
        return grid;
    }

    private Label criarLabel(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
        return label;
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
        StyledAlert.showInformationAndWait(title, content);
    }

    /**
     * Exibe um alerta de erro
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     */
    private void showError(String title, String content) {
        StyledAlert.showErrorAndWait(title, content);
    }

    private void aplicarEstiloTableView(TableView<?> tableView) {
        aplicarEstiloTableViewGenerico(tableView);
    }
    
    @SuppressWarnings("unchecked")
    private <T> void aplicarEstiloTableViewGenerico(TableView<T> tableView) {
        // Aplicar estilo inline diretamente no TableView
        tableView.setStyle(
            "-fx-background-color: #2c2c2c; " +
            "-fx-control-inner-background: #2c2c2c; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 0; " +
            "-fx-table-cell-border-color: #333;"
        );
        
        // Aplicar estilo usando setRowFactory para garantir fundo escuro
        tableView.setRowFactory(tv -> {
            javafx.scene.control.TableRow<T> row = new javafx.scene.control.TableRow<>();
            row.setStyle(
                "-fx-background-color: #2c2c2c; " +
                "-fx-text-fill: #ffb300; " +
                "-fx-border-color: #333;"
            );
            
            // Atualizar estilo quando o item mudar
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.setStyle(
                        "-fx-background-color: #2c2c2c; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-border-color: #333;"
                    );
                }
            });
            
            // Estilo de seleção
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle(
                        "-fx-background-color: #5A189A; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #333;"
                    );
                } else {
                    row.setStyle(
                        "-fx-background-color: #2c2c2c; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-border-color: #333;"
                    );
                }
            });
            
            return row;
        });
        
        // Estilizar headers após a tabela ser exibida
        tableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                tableView.applyCss();
                tableView.layout();
                
                // Estilizar headers
                javafx.scene.Node headerRow = tableView.lookup(".column-header-background");
                if (headerRow != null) {
                    headerRow.setStyle("-fx-background-color: #1e1e1e;");
                }
                
                tableView.lookupAll(".column-header").forEach(node -> {
                    node.setStyle(
                        "-fx-background-color: #1e1e1e; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #333;"
                    );
                });
                
                tableView.lookupAll(".column-header .label").forEach(node -> {
                    ((javafx.scene.control.Labeled) node).setStyle(
                        "-fx-text-fill: #ffb300; " +
                        "-fx-font-weight: bold;"
                    );
                });
            }
        });
    }
}
