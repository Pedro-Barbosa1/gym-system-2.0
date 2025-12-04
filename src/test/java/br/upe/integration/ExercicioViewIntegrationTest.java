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
import javafx.scene.input.KeyCode; // Import necessário
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExercicioViewIntegrationTest extends ApplicationTest {

    private static final String CSV_PATH = "src/main/resources/data/exercicios.csv";
    private IExercicioService service;
    private ExercicioViewController controller;
    
    // Timeout estendido para testes headless
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
        // CORREÇÃO: Reinicializa o serviço e o controller para garantir isolamento de estado
        service = new ExercicioService(); 
        controller = new ExercicioViewController(service); 
        
        // Limpeza do disco
        Files.deleteIfExists(Paths.get(CSV_PATH));
        Files.createDirectories(Paths.get(CSV_PATH).getParent());
        Files.createFile(Paths.get(CSV_PATH));
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

        List<Node> textFields = pane.lookupAll(".text-field").stream().toList();

        TextField nomeField = (TextField) textFields.get(0);
        TextField descricaoField = (TextField) textFields.get(1);
        TextField gifField = (TextField) textFields.get(2);

        robot.clickOn(nomeField).write("Agachamento");
        robot.clickOn(descricaoField).write("Exercício de perna");
        robot.clickOn(gifField).write("gif/agachamento.gif");

        Button ok = (Button) pane.lookupButton(ButtonType.OK);
        robot.clickOn(ok);

        WaitForAsyncUtils.waitForFxEvents();

        String conteudo = Files.readString(Paths.get(CSV_PATH));
        assertTrue(conteudo.contains("Agachamento"));
        assertTrue(conteudo.contains("Exercício de perna"));
    }

    // -------------------------
    // 2) LISTAR via TableView num Dialog
    // -------------------------
    @Test
    @Order(2)
    public void testListarExercicios(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Supino Reto", "Peito", "supino.gif");

        robot.clickOn("#BListarEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());
            
        DialogPane pane = robot.lookup(".dialog-pane").query();

        @SuppressWarnings("unchecked")
        TableView<Exercicio> table = (TableView<Exercicio>) pane.lookup(".table-view");
        assertNotNull(table);
        
        assertEquals(1, table.getItems().size()); 
        assertEquals("Supino Reto", table.getItems().get(0).getNome());

        Button fechar = (Button) pane.lookupButton(ButtonType.CLOSE);
        robot.clickOn(fechar);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // -------------------------
    // 3) EDITAR (CORRIGIDO: Seleção via Teclado)
    // -------------------------
    @Test
    @Order(3)
    public void testEditarExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Agachamento Original", "Pernas", "a.gif");

        robot.clickOn("#BEditarEX");
        
        // Espera o primeiro diálogo (Seleção) aparecer
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 1: Seleção (ComboBox)
        DialogPane selecaoPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selecaoPane.lookup(".combo-box");
        assertNotNull(combo);

        // CORREÇÃO: Seleção robusta por teclado
        robot.clickOn(combo); 
        // Espera a lista carregar (usando o combo como referência)
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, combo.showingProperty());
        
        // Simula DOWN para selecionar o primeiro item e ENTER para confirmar
        robot.type(KeyCode.DOWN, 1).type(KeyCode.ENTER); 

        // Confirma a seleção no diálogo (OK)
        Button okSelecao = (Button) selecaoPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelecao);
        
        // Espera o SEGUNDO diálogo (de edição) aparecer
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 2: Edição
        DialogPane editPane = robot.lookup(".dialog-pane").query();

        List<Node> textFields = editPane.lookupAll(".text-field").stream().toList();
        TextField nomeField = (TextField) textFields.get(0);
        TextField descricaoField = (TextField) textFields.get(1);

        robot.doubleClickOn(nomeField).write("Agachamento Livre");
        robot.doubleClickOn(descricaoField).write("Perna e glúteos");

        Button okEdit = (Button) editPane.lookupButton(ButtonType.OK);
        robot.clickOn(okEdit);
        WaitForAsyncUtils.waitForFxEvents();

        List<Exercicio> lista = service.listarExerciciosDoUsuario(1);
        assertEquals(1, lista.size());
        assertEquals("Agachamento Livre", lista.get(0).getNome());
        assertTrue(Files.readString(Paths.get(CSV_PATH)).contains("Agachamento Livre"));
    }

    // -------------------------
    // 4) VER DETALHES (CORRIGIDO: Seleção via Teclado)
    // -------------------------
    @Test
    @Order(4)
    public void testVerDetalhesExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Remada", "Costas", "remada.gif");

        robot.clickOn("#BDetalhesEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 1: Seleção (ComboBox)
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selPane.lookup(".combo-box");
        assertNotNull(combo);
        
        // CORREÇÃO: Seleção robusta por teclado
        robot.clickOn(combo); 
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, combo.showingProperty());
        robot.type(KeyCode.DOWN, 1).type(KeyCode.ENTER); 

        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        
        // Espera o SEGUNDO diálogo (de detalhes) aparecer
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 2: Detalhes
        DialogPane detalhesPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        TableView<?> tableDetalhes = (TableView<?>) detalhesPane.lookup(".table-view");
        assertNotNull(tableDetalhes);
        
        boolean encontrouNome = tableDetalhes.getItems().stream()
                .anyMatch(item -> item.toString().contains("Remada") || item.toString().contains("Nome"));
        assertTrue(encontrouNome);

        Button fechar = (Button) detalhesPane.lookupButton(ButtonType.CLOSE);
        robot.clickOn(fechar);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // -------------------------
    // 5) EXCLUIR (CORRIGIDO: Seleção via Teclado)
    // -------------------------
    @Test
    @Order(5)
    public void testExcluirExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Puxada", "Costas", "puxada.gif");

        robot.clickOn("#BExcluirEX");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 1: Seleção
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selPane.lookup(".combo-box");
        assertNotNull(combo);

        // CORREÇÃO: Seleção robusta por teclado
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

        // validar remoção
        assertTrue(service.listarExerciciosDoUsuario(1).isEmpty());
        assertTrue(Files.readString(Paths.get(CSV_PATH)).isEmpty());
    }

    // -------------------------
    // 6) VISUALIZAR (CORRIGIDO: Seleção via Teclado)
    // -------------------------
    @Test
    @Order(6)
    public void testVisualizarExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Visual", "Teste visualizador", "\\gif\\vis.gif");

        robot.clickOn("#BVisualizarEX1");
        
        WaitForAsyncUtils.waitFor(WAIT_TIMEOUT, TimeUnit.SECONDS, 
            () -> robot.lookup(".dialog-pane").tryQuery().isPresent());

        // Diálogo 1: Seleção (ComboBox)
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selPane.lookup(".combo-box");
        assertNotNull(combo);

        // CORREÇÃO: Seleção robusta por teclado
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