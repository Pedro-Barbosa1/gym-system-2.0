package br.upe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.service.IPlanoTreinoService;
import br.upe.service.PlanoTreinoService;
import br.upe.ui.util.StyledAlert;
import br.upe.ui.util.TableViewStyler;
import br.upe.util.UserSession;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.application.Platform;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.Window;

public class MenuPlanoTreinosController {
    // Logger para registrar informações e erros do controller
    private static final Logger logger = Logger.getLogger(MenuPlanoTreinosController.class.getName());

    // Services
    private IPlanoTreinoService planoTreinoService;
    private IExercicioService exercicioService;
   
    @FXML
    private Button sairB; // Botão para voltar ao menu anterior

    @FXML
    private Button criarPlanoB; // Botão para criar novo plano de treino

    @FXML
    private Button ListarPlanoB; // Botão para listar planos existentes

    // 'Editar Plano de Treino' button removed from UI; handler kept if needed

    @FXML
    private Button deletarPlanoB; // Botão para deletar um plano existente

    @FXML
    private Button adicionarExercicioAoPlanoB; // Botão para adicionar exercício a um plano

    @FXML
    private Button removerExercíciodoPlanoB; // Botão para remover exercício de um plano

    @FXML
    private Button verDetalhesDePlanoB; // Botão para visualizar detalhes de um plano

    @FXML
    public void initialize() {
        this.planoTreinoService = new PlanoTreinoService();
        this.exercicioService = new ExercicioService();
        
        logger.info("Menu Plano Treinos inicializado com sucesso!");
        configurarAcoes(); // Associa métodos aos botões
    }

    // --- MÉTODO AUXILIAR PARA CONFIGURAR BOTÕES ---
    private void configurarAcoes() {
        criarPlanoB.setOnAction(e -> handleCriarPlano());
        ListarPlanoB.setOnAction(e -> handleListarPlanos());
        deletarPlanoB.setOnAction(e -> handleDeletarPlano());
        adicionarExercicioAoPlanoB.setOnAction(e -> handleAdicionarExercicio());
        removerExercíciodoPlanoB.setOnAction(e -> handleRemoverExercicio());
        verDetalhesDePlanoB.setOnAction(e -> handleVerDetalhes());
        sairB.setOnAction(e -> handleSair());
    }


