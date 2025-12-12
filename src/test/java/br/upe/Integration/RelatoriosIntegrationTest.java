package br.upe.Integration;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.impl.IndicadorBiomedicoRepositoryImpl;
import br.upe.service.IndicadorBiomedicoService;
import br.upe.service.RelatorioDiferencaIndicadores;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RelatoriosIntegrationTest {

    private static IndicadorBiomedicoService service;


    private static final Path DB_TESTE = Paths.get("target/indicadores_teste_integracao.csv");
    private static final Path CSV_EXPORTADO = Paths.get("target/relatorio_exportado.csv");


    private static final Path CAMINHO_ORIGINAL = Paths.get("src/main/resources/data/indicadores.csv");

    @BeforeAll
    static void setup() throws IOException {

        Files.deleteIfExists(DB_TESTE);
        Files.deleteIfExists(CSV_EXPORTADO);


        IndicadorBiomedicoRepositoryImpl.setCaminhoArquivo(DB_TESTE);


        service = new IndicadorBiomedicoService();
    }

    @AfterAll
    static void tearDown() throws IOException {
        // Limpeza final e restauração do caminho original
        Files.deleteIfExists(DB_TESTE);
        Files.deleteIfExists(CSV_EXPORTADO);

        IndicadorBiomedicoRepositoryImpl.setCaminhoArquivo(CAMINHO_ORIGINAL);
    }

    @Test
    @Order(1)
    void deveCadastrarDadosParaCenarioDeRelatorio() {
        // Cenário: Usuário 50 tinha peso alto mês passado e baixou este mês.

        service.cadastrarIndicador(50, LocalDate.of(2025, 3, 1), 85.0, 175.0, 22.0, 38.0);

        service.cadastrarIndicador(50, LocalDate.of(2025, 4, 1), 80.0, 175.0, 18.0, 40.0);

        List<IndicadorBiomedico> lista = service.listarTodosDoUsuario(50);

        Assertions.assertEquals(2, lista.size(), "Deveria ter salvo 2 registros no arquivo de teste.");
    }

    @Test
    @Order(2)
    void deveGerarRelatorioPorData() {
        LocalDate inicio = LocalDate.of(2025, 3, 1);
        LocalDate fim = LocalDate.of(2025, 4, 30);

        List<IndicadorBiomedico> relatorio = service.gerarRelatorioPorData(50, inicio, fim);

        Assertions.assertNotNull(relatorio);
        Assertions.assertEquals(2, relatorio.size());

        Assertions.assertEquals(LocalDate.of(2025, 3, 1), relatorio.get(0).getData());
    }

    @Test
    @Order(3)
    void deveGerarRelatorioDeDiferencaEvolutiva() {

        RelatorioDiferencaIndicadores resultado = service.gerarRelatorioDiferenca(
                50,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 4, 1)
        );

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(-5.0, resultado.getDiferencaPeso(), 0.01, "O peso deveria ter baixado 5kg");
        Assertions.assertEquals(-4.0, resultado.getDiferencaPercentualGordura(), 0.01, "Gordura deveria ter baixado 4%");
    }

    @Test
    @Order(4)
    void deveExportarRelatorioParaCsv() throws IOException {
        LocalDate inicio = LocalDate.of(2025, 3, 1);
        LocalDate fim = LocalDate.of(2025, 4, 30);

        service.exportarRelatorioPorDataParaCsv(50, inicio, fim, CSV_EXPORTADO.toString());


        Assertions.assertTrue(Files.exists(CSV_EXPORTADO), "O arquivo não foi criado.");
        String conteudo = Files.readString(CSV_EXPORTADO);
        Assertions.assertFalse(conteudo.isEmpty());
        Assertions.assertTrue(conteudo.contains("Data;Peso (Kg);Altura (cm);Gordura (%);Massa Magra (%);IMC"));

        Assertions.assertTrue(conteudo.contains("2025-03-01;85.0"));
        Assertions.assertTrue(conteudo.contains("2025-04-01;80.0"));
    }

    @Test
    @Order(5)
    void deveValidarDatasInvalidas() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.gerarRelatorioPorData(50, LocalDate.now(), LocalDate.now().minusDays(1));
        });
    }
}