package br.upe.service;

import br.upe.model.Exercicio;
import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.repository.IExercicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExercicioService Tests")
class ExercicioServiceTest {

    @Mock
    private IExercicioRepository exercicioRepository;

    private ExercicioService exercicioService;
    private Usuario usuario;
    private Exercicio exercicio;

    @BeforeEach
    void setUp() {
        exercicioService = new ExercicioService(exercicioRepository);
        usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
        exercicio = new Exercicio(usuario, "Supino", "Exercicio de peito", "/gif/supino.gif");
        exercicio.setIdExercicio(1);
    }

    @Test
    @DisplayName("Deve listar exercicios do usuario")
    void deveListarExerciciosDoUsuario() {
        List<Exercicio> exercicios = Arrays.asList(exercicio);
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(exercicios);
        
        List<Exercicio> resultado = exercicioService.listarExerciciosDoUsuario(1);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Supino", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar exercicio por id")
    void deveBuscarExercicioPorId() {
        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));
        
        Optional<Exercicio> resultado = exercicioService.buscarExercicioPorIdGlobal(1);
        
        assertTrue(resultado.isPresent());
        assertEquals("Supino", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio quando exercicio nao existe")
    void deveRetornarVazioQuandoExercicioNaoExiste() {
        when(exercicioRepository.buscarPorId(999)).thenReturn(Optional.empty());
        
        Optional<Exercicio> resultado = exercicioService.buscarExercicioPorIdGlobal(999);
        
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar exercicio do usuario por nome")
    void deveBuscarExercicioDoUsuarioPorNome() {
        List<Exercicio> exercicios = Arrays.asList(exercicio);
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(exercicios);
        
        Optional<Exercicio> resultado = exercicioService.buscarExercicioDoUsuarioPorNome(1, "Supino");
        
        assertTrue(resultado.isPresent());
        assertEquals("Supino", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio quando nome for null")
    void deveRetornarVazioQuandoNomeForNull() {
        Optional<Exercicio> resultado = exercicioService.buscarExercicioDoUsuarioPorNome(1, null);
        
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve retornar vazio quando nome for vazio")
    void deveRetornarVazioQuandoNomeForVazio() {
        Optional<Exercicio> resultado = exercicioService.buscarExercicioDoUsuarioPorNome(1, "   ");
        
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve deletar exercicio por nome com sucesso")
    void deveDeletarExercicioPorNomeComSucesso() {
        List<Exercicio> exercicios = Arrays.asList(exercicio);
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(exercicios);
        doNothing().when(exercicioRepository).deletar(1);
        
        boolean resultado = exercicioService.deletarExercicioPorNome(1, "Supino");
        
        assertTrue(resultado);
        verify(exercicioRepository).deletar(1);
    }

    @Test
    @DisplayName("Deve lancar excecao ao deletar com nome vazio")
    void deveLancarExcecaoAoDeletarComNomeVazio() {
        assertThrows(IllegalArgumentException.class, () -> 
            exercicioService.deletarExercicioPorNome(1, ""));
    }

    @Test
    @DisplayName("Deve retornar false ao deletar exercicio inexistente")
    void deveRetornarFalseAoDeletarExercicioInexistente() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(Collections.emptyList());
        
        boolean resultado = exercicioService.deletarExercicioPorNome(1, "Inexistente");
        
        assertFalse(resultado);
    }
}
