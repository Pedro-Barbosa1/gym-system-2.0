package br.upe.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DisplayName("SessaoTreino Model Tests")
class SessaoTreinoTest {

    @Test
    @DisplayName("Deve criar sessao de treino com construtor completo")
    void deveCriarSessaoTreinoComConstrutorCompleto() {
        List<ItemSessaoTreino> itens = new ArrayList<>();
        LocalDate data = LocalDate.now();
        
        SessaoTreino sessao = new SessaoTreino(1, 1, 1, data, itens);
        
        assertEquals(1, sessao.getIdSessao());
        assertEquals(1, sessao.getIdUsuario());
        assertEquals(1, sessao.getIdPlanoTreino());
        assertEquals(data, sessao.getDataSessao());
        assertEquals(itens, sessao.getItensExecutados());
    }

    @Test
    @DisplayName("Deve criar sessao de treino com construtor simples")
    void deveCriarSessaoTreinoComConstrutorSimples() {
        SessaoTreino sessao = new SessaoTreino(1, 1);
        
        assertEquals(1, sessao.getIdUsuario());
        assertEquals(1, sessao.getIdPlanoTreino());
        assertEquals(LocalDate.now(), sessao.getDataSessao());
        assertNotNull(sessao.getItensExecutados());
        assertTrue(sessao.getItensExecutados().isEmpty());
    }

    @Test
    @DisplayName("Deve criar sessao de treino com construtor vazio")
    void deveCriarSessaoTreinoComConstrutorVazio() {
        SessaoTreino sessao = new SessaoTreino();
        assertNotNull(sessao);
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        SessaoTreino sessao = new SessaoTreino();
        LocalDate data = LocalDate.of(2024, 1, 15);
        
        sessao.setIdSessao(5);
        sessao.setIdUsuario(2);
        sessao.setIdPlanoTreino(3);
        sessao.setDataSessao(data);
        
        assertEquals(5, sessao.getIdSessao());
        assertEquals(2, sessao.getIdUsuario());
        assertEquals(3, sessao.getIdPlanoTreino());
        assertEquals(data, sessao.getDataSessao());
    }

    @Test
    @DisplayName("Deve adicionar item executado")
    void deveAdicionarItemExecutado() {
        SessaoTreino sessao = new SessaoTreino(1, 1);
        sessao.setIdSessao(1);
        
        ItemSessaoTreino item = new ItemSessaoTreino(1, 1, 10, 50.0);
        sessao.adicionarItemExecutado(item);
        
        assertEquals(1, sessao.getItensExecutados().size());
        assertTrue(sessao.getItensExecutados().contains(item));
    }

    @Test
    @DisplayName("Deve definir lista de itens executados")
    void deveDefinirListaDeItensExecutados() {
        SessaoTreino sessao = new SessaoTreino(1, 1);
        sessao.setIdSessao(1);
        
        List<ItemSessaoTreino> itens = new ArrayList<>();
        itens.add(new ItemSessaoTreino(1, 1, 10, 50.0));
        itens.add(new ItemSessaoTreino(1, 2, 12, 60.0));
        
        sessao.setItensExecutados(itens);
        
        assertEquals(2, sessao.getItensExecutados().size());
    }

    @Test
    @DisplayName("Deve retornar 0 quando usuario for null")
    void deveRetornarZeroQuandoUsuarioForNull() {
        SessaoTreino sessao = new SessaoTreino();
        assertEquals(0, sessao.getIdUsuario());
    }

    @Test
    @DisplayName("Deve retornar 0 quando plano for null")
    void deveRetornarZeroQuandoPlanoForNull() {
        SessaoTreino sessao = new SessaoTreino();
        assertEquals(0, sessao.getIdPlanoTreino());
    }
}
