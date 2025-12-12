package br.upe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.ui.util.StyledAlert;
import br.upe.ui.util.TableViewStyler;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ExercicioViewController {

    private static final Logger logger = Logger.getLogger(ExercicioViewController.class.getName());
    private final IExercicioService exercicioService;
    private int idUsuarioLogado = 1; 

    @FXML private Button BCadastrarEX;
    @FXML private Button BDetalhesEX;
    @FXML private Button BEditarEX;
    @FXML private Button BExcluirEX;
    @FXML private Button BListarEX;
    @FXML private Button BVisualizarEX1;
    @FXML private Button IFechar;
    @FXML private ImageView IMenu;

    public ExercicioViewController() {
        this.exercicioService = new ExercicioService();
    }

    // ============================================================
    //                     AÇÕES DOS BOTÕES
    // ============================================================

    @FXML
    void handleCadastrarExercicio(ActionEvent event) {
        logger.info("Abrindo diálogo para cadastrar novo exercício...");

        Dialog<ButtonType> dialog = criarDialogPadrao("Cadastrar Novo Exercício", "Preencha os dados do exercício");
        GridPane grid = criarGridPadrao();

        TextField nomeField = criarCampoTexto("Ex: Supino Reto");
        TextField descricaoField = criarCampoTexto("Ex: Exercício para peito");
        TextField gifField = criarCampoTexto("Ex: supino_reto_barra.gif");
        
        // Auto-sugerir GIF quando o nome mudar
        nomeField.textProperty().addListener((obs, oldVal, newVal) -> {
            String sugestao = sugerirCaminhoGif(newVal);
            if (sugestao != null) {
                gifField.setText(sugestao);
            }
        });

        grid.add(criarLabel("Nome do Exercício:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(criarLabel("Descrição:"), 0, 1);
        grid.add(descricaoField, 1, 1);
        grid.add(criarLabel("Caminho do GIF:"), 0, 2);
        grid.add(gifField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String nome = nomeField.getText().trim();
                    String descricao = descricaoField.getText().trim();
                    String caminhoGif = gifField.getText().trim();

                    if (nome.isEmpty() || descricao.isEmpty()) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Campos obrigatórios", "Nome e descrição são obrigatórios.");
                        return;
                    }
                    
                    // Normalizar caminho do GIF
                    caminhoGif = normalizarCaminhoGif(caminhoGif);

                    Exercicio novo = exercicioService.cadastrarExercicio(idUsuarioLogado, nome, descricao, caminhoGif);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Exercício '" + novo.getNome() + "' cadastrado com sucesso!");

                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao cadastrar exercício: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao cadastrar exercício", e);
                }
            }
        });
    }

    @FXML
    void handleListarExercicios(ActionEvent event) {
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sem Exercícios", "Você ainda não cadastrou nenhum exercício.");
            return;
        }

        Dialog<ButtonType> dialog = criarDialogPadrao("Meus Exercícios",
                String.format("Total de %d exercício(s) cadastrados", exercicios.size()));

        // Criar TableView com tema dark
        TableView<Exercicio> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(exercicios));
        tableView.setPrefWidth(700);
        tableView.setPrefHeight(400);

        // Coluna ID
        TableColumn<Exercicio, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdExercicio()).asObject());
        colId.setPrefWidth(60);
        colId.setStyle("-fx-alignment: CENTER;");

        // Coluna Nome
        TableColumn<Exercicio, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colNome.setPrefWidth(200);

        // Coluna Descrição
        TableColumn<Exercicio, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescricao()));
        colDescricao.setPrefWidth(300);

        // Coluna GIF
        TableColumn<Exercicio, String> colGif = new TableColumn<>("Caminho GIF");
        colGif.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCaminhoGif()));
        colGif.setPrefWidth(140);

        tableView.getColumns().addAll(colId, colNome, colDescricao, colGif);

        // Aplicar estilo dark theme
        aplicarEstiloTableView(tableView);

        dialog.getDialogPane().setContent(tableView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    void handleEditarExercicio(ActionEvent event) {
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", "Você não possui exercícios para editar.");
            return;
        }

        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Editar Exercício");
        if (exercicioSelecionado == null) return;

        Dialog<ButtonType> dialog = criarDialogPadrao("Editar Exercício",
                "Editar: " + exercicioSelecionado.getNome());
        GridPane grid = criarGridPadrao();

        TextField nomeField = criarCampoTexto(exercicioSelecionado.getNome());
        TextField descricaoField = criarCampoTexto(exercicioSelecionado.getDescricao());
        TextField gifField = criarCampoTexto(exercicioSelecionado.getCaminhoGif());
        
        // Auto-sugerir GIF quando o nome mudar
        nomeField.textProperty().addListener((obs, oldVal, newVal) -> {
            String sugestao = sugerirCaminhoGif(newVal);
            if (sugestao != null) {
                gifField.setText(sugestao);
            }
        });

        grid.add(criarLabel("Novo Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(criarLabel("Nova Descrição:"), 0, 1);
        grid.add(descricaoField, 1, 1);
        grid.add(criarLabel("Novo Caminho GIF:"), 0, 2);
        grid.add(gifField, 1, 2);
        grid.add(criarLabel("(Deixe em branco para não alterar)"), 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String novoCaminhoGif = gifField.getText().isEmpty() ? null : normalizarCaminhoGif(gifField.getText());
                
                exercicioService.atualizarExercicio(
                        idUsuarioLogado,
                        exercicioSelecionado.getNome(),
                        nomeField.getText().isEmpty() ? null : nomeField.getText(),
                        descricaoField.getText().isEmpty() ? null : descricaoField.getText(),
                        novoCaminhoGif
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Exercício atualizado com sucesso!");
            }
        });
    }

    @FXML
    void handleExcluirExercicio(ActionEvent event) {
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", "Você não possui exercícios para excluir.");
            return;
        }

        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Excluir Exercício");
        if (exercicioSelecionado == null) return;

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir: " + exercicioSelecionado.getNome());
        confirmacao.setContentText("Tem certeza que deseja excluir este exercício?");
        confirmacao.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                exercicioService.deletarExercicioPorNome(idUsuarioLogado, exercicioSelecionado.getNome());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Exercício excluído com sucesso!");
            }
        });
    }

    @FXML
    void handleVerDetalhesExercicio(ActionEvent event) {
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", "Você não possui exercícios cadastrados.");
            return;
        }

        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Ver Detalhes");
        if (exercicioSelecionado == null) return;

        Dialog<ButtonType> dialog = criarDialogPadrao("Detalhes do Exercício",
                String.format("%s (ID: %d)", exercicioSelecionado.getNome(), exercicioSelecionado.getIdExercicio()));

        // Criar TableView para exibir os detalhes
        TableView<String[]> tableView = new TableView<>();
        tableView.setPrefWidth(500);
        tableView.setPrefHeight(250);

        // Preparar dados
        List<String[]> dadosDetalhes = new ArrayList<>();
        dadosDetalhes.add(new String[]{"ID", String.valueOf(exercicioSelecionado.getIdExercicio())});
        dadosDetalhes.add(new String[]{"Nome", exercicioSelecionado.getNome()});
        dadosDetalhes.add(new String[]{"Descrição", exercicioSelecionado.getDescricao()});
        dadosDetalhes.add(new String[]{"Caminho GIF", exercicioSelecionado.getCaminhoGif()});
        dadosDetalhes.add(new String[]{"ID Usuário", String.valueOf(exercicioSelecionado.getIdUsuario())});

        tableView.setItems(FXCollections.observableArrayList(dadosDetalhes));

        // Colunas
        TableColumn<String[], String> colCampo = new TableColumn<>("Campo");
        colCampo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colCampo.setPrefWidth(150);
        colCampo.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold;");

        TableColumn<String[], String> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        colValor.setPrefWidth(350);

        tableView.getColumns().addAll(colCampo, colValor);

        // Aplicar estilo
        aplicarEstiloTableView(tableView);

        dialog.getDialogPane().setContent(tableView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    void handleVisualizarExercicio(ActionEvent event) {
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", "Você não possui exercícios cadastrados.");
            return;
        }

        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Visualizar Exercício");
        if (exercicioSelecionado == null) return;

        try {
            // Normalizar o caminho do GIF
            String caminhoGif = exercicioSelecionado.getCaminhoGif();
            caminhoGif = caminhoGif.replace("\\", "/");
            caminhoGif = caminhoGif.replaceFirst("^/gif/", "");
            caminhoGif = caminhoGif.replaceFirst("^gif/", "");
            caminhoGif = "/gif/" + caminhoGif;
            
            logger.info("Abrindo visualizador para o exercício: " + exercicioSelecionado.getNome());
            
            // Carregar o FXML do visualizador
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/visualizador.fxml"));
            Parent root = loader.load();
            
            // Obter o controller e passar os dados do exercício
            VisualizadorExercicioController controller = loader.getController();
            controller.exibirExercicio(caminhoGif, exercicioSelecionado.getNome(), exercicioSelecionado.getDescricao());
            
            // Criar nova janela (Stage)
            Stage stage = new Stage();
            stage.setTitle("Visualizar Exercício - " + exercicioSelecionado.getNome());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao abrir visualizador de exercício", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o visualizador do exercício.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar dados do exercício", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar o exercício: " + e.getMessage());
        }
    }

    @FXML
    void handleAbrirMenu(ActionEvent event) {
        carregarNovaTela("/ui/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
    }

    // ============================================================
    //                     MÉTODOS AUXILIARES
    // ============================================================
    
    /**
     * Sugere um caminho de GIF baseado no nome do exercício.
     * Mapeia nomes conhecidos para os GIFs disponíveis na pasta resources/gif/
     */
    private String sugerirCaminhoGif(String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            return null;
        }
        
        String nomeLower = nomeExercicio.toLowerCase().trim();
        
        // Mapeamento dos 3 GIFs disponíveis
        if (nomeLower.contains("agachamento") || nomeLower.contains("squat")) {
            return "agachamento_livre.gif";
        } else if (nomeLower.contains("remada") || nomeLower.contains("row")) {
            return "remada_curvada.gif";
        } else if (nomeLower.contains("supino") || nomeLower.contains("bench") || nomeLower.contains("press")) {
            return "supino_reto_barra.gif";
        }
        
        return null;
    }
    
    /**
     * Normaliza o caminho do GIF removendo barras e prefixos desnecessários.
     * Garante que o caminho seja apenas o nome do arquivo.
     */
    private String normalizarCaminhoGif(String caminhoGif) {
        if (caminhoGif == null || caminhoGif.trim().isEmpty()) {
            return "";
        }
        
        String normalizado = caminhoGif.replace("\\", "/");
        normalizado = normalizado.replaceFirst("^/gif/", "");
        normalizado = normalizado.replaceFirst("^gif/", "");
        normalizado = normalizado.replaceFirst("^/", "");
        
        return normalizado.trim();
    }

    private Exercicio exibirDialogSelecaoExercicio(List<Exercicio> exercicios, String titulo) {
        List<String> opcoes = new ArrayList<>();
        for (Exercicio ex : exercicios) {
            opcoes.add(ex.getIdExercicio() + " - " + ex.getNome());
        }

        Dialog<ButtonType> dialog = criarDialogPadrao(titulo, "Selecione o exercício:");
        GridPane grid = criarGridPadrao();

        Label label = criarLabel("Exercício:");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(opcoes);
        comboBox.getSelectionModel().selectFirst();

        // === Estilo igual ao TreinoViewController ===
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
            int id = Integer.parseInt(comboBox.getValue().split(" - ")[0]);
            return exercicios.stream().filter(e -> e.getIdExercicio() == id).findFirst().orElse(null);
        }
        return null;
    }

    private void carregarNovaTela(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) BCadastrarEX.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxml, e);
        }
    }

    // ============================================================
    //                     ESTILO PADRONIZADO
    // ============================================================

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

    private TextField criarCampoTexto(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle("-fx-text-fill: #ffb300; -fx-background-color: #333; -fx-border-color: #1e1e1e;");
        return field;
    }

    private void aplicarEstiloTableView(TableView<?> tableView) {
        TableViewStyler.aplicarEstilo(tableView);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        StyledAlert.mostrarAlerta(tipo, titulo, mensagem);
    }
}
