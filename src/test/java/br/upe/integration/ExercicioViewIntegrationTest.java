package br.upe.integration;

import br.upe.model.Exercicio;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.controller.ExercicioViewController;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExercicioViewIntegrationTest extends ApplicationTest {

    private static final String CSV_PATH = "src/main/resources/data/exercicios.csv";
    private IExercicioService service;
    private ExercicioViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        service = new ExercicioService(); // CSV real
        controller = new ExercicioViewController(service);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/exercicio_view.fxml"));
        loader.setController(controller);
        stage.setScene(new javafx.scene.Scene(loader.load()));
        stage.show();
    }

    @BeforeEach
    public void limparCSV() throws Exception {
        Files.deleteIfExists(Paths.get(CSV_PATH));
        Files.createFile(Paths.get(CSV_PATH));
    }

    // ========================================
    // 1. Cadastrar exercício
    // ========================================
    @Test
    @Order(1)
    public void testCadastrarExercicio() {
        // Simula o diálogo retornando os dados
        Dialog<ButtonType> dialogMock = new Dialog<>();
        TextField nomeField = new TextField("Agachamento");
        TextField descricaoField = new TextField("Exercício de perna");
        TextField gifField = new TextField("\\gif\\agachamento_livre.gif");

        controller = new ExercicioViewController(new IExercicioService() {
            @Override
            public Exercicio cadastrarExercicio(int idUsuario, String nome, String descricao, String caminhoGif) {
                return service.cadastrarExercicio(idUsuario, nome, descricao, caminhoGif);
            }
            // delega outros métodos ao service real
            @Override public java.util.List<Exercicio> listarExerciciosDoUsuario(int idUsuario) { return service.listarExerciciosDoUsuario(idUsuario); }
            @Override public void atualizarExercicio(int idUsuario, String nomeAtual, String novoNome, String novaDescricao, String novoGif) { service.atualizarExercicio(idUsuario, nomeAtual, novoNome, novaDescricao, novoGif); }
            @Override public void deletarExercicioPorNome(int idUsuario, String nome) { service.deletarExercicioPorNome(idUsuario, nome); }
        });

        // Chamando diretamente o método do controller
        Exercicio novo = service.cadastrarExercicio(1, nomeField.getText(), descricaoField.getText(), gifField.getText());

        assertEquals("Agachamento", novo.getNome());
        assertEquals("Exercício de perna", novo.getDescricao());

        // CSV deve conter o exercício
        assertTrue(Files.readString(Paths.get(CSV_PATH)).contains("Agachamento"));
    }

    // ========================================
    // 2. Listar exercícios
    // ========================================
    @Test
    @Order(2)
    public void testListarExercicios() {
        // Garante que existe exercício
        service.cadastrarExercicio(1, "Agachamento", "Exercício de perna", "\\gif\\agachamento_livre.gif");

        var lista = service.listarExerciciosDoUsuario(1);
        assertEquals(1, lista.size());
        assertEquals("Agachamento", lista.get(0).getNome());
    }

    // ========================================
    // 3. Editar exercício
    // ========================================
    @Test
    @Order(3)
    public void testEditarExercicio() {
        service.cadastrarExercicio(1, "Agachamento", "Exercício de perna", "\\gif\\agachamento_livre.gif");

        service.atualizarExercicio(1, "Agachamento", "Agachamento Livre", "Perna e glúteos", "\\gif\\agachamento_livre.gif");

        var lista = service.listarExerciciosDoUsuario(1);
        assertEquals("Agachamento Livre", lista.get(0).getNome());
        assertEquals("Perna e glúteos", lista.get(0).getDescricao());

        assertTrue(Files.readString(Paths.get(CSV_PATH)).contains("Agachamento Livre"));
    }

    // ========================================
    // 4. Excluir exercício
    // ========================================
    @Test
    @Order(4)
    public void testExcluirExercicio() {
        service.cadastrarExercicio(1, "Agachamento Livre", "Perna e glúteos", "\\gif\\agachamento_livre.gif");

        service.deletarExercicioPorNome(1, "Agachamento Livre");

        var lista = service.listarExerciciosDoUsuario(1);
        assertTrue(lista.isEmpty());

        assertTrue(Files.readString(Paths.get(CSV_PATH)).isEmpty());
    }
}
