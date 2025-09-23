package br.upe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.upe.business.ExercicioService;

public class ExercicioServiceTest {

    ExercicioService exercicioService;

    @BeforeEach
    public void setUp() {
        exercicioService = new ExercicioService();
        exercicioService.limparDados();
    }

    @Test
    public void testCadastrarExercicioComNomeVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(1, "   ", "Descricao", "caminho.gif");
        });
        assertEquals("Nome do exercício não pode ser vazio.", exception.getMessage());
    }
 
    @Test
    public void testCadastrarExercicioComNomeDuplicado() {
    exercicioService.cadastrarExercicio(1, "ExercicioDuplicado", "Descricao1", "caminho1.gif");
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
    exercicioService.cadastrarExercicio(1, "ExercicioDuplicado", "Descricao2", "caminho2.gif");
        });
    assertEquals("Você já possui um exercício com o nome 'ExercicioDuplicado'.", exception.getMessage());
    }

    @Test
    public void testBuscarExercicioComNomeVazio() {
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(1, "  ").isEmpty());
    }

    @Test
    public void testBuscarExercicioInexistente() {
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(1, "Inexistente").isEmpty());
    }

    @Test
    public void testCadastrarEBuscarExercicio() {
        // Use um nome exclusivo para este teste
        exercicioService.cadastrarExercicio(1, "ExercicioBuscar1", "Descricao", "caminho.gif");
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(1, "ExercicioBuscar1").isPresent());
    }
 
    @Test
    public void testListarExerciciosDoUsuario() {
        // Cadastra exercícios usando IDs de usuário diferentes ou nomes únicos
        exercicioService.cadastrarExercicio(2, "ExercicioListarrrrr", "Descricao", "caminho.gif");
        exercicioService.cadastrarExercicio(2, "ExercicioListarrrr", "Descricao", "caminho.gif");
        // Verifica se a contagem corresponde apenas aos exercícios deste teste
        assertEquals(2, exercicioService.listarExerciciosDoUsuario(2).size());
    }

    @Test
    public void testBuscarExercicioPorIdGlobal() {
        // Cadastra um exercício com nome e ID de usuário únicos
        var exercicio = exercicioService.cadastrarExercicio(3, "ExercicioID1", "Descricao", "caminho.gif");
        assertTrue(exercicioService.buscarExercicioPorIdGlobal(exercicio.getIdExercicio()).isPresent());
        assertTrue(exercicioService.buscarExercicioPorIdGlobal(9999).isEmpty());
    }

    @Test
    public void testDeletarExercicioComNomeVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.deletarExercicioPorNome(1, "   ");
        });
        assertEquals("Nome do exercício para deletar não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testDeletarExercicioExistente() {
        exercicioService.cadastrarExercicio(4, "ExercicioDel", "Descricao", "caminho.gif");
        assertTrue(exercicioService.deletarExercicioPorNome(4, "ExercicioDel"));
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(4, "ExercicioDel").isEmpty());
    }

    @Test
    public void testDeletarExercicioInexistente() {
        assertFalse(exercicioService.deletarExercicioPorNome(1, "Inexistente"));
    }

    @Test
    public void testAtualizarExercicioComNomeAtualVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.atualizarExercicio(1, "   ", "NovoNome", "NovaDesc", "novo.gif");
        });
        assertEquals("O nome atual do exercício não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testAtualizarExercicioInexistente() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.atualizarExercicio(1, "Inexistente", "NovoNome", "NovaDesc", "novo.gif");
        });
        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    public void testAtualizarExercicioComNomeDuplicado() {
        // Cria um estado inicial para o teste
        exercicioService.cadastrarExercicio(5, "ExercicioA", "Desc", "caminho.gif");
        exercicioService.cadastrarExercicio(5, "ExercicioB", "Desc", "caminho.gif");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Tenta renomear "ExercicioB" para "ExercicioA" para o mesmo usuário
            exercicioService.atualizarExercicio(5, "ExercicioB", "ExercicioA", "NovaDesc", "novo.gif");
        });
        assertTrue(exception.getMessage().contains("Você já possui um exercício com o novo nome"));
    }

    @Test
    public void testAtualizarExercicioFluxoFeliz() {
        // Cria um exercício para ser atualizado, com nome e ID de usuário únicos
        exercicioService.cadastrarExercicio(6, "ExercicioAntigo", "DescAntiga", "gifAntigo.gif");

        exercicioService.atualizarExercicio(6, "ExercicioAntigo", "ExercicioNovo", "DescNova", "gifNovo.gif");

        // Verifica se a atualização foi bem-sucedida e o nome antigo não existe mais
        var exercicioAtualizado = exercicioService.buscarExercicioDoUsuarioPorNome(6, "ExercicioNovo").get();
        assertEquals("DescNova", exercicioAtualizado.getDescricao());
        assertEquals("gifNovo.gif", exercicioAtualizado.getCaminhoGif());
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(6, "ExercicioAntigo").isEmpty());
    }
}