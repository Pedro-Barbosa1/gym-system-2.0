package br.upe.Integration;

import br.upe.model.Exercicio;
import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.repository.IExercicioRepository;
import br.upe.repository.impl.ExercicioRepositoryImpl;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ExercicioIntegrationTest {

    private static IExercicioRepository repository;

    @BeforeAll
    static void setup() {
        repository = new ExercicioRepositoryImpl();
    }

    private Usuario criarUsuario(int id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome("User " + id);
        return u;
    }

    @Test
    @Order(1)
    void testSalvarExercicio() {
        Usuario user = criarUsuario(1);

        Exercicio e = new Exercicio(user, "Supino", "Peito", "supino.gif");

        Exercicio salvo = repository.salvar(e);

        assertNotEquals(0, salvo.getIdExercicio());
        assertEquals("Supino", salvo.getNome());
    }

    @Test
    @Order(2)
    void testBuscarTodosDoUsuario() {
        List<Exercicio> lista = repository.buscarTodosDoUsuario(1);
        assertFalse(lista.isEmpty());
    }


    @Test
    @Order(3)
    void testEditarExercicio() {
        Optional<Exercicio> opt = repository.buscarPorNome("Supino");
        assertTrue(opt.isPresent());

        Exercicio e = opt.get();
        e.setDescricao("Peito Completo");

        repository.editar(e);

        Optional<Exercicio> editado = repository.buscarPorId(e.getIdExercicio());
        assertEquals("Peito Completo", editado.get().getDescricao());
    }

    @Test
    @Order(4)
    void testDeletarExercicio() {
        // Criar um novo exercício sem dependências para garantir que pode ser deletado
        Usuario user = criarUsuario(1);
        Exercicio novoExercicio = new Exercicio(user, "Exercicio Teste Delete", "Teste", "teste.gif");
        Exercicio salvo = repository.salvar(novoExercicio);
        
        int idParaDeletar = salvo.getIdExercicio();
        
        // Tentar deletar
        repository.deletar(idParaDeletar);

        // Verificar que foi deletado
        Optional<Exercicio> deleted = repository.buscarPorId(idParaDeletar);
        assertTrue(deleted.isEmpty());
    }
}