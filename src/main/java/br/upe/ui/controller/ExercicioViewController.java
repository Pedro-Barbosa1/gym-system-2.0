package br.upe.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class ExercicioViewController {

    private static final Logger logger = Logger.getLogger(ExercicioViewController.class.getName());

    private final IExercicioService exercicioService;
    
    private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login

    @FXML
    private Button BCadastrarEX;      // Botão para cadastrar novo exercício

    @FXML
    private Button BDetalhesEX;       // Botão para visualizar detalhes de um exercício

    @FXML
    private Button BEditarEX;         // Botão para editar um exercício existente

    @FXML
    private Button BExcluirEX;        // Botão para excluir um exercício

    @FXML
    private Button BListarEX;         // Botão para listar todos os exercícios

    @FXML
    private ImageView IFechar;        // Ícone para fechar a tela

    @FXML
    private ImageView IMenu;          // Ícone para abrir o menu principal

    public ExercicioViewController() {
        this.exercicioService = new ExercicioService();
    }

    @FXML
    void handleCadastrarExercicio(ActionEvent event) {
        logger.info("Abrindo dialog para cadastrar novo exercício...");
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Novo Exercício");
        dialog.setHeaderText("Preencha os dados do exercício");

        // Criar grid para os campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos do formulário
        TextField nomeField = new TextField();
        nomeField.setPromptText("Ex: Supino Reto");
        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Ex: Exercício para peito");
        TextField gifField = new TextField();
        gifField.setPromptText("Ex: supino.gif");

        grid.add(new Label("Nome do Exercício:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descricaoField, 1, 1);
        grid.add(new Label("Caminho do GIF:"), 0, 2);
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
                        mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", 
                            "Nome e descrição são obrigatórios!");
                        return;
                    }

                    Exercicio novo = exercicioService.cadastrarExercicio(
                        idUsuarioLogado, nome, descricao, caminhoGif
                    );

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                        "Exercício '" + novo.getNome() + "' cadastrado com sucesso!");
                    logger.info("Exercício cadastrado: " + novo);

                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                        "Erro ao cadastrar exercício: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao cadastrar exercício", e);
                }
            }
        });
    }

    @FXML
    void handleListarExercicios(ActionEvent event) {
        logger.info("Listando exercícios do usuário...");
        
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sem Exercícios", 
                "Você ainda não cadastrou nenhum exercício.");
            return;
        }

        // Criar dialog com área de texto para mostrar os exercícios
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Meus Exercícios");
        dialog.setHeaderText(String.format("Total de %d exercício(s) cadastrado(s)", exercicios.size()));

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);
        
        StringBuilder sb = new StringBuilder();
        for (Exercicio ex : exercicios) {
            sb.append("═══════════════════════════════════════\n");
            sb.append(String.format("ID: %d\n", ex.getIdExercicio()));
            sb.append(String.format("Nome: %s\n", ex.getNome()));
            sb.append(String.format("Descrição: %s\n", ex.getDescricao()));
            sb.append(String.format("GIF: %s\n", ex.getCaminhoGif()));
            sb.append("\n");
        }
        
        textArea.setText(sb.toString());
        
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    void handleEditarExercicio(ActionEvent event) {
        logger.info("Iniciando edição de exercício...");
        
        // Buscar exercícios do usuário
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", 
                "Você não possui exercícios cadastrados para editar.");
            return;
        }

        // Dialog para selecionar exercício
        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Editar Exercício");
        if (exercicioSelecionado == null) {
            return;
        }

        // Dialog para editar os dados
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Exercício");
        dialog.setHeaderText("Editar: " + exercicioSelecionado.getNome());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomeField = new TextField(exercicioSelecionado.getNome());
        TextField descricaoField = new TextField(exercicioSelecionado.getDescricao());
        TextField gifField = new TextField(exercicioSelecionado.getCaminhoGif());

        grid.add(new Label("Novo Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Nova Descrição:"), 0, 1);
        grid.add(descricaoField, 1, 1);
        grid.add(new Label("Novo Caminho GIF:"), 0, 2);
        grid.add(gifField, 1, 2);
        grid.add(new Label("(Deixe em branco para não alterar)"), 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String novoNome = nomeField.getText().trim();
                    String novaDescricao = descricaoField.getText().trim();
                    String novoGif = gifField.getText().trim();

                    exercicioService.atualizarExercicio(
                        idUsuarioLogado, 
                        exercicioSelecionado.getNome(),
                        novoNome.isEmpty() ? null : novoNome,
                        novaDescricao.isEmpty() ? null : novaDescricao,
                        novoGif.isEmpty() ? null : novoGif
                    );

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                        "Exercício atualizado com sucesso!");
                    logger.info("Exercício atualizado: " + exercicioSelecionado.getNome());

                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                        "Erro ao atualizar exercício: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao atualizar exercício", e);
                }
            }
        });
    }

    @FXML
    void handleExcluirExercicio(ActionEvent event) {
        logger.info("Iniciando exclusão de exercício...");
        
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", 
                "Você não possui exercícios cadastrados para excluir.");
            return;
        }

        // Dialog para selecionar exercício
        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Excluir Exercício");
        if (exercicioSelecionado == null) {
            return;
        }

        // Confirmação de exclusão
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir: " + exercicioSelecionado.getNome());
        confirmacao.setContentText("Tem certeza que deseja excluir este exercício?\nEsta ação não pode ser desfeita.");

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deletado = exercicioService.deletarExercicioPorNome(
                        idUsuarioLogado, exercicioSelecionado.getNome()
                    );

                    if (deletado) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                            "Exercício '" + exercicioSelecionado.getNome() + "' excluído com sucesso!");
                        logger.info("Exercício excluído: " + exercicioSelecionado.getNome());
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                            "Não foi possível excluir o exercício.");
                    }

                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                        "Erro ao excluir exercício: " + e.getMessage());
                    logger.log(Level.WARNING, "Erro ao excluir exercício", e);
                }
            }
        });
    }

    @FXML
    void handleVerDetalhesExercicio(ActionEvent event) {
        logger.info("Visualizando detalhes de exercício...");
        
        List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(idUsuarioLogado);
        
        if (exercicios.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sem Exercícios", 
                "Você não possui exercícios cadastrados.");
            return;
        }

        // Dialog para selecionar exercício
        Exercicio exercicioSelecionado = exibirDialogSelecaoExercicio(exercicios, "Ver Detalhes");
        if (exercicioSelecionado == null) {
            return;
        }

        // Dialog com detalhes completos
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Detalhes do Exercício");
        dialog.setHeaderText(exercicioSelecionado.getNome());

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(300);
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n\n");
        sb.append(String.format("ID: %d\n\n", exercicioSelecionado.getIdExercicio()));
        sb.append(String.format("Nome: %s\n\n", exercicioSelecionado.getNome()));
        sb.append(String.format("Descrição:\n%s\n\n", exercicioSelecionado.getDescricao()));
        sb.append(String.format("Caminho do GIF: %s\n\n", exercicioSelecionado.getCaminhoGif()));
        sb.append(String.format("ID do Usuário: %d\n", exercicioSelecionado.getIdUsuario()));
        sb.append("\n═══════════════════════════════════════");
        
        textArea.setText(sb.toString());
        
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    void handleAbrirMenu(MouseEvent event) {
        logger.info("Ícone 'Fechar' clicado! Retornando ao menu principal...");
        carregarNovaTela("/fxml/MenuUsuarioLogado.fxml", "Gym System - Menu do Usuário");
    }


    private Exercicio exibirDialogSelecaoExercicio(List<Exercicio> exercicios, String titulo) {
        List<String> opcoesExercicios = new ArrayList<>();
        for (Exercicio ex : exercicios) {
            opcoesExercicios.add(String.format("%d - %s", ex.getIdExercicio(), ex.getNome()));
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoesExercicios.get(0), opcoesExercicios);
        dialog.setTitle(titulo);
        dialog.setHeaderText("Selecione o exercício:");
        dialog.setContentText("Exercício:");

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String escolha = resultado.get();
            int idExercicio = Integer.parseInt(escolha.split(" - ")[0]);
            return exercicios.stream()
                .filter(ex -> ex.getIdExercicio() == idExercicio)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    /**
     * Método auxiliar para carregar uma nova tela FXML, fechando a atual.
     *
     * @param fxmlFile O caminho do arquivo FXML a ser carregado.
     * @param titulo   O título da nova janela.
     */
    private void carregarNovaTela(String fxmlFile, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Obtém o stage atual através do botão BCadastrarEX
            Stage stage = (Stage) BCadastrarEX.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            logger.info(() -> "Tela carregada com sucesso: " + fxmlFile);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela solicitada.");
            logger.log(Level.SEVERE, "Erro ao carregar tela: " + fxmlFile, e);
        }
    }

    /**
     * Exibe uma caixa de diálogo para feedback do usuário.
     *
     * @param tipo     Tipo do alerta (INFORMATION, WARNING, ERROR, etc.)
     * @param titulo   Título da janela de alerta
     * @param mensagem Mensagem a ser exibida
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
