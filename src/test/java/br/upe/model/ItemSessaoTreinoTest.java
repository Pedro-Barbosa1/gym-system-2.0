package br.upe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemSessaoTreinoTest {

    @Test
    void deveCriarItemSessaoTreinoComTodosAtributos() {
        ItemSessaoTreino item = new ItemSessaoTreino(1, 7, 12, 45.5);

        assertEquals(7, item.getIdExercicio());
        assertEquals(12, item.getRepeticoesRealizadas());
        assertEquals(45.5, item.getCargaRealizada());
    }

    @Test
    void deveAlterarAtributosComSetters() {
        ItemSessaoTreino item = new ItemSessaoTreino(1, 1, 10, 30.0);

        item.setIdExercicio(2);
        item.setRepeticoesRealizadas(15);
        item.setCargaRealizada(50.75);

        assertEquals(2, item.getIdExercicio());
        assertEquals(15, item.getRepeticoesRealizadas());
        assertEquals(50.75, item.getCargaRealizada());
    }

    @Test
    void deveRetornarToStringFormatado() {
        ItemSessaoTreino item = new ItemSessaoTreino(1, 3, 8, 60.0);

        String esperado = "ID Exercício: 3, Repetições: 8, Carga: 60.0kg";

        assertEquals(esperado, item.toString());
    }
}
