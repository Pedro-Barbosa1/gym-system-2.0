package br.upe.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Controller para a tela do Menu de Planos de Treino
 * Gerencia todas as operações relacionadas aos planos de treino
 */
public class MenuPlanoTreinosController {

    // Referências aos botões do FXML
    @FXML
    private Button sairB;

    @FXML
    private Button criarPlanoB;

    @FXML
    private Button ListarPlanoB;

    @FXML
    private Button editarPlanoB;

    @FXML
    private Button deletarPlanoB;

    @FXML
    private Button adicionarExercicioAoPlanoB;

    @FXML
    private Button removerExercíciodoPlanoB;

    @FXML
    private Button verDetalhesDePlanoB;

    /**
     * Inicializa o controller após o carregamento do FXML
     * Este método é chamado automaticamente pelo JavaFX
     */
    @FXML
    public void initialize() {
        System.out.println("Menu Plano Treinos inicializado com sucesso!");
        
        // Configura as ações dos botões
        configurarAcoes();
    }

    /**
     * Configura as ações dos botões
     */
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

    /**
     * Manipula o clique no botão "Criar Novo Plano de Treino"
     * TODO: Implementar criação de plano
     */
    @FXML
    private void handleCriarPlano() {
        System.out.println("Criar Novo Plano de Treino clicado!");
        
        showInfo("Criar Plano", "Funcionalidade de criar plano será implementada em breve.");
        
        // TODO: Abrir formulário de criação de plano
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CriarPlanoTreino.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) criarPlanoB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erro", "Não foi possível abrir o formulário de criação.");
        }
        */
    }

    /**
     * Manipula o clique no botão "Listar Meus Planos de Treino"
     * TODO: Implementar listagem de planos
     */
    @FXML
    private void handleListarPlanos() {
        System.out.println("Listar Meus Planos de Treino clicado!");
        
        showInfo("Listar Planos", "Funcionalidade de listar planos será implementada em breve.");
        
        // TODO: Abrir tela de listagem
        // Exemplo:
        // PlanoTreinoService service = new PlanoTreinoService();
        // List<PlanoTreino> planos = service.listarPorUsuario(usuarioId);
        // Exibir em tabela ou lista
    }

    /**
     * Manipula o clique no botão "Editar Plano de Treino"
     * TODO: Implementar edição de plano
     */
    @FXML
    private void handleEditarPlano() {
        System.out.println("Editar Plano de Treino clicado!");
        
        showInfo("Editar Plano", "Funcionalidade de editar plano será implementada em breve.");
        
        // TODO: Abrir formulário de edição
        // 1. Solicitar ID ou nome do plano
        // 2. Carregar dados do plano
        // 3. Abrir formulário preenchido
    }

    /**
     * Manipula o clique no botão "Deletar Plano de Treino"
     * TODO: Implementar exclusão de plano
     */
    @FXML
    private void handleDeletarPlano() {
        System.out.println("Deletar Plano de Treino clicado!");
        
        showInfo("Deletar Plano", "Funcionalidade de deletar plano será implementada em breve.");
        
        // TODO: Implementar exclusão
        // 1. Solicitar ID ou nome do plano
        // 2. Confirmar exclusão
        // 3. Deletar do banco/arquivo
    }

    /**
     * Manipula o clique no botão "Adicionar Exercício ao Plano"
     * TODO: Implementar adição de exercício
     */
    @FXML
    private void handleAdicionarExercicio() {
        System.out.println("Adicionar Exercício ao Plano clicado!");
        
        showInfo("Adicionar Exercício", "Funcionalidade de adicionar exercício será implementada em breve.");
        
        // TODO: Implementar adição
        // 1. Selecionar plano
        // 2. Selecionar exercício
        // 3. Definir séries, repetições, carga
        // 4. Adicionar ao plano
    }

    /**
     * Manipula o clique no botão "Remover Exercício do Plano"
     * TODO: Implementar remoção de exercício
     */
    @FXML
    private void handleRemoverExercicio() {
        System.out.println("Remover Exercício do Plano clicado!");
        
        showInfo("Remover Exercício", "Funcionalidade de remover exercício será implementada em breve.");
        
        // TODO: Implementar remoção
        // 1. Selecionar plano
        // 2. Listar exercícios do plano
        // 3. Selecionar exercício a remover
        // 4. Confirmar e remover
    }

    /**
     * Manipula o clique no botão "Ver Detalhes do Plano"
     * TODO: Implementar visualização de detalhes
     */
    @FXML
    private void handleVerDetalhes() {
        System.out.println("Ver Detalhes do Plano clicado!");
        
        showInfo("Ver Detalhes", "Funcionalidade de ver detalhes será implementada em breve.");
        
        // TODO: Implementar visualização
        // 1. Solicitar ID ou nome do plano
        // 2. Carregar detalhes completos
        // 3. Exibir em tela de detalhes
    }

    /**
     * Manipula o clique no botão "Sair"
     * Fecha a aplicação ou volta para o menu anterior
     */
    @FXML
    private void handleSair() {
        System.out.println("Botão SAIR clicado - Encerrando aplicação...");
        
        // Opção 1: Fechar a aplicação completamente
        Platform.exit();
        
        // Opção 2: Voltar para o menu principal (quando implementado)
        // TODO: Implementar navegação para menu anterior
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Erro", "Não foi possível voltar ao menu.");
        }
        */
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

    /**
     * Exibe um alerta de confirmação
     * @param title Título do alerta
     * @param content Conteúdo da mensagem
     * @return true se o usuário confirmar, false caso contrário
     */
    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        return alert.showAndWait().get() == javafx.scene.control.ButtonType.OK;
    }
}
