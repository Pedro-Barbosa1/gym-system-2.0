package br.upe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanoTreinoTest {

    private PlanoTreino plano;
    private ItemPlanoTreino item1;
    private ItemPlanoTreino item2;

    @BeforeEach
    void setUp() {
        item1 = new ItemPlanoTreino(1, 20, 10);
        item2 = new ItemPlanoTreino(2, 30, 8);
        plano = new PlanoTreino(1, 100, "Treino A", new ArrayList<>(List.of(item1)));
    }

    @Test
    void testConstrutorComTodosOsCampos() {
        assertEquals(1, plano.getIdPlano());
        assertEquals(100, plano.getIdUsuario());
        assertEquals("Treino A", plano.getNome());
        assertEquals(1, plano.getItensTreino().size());
        assertEquals(item1, plano.getItensTreino().get(0));
    }

    @Test
    void testConstrutorComIdUsuarioENome() {
        PlanoTreino novoPlano = new PlanoTreino(200, "Treino B");
        assertEquals(200, novoPlano.getIdUsuario());
        assertEquals("Treino B", novoPlano.getNome());
        assertTrue(novoPlano.getItensTreino().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        plano.setIdPlano(2);
        plano.setIdUsuario(300);
        plano.setNome("Treino C");

        List<ItemPlanoTreino> novosItens = new ArrayList<>();
        novosItens.add(item2);
        plano.setItensTreino(novosItens);

        assertEquals(2, plano.getIdPlano());
        assertEquals(300, plano.getIdUsuario());
        assertEquals("Treino C", plano.getNome());
        assertEquals(1, plano.getItensTreino().size());
        assertEquals(item2, plano.getItensTreino().get(0));
    }

    @Test
    void testAdicionarItem() {
        assertEquals(1, plano.getItensTreino().size());

        plano.adicionarItem(item2);

        assertEquals(2, plano.getItensTreino().size());
        assertTrue(plano.getItensTreino().contains(item2));
    }

    @Test
    void testToStringComItens() {
        plano.adicionarItem(item2);
        String result = plano.toString();

        assertTrue(result.contains("ID Plano: 1"));
        assertTrue(result.contains("ID Usuário: 100"));
        assertTrue(result.contains("Nome: 'Treino A'"));
        assertTrue(result.contains("Exercícios no Plano"));
        assertTrue(result.contains("ID Exercício: 1, Carga: 20kg, Repetições: 10"));
        assertTrue(result.contains("ID Exercício: 2, Carga: 30kg, Repetições: 8"));
    }

    @Test
    void testToStringSemItens() {
        PlanoTreino vazio = new PlanoTreino(101, "Treino Vazio");
        String result = vazio.toString();

        assertTrue(result.contains("ID Usuário: 101"));
        assertTrue(result.contains("[Este plano não possui exercícios ainda.]"));
    }
}
