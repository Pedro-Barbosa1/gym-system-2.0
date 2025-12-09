package br.upe.repository;

import br.upe.model.Exercicio;
import br.upe.model.Usuario;
import br.upe.model.TipoUsuario;
import br.upe.repository.impl.ExercicioRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExercicioRepositoryImplTest {

    private ExercicioRepositoryImpl repository;

    @BeforeEach
    void setup() {
        repository = new ExercicioRepositoryImpl();
    }

    @Test
    void testSalvarExercicio() {
        Usuario usuario = new Usuario(1, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        Exercicio e = new Exercicio(usuario, "Supino", "Supino reto", "supino.gif");
        Exercicio salvo = repository.salvar(e);

        assertNotNull(salvo);
        assertEquals(0, salvo.getIdExercicio());
        assertEquals(1, repository.buscarTodosDoUsuario(1).size());
    }

    @Test
    void testBuscarPorId() {
        Usuario usuario = new Usuario(1, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        Exercicio e = repository.salvar(new Exercicio(usuario, "Agachamento", "Agachamento livre", "agachamento.gif"));
        Optional<Exercicio> encontrado = repository.buscarPorId(e.getIdExercicio());

        assertTrue(encontrado.isPresent());
        assertEquals("Agachamento", encontrado.get().getNome());
    }

    @Test
    void testBuscarPorNome() {
        Usuario usuario = new Usuario(2, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        repository.salvar(new Exercicio(usuario, "Leg Press", "Leg Press máquina", "legpress.gif"));
        Optional<Exercicio> encontrado = repository.buscarPorNome("Leg Press");

        assertTrue(encontrado.isPresent());
        assertEquals(2, encontrado.get().getIdUsuario());
    }

    @Test
    void testEditarExercicio() {
        Usuario usuario = new Usuario(1, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        Exercicio e = repository.salvar(new Exercicio(usuario, "Flexão", "Flexão no solo", "flexao.gif"));
        e.setDescricao("Flexão tradicional");
        repository.editar(e);

        Optional<Exercicio> editado = repository.buscarPorId(e.getIdExercicio());
        assertTrue(editado.isPresent());
        assertEquals("Flexão tradicional", editado.get().getDescricao());
    }

    @Test
    void testDeletarExercicio() {
        Usuario usuario = new Usuario(1, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        Exercicio e = repository.salvar(new Exercicio(usuario, "Puxada", "Puxada alta", "puxada.gif"));
        repository.deletar(e.getIdExercicio());

        Optional<Exercicio> deletado = repository.buscarPorId(e.getIdExercicio());
        assertFalse(deletado.isPresent());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        Usuario usuario1 = new Usuario(1, "Teste1", "teste1@email.com", "senha123", TipoUsuario.COMUM);
        Usuario usuario2 = new Usuario(2, "Teste2", "teste2@email.com", "senha123", TipoUsuario.COMUM);
        repository.salvar(new Exercicio(usuario1, "Supino", "Supino reto", "supino.gif"));
        repository.salvar(new Exercicio(usuario1, "Agachamento", "Agachamento livre", "agachamento.gif"));
        repository.salvar(new Exercicio(usuario2, "Leg Press", "Leg Press máquina", "legpress.gif"));

        List<Exercicio> exerciciosUsuario1 = repository.buscarTodosDoUsuario(1);
        List<Exercicio> exerciciosUsuario2 = repository.buscarTodosDoUsuario(2);

        assertEquals(2, exerciciosUsuario1.size());
        assertEquals(1, exerciciosUsuario2.size());
    }

    @Test
    void testProximoId() {
        Usuario usuario = new Usuario(1, "Teste", "teste@email.com", "senha123", TipoUsuario.COMUM);
        assertEquals(0, repository.proximoId());
        repository.salvar(new Exercicio(usuario, "Ex1", "Desc1", "gif1.gif"));
        assertEquals(1, repository.proximoId());
        repository.salvar(new Exercicio(usuario, "Ex2", "Desc2", "gif2.gif"));
        assertEquals(2, repository.proximoId());
    }

}
