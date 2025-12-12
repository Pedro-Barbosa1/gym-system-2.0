package br.upe.Integration;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.IIndicadorBiomedicoRepository;
import br.upe.repository.impl.IndicadorBiomedicoRepositoryImpl;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Teste de integracao que usa o repositorio real.
 * Requer MySQL rodando - desabilitado para CI/CD.
 */
@Disabled("Requer MySQL - usar IndicadorBiomedicoServiceIntegrationTest para H2")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IndicadorBiomedicoIntegrationTest {

    private static IIndicadorBiomedicoRepository repository;
    private static int indicadorIdSalvo;

    @BeforeAll
    static void setup() {
        repository = new IndicadorBiomedicoRepositoryImpl();
    }

    @Test
    @Order(1)
    void deveSalvarIndicador() {
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                .idUsuario(10)
                .data(LocalDate.now())
                .pesoKg(80)
                .alturaCm(180)
                .percentualGordura(15)
                .percentualMassaMagra(40)
                .imc(24.6)
                .build();

        IndicadorBiomedico salvo = repository.salvar(indicador);
        indicadorIdSalvo = salvo.getId();

        Assertions.assertTrue(salvo.getId() > 0);
    }

    @Test
    @Order(2)
    void deveBuscarPorId() {
        Optional<IndicadorBiomedico> encontrado = repository.buscarPorId(indicadorIdSalvo);

        Assertions.assertTrue(encontrado.isPresent());
        Assertions.assertEquals(indicadorIdSalvo, encontrado.get().getId());
    }

    @Test
    @Order(3)
    void deveListarPorUsuario() {
        List<IndicadorBiomedico> lista = repository.listarPorUsuario(10);

        Assertions.assertNotNull(lista);
        Assertions.assertFalse(lista.isEmpty());
    }

    @Test
    @Order(4)
    void deveBuscarPorPeriodo() {
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now().plusDays(1);

        List<IndicadorBiomedico> lista = repository.buscarPorPeriodo(10, inicio, fim);

        Assertions.assertNotNull(lista);
        Assertions.assertFalse(lista.isEmpty());
    }

    @Test
    @Order(5)
    void deveEditarIndicador() {
        Optional<IndicadorBiomedico> opt = repository.buscarPorId(indicadorIdSalvo);
        Assertions.assertTrue(opt.isPresent());

        IndicadorBiomedico indicador = opt.get();
        indicador.setPesoKg(90);

        repository.editar(indicador);

        Optional<IndicadorBiomedico> atualizado = repository.buscarPorId(indicadorIdSalvo);
        Assertions.assertEquals(90, atualizado.get().getPesoKg());
    }

    @Test
    @Order(6)
    void deveListarTodos() {
        List<IndicadorBiomedico> lista = repository.listarTodos();

        Assertions.assertNotNull(lista);
        Assertions.assertFalse(lista.isEmpty());
    }

    @Test
    @Order(7)
    void deveDeletar() {
        repository.deletar(indicadorIdSalvo);

        Optional<IndicadorBiomedico> resultado = repository.buscarPorId(indicadorIdSalvo);
        Assertions.assertTrue(resultado.isEmpty());
    }
}
