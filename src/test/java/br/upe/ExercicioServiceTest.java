package br.upe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import br.upe.business.ExercicioService;

public class ExercicioServiceTest {

    ExercicioService exercicioService = new ExercicioService();

    @Test
    public void testCadastrarExercicioComNomeVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(1, "   ", "Descricao", "caminho.gif");
        });
        assertEquals("Nome do exercício não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCadastrarExercicioComNomeDuplicado() {
        exercicioService.cadastrarExercicio(1, "Exercicio1", "Descricao1", "caminho1.gif");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(1, "Exercicio1", "Descricao2", "caminho2.gif");
        });
        assertEquals("Você já possui um exercício com o nome 'Exercicio1'.", exception.getMessage());
    }

    @Test
    public void testBuscarExercicioComNomeVazio() {
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(1, "   ").isEmpty());
    }

    @Test
    public void testBuscarExercicioInexistente() {
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(1, "Inexistente").isEmpty());
    }

    @Test
    public void testCadastrarEBuscarExercicio() {
        exercicioService.cadastrarExercicio(1, "Exercicio2", "Descricao2", "caminho2.gif");
        assertTrue(exercicioService.buscarExercicioDoUsuarioPorNome(1, "Exercicio2").isPresent());
    }

    @Test
    public void testListarExerciciosDoUsuario() {
    exercicioService.cadastrarExercicio(1, "ExercicioListar", "Descricao", "caminho.gif");
    assertFalse(exercicioService.listarExerciciosDoUsuario(1).isEmpty());
    }

    @Test
    public void testBuscarExercicioPorIdGlobal() {
        var exercicio = exercicioService.cadastrarExercicio(1, "ExercicioId", "Descricao", "caminho.gif");
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
        exercicioService.cadastrarExercicio(1, "ExercicioDel", "Descricao", "caminho.gif");
        assertTrue(exercicioService.deletarExercicioPorNome(1, "ExercicioDel"));
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
        exercicioService.cadastrarExercicio(1, "ExercicioA", "Desc", "caminho.gif");
        exercicioService.cadastrarExercicio(1, "ExercicioB", "Desc", "caminho.gif");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.atualizarExercicio(1, "ExercicioB", "ExercicioA", "NovaDesc", "novo.gif");
        });
        assertTrue(exception.getMessage().contains("Você já possui um exercício com o novo nome"));
    }

    @Test
    public void testAtualizarExercicioFluxoFeliz() {
        exercicioService.cadastrarExercicio(1, "ExercicioAtualizar", "DescAntiga", "gifAntigo.gif");

        exercicioService.atualizarExercicio(1, "ExercicioAtualizar", "ExercicioNovo", "DescNova", "gifNovo.gif");

        var atualizado = exercicioService.buscarExercicioDoUsuarioPorNome(1, "ExercicioNovo").get();
        assertEquals("DescNova", atualizado.getDescricao());
        assertEquals("gifNovo.gif", atualizado.getCaminhoGif());
    }

}
