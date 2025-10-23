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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * Controller para a tela do Menu de Planos de Treino.
 *
 * Responsabilidades:
 * - Gerenciar ações dos botões da tela de planos de treino.
 * - Abrir telas correspondentes para criar, listar, editar, deletar planos.
 * - Exibir mensagens de informação, erro ou confirmação ao usuário.
 */
public class MenuPlanoTreinosController {

    // Logger para registrar informações e erros do controller
    private static final Logger logger = Logger.getLogger(MenuPlanoTreinosController.class.getName());

    // Services
    private IPlanoTreinoService planoTreinoService;
    private IExercicioService exercicioService;

    // ID do usuário logado
    private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login

    // --- VARIÁVEIS VINCULADAS AOS BOTÕES DO FXML ---
    @FXML
    private Button sairB; // Botão para voltar ao menu anterior

    @FXML
    private Button criarPlanoB; // Botão para criar novo plano de treino

    @FXML
    private Button ListarPlanoB; // Botão para listar planos existentes

    @FXML
    private Button editarPlanoB; // Botão para editar um plano existente

    @FXML
    private Button deletarPlanoB; // Botão para deletar um plano existente

    @FXML
    private Button adicionarExercicioAoPlanoB; // Botão para adicionar exercício a um plano

    @FXML
    private Button removerExercíciodoPlanoB; // Botão para remover exercício de um plano

    @FXML
    private Button verDetalhesDePlanoB; // Botão para visualizar detalhes de um plano

