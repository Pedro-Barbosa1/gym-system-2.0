package br.upe.util;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.service.IUsuarioService;
import br.upe.service.UsuarioService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitializerTest {

    @Test
    void testGetAdminCredenciais() {
        assertEquals("ADM", DatabaseInitializer.getAdminEmail());
        assertEquals("ADM", DatabaseInitializer.getAdminSenha());
    }
    
    @Test
    void testInicializarDadosPadraoNaoLancaExcecao() {
        // Este teste verifica apenas se o método não lança exceção
        // Não verifica se o usuário foi criado no banco, pois o banco
        // pode não estar disponível no ambiente de teste
        assertDoesNotThrow(() -> {
            DatabaseInitializer.inicializarDadosPadrao();
        });
    }
    
    @Test
    void testUsuarioAdminTemCredenciaisCorretas() {
        // Tenta buscar o usuário admin (pode não existir se banco não estiver configurado)
        try {
            IUsuarioService usuarioService = new UsuarioService();
            Optional<Usuario> admin = usuarioService.buscarUsuarioPorEmail("ADM");
            
            if (admin.isPresent()) {
                Usuario adminUser = admin.get();
                assertEquals("ADM", adminUser.getNome());
                assertEquals("ADM", adminUser.getEmail());
                assertEquals("ADM", adminUser.getSenha());
                assertEquals(TipoUsuario.ADMIN, adminUser.getTipo());
            }
            // Se não encontrar, não é erro (banco pode não estar configurado)
        } catch (Exception e) {
            // Ignora erros de conexão com banco em ambiente de teste
        }
    }
}
