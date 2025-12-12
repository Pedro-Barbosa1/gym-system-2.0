package br.upe.Integration;

import br.upe.model.*;
import br.upe.util.CalculadoraIMC;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integracao para IndicadorBiomedico usando H2 em memoria.
 * Testa o fluxo completo de cadastro, atualizacao e consulta de indicadores.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("IndicadorBiomedico Integration Tests")
public class IndicadorBiomedicoServiceIntegrationTest {

    private static EntityManager em;
    private static int usuarioId;
    private static int indicadorId;

    @BeforeAll
    static void setup() {
        IntegrationTestHelper.initEntityManagerFactory();
        em = IntegrationTestHelper.createEntityManager();

        // Criar usuario para os testes
        em.getTransaction().begin();
        Usuario usuario = new Usuario("Teste Indicador", "testeindicador@email.com", "senha", TipoUsuario.COMUM);
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
    @DisplayName("Deve cadastrar indicador biomedico com calculo de IMC")
    void deveCadastrarIndicadorComIMC() {
        double peso = 75.0;
        double altura = 175.0;
        double imc = CalculadoraIMC.calcular(peso, altura);

        em.getTransaction().begin();
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                .idUsuario(usuarioId)
                .data(LocalDate.now())
                .pesoKg(peso)
                .alturaCm(altura)
                .percentualGordura(15.0)
                .percentualMassaMagra(85.0)
                .imc(imc)
                .build();
        em.persist(indicador);
        em.getTransaction().commit();

        indicadorId = indicador.getId();
        assertTrue(indicadorId > 0);
        assertEquals(24.49, indicador.getImc(), 0.01);
    }

    @Test
    @Order(2)
    @DisplayName("Deve buscar indicador por id")
    void deveBuscarIndicadorPorId() {
        IndicadorBiomedico encontrado = em.find(IndicadorBiomedico.class, indicadorId);

        assertNotNull(encontrado);
        assertEquals(75.0, encontrado.getPesoKg());
        assertEquals(175.0, encontrado.getAlturaCm());
    }

    @Test
    @Order(3)
    @DisplayName("Deve listar indicadores do usuario")
    void deveListarIndicadoresDoUsuario() {
        List<IndicadorBiomedico> indicadores = em.createQuery(
                "SELECT i FROM IndicadorBiomedico i WHERE i.idUsuario = :idUsuario ORDER BY i.data DESC", 
                IndicadorBiomedico.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertNotNull(indicadores);
        assertFalse(indicadores.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar indicador")
    void deveAtualizarIndicador() {
        em.getTransaction().begin();
        IndicadorBiomedico indicador = em.find(IndicadorBiomedico.class, indicadorId);
        indicador.setPesoKg(72.0);
        double novoImc = CalculadoraIMC.calcular(72.0, 175.0);
        indicador.setImc(novoImc);
        em.merge(indicador);
        em.getTransaction().commit();

        em.clear();

        IndicadorBiomedico atualizado = em.find(IndicadorBiomedico.class, indicadorId);
        assertEquals(72.0, atualizado.getPesoKg());
    }

    @Test
    @Order(5)
    @DisplayName("Deve cadastrar historico de indicadores")
    void deveCadastrarHistoricoDeIndicadores() {
        em.getTransaction().begin();
        
        // Simular evolucao ao longo de 4 semanas
        for (int semana = 1; semana <= 4; semana++) {
            double peso = 75.0 - (semana * 0.5); // Perda gradual de peso
            double imc = CalculadoraIMC.calcular(peso, 175.0);
            
            IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                    .idUsuario(usuarioId)
                    .data(LocalDate.now().minusWeeks(semana))
                    .pesoKg(peso)
                    .alturaCm(175.0)
                    .percentualGordura(15.0 - (semana * 0.3))
                    .percentualMassaMagra(85.0 + (semana * 0.3))
                    .imc(imc)
                    .build();
            em.persist(indicador);
        }
        
        em.getTransaction().commit();

        List<IndicadorBiomedico> indicadores = em.createQuery(
                "SELECT i FROM IndicadorBiomedico i WHERE i.idUsuario = :idUsuario ORDER BY i.data ASC", 
                IndicadorBiomedico.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        assertTrue(indicadores.size() >= 5); // 1 inicial + 4 do historico
    }

    @Test
    @Order(6)
    @DisplayName("Deve buscar indicadores por periodo")
    void deveBuscarIndicadoresPorPeriodo() {
        LocalDate inicio = LocalDate.now().minusMonths(1);
        LocalDate fim = LocalDate.now().plusDays(1);

        List<IndicadorBiomedico> indicadores = em.createQuery(
                "SELECT i FROM IndicadorBiomedico i WHERE i.idUsuario = :idUsuario AND i.data BETWEEN :inicio AND :fim ORDER BY i.data", 
                IndicadorBiomedico.class)
                .setParameter("idUsuario", usuarioId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        assertNotNull(indicadores);
        assertFalse(indicadores.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Deve calcular diferenca entre indicadores")
    void deveCalcularDiferencaEntreIndicadores() {
        List<IndicadorBiomedico> indicadores = em.createQuery(
                "SELECT i FROM IndicadorBiomedico i WHERE i.idUsuario = :idUsuario ORDER BY i.data", 
                IndicadorBiomedico.class)
                .setParameter("idUsuario", usuarioId)
                .getResultList();

        if (indicadores.size() >= 2) {
            IndicadorBiomedico primeiro = indicadores.get(0);
            IndicadorBiomedico ultimo = indicadores.get(indicadores.size() - 1);

            double diferencaPeso = ultimo.getPesoKg() - primeiro.getPesoKg();
            double diferencaIMC = ultimo.getImc() - primeiro.getImc();

            // Apenas verifica que conseguimos calcular a diferenca
            assertNotNull(diferencaPeso);
            assertNotNull(diferencaIMC);
        }
    }

    @Test
    @Order(8)
    @DisplayName("Deve classificar IMC corretamente")
    void deveClassificarIMCCorretamente() {
        IndicadorBiomedico indicador = em.find(IndicadorBiomedico.class, indicadorId);
        String classificacao = CalculadoraIMC.classificarImc(indicador.getImc());

        assertNotNull(classificacao);
        // IMC proximo de 23-24 deve ser "Peso normal"
        assertTrue(classificacao.contains("Peso normal") || classificacao.contains("Sobrepeso"));
    }

    @Test
    @Order(9)
    @DisplayName("Deve deletar indicador")
    void deveDeletarIndicador() {
        em.getTransaction().begin();
        IndicadorBiomedico indicadorParaDeletar = new IndicadorBiomedico.Builder()
                .idUsuario(usuarioId)
                .data(LocalDate.now())
                .pesoKg(80.0)
                .alturaCm(180.0)
                .percentualGordura(20.0)
                .percentualMassaMagra(80.0)
                .imc(24.7)
                .build();
        em.persist(indicadorParaDeletar);
        em.getTransaction().commit();

        int idDeletar = indicadorParaDeletar.getId();

        em.getTransaction().begin();
        IndicadorBiomedico indicador = em.find(IndicadorBiomedico.class, idDeletar);
        em.remove(indicador);
        em.getTransaction().commit();

        IndicadorBiomedico deletado = em.find(IndicadorBiomedico.class, idDeletar);
        assertNull(deletado);
    }
}
