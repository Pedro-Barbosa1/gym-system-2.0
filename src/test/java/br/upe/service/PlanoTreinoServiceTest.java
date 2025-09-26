package br.upe.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.upe.model.PlanoTreino;
import br.upe.repository.impl.ExercicioRepositoryImpl;

public class PlanoTreinoServiceTest {

    PlanoTreinoService planoTreinoService = new PlanoTreinoService();
    ExercicioRepositoryImpl exercicioRepository = new ExercicioRepositoryImpl();

    @BeforeEach
    public void setUp() {
        exercicioRepository = new ExercicioRepositoryImpl();
        planoTreinoService = new PlanoTreinoService();
        planoTreinoService.limparDados();
        exercicioRepository.carregarDoCsv(); 
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
        planoTreinoService.criarPlano(2, "Plano para adicionar exercício");
        
        planoTreinoService.adicionarExercicioAoPlano(2, "Plano para adicionar exercício", 2, 70, 10);
        
        PlanoTreino plano = planoTreinoService.buscarPlanoPorNomeEUsuario(2, "Plano para adicionar exercício").get();
        assertFalse(plano.getItensTreino().isEmpty());
        assertEquals(1, plano.getItensTreino().size());
        assertEquals(2, plano.getItensTreino().get(0).getIdExercicio());
    }

    @Test
    public void testAdicionarExercicioAJaExistenteNoPlano() {

        planoTreinoService.criarPlano(2, "Plano para adicionar exercício");
        planoTreinoService.adicionarExercicioAoPlano(2, "Plano para adicionar exercício", 2, 70, 10);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.adicionarExercicioAoPlano(2, "Plano para adicionar exercício", 2, 70, 12);
        });
        assertEquals("Exercício já adicionado a este plano. Considere editá-lo.", exception.getMessage());
    }

    @Test
    public void testRemoverExercicioDoPlanoComSucesso() {

        planoTreinoService.criarPlano(2, "Plano para remover exercício");
        planoTreinoService.adicionarExercicioAoPlano(2, "Plano para remover exercício", 2, 70, 10);
    }*/

}