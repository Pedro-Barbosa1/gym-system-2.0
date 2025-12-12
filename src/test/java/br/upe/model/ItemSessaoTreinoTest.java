package br.upe.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemSessaoTreino Model Tests")
class ItemSessaoTreinoTest {

    @Test
    @DisplayName("Deve criar item com construtor principal")
    void deveCriarItemComConstrutorPrincipal() {
        ItemSessaoTreino item = new ItemSessaoTreino(1, 1, 10, 50.0);
        
        assertEquals(10, item.getRepeticoesRealizadas());
        assertEquals(50.0, item.getCargaRealizada());
        assertEquals(1, item.getIdExercicio());
    }

    @Test
    @DisplayName("Deve criar item com construtor vazio")
    void deveCriarItemComConstrutorVazio() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        assertNotNull(item);
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        
        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(60.5);
        item.setIdExercicio(5);
        
        assertEquals(12, item.getRepeticoesRealizadas());
        assertEquals(60.5, item.getCargaRealizada());
        assertEquals(5, item.getIdExercicio());
    }

    @Test
    @DisplayName("Deve definir exercicio")
    void deveDefinirExercicio() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        Exercicio exercicio = new Exercicio();
        exercicio.setIdExercicio(10);
        
        item.setExercicio(exercicio);
        
        assertEquals(exercicio, item.getExercicio());
        assertEquals(10, item.getIdExercicio());
    }

    @Test
    @DisplayName("Deve definir sessao de treino")
    void deveDefinirSessaoTreino() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        SessaoTreino sessao = new SessaoTreino();
        sessao.setIdSessao(5);
        
        item.setSessaoTreino(sessao);
        
        assertEquals(sessao, item.getSessaoTreino());
    }

    @Test
    @DisplayName("Deve retornar 0 quando exercicio for null")
    void deveRetornarZeroQuandoExercicioForNull() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        assertEquals(0, item.getIdExercicio());
    }
}
