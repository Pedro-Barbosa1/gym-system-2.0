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
        item1 = new ItemPlanoTreino(1, 1, 20, 10);
        item2 = new ItemPlanoTreino(1, 2, 30, 8);
        Usuario usuario = new Usuario(100, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        plano = new PlanoTreino(usuario, "Treino A");
        plano.setIdPlano(1);
        plano.adicionarItem(item1);
    }

    @Test
    void testConstrutorComTodosOsCampos() {
        assertEquals(1, plano.getIdPlano());
        assertEquals(100, plano.getIdUsuario());
        assertEquals("Treino A", plano.getNome());
        assertEquals(1, plano.getItensTreino().size());
    }

    @Test
    void testConstrutorComIdUsuarioENome() {
        Usuario usuario = new Usuario(200, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        PlanoTreino novoPlano = new PlanoTreino(usuario, "Treino B");
        assertEquals(200, novoPlano.getIdUsuario());
        assertEquals("Treino B", novoPlano.getNome());
        assertTrue(novoPlano.getItensTreino().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        plano.setIdPlano(2);
        Usuario novoUsuario = new Usuario(300, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        plano.setUsuario(novoUsuario);
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
        Usuario usuario = new Usuario(101, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        PlanoTreino vazio = new PlanoTreino(usuario, "Treino Vazio");
        String result = vazio.toString();

        assertTrue(result.contains("ID Usuário: 101"));
        assertTrue(result.contains("[Este plano não possui exercícios ainda.]"));
    }
}