    @FXML
    private void handleCriarPlano() {
        logger.info("Criar Novo Plano de Treino clicado!");

        Dialog<ButtonType> dialog = criarDialogPadrao("Criar Novo Plano de Treino", "Preencha o nome do plano:");

        // GridPane padronizado
        GridPane grid = criarGridPadrao();

        TextField nomeField = criarCampoTexto("Nome do plano");
        grid.add(criarLabel("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String nome = nomeField.getText().trim();

            if (nome.isEmpty()) {
                showError("Erro", "O nome do plano não pode estar vazio.");
                return;
            }

            try {
                PlanoTreino novoPlano = planoTreinoService.criarPlano(UserSession.getInstance().getIdUsuarioLogado(), nome);
                showInfo("Sucesso", "Plano de Treino '" + novoPlano.getNome() + "' criado com sucesso!\nID: " + novoPlano.getIdPlano());
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao criar plano: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleListarPlanos() {
        logger.info("Listar Meus Planos de Treino clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());

        if (planos.isEmpty()) {
            showInfo("Meus Planos", "Nenhum plano de treino cadastrado por você ainda.");
            return;
        }

        // Header para a janela de listagem
        javafx.scene.control.Label header = new javafx.scene.control.Label(
            String.format("Meus Planos de Treino — Total: %d", planos.size())
        );
        header.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold; -fx-font-size: 16px;");

        // Criar TableView
        TableView<PlanoTreino> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(planos));
        tableView.setPrefWidth(700);
        tableView.setPrefHeight(400);

        // Coluna ID
        TableColumn<PlanoTreino, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdPlano()).asObject());
        colId.setPrefWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        // Coluna Nome
        TableColumn<PlanoTreino, String> colNome = new TableColumn<>("Nome do Plano");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colNome.setPrefWidth(400);

        // Coluna Exercícios
        TableColumn<PlanoTreino, Integer> colExercicios = new TableColumn<>("Exercícios");
        colExercicios.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getItensTreino().size()).asObject());
        colExercicios.setPrefWidth(220);
        colExercicios.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(colId, colNome, colExercicios);

        // Coluna de ações (Detalhes / Excluir)
        TableColumn<PlanoTreino, Void> colAcoes = new TableColumn<>("Ações");
        colAcoes.setPrefWidth(220);
        colAcoes.setCellFactory(tc -> new javafx.scene.control.TableCell<PlanoTreino, Void>() {
            private final Button btnDetalhes = new Button("Detalhes");
            private final Button btnExcluir = new Button("Excluir");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(8, btnDetalhes, btnExcluir);

            {
                btnDetalhes.setOnAction(e -> {
                    PlanoTreino plano = getTableView().getItems().get(getIndex());
                    showDetalhesPlano(plano);
                });

                btnExcluir.setOnAction(e -> {
                    PlanoTreino plano = getTableView().getItems().get(getIndex());
                    Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacao.setTitle("Confirmar Exclusão");
                    confirmacao.setHeaderText("Deletar: " + plano.getNome());
                    confirmacao.setContentText("Tem certeza que deseja deletar este plano de treino?");
                    confirmacao.showAndWait().ifPresent(resp -> {
                        if (resp == ButtonType.OK) {
                            try {
                                boolean deletado = planoTreinoService.deletarPlano(UserSession.getInstance().getIdUsuarioLogado(), plano.getNome());
                                if (deletado) {
                                    getTableView().getItems().remove(plano);
                                    showInfo("Sucesso", "Plano '" + plano.getNome() + "' deletado com sucesso!");
                                } else {
                                    showError("Erro", "Plano não encontrado ou não pertence a você.");
                                }
                            } catch (IllegalArgumentException ex) {
                                showError("Erro", "Erro ao deletar plano: " + ex.getMessage());
                            }
                        }
                    });
                });
                box.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 4;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        tableView.getColumns().add(colAcoes);

        // Aplicar estilo dark theme
        aplicarEstiloTableView(tableView);

        // Criar layout com botão 'Adicionar' acima e 'Voltar' abaixo
        javafx.scene.control.Button btnAdicionar = new javafx.scene.control.Button("Adicionar");
        btnAdicionar.setOnAction(e -> {
            handleCriarPlano();
            // Atualizar lista após possível criação
            List<PlanoTreino> atualizados = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());
            tableView.setItems(FXCollections.observableArrayList(atualizados));
        });

        javafx.scene.control.Button btnVoltar = new javafx.scene.control.Button("Voltar");
        // O handler será associado ao Stage criado adiante (fechará a janela)

        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(8);
        container.setPadding(new Insets(10));
        container.getChildren().addAll(btnAdicionar, tableView, btnVoltar);

        // Criar Stage modal
        Stage stage = new Stage();
        try {
            if (sairB != null && sairB.getScene() != null) {
                stage.initOwner(sairB.getScene().getWindow());
            }
        } catch (Exception ex) {
            logger.log(Level.FINE, "Não foi possível inicializar owner do Stage de listar planos", ex);
        }
        stage.initModality(Modality.WINDOW_MODAL);

        // Montar cena com header + container
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(header, container);

        Scene scene = new Scene(root, 760, 520);
        scene.getStylesheets().addAll();
        stage.setScene(scene);
        stage.setTitle("Meus Planos de Treino");

        // Garantir que o botão Voltar fecha o Stage
        btnVoltar.setOnAction(e -> stage.close());

        // Mostrar de maneira modal e esperar até fechar
        stage.showAndWait();
    }

    // 'Editar Plano' action removed — method deleted because UI no longer exposes it.

    @FXML
    private void handleDeletarPlano() {
        logger.info("Deletar Plano de Treino clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());

        if (planos.isEmpty()) {
            showInfo("Deletar Plano", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Deletar Plano");
        if (planoSelecionado == null) {
            return;
        }

        // Confirmação usando Alert puro (igual ExercicioViewController)
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deletar: " + planoSelecionado.getNome());
        confirmacao.setContentText("Tem certeza que deseja deletar este plano de treino?");
        confirmacao.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    boolean deletado = planoTreinoService.deletarPlano(UserSession.getInstance().getIdUsuarioLogado(), planoSelecionado.getNome());
                    if (deletado) {
                        showInfo("Sucesso", "Plano '" + planoSelecionado.getNome() + "' deletado com sucesso!");
                    } else {
                        showError("Erro", "Plano não encontrado ou não pertence a você.");
                    }
                } catch (IllegalArgumentException e) {
                    showError("Erro", "Erro ao deletar plano: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleAdicionarExercicio() {
        logger.info("Adicionar Exercício ao Plano clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());

        if (planos.isEmpty()) {
            showInfo("Adicionar Exercício", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Adicionar Exercício ao Plano");
        if (planoSelecionado == null) {
            return;
        }

        // Listar exercícios do usuário
        List<Exercicio> meusExercicios = exercicioService.listarExerciciosDoUsuario(UserSession.getInstance().getIdUsuarioLogado());

        if (meusExercicios.isEmpty()) {
            showError("Erro", "Você não possui exercícios cadastrados. Cadastre um exercício primeiro.");
            return;
        }

        // Selecionar exercício usando Dialog com ComboBox (igual ExercicioViewController)
        List<String> opcoesExercicios = new ArrayList<>();
        for (Exercicio ex : meusExercicios) {
            opcoesExercicios.add(String.format("%d - %s", ex.getIdExercicio(), ex.getNome()));
        }

        Dialog<ButtonType> dialogExercicio = criarDialogPadrao("Selecionar Exercício", 
            "Selecione o exercício para adicionar ao plano:");
        GridPane gridExercicio = criarGridPadrao();

        Label labelExercicio = criarLabel("Exercício:");
        ComboBox<String> comboBoxExercicio = new ComboBox<>();
        comboBoxExercicio.getItems().addAll(opcoesExercicios);
        comboBoxExercicio.getSelectionModel().selectFirst();

        // Estilo do ComboBox
        comboBoxExercicio.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(javafx.scene.paint.Color.web("#ffb300"));
                setStyle("-fx-background-color: #222;");
            }
        });
        comboBoxExercicio.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(javafx.scene.paint.Color.web("#ffb300"));
                setStyle("-fx-background-color: #222;");
            }
        });
        comboBoxExercicio.setStyle("-fx-background-color: #222; -fx-text-fill: #ffb300;");

        gridExercicio.add(labelExercicio, 0, 0);
        gridExercicio.add(comboBoxExercicio, 1, 0);

        dialogExercicio.getDialogPane().setContent(gridExercicio);
        dialogExercicio.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultadoExercicio = dialogExercicio.showAndWait();
        if (!resultadoExercicio.isPresent() || resultadoExercicio.get() != ButtonType.OK) {
            return;
        }

        int idExercicio = Integer.parseInt(comboBoxExercicio.getValue().split(" - ")[0]);

        // Adicionar exercício diretamente sem solicitar carga e repetições
        try {
            planoTreinoService.adicionarExercicioAoPlano(
                UserSession.getInstance().getIdUsuarioLogado(),
                planoSelecionado.getNome(),
                idExercicio
            );

            showInfo("Sucesso", "Exercício adicionado ao plano '" + planoSelecionado.getNome() + "' com sucesso!\n\nCarga e repetições serão definidas durante a sessão de treino.");
        } catch (IllegalArgumentException e) {
            showError("Erro", "Erro ao adicionar exercício: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoverExercicio() {
        logger.info("Remover Exercício do Plano clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());

        if (planos.isEmpty()) {
            showInfo("Remover Exercício", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Remover Exercício do Plano");
        if (planoSelecionado == null) {
            return;
        }

        if (planoSelecionado.getItensTreino().isEmpty()) {
            showInfo("Remover Exercício", "Este plano não possui exercícios para remover.");
            return;
        }

        // Criar lista de opções de exercícios
        List<String> opcoesExercicios = new ArrayList<>();
        for (ItemPlanoTreino item : planoSelecionado.getItensTreino()) {
            Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
            String nomeExercicio = "Desconhecido";
            if (exercicioOpt.isPresent() && exercicioOpt.get().getIdUsuario() == UserSession.getInstance().getIdUsuarioLogado()) {
                nomeExercicio = exercicioOpt.get().getNome();
            }
            opcoesExercicios.add(String.format("%d - %s (Carga: %dkg, Reps: %d)", 
                item.getIdExercicio(), nomeExercicio, item.getCargaKg(), item.getRepeticoes()));
        }

        // Selecionar exercício usando Dialog com ComboBox (igual ExercicioViewController)
        Dialog<ButtonType> dialogExercicio = criarDialogPadrao("Remover Exercício", 
            "Selecione o exercício para remover:");
        GridPane gridExercicio = criarGridPadrao();

        Label labelExercicio = criarLabel("Exercício:");
        ComboBox<String> comboBoxExercicio = new ComboBox<>();
        comboBoxExercicio.getItems().addAll(opcoesExercicios);
        comboBoxExercicio.getSelectionModel().selectFirst();

        // Estilo do ComboBox
        comboBoxExercicio.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(javafx.scene.paint.Color.web("#ffb300"));
                setStyle("-fx-background-color: #222;");
            }
        });
        comboBoxExercicio.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(javafx.scene.paint.Color.web("#ffb300"));
                setStyle("-fx-background-color: #222;");
            }
        });
        comboBoxExercicio.setStyle("-fx-background-color: #222; -fx-text-fill: #ffb300;");

        gridExercicio.add(labelExercicio, 0, 0);
        gridExercicio.add(comboBoxExercicio, 1, 0);

        dialogExercicio.getDialogPane().setContent(gridExercicio);
        dialogExercicio.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultadoExercicio = dialogExercicio.showAndWait();
        if (!resultadoExercicio.isPresent() || resultadoExercicio.get() != ButtonType.OK) {
            return;
        }

        int idExercicio = Integer.parseInt(comboBoxExercicio.getValue().split(" - ")[0]);

        // Confirmação usando Alert puro (igual ExercicioViewController)
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover exercício do plano");
        confirmacao.setContentText("Tem certeza que deseja remover este exercício do plano?");
        confirmacao.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    planoTreinoService.removerExercicioDoPlano(UserSession.getInstance().getIdUsuarioLogado(), planoSelecionado.getNome(), idExercicio);
                    showInfo("Sucesso", "Exercício removido do plano '" + planoSelecionado.getNome() + "' com sucesso!");
                } catch (IllegalArgumentException e) {
                    showError("Erro", "Erro ao remover exercício: " + e.getMessage());
                }
            }
        });
    }


    @FXML
    private void handleVerDetalhes() {
        logger.info("Ver Detalhes do Plano clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(UserSession.getInstance().getIdUsuarioLogado());

        if (planos.isEmpty()) {
            showInfo("Ver Detalhes", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Ver Detalhes do Plano");
        if (planoSelecionado == null) {
            return;
        }

        // Usar o método showDetalhesPlano que sempre exibe a tabela
        showDetalhesPlano(planoSelecionado);
    }



    /**
     * Mostra os detalhes do plano fornecido (versão que recebe o PlanoTreino diretamente).
     */
    private void showDetalhesPlano(PlanoTreino planoSelecionado) {
        if (planoSelecionado == null) return;

        Dialog<ButtonType> dialog = criarDialogPadrao("Detalhes do Plano", 
            String.format("Plano: %s (ID: %d) | %d exercício(s)", 
                planoSelecionado.getNome(), 
                planoSelecionado.getIdPlano(), 
                planoSelecionado.getItensTreino().size()));

        if (planoSelecionado.getItensTreino().isEmpty()) {
            TextArea textArea = criarTextArea(650, 420);
            textArea.setText("Nenhum exercício adicionado ainda ao plano \"" + planoSelecionado.getNome() + "\".");
            dialog.getDialogPane().setContent(textArea);
        } else {
            List<ExercicioPlanoData> dadosTabela = new ArrayList<>();
            for (ItemPlanoTreino item : planoSelecionado.getItensTreino()) {
                Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
                String nomeExercicio = "Desconhecido";
                if (exercicioOpt.isPresent() && exercicioOpt.get().getIdUsuario() == UserSession.getInstance().getIdUsuarioLogado()) {
                    nomeExercicio = exercicioOpt.get().getNome();
                }
                dadosTabela.add(new ExercicioPlanoData(
                    item.getIdExercicio(),
                    nomeExercicio,
                    item.getCargaKg(),
                    item.getRepeticoes()
                ));
            }

            TableView<ExercicioPlanoData> tableView = new TableView<>();
            tableView.setItems(FXCollections.observableArrayList(dadosTabela));
            tableView.setPrefWidth(650);
            tableView.setPrefHeight(420);

            TableColumn<ExercicioPlanoData, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdExercicio()).asObject());
            colId.setPrefWidth(80);
            colId.setStyle("-fx-alignment: CENTER;");

            TableColumn<ExercicioPlanoData, String> colNome = new TableColumn<>("Exercício");
            colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
            colNome.setPrefWidth(320);

            TableColumn<ExercicioPlanoData, Integer> colCarga = new TableColumn<>("Carga (kg)");
            colCarga.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCargaKg()).asObject());
            colCarga.setPrefWidth(120);
            colCarga.setStyle("-fx-alignment: CENTER;");

            TableColumn<ExercicioPlanoData, Integer> colRepeticoes = new TableColumn<>("Repetições");
            colRepeticoes.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRepeticoes()).asObject());
            colRepeticoes.setPrefWidth(130);
            colRepeticoes.setStyle("-fx-alignment: CENTER;");

            tableView.getColumns().addAll(colId, colNome, colCarga, colRepeticoes);

            aplicarEstiloTableView(tableView);

            dialog.getDialogPane().setContent(tableView);
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // Classe auxiliar para dados dos exercícios do plano
    private static class ExercicioPlanoData {
        private final int idExercicio;
        private final String nome;
        private final int cargaKg;
        private final int repeticoes;

        public ExercicioPlanoData(int idExercicio, String nome, int cargaKg, int repeticoes) {
            this.idExercicio = idExercicio;
            this.nome = nome;
            this.cargaKg = cargaKg;
            this.repeticoes = repeticoes;
        }

        public int getIdExercicio() { return idExercicio; }
        public String getNome() { return nome; }
        public int getCargaKg() { return cargaKg; }
        public int getRepeticoes() { return repeticoes; }
    }

    /**
     * Executado ao clicar no botão "Sair".
     * Tenta carregar a tela do menu do usuário logado.
     */
    @FXML
    private void handleSair() {
        if (!br.upe.util.NavigationUtil.navigateFrom(sairB, "/ui/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário")) {
            showError("Erro", "Não foi possível voltar ao menu.");
        }
    }

    // --- MÉTODOS AUXILIARES ---
    /**
     * Exibe dialog para seleção de plano de treino de uma lista.
     */
    private PlanoTreino exibirDialogSelecaoPlano(List<PlanoTreino> planos, String titulo) {
        List<String> opcoesPlanos = new ArrayList<>();
        for (PlanoTreino plano : planos) {
            opcoesPlanos.add(String.format("%d - %s (%d exercícios)", 
                plano.getIdPlano(), plano.getNome(), plano.getItensTreino().size()));
        }
        
        Dialog<ButtonType> dialog = criarDialogPadrao(titulo, "Selecione o plano de treino:");
        GridPane grid = criarGridPadrao();

        Label label = criarLabel("Plano:");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(opcoesPlanos);
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
            int idPlano = Integer.parseInt(comboBox.getValue().split(" - ")[0]);
            return planos.stream()
                .filter(p -> p.getIdPlano() == idPlano)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    // --- MÉTODOS AUXILIARES PARA ALERTAS ---
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

    private TextField criarCampoTexto(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle(
            "-fx-text-fill: #ffb300;" +
            "-fx-background-color: #333;" +
            "-fx-border-color: #1e1e1e;" +
            "-fx-prompt-text-fill: #888888;"  // Texto do placeholder em cinza
        );
        return field;
    }

    private TextArea criarTextArea(int largura, int altura) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(largura);
        textArea.setPrefHeight(altura);
        textArea.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-background-color: #2c2c2c;" +
            "-fx-text-fill: #ffb300;" +
            "-fx-border-color: transparent;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;" +
            "-fx-padding: 0;"
        );
        return textArea;
    }

    private void aplicarEstiloTableView(TableView<?> tableView) {
        TableViewStyler.aplicarEstilo(tableView);
    }

    /**
     * Exibe uma caixa de diálogo de informação.
     */
    private void showInfo(String title, String content) {
        StyledAlert.showInformationAndWait(title, content);
    }

    /**
     * Exibe uma caixa de diálogo de erro.
     */
    private void showError(String title, String content) {
        StyledAlert.showErrorAndWait(title, content);
    }
}
