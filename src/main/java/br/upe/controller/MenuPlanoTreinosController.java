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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        
        // Criar Dialog com GridPane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Criar Novo Plano de Treino");
        dialog.setHeaderText("Preencha o nome do plano:");
        
        // Criar GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do plano");
        
        grid.add(new Label("Nome:"), 0, 0);
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
        
        // Criar Dialog com TextArea
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Meus Planos de Treino");
        dialog.setHeaderText("Lista de todos os seus planos:");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefRowCount(15);
        textArea.setPrefColumnCount(50);
        
        StringBuilder sb = new StringBuilder();
        for (PlanoTreino plano : planos) {
            sb.append("═══════════════════════════════════════\n");
            sb.append(String.format("ID: %d\n", plano.getIdPlano()));
            sb.append(String.format("Nome: %s\n", plano.getNome()));
            sb.append(String.format("Exercícios no plano: %d\n", plano.getItensTreino().size()));
            sb.append("\n");
        }
        
        textArea.setText(sb.toString());
        dialog.getDialogPane().setContent(textArea);
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
        
        // Selecionar plano
        PlanoTreino planoSelecionado = exibirDialogSelecaoPlano(planos, "Editar Plano");
        
        if (planoSelecionado == null) {
            return;
        }
        
        // Dialog para editar
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Plano de Treino");
        dialog.setHeaderText("Edite o plano '" + planoSelecionado.getNome() + "'");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomeField = new TextField(planoSelecionado.getNome());
        
        grid.add(new Label("Novo Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("(deixe em branco para não alterar)"), 0, 1, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> resultado = dialog.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String novoNome = nomeField.getText().trim();
            
            try {
                planoTreinoService.editarPlano(idUsuarioLogado, planoSelecionado.getNome(), 
                    novoNome.isEmpty() ? null : novoNome);
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
        
        // Confirmação
        boolean confirmado = showConfirmation("Confirmar Exclusão", 
            "Deseja realmente deletar o plano '" + planoSelecionado.getNome() + "'?");
        
        if (confirmado) {
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
        
        // Selecionar exercício
        List<String> opcoesExercicios = new ArrayList<>();
        for (Exercicio ex : meusExercicios) {
            opcoesExercicios.add(String.format("%d - %s", ex.getIdExercicio(), ex.getNome()));
        }
        
        ChoiceDialog<String> dialogExercicio = new ChoiceDialog<>(opcoesExercicios.get(0), opcoesExercicios);
        dialogExercicio.setTitle("Selecionar Exercício");
        dialogExercicio.setHeaderText("Selecione o exercício para adicionar ao plano:");
        dialogExercicio.setContentText("Exercício:");
        
        Optional<String> escolhaExercicio = dialogExercicio.showAndWait();
        
        if (!escolhaExercicio.isPresent()) {
            return;
        }
        
        int idExercicio = Integer.parseInt(escolhaExercicio.get().split(" - ")[0]);
        
        // Dialog para carga e repetições
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Exercício ao Plano");
        dialog.setHeaderText("Configure o exercício no plano:");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField cargaField = new TextField();
        cargaField.setPromptText("Carga em kg");
        TextField repeticoesField = new TextField();
        repeticoesField.setPromptText("Número de repetições");
        
        grid.add(new Label("Carga (kg):"), 0, 0);
        grid.add(cargaField, 1, 0);
        grid.add(new Label("Repetições:"), 0, 1);
        grid.add(repeticoesField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> resultado = dialog.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                int carga = Integer.parseInt(cargaField.getText().trim());
                int repeticoes = Integer.parseInt(repeticoesField.getText().trim());
                
                planoTreinoService.adicionarExercicioAoPlano(idUsuarioLogado, planoSelecionado.getNome(), 
                    idExercicio, carga, repeticoes);
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
        
        // Selecionar exercício para remover
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
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoesExercicios.get(0), opcoesExercicios);
        dialog.setTitle("Remover Exercício");
        dialog.setHeaderText("Selecione o exercício para remover:");
        dialog.setContentText("Exercício:");
        
        Optional<String> escolha = dialog.showAndWait();
        
        if (escolha.isPresent()) {
            int idExercicio = Integer.parseInt(escolha.get().split(" - ")[0]);
            
            boolean confirmado = showConfirmation("Confirmar Remoção", 
                "Deseja realmente remover este exercício do plano?");
            
            if (confirmado) {
                try {
                    planoTreinoService.removerExercicioDoPlano(idUsuarioLogado, planoSelecionado.getNome(), idExercicio);
                    showInfo("Sucesso", "Exercício removido do plano '" + planoSelecionado.getNome() + "' com sucesso!");
                } catch (IllegalArgumentException e) {
                    showError("Erro", "Erro ao remover exercício: " + e.getMessage());
                }
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
        
        // Criar Dialog com TextArea mostrando detalhes
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Detalhes do Plano");
        dialog.setHeaderText("Informações completas do plano:");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
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
                sb.append(String.format("  • %s (ID: %d)\n", nomeExercicio, item.getIdExercicio()));
                sb.append(String.format("    Carga: %d kg\n", item.getCargaKg()));
                sb.append(String.format("    Repetições: %d\n\n", item.getRepeticoes()));
            }
        } else {
            sb.append("Nenhum exercício adicionado ainda.\n");
        }
        
        sb.append("\n═══════════════════════════════════════");
        
        textArea.setText(sb.toString());
        dialog.getDialogPane().setContent(textArea);
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
