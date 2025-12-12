package br.upe.service;

import br.upe.model.*;
import br.upe.repository.IExercicioRepository;
import br.upe.repository.IPlanoTreinoRepository;
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
@DisplayName("PlanoTreinoService Tests")
class PlanoTreinoServiceTest {

    @Mock
    private IPlanoTreinoRepository planoTreinoRepository;

    @Mock
    private IExercicioRepository exercicioRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    private PlanoTreinoService planoTreinoService;
    private Usuario usuario;
    private PlanoTreino planoTreino;
    private Exercicio exercicio;

    @BeforeEach
    void setUp() {
        planoTreinoService = new PlanoTreinoService(planoTreinoRepository, exercicioRepository, usuarioRepository);
        usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
        planoTreino = new PlanoTreino(usuario, "Treino A");
        planoTreino.setIdPlano(1);
        exercicio = new Exercicio(usuario, "Supino", "Exercicio de peito", "/gif/supino.gif");
        exercicio.setIdExercicio(1);
    }

    @Test
    @DisplayName("Deve criar plano com sucesso")
    void deveCriarPlanoComSucesso() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoTreinoRepository.salvar(any(PlanoTreino.class))).thenReturn(planoTreino);
        
        PlanoTreino resultado = planoTreinoService.criarPlano(1, "Treino A");
        
        assertNotNull(resultado);
        assertEquals("Treino A", resultado.getNome());
        verify(planoTreinoRepository).salvar(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar plano para usuario inexistente")
    void deveLancarExcecaoAoCriarPlanoParaUsuarioInexistente() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> 
            planoTreinoService.criarPlano(999, "Treino A"));
    }

    @Test
    @DisplayName("Deve adicionar exercicio ao plano")
    void deveAdicionarExercicioAoPlano() {
        when(planoTreinoRepository.buscarPorNome("Treino A", 1)).thenReturn(Optional.of(planoTreino));
        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));
        doNothing().when(planoTreinoRepository).editar(any(PlanoTreino.class));
        
        planoTreinoService.adicionarExercicioAoPlano(1, "Treino A", 1);
        
        verify(planoTreinoRepository).editar(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao adicionar exercicio a plano inexistente")
    void deveLancarExcecaoAoAdicionarExercicioAPlanoInexistente() {
        when(planoTreinoRepository.buscarPorNome("Inexistente", 1)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> 
            planoTreinoService.adicionarExercicioAoPlano(1, "Inexistente", 1));
    }

    @Test
    @DisplayName("Deve remover exercicio do plano")
    void deveRemoverExercicioDoPlano() {
        ItemPlanoTreino item = new ItemPlanoTreino(1, 1, 50, 10);
        planoTreino.adicionarItem(item);
        
        when(planoTreinoRepository.buscarPorNome("Treino A", 1)).thenReturn(Optional.of(planoTreino));
        doNothing().when(planoTreinoRepository).editar(any(PlanoTreino.class));
        
        planoTreinoService.removerExercicioDoPlano(1, "Treino A", 1);
        
        verify(planoTreinoRepository).editar(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve listar planos do usuario")
    void deveListarPlanosDoUsuario() {
        List<PlanoTreino> planos = Arrays.asList(planoTreino);
        when(planoTreinoRepository.buscarTodosDoUsuario(1)).thenReturn(planos);
        
        List<PlanoTreino> resultado = planoTreinoService.listarMeusPlanos(1);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Treino A", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve deletar plano com sucesso")
    void deveDeletarPlanoComSucesso() {
        when(planoTreinoRepository.buscarPorNome("Treino A", 1)).thenReturn(Optional.of(planoTreino));
        doNothing().when(planoTreinoRepository).deletar(1);
        
        boolean resultado = planoTreinoService.deletarPlano(1, "Treino A");
        
        assertTrue(resultado);
        verify(planoTreinoRepository).deletar(1);
    }

    @Test
    @DisplayName("Deve retornar false ao deletar plano inexistente")
    void deveRetornarFalseAoDeletarPlanoInexistente() {
        when(planoTreinoRepository.buscarPorNome("Inexistente", 1)).thenReturn(Optional.empty());
        
        boolean resultado = planoTreinoService.deletarPlano(1, "Inexistente");
        
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve buscar plano por id")
    void deveBuscarPlanoPorId() {
        when(planoTreinoRepository.buscarPorId(1)).thenReturn(Optional.of(planoTreino));
        
        Optional<PlanoTreino> resultado = planoTreinoService.buscarPlanoPorId(1);
        
        assertTrue(resultado.isPresent());
        assertEquals("Treino A", resultado.get().getNome());
    }
}
