package br.upe.util;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para UserSession.
 */
@DisplayName("UserSession Tests")
class UserSessionTest {

    @BeforeEach
    void setup() {
        // Limpar sessao antes de cada teste
        UserSession.getInstance().limparSessao();
    }

    @AfterEach
    void teardown() {
        UserSession.getInstance().limparSessao();
    }

    @Test
    @DisplayName("Deve retornar mesma instância (Singleton)")
    void deveRetornarMesmaInstancia() {
        UserSession session1 = UserSession.getInstance();
        UserSession session2 = UserSession.getInstance();
        
        assertSame(session1, session2);
    }

    @Test
    @DisplayName("Deve iniciar sem usuario logado")
    void deveIniciarSemUsuarioLogado() {
        assertFalse(UserSession.getInstance().isLogado());
        assertNull(UserSession.getInstance().getUsuarioLogado());
    }

    @Test
    @DisplayName("Deve retornar 0 para ID quando nao ha usuario logado")
    void deveRetornarZeroQuandoNaoHaUsuario() {
        assertEquals(0, UserSession.getInstance().getIdUsuarioLogado());
    }

    @Test
    @DisplayName("Deve definir usuario logado")
    void deveDefinirUsuarioLogado() {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "senha", TipoUsuario.COMUM);
        usuario.setId(5);
        
        UserSession.getInstance().setUsuarioLogado(usuario);
        
        assertTrue(UserSession.getInstance().isLogado());
        assertNotNull(UserSession.getInstance().getUsuarioLogado());
        assertEquals("Teste", UserSession.getInstance().getUsuarioLogado().getNome());
    }

    @Test
    @DisplayName("Deve obter ID do usuario logado")
    void deveObterIdDoUsuarioLogado() {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "senha", TipoUsuario.COMUM);
        usuario.setId(10);
        
        UserSession.getInstance().setUsuarioLogado(usuario);
        
        assertEquals(10, UserSession.getInstance().getIdUsuarioLogado());
    }

    @Test
    @DisplayName("Deve limpar sessao (logout)")
    void deveLimparSessao() {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "senha", TipoUsuario.COMUM);
        usuario.setId(5);
        
        UserSession.getInstance().setUsuarioLogado(usuario);
        assertTrue(UserSession.getInstance().isLogado());
        
        UserSession.getInstance().limparSessao();
        
        assertFalse(UserSession.getInstance().isLogado());
        assertNull(UserSession.getInstance().getUsuarioLogado());
        assertEquals(0, UserSession.getInstance().getIdUsuarioLogado());
    }

    @Test
    @DisplayName("Deve substituir usuario logado")
    void deveSubstituirUsuarioLogado() {
        Usuario usuario1 = new Usuario("Usuario1", "user1@email.com", "senha1", TipoUsuario.COMUM);
        usuario1.setId(1);
        
        Usuario usuario2 = new Usuario("Usuario2", "user2@email.com", "senha2", TipoUsuario.ADMIN);
        usuario2.setId(2);
        
        UserSession.getInstance().setUsuarioLogado(usuario1);
        assertEquals("Usuario1", UserSession.getInstance().getUsuarioLogado().getNome());
        
        UserSession.getInstance().setUsuarioLogado(usuario2);
        assertEquals("Usuario2", UserSession.getInstance().getUsuarioLogado().getNome());
        assertEquals(2, UserSession.getInstance().getIdUsuarioLogado());
    }
}
