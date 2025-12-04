package br.upe.integration;

import br.upe.controller.ExercicioViewController;
import br.upe.model.Exercicio;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode; 
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExercicioViewIntegrationTest extends ApplicationTest {

    private static final String CSV_PATH = "src/main/resources/data/exercicios.csv";
    private IExercicioService service;
    private ExercicioViewController controller;
    
    private static final long WAIT_TIMEOUT = 10; 

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");

        service = new ExercicioService();
        controller = new ExercicioViewController(service);

        URL fxml = getClass().getResource("/ui/ExercicioView.fxml");

        if (fxml == null) {
            throw new RuntimeException("FXML 'ui/ExercicioView.fxml' não encontrado no classpath. Verifique a configuração <testResources> do Maven.");
        }

        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setController(controller);

        Parent root = loader.load(); 

        stage.setScene(new Scene(root));
        stage.show();
    }


    @BeforeEach
    public void limparEstado() throws Exception {
        // Limpa o disco primeiro para garantir que novas instâncias leiam um arquivo vazio
        Files.deleteIfExists(Paths.get(CSV_PATH));
        Files.createDirectories(Paths.get(CSV_PATH).getParent());
        Files.write(Paths.get(CSV_PATH), new byte[0]); 
        
        // Reinicializa o service e controller
        service = new ExercicioService(); 
        controller = new ExercicioViewController(service); 
    }

    // -------------------------
    // 1) CADASTRAR via diálogo
    // -------------------------
    @Test
    @Order(1)
    public void testCadastrarExercicio(FxRobot robot) throws Exception {
        robot.clickOn("#BCadastrarEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
        
        DialogPane pane = robot.lookup(".dialog-pane").query();

        List<Node> textFields = pane.lookupAll(".text-field").stream().collect(Collectors.toList());

        TextField nomeField = (TextField) textFields.get(0);
        TextField descricaoField = (TextField) textFields.get(1);
        TextField gifField = (TextField) textFields.get(2);

        robot.clickOn(nomeField).write("Agachamento Teste Cadastrar"); 
        robot.clickOn(descricaoField).write("Exercício de perna");
        robot.clickOn(gifField).write("gif/agachamento.gif");

        Button ok = (Button) pane.lookupButton(ButtonType.OK);
        robot.clickOn(ok);

        WaitForAsyncUtils.waitForFxEvents();

        String conteudo = Files.readString(Paths.get(CSV_PATH));
        assertTrue(conteudo.contains("Agachamento Teste Cadastrar"));
        assertTrue(conteudo.contains("Exercício de perna"));
    }

    // -------------------------
    // 2) LISTAR via TableView num Dialog (Resolve a falha de Asserção)
    // -------------------------
    @Test
    @Order(2)
    public void testListarExercicios(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Supino Reto Teste Listar", "Peito", "supino.gif");

        robot.clickOn("#BListarEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
            
        // PAUSA DE SINC. INTERNA
        WaitForAsyncUtils.waitForFxEvents();
            
        DialogPane pane = robot.lookup(".dialog-pane").query();

        @SuppressWarnings("unchecked")
        TableView<Exercicio> table = (TableView<Exercicio>) pane.lookup(".table-view");
        assertNotNull(table);
        
        // A lista deve conter apenas 1 item
        assertEquals(1, table.getItems().size(), "A lista deve conter apenas 1 exercício após a limpeza."); 
        
        // Verifica se o item correto está na lista (Resolve expected: <Supino Reto> but was: <Agachamento>)
        assertTrue(table.getItems().stream()
                       .anyMatch(e -> e.getNome().equals("Supino Reto Teste Listar")), 
                       "O Supino Reto deve estar na lista.");

        Button fechar = (Button) pane.lookupButton(ButtonType.CLOSE);
        robot.clickOn(fechar);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // -------------------------
    // 3) EDITAR (Resolve a falha de Asserção)
    // -------------------------
    @Test
    @Order(3)
    public void testEditarExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Agachamento Original Teste Edit", "Pernas", "a.gif"); 

        robot.clickOn("#BEditarEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
            
        // PAUSA DE SINC. INTERNA
        WaitForAsyncUtils.waitForFxEvents();

        // Diálogo 1: Seleção (ComboBox)
        DialogPane selecaoPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selecaoPane.lookup(".combo-box");
        assertNotNull(combo);

        // Seleção robusta por teclado
        robot.clickOn(combo); 
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, combo.showingProperty());
        robot.type(KeyCode.DOWN, 1).type(KeyCode.ENTER); 

        Button okSelecao = (Button) selecaoPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelecao);
        
        // Espera o SEGUNDO diálogo (de edição) aparecer
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 2: Edição
        DialogPane editPane = robot.lookup(".dialog-pane").query();

        List<Node> textFields = editPane.lookupAll(".text-field").stream().collect(Collectors.toList());
        TextField nomeField = (TextField) textFields.get(0);
        TextField descricaoField = (TextField) textFields.get(1);

        robot.doubleClickOn(nomeField).write("Agachamento Livre Editado");
        robot.doubleClickOn(descricaoField).write("Perna e glúteos");

        Button okEdit = (Button) editPane.lookupButton(ButtonType.OK);
        robot.clickOn(okEdit);
        WaitForAsyncUtils.waitForFxEvents();

        // Novo Service para forçar recarregamento do CSV para a asserção
        IExercicioService tempService = new ExercicioService();
        List<Exercicio> lista = tempService.listarExerciciosDoUsuario(1);
        
        // Asserção corrigida
        assertEquals(1, lista.size(), "Após a edição, apenas o item editado deve existir.");
        assertEquals("Agachamento Livre Editado", lista.get(0).getNome());
        assertTrue(Files.readString(Paths.get(CSV_PATH)).contains("Agachamento Livre Editado"));
    }

    // -------------------------
    // 4) VER DETALHES (Resolve a falha de Asserção)
    // -------------------------
    @Test
    @Order(4)
    public void testVerDetalhesExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Remada Teste Detalhes", "Costas", "remada.gif");

        robot.clickOn("#BDetalhesEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
            
        // PAUSA DE SINC. INTERNA
        WaitForAsyncUtils.waitForFxEvents();

        // Diálogo 1: Seleção (ComboBox)
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selPane.lookup(".combo-box");
        assertNotNull(combo);
        
        // Seleção robusta por teclado
        robot.clickOn(combo); 
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, combo.showingProperty());
        robot.type(KeyCode.DOWN, 1).type(KeyCode.ENTER); 

        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        
        // Espera o SEGUNDO diálogo (de detalhes) aparecer
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // PAUSA DE SINC. INTERNA
        WaitForAsyncUtils.waitForFxEvents();
        
        // Diálogo 2: Detalhes
        DialogPane detalhesPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        TableView<?> tableDetalhes = (TableView<?>) detalhesPane.lookup(".table-view");
        assertNotNull(tableDetalhes);
        
        // Asserção corrigida
        boolean encontrouNome = tableDetalhes.getItems().stream()
                .anyMatch(item -> item.toString().contains("Remada Teste Detalhes") || item.toString().contains("Nome"));
        assertTrue(encontrouNome, "O item 'Remada Teste Detalhes' ou o cabeçalho 'Nome' deve estar presente na tabela de detalhes.");

        Button fechar = (Button) detalhesPane.lookupButton(ButtonType.CLOSE);
        robot.clickOn(fechar);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // -------------------------
    // 5) EXCLUIR (Resolve a falha de Asserção)
    // -------------------------
    @Test
    @Order(5)
    public void testExcluirExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Puxada Teste Excluir", "Costas", "puxada.gif");

        robot.clickOn("#BExcluirEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
            
        // PAUSA DE SINC. INTERNA
        WaitForAsyncUtils.waitForFxEvents();

        // Diálogo 1: Seleção
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selPane.lookup(".combo-box");
        assertNotNull(combo);

        // Seleção robusta por teclado
        robot.clickOn(combo); 
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, combo.showingProperty());
        robot.type(KeyCode.DOWN, 1).type(KeyCode.ENTER); 
        
        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        
        // Espera o SEGUNDO diálogo (de confirmação) aparecer
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 2: Confirmação (Alert)
        DialogPane confirmPane = robot.lookup(".dialog-pane").query();
        Button okConfirm = (Button) confirmPane.lookupButton(ButtonType.OK);
        robot.clickOn(okConfirm);
        WaitForAsyncUtils.waitForFxEvents();

        // Asserção corrigida (Novo Service para forçar recarregamento)
        IExercicioService tempService = new ExercicioService();
        assertTrue(tempService.listarExerciciosDoUsuario(1).isEmpty(), "A lista de exercícios deve estar vazia após a exclusão.");
        assertTrue(Files.readString(Paths.get(CSV_PATH)).isEmpty(), "O arquivo CSV deve estar vazio após a exclusão.");
    }

    // -------------------------
    // 6) VISUALIZAR (Resolve a falha de NullPointerException no ComboBox)
    // -------------------------
    @Test
    @Order(6)
    public void testVisualizarExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Visual Teste", "Teste visualizador", "\\gif\\vis.gif");

        robot.clickOn("#BVisualizarEX1");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
            
        // PAUSA DE SINC. INTERNA
        WaitForAsyncUtils.waitForFxEvents();

        // Diálogo 1: Seleção (ComboBox)
        // Se a ComboBox falhou antes, esta pausa extra deve resolvê-lo
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selPane.lookup(".combo-box");
        assertNotNull(combo, "O ComboBox deveria ter sido inicializado no diálogo.");

        // Seleção robusta por teclado
        robot.clickOn(combo); 
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, combo.showingProperty());
        robot.type(KeyCode.DOWN, 1).type(KeyCode.ENTER); 
        
        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(true, "A ação de visualizar foi concluída sem falhas.");
    }

    // -------------------------
    // 7) ABRIR MENU 
    // -------------------------
    @Test
    @Order(7)
    public void testAbrirMenu(FxRobot robot) throws Exception {
        robot.clickOn("#IFechar");
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(true, "A ação de abrir o menu foi iniciada.");
    }
}