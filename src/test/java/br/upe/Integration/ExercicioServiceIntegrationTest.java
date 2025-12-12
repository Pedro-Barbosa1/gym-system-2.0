package br.upe.Integration;

import br.upe.model.*;
import br.upe.service.*;
import br.upe.repository.*;
import br.upe.repository.impl.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integracao para o fluxo completo de exercicios.
 * Testa a integracao entre Service -> Repository -> Database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Exercicio Service Integration Tests")
public class ExercicioServiceIntegrationTest {

    private static EntityManager em;
    private static int usuarioId;
    private static int exercicioId;

    private static String gerarEmailUnico() {
        return "exercicio_" + UUID.randomUUID().toString().substring(0, 8) + "@email.com";
    }

    @BeforeAll
    static void setup() {
        IntegrationTestHelper.initEntityManagerFactory();
        em = IntegrationTestHelper.createEntityManager();

        // Criar usuario para os testes
        em.getTransaction().begin();
        Usuario usuario = new Usuario("Teste Exercicio Service", gerarEmailUnico(), "senha", TipoUsuario.COMUM);
        em.persist(usuario);
        em.getTransaction().commit();
        usuarioId = usuario.getId();
    }

    @AfterAll
    static void teardown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        IntegrationTestHelper.closeEntityManagerFactory();
    }

    @Test
    @Order(1)
    @DisplayName("Deve criar exercicio via JPA")
    void deveCriarExercicioViaJPA() {
        Usuario usuario = em.find(Usuario.class, usuarioId);

        em.getTransaction().begin();
        Exercicio exercicio = new Exercicio(usuario, "Leg Press", "Exercicio de pernas", "/gif/legpress.gif");
        em.persist(exercicio);
        em.getTransaction().commit();

        exercicioId = exercicio.getIdExercicio();
        assertTrue(exercicioId > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Deve buscar exercicio por id via JPA")
    void deveBuscarExercicioPorId() {
        Exercicio encontrado = em.find(Exercicio.class, exercicioId);

        assertNotNull(encontrado);
        assertEquals("Leg Press", encontrado.getNome());
        assertEquals("Exercicio de pernas", encontrado.getDescricao());
    }

    @Test
    @Order(3)
    @DisplayName("Deve listar exercicios do usuario via JPQL")
    void deveListarExerciciosDoUsuario() {
        List<Exercicio> exercicios = em.createQuery(
                "SELECT e FROM Exercicio e WHERE e.usuario.id = :idUsuario", Exercicio.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertNotNull(exercicios);
        assertFalse(exercicios.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar exercicio via JPA")
    void deveAtualizarExercicio() {
        em.getTransaction().begin();
        Exercicio exercicio = em.find(Exercicio.class, exercicioId);
        exercicio.setNome("Leg Press 45");
        exercicio.setDescricao("Exercicio composto de pernas");
        em.merge(exercicio);
        em.getTransaction().commit();

        em.clear();

        Exercicio atualizado = em.find(Exercicio.class, exercicioId);
        assertEquals("Leg Press 45", atualizado.getNome());
        assertEquals("Exercicio composto de pernas", atualizado.getDescricao());
    }

    @Test
    @Order(5)
    @DisplayName("Deve criar multiplos exercicios")
    void deveCriarMultiplosExercicios() {
        Usuario usuario = em.find(Usuario.class, usuarioId);

        em.getTransaction().begin();
        em.persist(new Exercicio(usuario, "Extensora", "Exercicio de quadriceps", "/gif/extensora.gif"));
        em.persist(new Exercicio(usuario, "Flexora", "Exercicio de posterior", "/gif/flexora.gif"));
        em.persist(new Exercicio(usuario, "Panturrilha", "Exercicio de panturrilha", "/gif/panturrilha.gif"));
        em.getTransaction().commit();

        List<Exercicio> exercicios = em.createQuery(
                "SELECT e FROM Exercicio e WHERE e.usuario.id = :idUsuario", Exercicio.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertTrue(exercicios.size() >= 4);
    }

    @Test
    @Order(6)
    @DisplayName("Deve buscar exercicio por nome via JPQL")
    void deveBuscarExercicioPorNome() {
        List<Exercicio> exercicios = em.createQuery(
                "SELECT e FROM Exercicio e WHERE e.nome LIKE :nome", Exercicio.class)
                .setParameter("nome", "%Leg%")
                .getResultList();

        assertNotNull(exercicios);
        assertFalse(exercicios.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Deve deletar exercicio")
    void deveDeletarExercicio() {
        Usuario usuario = em.find(Usuario.class, usuarioId);

        em.getTransaction().begin();
        Exercicio exercicioParaDeletar = new Exercicio(usuario, "Para Deletar", "Desc", "/gif/deletar.gif");
        em.persist(exercicioParaDeletar);
        em.getTransaction().commit();

        int idDeletar = exercicioParaDeletar.getIdExercicio();

        em.getTransaction().begin();
        Exercicio ex = em.find(Exercicio.class, idDeletar);
        em.remove(ex);
        em.getTransaction().commit();

        Exercicio deletado = em.find(Exercicio.class, idDeletar);
        assertNull(deletado);
    }
}
