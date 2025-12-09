package br.upe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPlanoTreinoTest {

    @Test
    void deveCriarItemPlanoTreinoComTodosAtributos() {
        ItemPlanoTreino item = new ItemPlanoTreino(1, 5, 40, 12);

        assertEquals(5, item.getIdExercicio());
        assertEquals(40, item.getCargaKg());
        assertEquals(12, item.getRepeticoes());
    }

    @Test
    void deveAlterarAtributosComSetters() {
        ItemPlanoTreino item = new ItemPlanoTreino(1, 1, 20, 10);

        item.setCargaKg(35);
        item.setRepeticoes(15);

        assertEquals(1, item.getIdExercicio()); // ID do exercício não muda pois não há setter direto
        assertEquals(35, item.getCargaKg());
        assertEquals(15, item.getRepeticoes());
    }

    @Test
    void deveRetornarToStringFormatado() {
        ItemPlanoTreino item = new ItemPlanoTreino(1, 3, 50, 8);

        String esperado = "ID Exercício: 3, Carga: 50kg, Repetições: 8";

        assertEquals(esperado, item.toString());
    }
}
