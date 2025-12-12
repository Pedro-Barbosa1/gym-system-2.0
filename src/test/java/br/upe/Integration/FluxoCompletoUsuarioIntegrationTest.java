package br.upe.Integration;

import br.upe.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integracao que simula o fluxo completo de um usuario no sistema.
 * Desde o cadastro ate a finalizacao de um treino.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Fluxo Completo do Usuario - Integration Test")
public class FluxoCompletoUsuarioIntegrationTest {

    private static EntityManager em;
    private static int usuarioId;
    private static int exercicio1Id;
    private static int exercicio2Id;
    private static int planoId;
    private static int sessaoId;

    @BeforeAll
    static void setup() {
        IntegrationTestHelper.initEntityManagerFactory();
        em = IntegrationTestHelper.createEntityManager();
    }

    @AfterAll
    static void teardown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        IntegrationTestHelper.closeEntityManagerFactory();
    }

    private static String gerarEmailUnico() {
        return "fluxo_" + UUID.randomUUID().toString().substring(0, 8) + "@email.com";
    }

    @Test
    @Order(1)
    @DisplayName("1. Usuario se cadastra no sistema")
    void usuarioSeCadastra() {
        em.getTransaction().begin();
        Usuario usuario = new Usuario("Carlos Almeida", gerarEmailUnico(), "minhasenha123", TipoUsuario.COMUM);
        em.persist(usuario);
        em.getTransaction().commit();

        usuarioId = usuario.getId();
        assertTrue(usuarioId > 0);
        assertEquals("Carlos Almeida", usuario.getNome());
    }

    @Test
    @Order(2)
    @DisplayName("2. Usuario registra seus indicadores iniciais")
    void usuarioRegistraIndicadoresIniciais() {
        em.getTransaction().begin();
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                .idUsuario(usuarioId)
                .data(LocalDate.now())
                .pesoKg(85.0)
                .alturaCm(178.0)
                .percentualGordura(22.0)
                .percentualMassaMagra(78.0)
                .imc(26.8) // Sobrepeso
                .build();
        em.persist(indicador);
        em.getTransaction().commit();

        assertTrue(indicador.getId() > 0);
    }

    @Test
    @Order(3)
    @DisplayName("3. Usuario cria seus exercicios personalizados")
    void usuarioCriaExercicios() {
        Usuario usuario = em.find(Usuario.class, usuarioId);

        em.getTransaction().begin();
        
        Exercicio ex1 = new Exercicio(usuario, "Supino Reto", "Exercicio para peitoral", "/gif/supino.gif");
        Exercicio ex2 = new Exercicio(usuario, "Remada Curvada", "Exercicio para costas", "/gif/remada.gif");
        
        em.persist(ex1);
        em.persist(ex2);
        em.getTransaction().commit();

        exercicio1Id = ex1.getIdExercicio();
        exercicio2Id = ex2.getIdExercicio();

        assertTrue(exercicio1Id > 0);
        assertTrue(exercicio2Id > 0);
    }

    @Test
    @Order(4)
    @DisplayName("4. Usuario cria um plano de treino")
    void usuarioCriaPlanoTreino() {
        Usuario usuario = em.find(Usuario.class, usuarioId);

        em.getTransaction().begin();
        PlanoTreino plano = new PlanoTreino(usuario, "Treino Full Body");
        em.persist(plano);
        em.getTransaction().commit();

        planoId = plano.getIdPlano();
        assertTrue(planoId > 0);
    }

    @Test
    @Order(5)
    @DisplayName("5. Usuario adiciona exercicios ao plano")
    void usuarioAdicionaExerciciosAoPlano() {
        Exercicio ex1 = em.find(Exercicio.class, exercicio1Id);
        Exercicio ex2 = em.find(Exercicio.class, exercicio2Id);

        em.getTransaction().begin();
        PlanoTreino plano = em.find(PlanoTreino.class, planoId);

        ItemPlanoTreino item1 = new ItemPlanoTreino(planoId, exercicio1Id, 40, 12);
        item1.setPlanoTreino(plano);
        item1.setExercicio(ex1);

        ItemPlanoTreino item2 = new ItemPlanoTreino(planoId, exercicio2Id, 30, 10);
        item2.setPlanoTreino(plano);
        item2.setExercicio(ex2);

        plano.adicionarItem(item1);
        plano.adicionarItem(item2);

        em.merge(plano);
        em.getTransaction().commit();

        em.clear();

        PlanoTreino atualizado = em.find(PlanoTreino.class, planoId);
        assertEquals(2, atualizado.getItensTreino().size());
    }

    @Test
    @Order(6)
    @DisplayName("6. Usuario inicia uma sessao de treino")
    void usuarioIniciaSessaoTreino() {
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
    @Order(7)
    @DisplayName("7. Usuario registra exercicios executados na sessao")
    void usuarioRegistraExerciciosExecutados() {
        Exercicio ex1 = em.find(Exercicio.class, exercicio1Id);
        Exercicio ex2 = em.find(Exercicio.class, exercicio2Id);

        em.getTransaction().begin();
        SessaoTreino sessao = em.find(SessaoTreino.class, sessaoId);

        // Usuario executou os exercicios com cargas e repeticoes reais
        ItemSessaoTreino item1 = new ItemSessaoTreino(sessaoId, exercicio1Id, 12, 42.0);
        item1.setSessaoTreino(sessao);
        item1.setExercicio(ex1);

        ItemSessaoTreino item2 = new ItemSessaoTreino(sessaoId, exercicio2Id, 10, 32.0);
        item2.setSessaoTreino(sessao);
        item2.setExercicio(ex2);

        sessao.adicionarItemExecutado(item1);
        sessao.adicionarItemExecutado(item2);

        em.merge(sessao);
        em.getTransaction().commit();

        em.clear();

        SessaoTreino atualizada = em.find(SessaoTreino.class, sessaoId);
        assertEquals(2, atualizada.getItensExecutados().size());
    }

    @Test
    @Order(8)
    @DisplayName("8. Usuario atualiza seus indicadores apos semanas de treino")
    void usuarioAtualizaIndicadores() {
        em.getTransaction().begin();
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                .idUsuario(usuarioId)
                .data(LocalDate.now())
                .pesoKg(82.0) // Perdeu 3kg
                .alturaCm(178.0)
                .percentualGordura(19.0) // Reduziu gordura
                .percentualMassaMagra(81.0) // Aumentou massa magra
                .imc(25.9)
                .build();
        em.persist(indicador);
        em.getTransaction().commit();

        assertTrue(indicador.getId() > 0);
    }

    @Test
    @Order(9)
    @DisplayName("9. Sistema lista historico de sessoes do usuario")
    void sistemaListaHistoricoSessoes() {
        List<SessaoTreino> sessoes = em.createQuery(
                "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario ORDER BY s.dataSessao DESC", 
                SessaoTreino.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertNotNull(sessoes);
        assertFalse(sessoes.isEmpty());
    }

    @Test
    @Order(10)
    @DisplayName("10. Sistema lista evolucao dos indicadores")
    void sistemaListaEvolucaoIndicadores() {
        List<IndicadorBiomedico> indicadores = em.createQuery(
                "SELECT i FROM IndicadorBiomedico i WHERE i.idUsuario = :idUsuario ORDER BY i.data ASC", 
                IndicadorBiomedico.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertNotNull(indicadores);
        assertEquals(2, indicadores.size());

        // Verifica evolucao positiva
        IndicadorBiomedico primeiro = indicadores.get(0);
        IndicadorBiomedico ultimo = indicadores.get(indicadores.size() - 1);

        assertTrue(ultimo.getPesoKg() < primeiro.getPesoKg()); // Peso diminuiu
        assertTrue(ultimo.getPercentualGordura() < primeiro.getPercentualGordura()); // Gordura diminuiu
        assertTrue(ultimo.getPercentualMassaMagra() > primeiro.getPercentualMassaMagra()); // Massa aumentou
    }

    @Test
    @Order(11)
    @DisplayName("11. Sistema verifica consistencia dos dados do usuario")
    void sistemaVerificaConsistenciaDados() {
        Usuario usuario = em.find(Usuario.class, usuarioId);
        
        // Verifica que todos os dados estao conectados corretamente
        assertNotNull(usuario);
        
        List<Exercicio> exercicios = em.createQuery(
                "SELECT e FROM Exercicio e WHERE e.usuario.id = :idUsuario", Exercicio.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();
        assertEquals(2, exercicios.size());

        List<PlanoTreino> planos = em.createQuery(
                "SELECT p FROM PlanoTreino p WHERE p.usuario.id = :idUsuario", PlanoTreino.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();
        assertEquals(1, planos.size());

        PlanoTreino plano = planos.get(0);
        assertEquals(2, plano.getItensTreino().size());
    }
}
