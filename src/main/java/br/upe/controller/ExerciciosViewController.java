package br.upe.controller;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.ui.util.StyledAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;

public class ExerciciosViewController {

    private static final Logger logger = Logger.getLogger(ExerciciosViewController.class.getName());
    private final IExercicioService exercicioService;
    private int idUsuarioLogado = 1; // ajuste conforme autenticação do app

    @FXML private TableView<Exercicio> tableExercicios;
    @FXML private TableColumn<Exercicio, Integer> colId;
    @FXML private TableColumn<Exercicio, String> colNome;
    @FXML private TableColumn<Exercicio, String> colDescricao;
    // coluna GIF removida (agora usamos botão Visualizar)
    @FXML private TableColumn<Exercicio, Void> colAcoes;
    @FXML private Button BAddExercicio;
    @FXML private Button BVoltar;
    @FXML private Label totalLabel;

    public ExerciciosViewController() {
        this.exercicioService = new ExercicioService();
    }

    @FXML
    private void initialize() {
        // configurar colunas
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdExercicio()).asObject());
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colDescricao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescricao()));

        // ações (editar / remover)
        adicionarColunaAcoes();

        // forçar ajuste das colunas à largura da tabela
        tableExercicios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // definir um tamanho fixo por linha para evitar sobreposição visual
        tableExercicios.setFixedCellSize(56);

        // carregar dados
        loadExercicios();
        // listeners para redimensionamento / mudanças de itens
        setupResizeListeners();
    }

    private void adicionarColunaAcoes() {
        colAcoes.setCellFactory(col -> new TableCell<Exercicio, Void>() {
            private final Button btnVisualizar = new Button("Visualizar");
            private final Button btnEditar = new Button("Editar");
            private final Button btnRemover = new Button("Remover");
            private final HBox container = new HBox(8, btnVisualizar, btnEditar, btnRemover);

            {
                btnVisualizar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnEditar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");
                btnRemover.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e5a000;");

                btnVisualizar.setPrefWidth(90);
                btnEditar.setPrefWidth(70);
                btnRemover.setPrefWidth(70);

                // centralizar e padronizar margens (vertical = horizontal spacing)
                container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(6, 4, 6, 4));
                // garantir altura do container para combinar com a altura fixa da linha
                container.setPrefHeight(48);
                container.setMinHeight(48);

                // forçar altura dos botões para manter alinhamento vertical
                btnVisualizar.setPrefHeight(28);
                btnEditar.setPrefHeight(28);
                btnRemover.setPrefHeight(28);

                btnVisualizar.setOnAction(e -> {
                    Exercicio ex = getTableView().getItems().get(getIndex());
                    abrirVisualizadorExercicio(ex);
                });

                btnEditar.setOnAction(e -> {
                    Exercicio ex = getTableView().getItems().get(getIndex());
                    abrirDialogEditar(ex);
                });

                btnRemover.setOnAction(e -> {
                    Exercicio ex = getTableView().getItems().get(getIndex());
                    confirmarRemocao(ex);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void abrirVisualizadorExercicio(Exercicio exercicio) {
        try {
            String caminhoGif = exercicio.getCaminhoGif();
            if (caminhoGif == null) caminhoGif = "";
            caminhoGif = caminhoGif.replace("\\", "/");
            caminhoGif = caminhoGif.replaceFirst("^/gif/", "");
            caminhoGif = caminhoGif.replaceFirst("^gif/", "");
            caminhoGif = "/gif/" + caminhoGif;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/visualizador.fxml"));
            Parent root = loader.load();
            VisualizadorExercicioController controller = loader.getController();
            controller.exibirExercicio(caminhoGif, exercicio.getNome(), exercicio.getDescricao());

            Stage stage = new Stage();
            stage.setTitle("Visualizar Exercício - " + exercicio.getNome());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o visualizador do exercício.");
            logger.log(Level.SEVERE, "Erro ao abrir visualizador de exercício", e);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar o exercício: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao carregar dados do exercício", e);
        }
    }

    private void loadExercicios() {
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        tableExercicios.setItems(FXCollections.observableArrayList(exercicios));
        if (totalLabel != null) {
            totalLabel.setText(String.format("Total de %d exercício(s) cadastrados", exercicios.size()));
        }
        // ajustar altura preferencial da tabela para que cada item ocupe uma linha
        double fixed = tableExercicios.getFixedCellSize();
        if (fixed <= 0) fixed = 56;
        int rowsToShow = Math.max(6, exercicios.size());
        double header = 30; // estimativa para a altura do cabeçalho
        tableExercicios.setPrefHeight(fixed * rowsToShow + header);
        aplicarEstiloTableView(tableExercicios);
    }

    @FXML
    private void handleAdicionarExercicio(ActionEvent event) {
        // Reabrir o diálogo em branco (com placeholders) até o usuário cancelar
        while (true) {
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
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

            java.util.Optional<ButtonType> opt = dialog.showAndWait();
            if (opt.isEmpty() || opt.get() == ButtonType.CANCEL) {
                // usuário cancelou — encerra
                break;
            }

            // usuário clicou OK — tenta cadastrar e sempre notifica o resultado
            try {
                String nome = nomeField.getText().trim();
                String descricao = descricaoField.getText().trim();
                String caminhoGif = gifField.getText().trim();

                if (nome.isEmpty() || descricao.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos obrigatórios", "Nome e descrição são obrigatórios.");
                    // reabrir pop-up em branco (continua loop)
                    continue;
                }
                
                // Normalizar caminho do GIF
                caminhoGif = normalizarCaminhoGif(caminhoGif);

                exercicioService.cadastrarExercicio(idUsuarioLogado, nome, descricao, caminhoGif);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Exercício cadastrado com sucesso!");
                loadExercicios();
                // reabrir pop-up em branco (continua loop)

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao cadastrar exercício: " + e.getMessage());
                logger.log(Level.WARNING, "Erro ao cadastrar exercício", e);
                // reabrir pop-up em branco
            }
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        if (!br.upe.util.NavigationUtil.navigateFrom(BVoltar, "/ui/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar para a tela anterior.");
        }
    }

    private void abrirDialogEditar(Exercicio exercicio) {
        Dialog<ButtonType> dialog = criarDialogPadrao("Editar Exercício", "Editar: " + exercicio.getNome());
        GridPane grid = criarGridPadrao();

        TextField nomeField = criarCampoTexto(exercicio.getNome());
        TextField descricaoField = criarCampoTexto(exercicio.getDescricao());
        TextField gifField = criarCampoTexto(exercicio.getCaminhoGif());
        
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
                try {
                    String novoCaminhoGif = gifField.getText().isEmpty() ? null : normalizarCaminhoGif(gifField.getText());
                    
                    exercicioService.atualizarExercicio(
                            idUsuarioLogado,
                            exercicio.getNome(),
                            nomeField.getText().isEmpty() ? null : nomeField.getText(),
                            descricaoField.getText().isEmpty() ? null : descricaoField.getText(),
                            novoCaminhoGif
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Exercício atualizado com sucesso!");
                    loadExercicios();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar exercício: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao atualizar exercício", e);
                }
            }
        });
    }

    private void confirmarRemocao(Exercicio exercicio) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setHeaderText("Excluir: " + exercicio.getNome());
        confirm.setContentText("Tem certeza que deseja excluir este exercício?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    exercicioService.deletarExercicioPorNome(idUsuarioLogado, exercicio.getNome());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Exercício excluído com sucesso!");
                    loadExercicios();
                } catch (IllegalArgumentException e) {
                    // Exercício está sendo usado - oferecer opções
                    mostrarDialogExclusaoBloqueada(exercicio, e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir exercício: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao excluir exercício", e);
                }
            }
        });
    }

    private void mostrarDialogExclusaoBloqueada(Exercicio exercicio, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Exclusão Bloqueada");
        alert.setHeaderText("Não foi possível excluir o exercício");
        alert.setContentText(mensagem);
        
        // Adicionar botões personalizados
        ButtonType btnVerSessoes = new ButtonType("Ver Sessões de Treino");
        ButtonType btnExcluirForcado = new ButtonType("Excluir de Qualquer Forma");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnVerSessoes, btnExcluirForcado, btnCancelar);
        
        Optional<ButtonType> resultado = alert.showAndWait();
        
        if (resultado.isPresent()) {
            if (resultado.get() == btnVerSessoes) {
                // Navegar para a tela de sessões de treino
                navegarParaSessoes();
            } else if (resultado.get() == btnExcluirForcado) {
                confirmarExclusaoForcada(exercicio);
            }
        }
    }

    private void navegarParaSessoes() {
        if (!br.upe.util.NavigationUtil.navigateFrom(
                tableExercicios, 
                "/ui/TreinoView.fxml", 
                "Gym System - Sessões de Treino")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de sessões de treino.");
        }
    }

    private void confirmarExclusaoForcada(Exercicio exercicio) {
        Alert confirmForcado = new Alert(Alert.AlertType.WARNING);
        confirmForcado.setTitle("Confirmar Exclusão Forçada");
        confirmForcado.setHeaderText("ATENÇÃO: Exclusão Forçada");
        confirmForcado.setContentText(
            "Isso vai excluir o exercício E todas as suas referências:\n" +
            "• Será removido dos planos de treino\n" +
            "• Será removido do histórico de sessões\n\n" +
            "Esta ação NÃO PODE SER DESFEITA!\n\n" +
            "Tem certeza absoluta?"
        );
        
        ButtonType btnConfirmar = new ButtonType("Sim, Excluir Tudo", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Não, Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmForcado.getButtonTypes().setAll(btnConfirmar, btnCancelar);
        
        confirmForcado.showAndWait().ifPresent(resp -> {
            if (resp == btnConfirmar) {
                try {
                    exercicioService.deletarExercicioForcado(idUsuarioLogado, exercicio.getNome());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                        "Exercício e todas as suas referências foram excluídos!");
                    loadExercicios();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                        "Erro ao excluir exercício forçadamente: " + e.getMessage());
                    logger.log(Level.SEVERE, "Erro na exclusão forçada", e);
                }
            }
        });
    }

    // ========================= Helpers (copiados e adaptados) =========================
    
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
    
    private Dialog<javafx.scene.control.ButtonType> criarDialogPadrao(String titulo, String cabecalho) {
        Dialog<javafx.scene.control.ButtonType> dialog = new Dialog<>();
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

    private void aplicarEstiloTableView(TableView<Exercicio> tableView) {
        // Reaproveita estilo simples usado no outro controller
        tableView.setStyle("-fx-background-color: #2c2c2c; -fx-control-inner-background: #2c2c2c;");
        tableView.setRowFactory(tv -> new javafx.scene.control.TableRow<Exercicio>() {
            {
                // forçar prefHeight por linha (combinado com fixedCellSize)
                setPrefHeight(56);
                setMinHeight(56);
            }

            @Override
            protected void updateItem(Exercicio item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setStyle("");
                } else {
                    // fundo escuro + borda inferior para demarcar separação de linhas
                    setStyle(
                        "-fx-background-color: #2c2c2c; " +
                        "-fx-text-fill: #ffb300; " +
                        "-fx-border-color: transparent transparent #e6e6e6 transparent; " +
                        "-fx-border-width: 0 0 1 0; " +
                        "-fx-border-style: solid; " +
                        "-fx-border-insets: 0;"
                    );
                }
            }
        });
    }

    private void setupResizeListeners() {
        Runnable recompute = () -> Platform.runLater(() -> {
            double fixed = tableExercicios.getFixedCellSize();
            if (fixed <= 0) fixed = 56;
            int rowsToShow = Math.max(6, tableExercicios.getItems() == null ? 0 : tableExercicios.getItems().size());
            double header = 30;
            tableExercicios.setPrefHeight(fixed * rowsToShow + header);
            tableExercicios.refresh();
        });

        tableExercicios.widthProperty().addListener((obs, old, nw) -> recompute.run());
        tableExercicios.sceneProperty().addListener((obs, old, nw) -> { if (nw != null) recompute.run(); });
        for (TableColumn<Exercicio, ?> c : tableExercicios.getColumns()) {
            c.widthProperty().addListener((obs, old, nw) -> recompute.run());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        StyledAlert.mostrarAlerta(tipo, titulo, mensagem);
    }
}
