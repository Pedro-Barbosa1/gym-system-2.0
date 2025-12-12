package br.upe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

@DisplayName("PlanoTreino Model Tests")
class PlanoTreinoTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
    }

    @Test
    @DisplayName("Deve criar plano de treino com construtor principal")
    void deveCriarPlanoTreinoComConstrutorPrincipal() {
        PlanoTreino plano = new PlanoTreino(usuario, "Treino A");
        
        assertEquals("Treino A", plano.getNome());
        assertEquals(usuario, plano.getUsuario());
    }

    @Test
    @DisplayName("Deve criar plano de treino com construtor vazio")
    void deveCriarPlanoTreinoComConstrutorVazio() {
        PlanoTreino plano = new PlanoTreino();
        assertNotNull(plano);
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        PlanoTreino plano = new PlanoTreino();
        
        plano.setIdPlano(10);
        plano.setNome("Treino B");
        plano.setUsuario(usuario);
        
        assertEquals(10, plano.getIdPlano());
        assertEquals("Treino B", plano.getNome());
        assertEquals(usuario, plano.getUsuario());
    }

    @Test
    @DisplayName("Deve adicionar item ao plano")
    void deveAdicionarItemAoPlano() {
        PlanoTreino plano = new PlanoTreino(usuario, "Treino A");
        plano.setIdPlano(1);
        
        ItemPlanoTreino item = new ItemPlanoTreino(1, 1, 50, 10);
        plano.adicionarItem(item);
        
        assertEquals(1, plano.getItensTreino().size());
        assertTrue(plano.getItensTreino().contains(item));
    }

    @Test
    @DisplayName("Deve retornar lista de itens vazia inicialmente")
    void deveRetornarListaDeItensVaziaInicialmente() {
        PlanoTreino plano = new PlanoTreino(usuario, "Treino A");
        
        assertNotNull(plano.getItensTreino());
        assertTrue(plano.getItensTreino().isEmpty());
    }

    @Test
    @DisplayName("Deve definir lista de itens")
    void deveDefinirListaDeItens() {
        PlanoTreino plano = new PlanoTreino(usuario, "Treino A");
        plano.setIdPlano(1);
        
        List<ItemPlanoTreino> itens = new ArrayList<>();
        itens.add(new ItemPlanoTreino(1, 1, 50, 10));
        itens.add(new ItemPlanoTreino(1, 2, 60, 12));
        
        plano.setItensTreino(itens);
        
        assertEquals(2, plano.getItensTreino().size());
    }

    @Test
    @DisplayName("Deve retornar id do usuario atraves do getIdUsuario")
    void deveRetornarIdDoUsuario() {
        PlanoTreino plano = new PlanoTreino(usuario, "Treino A");
        assertEquals(1, plano.getIdUsuario());
    }
}
