package br.upe.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemPlanoTreino Model Tests")
class ItemPlanoTreinoTest {

    @Test
    @DisplayName("Deve criar item com construtor principal")
    void deveCriarItemComConstrutorPrincipal() {
        ItemPlanoTreino item = new ItemPlanoTreino(1, 1, 50, 10);
        
        assertEquals(50, item.getCargaKg());
        assertEquals(10, item.getRepeticoes());
        assertEquals(1, item.getIdExercicio());
    }

    @Test
    @DisplayName("Deve criar item com construtor vazio")
    void deveCriarItemComConstrutorVazio() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        assertNotNull(item);
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        
        item.setCargaKg(60);
        item.setRepeticoes(12);
        
        assertEquals(60, item.getCargaKg());
        assertEquals(12, item.getRepeticoes());
    }

    @Test
    @DisplayName("Deve definir plano de treino")
    void deveDefinirPlanoTreino() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        PlanoTreino plano = new PlanoTreino();
        plano.setIdPlano(5);
        
        item.setPlanoTreino(plano);
        
        assertEquals(plano, item.getPlanoTreino());
    }

    @Test
    @DisplayName("Deve definir exercicio")
    void deveDefinirExercicio() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        Exercicio exercicio = new Exercicio();
        exercicio.setIdExercicio(10);
        
        item.setExercicio(exercicio);
        
        assertEquals(10, item.getIdExercicio());
    }
}