    // --- MÉTODO DE INICIALIZAÇÃO ---
    /**
     * Chamado automaticamente pelo JavaFX após o carregamento do FXML.
     * Aqui configuramos as ações dos botões.
     */
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
        editarPlanoB.setOnAction(e -> handleEditarPlano());
        deletarPlanoB.setOnAction(e -> handleDeletarPlano());
        adicionarExercicioAoPlanoB.setOnAction(e -> handleAdicionarExercicio());
        removerExercíciodoPlanoB.setOnAction(e -> handleRemoverExercicio());
        verDetalhesDePlanoB.setOnAction(e -> handleVerDetalhes());
        sairB.setOnAction(e -> handleSair());
    }

    // --- MÉTODOS DE TRATAMENTO DOS BOTÕES ---
    /**
     * Executado ao clicar em "Criar Novo Plano de Treino".
     * Abre dialog com campo para nome do plano.
     */
    @FXML
    private void handleCriarPlano() {
        logger.info("Criar Novo Plano de Treino clicado!");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Criar Novo Plano de Treino");
        dialog.setHeaderText("Preencha o nome do plano:");

        // Estilo para dialog e header
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // Criar GridPane estilizado
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #2c2c2c;");

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do plano");
        nomeField.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-text-fill: #ffb300;" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        Label labelNome = new Label("Nome:");
        labelNome.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold;");

        grid.add(labelNome, 0, 0);
        grid.add(nomeField, 1, 0);

        VBox vbox = new VBox(grid);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #2c2c2c;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String nome = nomeField.getText().trim();

            if (nome.isEmpty()) {
                showError("Erro", "O nome do plano não pode estar vazio.");
                return;
            }

            try {
                PlanoTreino novoPlano = planoTreinoService.criarPlano(idUsuarioLogado, nome);
                showInfo("Sucesso", "Plano de Treino '" + novoPlano.getNome() + "' criado com sucesso!\nID: " + novoPlano.getIdPlano());
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao criar plano: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleListarPlanos() {
        logger.info("Listar Meus Planos de Treino clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

        if (planos.isEmpty()) {
            showInfo("Meus Planos", "Nenhum plano de treino cadastrado por você ainda.");
            return;
        }

        // Criar o Dialog customizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Meus Planos de Treino");
        dialog.setHeaderText(String.format("Total de %d plano(s) de treino registrado(s)", planos.size()));

        // Estilo do Dialog e cabeçalho
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // TextArea estilizado
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);
        textArea.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-text-fill: #ffb300;" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        // Montar o texto a ser exibido
        StringBuilder sb = new StringBuilder();
        for (PlanoTreino plano : planos) {
            sb.append("═══════════════════════════════════════\n");
            sb.append(String.format("ID: %d\n", plano.getIdPlano()));
            sb.append(String.format("Nome: %s\n", plano.getNome()));
            sb.append(String.format("Exercícios no plano: %d\n", plano.getItensTreino().size()));
            sb.append("\n");
        }
        textArea.setText(sb.toString());

        // VBox de fundo para consistência visual
        VBox vbox = new VBox(textArea);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #2c2c2c;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }


    @FXML
    private void handleEditarPlano() {
        logger.info("Editar Plano de Treino clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

        if (planos.isEmpty()) {
            showInfo("Editar Plano", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano existente
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Editar Plano");
        if (planoSelecionado == null) {
            return;
        }

        // Criar Dialog estilizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Plano de Treino");
        dialog.setHeaderText("Edite o plano '" + planoSelecionado.getNome() + "'");

        // Estilo do Dialog e cabeçalho
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // GridPane para edição, com cores e espaçamento
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #2c2c2c;");

        Label labelNome = new Label("Novo Nome:");
        labelNome.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold;");

        Label labelHint = new Label("(deixe em branco para não alterar)");
        labelHint.setStyle("-fx-text-fill: #ffb30099; -fx-font-size: 12px; -fx-font-style: italic;");

        TextField nomeField = new TextField(planoSelecionado.getNome());
        nomeField.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-text-fill: #ffb300;" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        grid.add(labelNome, 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(labelHint, 0, 1, 2, 1);

        VBox vbox = new VBox(grid);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #2c2c2c;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String novoNome = nomeField.getText().trim();

            try {
                planoTreinoService.editarPlano(
                    idUsuarioLogado,
                    planoSelecionado.getNome(),
                    novoNome.isEmpty() ? null : novoNome
                );
                showInfo("Sucesso", "Plano '" + planoSelecionado.getNome() + "' atualizado com sucesso!");
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao editar plano: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeletarPlano() {
        logger.info("Deletar Plano de Treino clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

        if (planos.isEmpty()) {
            showInfo("Deletar Plano", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Deletar Plano");
        if (planoSelecionado == null) {
            return;
        }

        // Criar o Dialog de confirmação estilizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Exclusão");
        dialog.setHeaderText("Deseja realmente deletar o plano '" + planoSelecionado.getNome() + "'?");

        // Estilo do Dialog e do cabeçalho
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // Mensagem estilizada dentro do Dialog
        Label mensagem = new Label("Essa ação não poderá ser desfeita.");
        mensagem.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 14px;");

        VBox vbox = new VBox(mensagem);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #2c2c2c;");
        vbox.setAlignment(Pos.CENTER_LEFT);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Estilo dos botões
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setText("Deletar");
            okButton.setStyle(
                "-fx-background-color: #ffb300; -fx-text-fill: black; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
        }
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setText("Cancelar");
            cancelButton.setStyle(
                "-fx-background-color: #3a3a3a; -fx-text-fill: #ffb300; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
        }

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean deletado = planoTreinoService.deletarPlano(idUsuarioLogado, planoSelecionado.getNome());
                if (deletado) {
                    showInfo("Sucesso", "Plano '" + planoSelecionado.getNome() + "' deletado com sucesso!");
                } else {
                    showError("Erro", "Plano não encontrado ou não pertence a você.");
                }
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao deletar plano: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAdicionarExercicio() {
        logger.info("Adicionar Exercício ao Plano clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

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
        List<Exercicio> meusExercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);

        if (meusExercicios.isEmpty()) {
            showError("Erro", "Você não possui exercícios cadastrados. Cadastre um exercício primeiro.");
            return;
        }

        // Criar ChoiceDialog estilizado para seleção de exercício
        List<String> opcoesExercicios = new ArrayList<>();
        for (Exercicio ex : meusExercicios) {
            opcoesExercicios.add(String.format("%d - %s", ex.getIdExercicio(), ex.getNome()));
        }

        ChoiceDialog<String> dialogExercicio = new ChoiceDialog<>(opcoesExercicios.get(0), opcoesExercicios);
        dialogExercicio.setTitle("Selecionar Exercício");
        dialogExercicio.setHeaderText("Selecione o exercício para adicionar ao plano:");
        dialogExercicio.setContentText("Exercício:");

        // Estilo escuro do ChoiceDialog
        DialogPane dpExercicio = dialogExercicio.getDialogPane();
        dpExercicio.setStyle("-fx-background-color: #1e1e1e;");
        dpExercicio.lookup(".content.label").setStyle("-fx-text-fill: #ffb300;");
        Node headerLabelEx = dpExercicio.lookup(".header-panel .label");
        if (headerLabelEx != null) {
            headerLabelEx.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        Optional<String> escolhaExercicio = dialogExercicio.showAndWait();
        if (!escolhaExercicio.isPresent()) {
            return;
        }

        int idExercicio = Integer.parseInt(escolhaExercicio.get().split(" - ")[0]);

        // Criar o diálogo final para carga e repetições
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Exercício ao Plano");
        dialog.setHeaderText("Configure o exercício no plano:");

        // Estilo do Dialog e cabeçalho
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // Grid estilizado
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #2c2c2c;");

        // Campos e labels
        Label labelCarga = new Label("Carga (kg):");
        Label labelRepeticoes = new Label("Repetições:");
        labelCarga.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold;");
        labelRepeticoes.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold;");

        TextField cargaField = new TextField();
        cargaField.setPromptText("Carga em kg");
        cargaField.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-text-fill: #ffb300;" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        TextField repeticoesField = new TextField();
        repeticoesField.setPromptText("Número de repetições");
        repeticoesField.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-text-fill: #ffb300;" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        grid.add(labelCarga, 0, 0);
        grid.add(cargaField, 1, 0);
        grid.add(labelRepeticoes, 0, 1);
        grid.add(repeticoesField, 1, 1);

        VBox vbox = new VBox(grid);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #2c2c2c;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                int carga = Integer.parseInt(cargaField.getText().trim());
                int repeticoes = Integer.parseInt(repeticoesField.getText().trim());

                planoTreinoService.adicionarExercicioAoPlano(
                    idUsuarioLogado,
                    planoSelecionado.getNome(),
                    idExercicio,
                    carga,
                    repeticoes
                );

                showInfo("Sucesso", "Exercício adicionado ao plano '" + planoSelecionado.getNome() + "' com sucesso!");
            } catch (NumberFormatException e) {
                showError("Erro", "Carga e repetições devem ser números válidos.");
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao adicionar exercício: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRemoverExercicio() {
        logger.info("Remover Exercício do Plano clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

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
            if (exercicioOpt.isPresent() && exercicioOpt.get().getIdUsuario() == idUsuarioLogado) {
                nomeExercicio = exercicioOpt.get().getNome();
            }
            opcoesExercicios.add(String.format("%d - %s (Carga: %dkg, Reps: %d)", 
                item.getIdExercicio(), nomeExercicio, item.getCargaKg(), item.getRepeticoes()));
        }

        // ChoiceDialog estilizado
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoesExercicios.get(0), opcoesExercicios);
        dialog.setTitle("Remover Exercício");
        dialog.setHeaderText("Selecione o exercício para remover:");
        dialog.setContentText("Exercício:");

        // Estilo do ChoiceDialog
        DialogPane dp = dialog.getDialogPane();
        dp.setStyle("-fx-background-color: #1e1e1e;");
        dp.getContentText();
        Node headerLabel = dp.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        dp.lookup(".content.label").setStyle("-fx-text-fill: #ffb300;");
        dp.lookup(".combo-box").setStyle(
            "-fx-background-color: #2c2c2c;" +
            "-fx-text-fill: #ffb300;" +
            "-fx-border-color: #1e1e1e;"
        );

        Optional<String> escolha = dialog.showAndWait();
        if (escolha.isEmpty()) {
            return;
        }

        int idExercicio = Integer.parseInt(escolha.get().split(" - ")[0]);

        // Dialogo de confirmação personalizado
        Dialog<ButtonType> confirmDialog = new Dialog<>();
        confirmDialog.setTitle("Confirmar Remoção");
        confirmDialog.setHeaderText("Deseja realmente remover o exercício do plano?");
        confirmDialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");

        confirmDialog.setOnShown(e -> {
            Node headerPanel = confirmDialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel2 = confirmDialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel2 != null) {
                headerLabel2.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        Label mensagem = new Label("Essa ação não poderá ser desfeita.");
        mensagem.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 14px;");

        VBox vbox = new VBox(mensagem);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #2c2c2c;");
        vbox.setAlignment(Pos.CENTER_LEFT);

        confirmDialog.getDialogPane().setContent(vbox);
        confirmDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) confirmDialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setText("Remover");
            okButton.setStyle(
                "-fx-background-color: #ffb300; -fx-text-fill: black; " +
                "-fx-font-weight: bold; -fx-cursor: hand;"
            );
        }
        Button cancelButton = (Button) confirmDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setText("Cancelar");
            cancelButton.setStyle(
                "-fx-background-color: #3a3a3a; -fx-text-fill: #ffb300; " +
                "-fx-font-weight: bold; -fx-cursor: hand;"
            );
        }

        Optional<ButtonType> resultado = confirmDialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                planoTreinoService.removerExercicioDoPlano(idUsuarioLogado, planoSelecionado.getNome(), idExercicio);
                showInfo("Sucesso", "Exercício removido do plano '" + planoSelecionado.getNome() + "' com sucesso!");
            } catch (IllegalArgumentException e) {
                showError("Erro", "Erro ao remover exercício: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleVerDetalhes() {
        logger.info("Ver Detalhes do Plano clicado!");

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

        if (planos.isEmpty()) {
            showInfo("Ver Detalhes", "Você não possui planos de treino cadastrados.");
            return;
        }

        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Ver Detalhes do Plano");
        if (planoSelecionado == null) {
            return;
        }

        // Criar dialog customizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Detalhes do Plano");
        dialog.setHeaderText("Informações completas do plano:");

        // Estilo do dialog e cabeçalho
        dialog.getDialogPane().setStyle("-fx-background-color: #1e1e1e;");
        dialog.setOnShown(e -> {
            Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
            if (headerPanel != null) {
                headerPanel.setStyle("-fx-background-color: #1e1e1e;");
            }
            Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        });

        // TextArea estilizado
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(650);
        textArea.setPrefHeight(420);
        textArea.setStyle(
            "-fx-control-inner-background: #2c2c2c;" +
            "-fx-text-fill: #ffb300;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Consolas';" +
            "-fx-highlight-fill: #ffb30033;" +
            "-fx-border-color: #1e1e1e;"
        );

        // Construir relatório detalhado
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n\n");
        sb.append(String.format("ID do Plano: %d\n\n", planoSelecionado.getIdPlano()));
        sb.append(String.format("Nome: %s\n\n", planoSelecionado.getNome()));
        sb.append(String.format("ID do Usuário: %d\n\n", planoSelecionado.getIdUsuario()));
        sb.append(String.format("Total de Exercícios: %d\n\n", planoSelecionado.getItensTreino().size()));

        if (!planoSelecionado.getItensTreino().isEmpty()) {
            sb.append("Exercícios no Plano:\n");
            sb.append("───────────────────────────────────────\n");
            for (ItemPlanoTreino item : planoSelecionado.getItensTreino()) {
                Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(item.getIdExercicio());
                String nomeExercicio = "Desconhecido";
                if (exercicioOpt.isPresent() && exercicioOpt.get().getIdUsuario() == idUsuarioLogado) {
                    nomeExercicio = exercicioOpt.get().getNome();
                }
                sb.append(String.format(" • %s (ID: %d)\n", nomeExercicio, item.getIdExercicio()));
                sb.append(String.format("    Carga: %d kg\n", item.getCargaKg()));
                sb.append(String.format("    Repetições: %d\n\n", item.getRepeticoes()));
            }
        } else {
            sb.append("Nenhum exercício adicionado ainda.\n");
        }

        sb.append("\n═══════════════════════════════════════");
        textArea.setText(sb.toString());

        // Layout com fundo escuro
        VBox vbox = new VBox(textArea);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #2c2c2c;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    /**
     * Executado ao clicar no botão "Sair".
     * Tenta carregar a tela do menu do usuário logado.
     */
    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuUsuarioLogado.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Não foi possível voltar ao menu.", e);
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
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoesPlanos.get(0), opcoesPlanos);
        dialog.setTitle(titulo);
        dialog.setHeaderText("Selecione o plano de treino:");
        dialog.setContentText("Plano:");
        
        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String escolha = resultado.get();
            int idPlano = Integer.parseInt(escolha.split(" - ")[0]);
            return planos.stream()
                .filter(p -> p.getIdPlano() == idPlano)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    // --- MÉTODOS AUXILIARES PARA ALERTAS ---
    /**
     * Exibe uma caixa de diálogo de informação.
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Exibe uma caixa de diálogo de erro.
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
