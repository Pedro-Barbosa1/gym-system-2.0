package br.upe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveCriarUsuarioComTodosAtributos() {
        Usuario usuario = new Usuario(1, "João", "joao@email.com", "senha123", TipoUsuario.ADMIN);

        assertEquals(1, usuario.getId());
        assertEquals("João", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuario.getTipo());
    }

    @Test
    void deveCriarUsuarioSemId() {
        Usuario usuario = new Usuario("Maria", "maria@email.com", "senha456", TipoUsuario.COMUM);

        assertEquals("Maria", usuario.getNome());
        assertEquals("maria@email.com", usuario.getEmail());
        assertEquals("senha456", usuario.getSenha());
        assertEquals(TipoUsuario.COMUM, usuario.getTipo());
        // id padrão de int é 0
        assertEquals(0, usuario.getId());
    }

    @Test
    void deveAlterarAtributosComSetters() {
        Usuario usuario = new Usuario("Ana", "ana@email.com", "1234", TipoUsuario.COMUM);

        usuario.setId(10);
        usuario.setNome("Ana Clara");
        usuario.setEmail("anaclara@email.com");
        usuario.setSenha("5678");
        usuario.setTipo(TipoUsuario.ADMIN);

        assertEquals(10, usuario.getId());
        assertEquals("Ana Clara", usuario.getNome());
        assertEquals("anaclara@email.com", usuario.getEmail());
        assertEquals("5678", usuario.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuario.getTipo());
    }

    @Test
    void deveRetornarToStringFormatado() {
        Usuario usuario = new Usuario(5, "Carlos", "carlos@email.com", "senha987", TipoUsuario.COMUM);

        String esperado = "ID: 5, Nome: 'Carlos', Email: 'carlos@email.com', Tipo: COMUM";
        assertEquals(esperado, usuario.toString());
    }
}
