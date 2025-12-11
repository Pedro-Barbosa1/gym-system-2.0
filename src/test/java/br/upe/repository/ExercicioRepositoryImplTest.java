package br.upe.repository;

import br.upe.model.Exercicio;
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
        // Usa o construtor de teste para evitar carregar CSV
        repository = new ExercicioRepositoryImpl(false);
    }

    @Test
    void testSalvarExercicio() {
        Exercicio e = new Exercicio(1, "Supino", "Supino reto", "supino.gif");
        Exercicio salvo = repository.salvar(e);

        assertNotNull(salvo);
        assertEquals(0, salvo.getIdExercicio());
        assertEquals(1, repository.buscarTodosDoUsuario(1).size());
    }

    @Test
    void testBuscarPorId() {
        Exercicio e = repository.salvar(new Exercicio(1, "Agachamento", "Agachamento livre", "agachamento.gif"));
        Optional<Exercicio> encontrado = repository.buscarPorId(e.getIdExercicio());

        assertTrue(encontrado.isPresent());
        assertEquals("Agachamento", encontrado.get().getNome());
    }

    @Test
    void testBuscarPorNome() {
        repository.salvar(new Exercicio(2, "Leg Press", "Leg Press máquina", "legpress.gif"));
        Optional<Exercicio> encontrado = repository.buscarPorNome("Leg Press");

        assertTrue(encontrado.isPresent());
        assertEquals(2, encontrado.get().getIdUsuario());
    }

    @Test
    void testEditarExercicio() {
        Exercicio e = repository.salvar(new Exercicio(1, "Flexão", "Flexão no solo", "flexao.gif"));
        e.setDescricao("Flexão tradicional");
        repository.editar(e);

        Optional<Exercicio> editado = repository.buscarPorId(e.getIdExercicio());
        assertTrue(editado.isPresent());
        assertEquals("Flexão tradicional", editado.get().getDescricao());
    }

    @Test
    void testDeletarExercicio() {
        Exercicio e = repository.salvar(new Exercicio(1, "Puxada", "Puxada alta", "puxada.gif"));
        repository.deletar(e.getIdExercicio());

        Optional<Exercicio> deletado = repository.buscarPorId(e.getIdExercicio());
        assertFalse(deletado.isPresent());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        repository.salvar(new Exercicio(1, "Supino", "Supino reto", "supino.gif"));
        repository.salvar(new Exercicio(1, "Agachamento", "Agachamento livre", "agachamento.gif"));
        repository.salvar(new Exercicio(2, "Leg Press", "Leg Press máquina", "legpress.gif"));

        List<Exercicio> usuario1 = repository.buscarTodosDoUsuario(1);
        List<Exercicio> usuario2 = repository.buscarTodosDoUsuario(2);

        assertEquals(2, usuario1.size());
        assertEquals(1, usuario2.size());
    }

    @Test
    void testProximoId() {
        assertEquals(0, repository.proximoId());
        repository.salvar(new Exercicio(1, "Ex1", "Desc1", "gif1.gif"));
        assertEquals(1, repository.proximoId());
        repository.salvar(new Exercicio(1, "Ex2", "Desc2", "gif2.gif"));
        assertEquals(2, repository.proximoId());
    }

    @Test
    void testLimpar() {
        repository.salvar(new Exercicio(1, "Ex1", "Desc1", "gif1.gif"));
        repository.limpar();
        assertEquals(0, repository.buscarTodosDoUsuario(1).size());
    }
}
