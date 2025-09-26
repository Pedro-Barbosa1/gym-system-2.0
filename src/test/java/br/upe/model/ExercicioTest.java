package br.upe.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExercicioTest {

    @Test
    void deveCriarExercicioComTodosAtributos() {
        Exercicio exercicio = new Exercicio(1, 10, "Agachamento", "Exercício para pernas", "agachamento.gif");

        assertEquals(1, exercicio.getIdExercicio());
        assertEquals(10, exercicio.getIdUsuario());
        assertEquals("Agachamento", exercicio.getNome());
        assertEquals("Exercício para pernas", exercicio.getDescricao());
        assertEquals("agachamento.gif", exercicio.getCaminhoGif());
    }

    @Test
    void deveCriarExercicioSemIdExercicio() {
        Exercicio exercicio = new Exercicio(20, "Flexão", "Exercício para peito", "flexao.gif");

        assertEquals(20, exercicio.getIdUsuario());
        assertEquals("Flexão", exercicio.getNome());
        assertEquals("Exercício para peito", exercicio.getDescricao());
        assertEquals("flexao.gif", exercicio.getCaminhoGif());

        // Como idExercicio não foi informado, deve ser 0 (valor default de int)
        assertEquals(0, exercicio.getIdExercicio());
    }

    @Test
    void deveAlterarAtributosComSetters() {
        Exercicio exercicio = new Exercicio(1, 10, "Agachamento", "Exercício para pernas", "agachamento.gif");

        exercicio.setIdExercicio(2);
        exercicio.setIdUsuario(30);
        exercicio.setNome("Abdominal");
        exercicio.setDescricao("Exercício para abdômen");
        exercicio.setCaminhoGif("abdominal.gif");

        assertEquals(2, exercicio.getIdExercicio());
        assertEquals(30, exercicio.getIdUsuario());
        assertEquals("Abdominal", exercicio.getNome());
        assertEquals("Exercício para abdômen", exercicio.getDescricao());
        assertEquals("abdominal.gif", exercicio.getCaminhoGif());
    }

    @Test
    void deveRetornarToStringFormatado() {
        Exercicio exercicio = new Exercicio(1, 10, "Agachamento", "Exercício para pernas", "agachamento.gif");

        String esperado = "ID: 1 | Nome: Agachamento | Descrição: Exercício para pernas | GIF: agachamento.gif";

        assertEquals(esperado, exercicio.toString());
    }
}