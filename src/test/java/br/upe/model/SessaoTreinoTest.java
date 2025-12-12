package br.upe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessaoTreinoTest {

    private SessaoTreino sessao;
    private ItemSessaoTreino item1;
    private ItemSessaoTreino item2;
    private LocalDate dataFixa;

    @BeforeEach
    void setUp() {
        dataFixa = LocalDate.of(2025, 9, 25);
        item1 = new ItemSessaoTreino(1, 1, 10, 20.0);
        item2 = new ItemSessaoTreino(1, 2, 8, 30.5);
        sessao = new SessaoTreino(1, 100, 200, dataFixa, new ArrayList<>(List.of(item1)));
    }

    @Test
    void testConstrutorComTodosCampos() {
        assertEquals(1, sessao.getIdSessao());
        assertEquals(100, sessao.getIdUsuario());
        assertEquals(200, sessao.getIdPlanoTreino());
        assertEquals(dataFixa, sessao.getDataSessao());
        assertEquals(1, sessao.getItensExecutados().size());
        assertEquals(item1, sessao.getItensExecutados().get(0));
    }

    @Test
    void testConstrutorComIdUsuarioEPlano() {
        SessaoTreino novaSessao = new SessaoTreino(101, 201);
        assertEquals(101, novaSessao.getIdUsuario());
        assertEquals(201, novaSessao.getIdPlanoTreino());
        assertNotNull(novaSessao.getDataSessao()); // deve ser LocalDate.now()
        assertTrue(novaSessao.getItensExecutados().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        sessao.setIdSessao(2);
        sessao.setIdUsuario(300);
        sessao.setIdPlanoTreino(400);
        LocalDate novaData = LocalDate.of(2024, 12, 31);
        sessao.setDataSessao(novaData);

        List<ItemSessaoTreino> novosItens = new ArrayList<>();
        novosItens.add(item2);
        sessao.setItensExecutados(novosItens);

        assertEquals(2, sessao.getIdSessao());
        assertEquals(300, sessao.getIdUsuario());
        assertEquals(400, sessao.getIdPlanoTreino());
        assertEquals(novaData, sessao.getDataSessao());
        assertEquals(1, sessao.getItensExecutados().size());
        assertEquals(item2, sessao.getItensExecutados().get(0));
    }

    @Test
    void testAdicionarItemExecutado() {
        assertEquals(1, sessao.getItensExecutados().size());

        sessao.adicionarItemExecutado(item2);

        assertEquals(2, sessao.getItensExecutados().size());
        assertTrue(sessao.getItensExecutados().contains(item2));
    }

    @Test
    void testToStringComItens() {
        sessao.adicionarItemExecutado(item2);
        String result = sessao.toString();

        assertTrue(result.contains("ID Sessão: 1"));
        assertTrue(result.contains("ID Usuário: 100"));
        assertTrue(result.contains("ID Plano: 200"));
        assertTrue(result.contains("Data: " + dataFixa));
        assertTrue(result.contains("Exercícios Registrados"));
        assertTrue(result.contains("ID Exercício: 1, Repetições: 10, Carga: 20.0kg"));
        assertTrue(result.contains("ID Exercício: 2, Repetições: 8, Carga: 30.5kg"));
    }

    @Test
    void testToStringSemItens() {
        SessaoTreino vazia = new SessaoTreino(101, 201);
        String result = vazia.toString();

        assertTrue(result.contains("ID Usuário: 101"));
        assertTrue(result.contains("[Nenhum exercício registrado nesta sessão.]"));
    }
}
