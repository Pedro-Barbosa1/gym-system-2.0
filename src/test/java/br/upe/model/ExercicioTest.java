package br.upe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exercicio Model Tests")
class ExercicioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
    }

    @Test
    @DisplayName("Deve criar exercicio com construtor principal")
    void deveCriarExercicioComConstrutorPrincipal() {
        Exercicio exercicio = new Exercicio(usuario, "Supino", "Exercicio de peito", "/gif/supino.gif");
        
        assertEquals("Supino", exercicio.getNome());
        assertEquals("Exercicio de peito", exercicio.getDescricao());
        assertEquals("/gif/supino.gif", exercicio.getCaminhoGif());
        assertEquals(usuario, exercicio.getUsuario());
    }

    @Test
    @DisplayName("Deve criar exercicio com construtor vazio")
    void deveCriarExercicioComConstrutorVazio() {
        Exercicio exercicio = new Exercicio();
        assertNotNull(exercicio);
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        Exercicio exercicio = new Exercicio();
        
        exercicio.setIdExercicio(5);
        exercicio.setNome("Agachamento");
        exercicio.setDescricao("Exercicio de pernas");
        exercicio.setCaminhoGif("/gif/agachamento.gif");
        exercicio.setUsuario(usuario);
        
        assertEquals(5, exercicio.getIdExercicio());
        assertEquals("Agachamento", exercicio.getNome());
        assertEquals("Exercicio de pernas", exercicio.getDescricao());
        assertEquals("/gif/agachamento.gif", exercicio.getCaminhoGif());
        assertEquals(usuario, exercicio.getUsuario());
    }

    @Test
    @DisplayName("Deve retornar id do usuario corretamente")
    void deveRetornarIdDoUsuario() {
        Exercicio exercicio = new Exercicio(usuario, "Rosca", "Exercicio de biceps", "/gif/rosca.gif");
        assertEquals(1, exercicio.getIdUsuario());
    }

    @Test
    @DisplayName("Deve retornar 0 quando usuario for null")
    void deveRetornarZeroQuandoUsuarioForNull() {
        Exercicio exercicio = new Exercicio();
        assertEquals(0, exercicio.getIdUsuario());
    }

    @Test
    @DisplayName("Deve gerar toString corretamente")
    void deveGerarToStringCorretamente() {
        Exercicio exercicio = new Exercicio();
        exercicio.setIdExercicio(1);
        exercicio.setNome("Supino");
        exercicio.setDescricao("Exercicio de peito");
        exercicio.setCaminhoGif("/gif/supino.gif");
        
        String toString = exercicio.toString();
        
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Supino"));
        assertTrue(toString.contains("Exercicio de peito"));
    }
}
