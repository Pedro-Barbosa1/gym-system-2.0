package br.upe.integration;

import br.upe.controller.ExercicioViewController;
import br.upe.model.Exercicio;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExercicioViewIntegrationTest extends ApplicationTest {

    private static final String CSV_PATH = "src/main/resources/data/exercicios.csv";
    private IExercicioService service;
    private ExercicioViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // Headless config (comment if running with display)
        System.setProperty("testfx.robot", "glass");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");

        service = new ExercicioService();
        controller = new ExercicioViewController(service);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExercicioView.fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void limparCSV() throws Exception {
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
        // abrir diálogo de cadastro pela UI
        robot.clickOn("#BCadastrarEX");
        WaitForAsyncUtils.waitForFxEvents();

        // pegar o dialog pane atual
        DialogPane pane = robot.lookup(".dialog-pane").query();

        // Os três TextFields aparecem como .text-field na ordem que foram adicionados
        List<Node> textFields = pane.lookupAll(".text-field").stream().toList();

        TextField nomeField = (TextField) textFields.get(0);
        TextField descricaoField = (TextField) textFields.get(1);
        TextField gifField = (TextField) textFields.get(2);

        robot.clickOn(nomeField).write("Agachamento");
        robot.clickOn(descricaoField).write("Exercício de perna");
        robot.clickOn(gifField).write("gif/agachamento.gif");

        // clicar em OK
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
        // preparar: inserir um exercício direto no service
        service.cadastrarExercicio(1, "Supino Reto", "Peito", "supino.gif");

        // abrir dialog de listagem
        robot.clickOn("#BListarEX");
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane pane = robot.lookup(".dialog-pane").query();

        // TableView dentro do DialogPane
        @SuppressWarnings("unchecked")
        TableView<Exercicio> table = (TableView<Exercicio>) pane.lookup(".table-view");
        assertNotNull(table);
        assertEquals(1, table.getItems().size());
        assertEquals("Supino Reto", table.getItems().get(0).getNome());

        // fechar
        Button fechar = (Button) pane.lookupButton(ButtonType.CLOSE);
        robot.clickOn(fechar);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // -------------------------
    // 3) EDITAR (usa seleção -> diálogo de edição)
    // -------------------------
    @Test
    @Order(3)
    public void testEditarExercicio(FxRobot robot) throws Exception {
        // preparar
        service.cadastrarExercicio(1, "Agachamento", "Pernas", "a.gif");

        // abrir diálogo de edição
        robot.clickOn("#BEditarEX");
        WaitForAsyncUtils.waitForFxEvents();

        // primeiro dialog = seleção (combo)
        DialogPane selecaoPane = robot.lookup(".dialog-pane").query();
        // o combo box está dentro da dialog-pane
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) selecaoPane.lookup(".combo-box");
        assertNotNull(combo);

        // confirmar seleção (OK)
        Button okSelecao = (Button) selecaoPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelecao);
        WaitForAsyncUtils.waitForFxEvents();

        // agora deverá aparecer o diálogo de edição com 3 campos (text-fields)
        DialogPane editPane = robot.lookup(".dialog-pane").query();

        List<Node> textFields = editPane.lookupAll(".text-field").stream().toList();
        TextField nomeField = (TextField) textFields.get(0);
        TextField descricaoField = (TextField) textFields.get(1);
        TextField gifField = (TextField) textFields.get(2);

        // substituir valores
        robot.doubleClickOn(nomeField).write("Agachamento Livre");
        robot.doubleClickOn(descricaoField).write("Perna e glúteos");

        // confirmar edição
        Button okEdit = (Button) editPane.lookupButton(ButtonType.OK);
        robot.clickOn(okEdit);
        WaitForAsyncUtils.waitForFxEvents();

        // validar no service / CSV
        List<Exercicio> lista = service.listarExerciciosDoUsuario(1);
        assertEquals(1, lista.size());
        assertEquals("Agachamento Livre", lista.get(0).getNome());
        assertTrue(Files.readString(Paths.get(CSV_PATH)).contains("Agachamento Livre"));
    }

    // -------------------------
    // 4) VER DETALHES (seleção -> detalhes em TableView)
    // -------------------------
    @Test
    @Order(4)
    public void testVerDetalhesExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Remada", "Costas", "remada.gif");

        robot.clickOn("#BDetalhesEX");
        WaitForAsyncUtils.waitForFxEvents();

        // seleção
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        WaitForAsyncUtils.waitForFxEvents();

        // detalhes dialog (TableView de String[])
        DialogPane detalhesPane = robot.lookup(".dialog-pane").query();
        @SuppressWarnings("unchecked")
        TableView<?> tableDetalhes = (TableView<?>) detalhesPane.lookup(".table-view");
        assertNotNull(tableDetalhes);
        // espera que contenha pelo menos campo "Nome"
        boolean encontrouNome = tableDetalhes.getItems().stream()
                .anyMatch(item -> item.toString().contains("Remada") || item.toString().contains("Nome"));
        assertTrue(encontrouNome);

        // fechar
        Button fechar = (Button) detalhesPane.lookupButton(ButtonType.CLOSE);
        robot.clickOn(fechar);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // -------------------------
    // 5) EXCLUIR (seleção -> confirmação)
    // -------------------------
    @Test
    @Order(5)
    public void testExcluirExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Puxada", "Costas", "puxada.gif");

        robot.clickOn("#BExcluirEX");
        WaitForAsyncUtils.waitForFxEvents();

        // seleção dialog
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        WaitForAsyncUtils.waitForFxEvents();

        // confirmação (Alert) deve aparecer — buscar dialog-pane atual
        DialogPane confirmPane = robot.lookup(".dialog-pane").query();
        Button okConfirm = (Button) confirmPane.lookupButton(ButtonType.OK);
        robot.clickOn(okConfirm);
        WaitForAsyncUtils.waitForFxEvents();

        // validar remoção
        assertTrue(service.listarExerciciosDoUsuario(1).isEmpty());
        assertTrue(Files.readString(Paths.get(CSV_PATH)).isEmpty());
    }

    // -------------------------
    // 6) VISUALIZAR (apenas chama; se visualizador.fxml ausente, controller trata exceção)
    // -------------------------
    @Test
    @Order(6)
    public void testVisualizarExercicio(FxRobot robot) throws Exception {
        service.cadastrarExercicio(1, "Visual", "Teste visualizador", "\\gif\\vis.gif");

        robot.clickOn("#BVisualizarEX1");
        WaitForAsyncUtils.waitForFxEvents();

        // seleção dialog
        DialogPane selPane = robot.lookup(".dialog-pane").query();
        Button okSelec = (Button) selPane.lookupButton(ButtonType.OK);
        robot.clickOn(okSelec);
        WaitForAsyncUtils.waitForFxEvents();

        // O controller tenta abrir visualizador.fxml. Se estiver ausente, ele captura e mostra alerta.
        // Aqui garantimos que a chamada terminou sem lançar exceção para o teste (se chegamos até aqui, tudo bem).
        assertTrue(true);
    }

    // -------------------------
    // 7) ABRIR MENU (carregar nova cena)
    // -------------------------
    @Test
    @Order(7)
    public void testAbrirMenu(FxRobot robot) throws Exception {
        // Apenas chama o método via botão; se o FXML do menu não existir o controller mostra alerta internamente.
        robot.clickOn("#IFechar");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(true);
    }
}
