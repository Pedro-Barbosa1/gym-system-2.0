package br.upe.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario Model Tests")
class UsuarioTest {

    @Test
    @DisplayName("Deve criar usuario com construtor completo")
    void deveCriarUsuarioComConstrutorCompleto() {
        Usuario usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
        
        assertEquals(1, usuario.getId());
        assertEquals("Joao", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals(TipoUsuario.COMUM, usuario.getTipo());
    }

    @Test
    @DisplayName("Deve criar usuario com construtor sem id")
    void deveCriarUsuarioComConstrutorSemId() {
        Usuario usuario = new Usuario("Maria", "maria@email.com", "senha456", TipoUsuario.ADMIN);
        
        assertEquals("Maria", usuario.getNome());
        assertEquals("maria@email.com", usuario.getEmail());
        assertEquals("senha456", usuario.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuario.getTipo());
    }

    @Test
    @DisplayName("Deve criar usuario com construtor vazio")
    void deveCriarUsuarioComConstrutorVazio() {
        Usuario usuario = new Usuario();
        assertNotNull(usuario);
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        Usuario usuario = new Usuario();
        
        usuario.setId(10);
        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");
        usuario.setSenha("novaSenha");
        usuario.setTipo(TipoUsuario.ADMIN);
        
        assertEquals(10, usuario.getId());
        assertEquals("Pedro", usuario.getNome());
        assertEquals("pedro@email.com", usuario.getEmail());
        assertEquals("novaSenha", usuario.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuario.getTipo());
    }

    @Test
    @DisplayName("TipoUsuario COMUM deve ter valor 0")
    void tipoUsuarioComumDeveSerValorZero() {
        assertEquals(0, TipoUsuario.COMUM.getValor());
    }

    @Test
    @DisplayName("TipoUsuario ADMIN deve ter valor 1")
    void tipoUsuarioAdminDeveSerValorUm() {
        assertEquals(1, TipoUsuario.ADMIN.getValor());
    }

    @Test
    @DisplayName("Deve converter valor para TipoUsuario")
    void deveConverterValorParaTipoUsuario() {
        assertEquals(TipoUsuario.COMUM, TipoUsuario.fromValor(0));
        assertEquals(TipoUsuario.ADMIN, TipoUsuario.fromValor(1));
    }

    @Test
    @DisplayName("Deve lancar excecao para valor de TipoUsuario invalido")
    void deveLancarExcecaoParaValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> TipoUsuario.fromValor(99));
    }
}
