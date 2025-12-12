package br.upe.Integration;

import br.upe.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integracao para SessaoTreino usando H2 em memoria.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SessaoTreino Integration Tests")
public class SessaoTreinoIntegrationTest {

    private static EntityManager em;
    private static int usuarioId;
    private static int planoId;
    private static int exercicioId;
    private static int sessaoId;

    private static String gerarEmailUnico() {
        return "sessao_" + UUID.randomUUID().toString().substring(0, 8) + "@email.com";
    }

    @BeforeAll
    static void setup() {
        IntegrationTestHelper.initEntityManagerFactory();
        em = IntegrationTestHelper.createEntityManager();

        // Criar dados necessarios para os testes
        em.getTransaction().begin();
        
        Usuario usuario = new Usuario("Teste Sessao", gerarEmailUnico(), "senha", TipoUsuario.COMUM);
        em.persist(usuario);
        usuarioId = usuario.getId();

        PlanoTreino plano = new PlanoTreino(usuario, "Treino para Sessao");
        em.persist(plano);
        planoId = plano.getIdPlano();

        Exercicio exercicio = new Exercicio(usuario, "Agachamento", "Exercicio de pernas", "/gif/agachamento.gif");
        em.persist(exercicio);
        exercicioId = exercicio.getIdExercicio();

        // Adicionar exercicio ao plano
        ItemPlanoTreino item = new ItemPlanoTreino(planoId, exercicioId, 60, 10);
        item.setPlanoTreino(plano);
        item.setExercicio(exercicio);
        plano.adicionarItem(item);
        em.merge(plano);

        em.getTransaction().commit();
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
    @DisplayName("Deve criar sessao de treino")
    void deveCriarSessaoTreino() {
        Usuario usuario = em.find(Usuario.class, usuarioId);
        PlanoTreino plano = em.find(PlanoTreino.class, planoId);

        em.getTransaction().begin();
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());
        em.persist(sessao);
        em.getTransaction().commit();

        sessaoId = sessao.getIdSessao();
        assertTrue(sessaoId > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Deve buscar sessao por id")
    void deveBuscarSessaoPorId() {
        SessaoTreino sessao = em.find(SessaoTreino.class, sessaoId);

        assertNotNull(sessao);
        assertEquals(LocalDate.now(), sessao.getDataSessao());
    }

    @Test
    @Order(3)
    @DisplayName("Deve adicionar item executado a sessao")
    void deveAdicionarItemExecutado() {
        Exercicio exercicio = em.find(Exercicio.class, exercicioId);

        em.getTransaction().begin();
        SessaoTreino sessao = em.find(SessaoTreino.class, sessaoId);
        
        ItemSessaoTreino itemExecutado = new ItemSessaoTreino(sessaoId, exercicioId, 10, 65.0);
        itemExecutado.setSessaoTreino(sessao);
        itemExecutado.setExercicio(exercicio);
        sessao.adicionarItemExecutado(itemExecutado);
        
        em.merge(sessao);
        em.getTransaction().commit();

        em.clear();

        SessaoTreino atualizada = em.find(SessaoTreino.class, sessaoId);
        assertFalse(atualizada.getItensExecutados().isEmpty());
        assertEquals(1, atualizada.getItensExecutados().size());
    }

    @Test
    @Order(4)
    @DisplayName("Deve buscar sessoes do usuario")
    void deveBuscarSessoesDoUsuario() {
        List<SessaoTreino> sessoes = em.createQuery(
                "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario", SessaoTreino.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertNotNull(sessoes);
        assertFalse(sessoes.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Deve buscar sessoes por periodo")
    void deveBuscarSessoesPorPeriodo() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now().plusDays(1);

        List<SessaoTreino> sessoes = em.createQuery(
                "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario AND s.dataSessao BETWEEN :inicio AND :fim", 
                SessaoTreino.class)
                .setParameter("idUsuario", usuarioId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        assertNotNull(sessoes);
        assertFalse(sessoes.isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Deve criar multiplas sessoes")
    void deveCriarMultiplasSessoes() {
        Usuario usuario = em.find(Usuario.class, usuarioId);
        PlanoTreino plano = em.find(PlanoTreino.class, planoId);

        em.getTransaction().begin();
        for (int i = 1; i <= 3; i++) {
            SessaoTreino sessao = new SessaoTreino();
            sessao.setUsuario(usuario);
            sessao.setPlanoTreino(plano);
            sessao.setDataSessao(LocalDate.now().minusDays(i));
            em.persist(sessao);
        }
        em.getTransaction().commit();

        List<SessaoTreino> sessoes = em.createQuery(
                "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario", SessaoTreino.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertTrue(sessoes.size() >= 4); // 1 original + 3 novas
    }

    @Test
    @Order(7)
    @DisplayName("Deve deletar sessao")
    void deveDeletarSessao() {
        Usuario usuario = em.find(Usuario.class, usuarioId);
        PlanoTreino plano = em.find(PlanoTreino.class, planoId);

        em.getTransaction().begin();
        SessaoTreino sessaoParaDeletar = new SessaoTreino();
        sessaoParaDeletar.setUsuario(usuario);
        sessaoParaDeletar.setPlanoTreino(plano);
        sessaoParaDeletar.setDataSessao(LocalDate.now());
        em.persist(sessaoParaDeletar);
        em.getTransaction().commit();

        int idDeletar = sessaoParaDeletar.getIdSessao();

        em.getTransaction().begin();
        SessaoTreino sessao = em.find(SessaoTreino.class, idDeletar);
        em.remove(sessao);
        em.getTransaction().commit();

        SessaoTreino deletada = em.find(SessaoTreino.class, idDeletar);
        assertNull(deletada);
    }
}
