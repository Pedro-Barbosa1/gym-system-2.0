package br.upe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.upe.business.PlanoTreinoService;
import br.upe.data.beans.PlanoTreino;

public class PlanoTreinoServiceTest {

    PlanoTreinoService planoTreinoService = new PlanoTreinoService();

    @BeforeEach
    public void setUp() {
        planoTreinoService = new PlanoTreinoService();
        planoTreinoService.limparDados(); 
    }

    @Test
    public void testCriarPlanoComNomeDuplicado() {
        planoTreinoService.criarPlano(1, "PlanoDuplicado");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.criarPlano(1, "PlanoDuplicado");
        });
        assertEquals("Você já possui um plano com o nome 'PlanoDuplicado'.", exception.getMessage());
    }

    @Test
    public void testCriarPlanoComNomeVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.criarPlano(1, "   ");
        });
        assertEquals("Nome do plano não pode ser vazio.", exception.getMessage());
    }

    @Test
    public void testCriarPlanoComNomeValido() {
        PlanoTreino plano = planoTreinoService.criarPlano(1, "PlanoValido");
        assertNotNull(plano);
        assertEquals("PlanoValido", plano.getNome());
        assertEquals(1, plano.getIdUsuario());
    }
/* 
    @Test
    public void testAdicionarExercicioAoPlanoComSucesso() {
        planoTreinoService.criarPlano(1, "Plano para adicionar exercício");
        
        planoTreinoService.adicionarExercicioAoPlano(1, "Plano para adicionar exercício", 1, 50, 10);
        
        PlanoTreino plano = planoTreinoService.buscarPlanoPorNomeEUsuario(1, "Plano para adicionar exercício").get();
        assertFalse(plano.getItensTreino().isEmpty());
        assertEquals(1, plano.getItensTreino().size());
        assertEquals(1, plano.getItensTreino().get(0).getIdExercicio());
    }

    @Test
    public void testAdicionarExercicioAJaExistenteNoPlano() {

        planoTreinoService.criarPlano(1, "Plano com exercício");
        planoTreinoService.adicionarExercicioAoPlano(1, "Plano com exercício", 1, 50, 10);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.adicionarExercicioAoPlano(1, "Plano com exercício", 1, 60, 12);
        });
        assertEquals("Exercício já adicionado a este plano. Considere editá-lo.", exception.getMessage());
    }

    @Test
    public void testRemoverExercicioDoPlanoComSucesso() {

        planoTreinoService.criarPlano(1, "Plano para remover exercício");
        planoTreinoService.adicionarExercicioAoPlano(1, "Plano para remover exercício", 1, 50, 10);
    }
*/
}