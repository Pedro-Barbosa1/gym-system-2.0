package br.upe.service;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
        usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
    }

    @Test
    @DisplayName("Deve autenticar usuario com sucesso")
    void deveAutenticarUsuarioComSucesso() {
        when(usuarioRepository.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        
        Usuario resultado = usuarioService.autenticarUsuario("joao@email.com", "senha123");
        
        assertNotNull(resultado);
        assertEquals("Joao", resultado.getNome());
    }

    @Test
    @DisplayName("Deve retornar null para email inexistente")
    void deveRetornarNullParaEmailInexistente() {
        when(usuarioRepository.buscarPorEmail("inexistente@email.com")).thenReturn(Optional.empty());
        
        Usuario resultado = usuarioService.autenticarUsuario("inexistente@email.com", "senha123");
        
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar null para senha incorreta")
    void deveRetornarNullParaSenhaIncorreta() {
        when(usuarioRepository.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        
        Usuario resultado = usuarioService.autenticarUsuario("joao@email.com", "senhaErrada");
        
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve cadastrar usuario com sucesso")
    void deveCadastrarUsuarioComSucesso() {
        when(usuarioRepository.buscarPorEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(new Usuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM));
        
        Usuario resultado = usuarioService.cadastrarUsuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);
        
        assertNotNull(resultado);
        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cadastrar email duplicado")
    void deveLancarExcecaoAoCadastrarEmailDuplicado() {
        when(usuarioRepository.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        
        assertThrows(RuntimeException.class, () -> 
            usuarioService.cadastrarUsuario("Outro", "joao@email.com", "senha", TipoUsuario.COMUM));
    }

    @Test
    @DisplayName("Deve buscar usuario por id")
    void deveBuscarUsuarioPorId() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        
        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorId(1);
        
        assertTrue(resultado.isPresent());
        assertEquals("Joao", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve buscar usuario por email")
    void deveBuscarUsuarioPorEmail() {
        when(usuarioRepository.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        
        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorEmail("joao@email.com");
        
        assertTrue(resultado.isPresent());
        assertEquals("Joao", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve listar todos usuarios")
    void deveListarTodosUsuarios() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.listarTodos()).thenReturn(usuarios);
        
        List<Usuario> resultado = usuarioService.listarTodosUsuarios();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar usuario com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).editar(any(Usuario.class));
        
        usuarioService.atualizarUsuario(1, "Joao Silva", "joao.silva@email.com", "novaSenha", TipoUsuario.ADMIN);
        
        verify(usuarioRepository).editar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar usuario inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> 
            usuarioService.atualizarUsuario(999, "Nome", "email@test.com", "senha", TipoUsuario.COMUM));
    }

    @Test
    @DisplayName("Deve remover usuario com sucesso")
    void deveRemoverUsuarioComSucesso() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).deletar(1);
        
        usuarioService.removerUsuario(1);
        
        verify(usuarioRepository).deletar(1);
    }

    @Test
    @DisplayName("Deve promover usuario a admin")
    void devePromoverUsuarioAAdmin() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).editar(any(Usuario.class));
        
        usuarioService.promoverUsuarioAAdmin(1);
        
        verify(usuarioRepository).editar(argThat(u -> u.getTipo() == TipoUsuario.ADMIN));
    }

    @Test
    @DisplayName("Deve rebaixar usuario a comum")
    void deveRebaixarUsuarioAComum() {
        usuario.setTipo(TipoUsuario.ADMIN);
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).editar(any(Usuario.class));
        
        usuarioService.rebaixarUsuarioAComum(1);
        
        verify(usuarioRepository).editar(argThat(u -> u.getTipo() == TipoUsuario.COMUM));
    }
}
