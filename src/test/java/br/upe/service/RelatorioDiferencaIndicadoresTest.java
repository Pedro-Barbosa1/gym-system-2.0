package br.upe.service;

import br.upe.model.IndicadorBiomedico;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RelatorioDiferencaIndicadores Tests")
class RelatorioDiferencaIndicadoresTest {

    private RelatorioDiferencaIndicadores relatorio;
    private IndicadorBiomedico indicadorInicial;
    private IndicadorBiomedico indicadorFinal;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        relatorio = new RelatorioDiferencaIndicadores();
        
        indicadorInicial = new IndicadorBiomedico.Builder()
                .idUsuario(1)
                .pesoKg(80.0)
                .percentualGordura(25.0)
                .percentualMassaMagra(75.0)
                .imc(26.0)
                .data(LocalDate.of(2024, 1, 1))
                .build();

        indicadorFinal = new IndicadorBiomedico.Builder()
                .idUsuario(1)
                .pesoKg(75.0)
                .percentualGordura(20.0)
                .percentualMassaMagra(80.0)
                .imc(24.0)
                .data(LocalDate.of(2024, 6, 1))
                .build();
    }

    @Test
    @DisplayName("Deve calcular diferencas corretamente")
    void deveCalcularDiferencasCorretamente() {
        relatorio.setIndicadorInicial(indicadorInicial);
        relatorio.setIndicadorFinal(indicadorFinal);
        
        relatorio.calcularDiferencas();
        
        assertEquals(-5.0, relatorio.getDiferencaPeso(), 0.01);
        assertEquals(-5.0, relatorio.getDiferencaPercentualGordura(), 0.01);
        assertEquals(5.0, relatorio.getDiferencaPercentualMassaMagra(), 0.01);
        assertEquals(-2.0, relatorio.getDiferencaImc(), 0.01);
    }

    @Test
    @DisplayName("Nao deve calcular diferencas sem indicador inicial")
    void naoDeveCalcularDiferencasSemIndicadorInicial() {
        relatorio.setIndicadorFinal(indicadorFinal);
        
        // Não deve lançar exceção, apenas log warning
        assertDoesNotThrow(() -> relatorio.calcularDiferencas());
        assertEquals(0.0, relatorio.getDiferencaPeso());
    }

    @Test
    @DisplayName("Nao deve calcular diferencas sem indicador final")
    void naoDeveCalcularDiferencasSemIndicadorFinal() {
        relatorio.setIndicadorInicial(indicadorInicial);
        
        // Não deve lançar exceção, apenas log warning
        assertDoesNotThrow(() -> relatorio.calcularDiferencas());
        assertEquals(0.0, relatorio.getDiferencaPeso());
    }

    @Test
    @DisplayName("Deve gerar toString com dados completos")
    void deveGerarToStringComDadosCompletos() {
        relatorio.setDataInicio(LocalDate.of(2024, 1, 1));
        relatorio.setDataFim(LocalDate.of(2024, 6, 1));
        relatorio.setIndicadorInicial(indicadorInicial);
        relatorio.setIndicadorFinal(indicadorFinal);
        relatorio.calcularDiferencas();
        
        String resultado = relatorio.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("Relatório de Evolução"));
        assertTrue(resultado.contains("01/01/2024"));
        assertTrue(resultado.contains("01/06/2024"));
        assertTrue(resultado.contains("Peso"));
        assertTrue(resultado.contains("Gordura"));
        assertTrue(resultado.contains("Massa Magra"));
        assertTrue(resultado.contains("IMC"));
    }

    @Test
    @DisplayName("Deve gerar toString sem indicadores")
    void deveGerarToStringSemIndicadores() {
        relatorio.setDataInicio(LocalDate.of(2024, 1, 1));
        relatorio.setDataFim(LocalDate.of(2024, 6, 1));
        
        String resultado = relatorio.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("Nenhum dado encontrado"));
    }

    @Test
    @DisplayName("Deve gerar toString sem datas")
    void deveGerarToStringSemDatas() {
        String resultado = relatorio.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("N/A"));
    }

    @Test
    @DisplayName("Deve exportar para CSV corretamente")
    void deveExportarParaCsvCorretamente() throws IOException {
        relatorio.setDataInicio(LocalDate.of(2024, 1, 1));
        relatorio.setDataFim(LocalDate.of(2024, 6, 1));
        relatorio.setIndicadorInicial(indicadorInicial);
        relatorio.setIndicadorFinal(indicadorFinal);
        relatorio.calcularDiferencas();
        
        Path csvPath = tempDir.resolve("relatorio.csv");
        relatorio.exportarParaCsv(csvPath.toString());
        
        assertTrue(Files.exists(csvPath));
        List<String> linhas = Files.readAllLines(csvPath);
        
        assertEquals(5, linhas.size());
        assertEquals("Indicador,Inicial,Final,Diferença", linhas.get(0));
        assertTrue(linhas.get(1).contains("Peso (kg)"));
        assertTrue(linhas.get(2).contains("Gordura"));
        assertTrue(linhas.get(3).contains("Massa Magra"));
        assertTrue(linhas.get(4).contains("IMC"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao exportar CSV sem indicador inicial")
    void deveLancarExcecaoAoExportarCsvSemIndicadorInicial() {
        relatorio.setIndicadorFinal(indicadorFinal);
        Path csvPath = tempDir.resolve("relatorio.csv");
        
        assertThrows(IllegalStateException.class, 
            () -> relatorio.exportarParaCsv(csvPath.toString()));
    }

    @Test
    @DisplayName("Deve lancar excecao ao exportar CSV sem indicador final")
    void deveLancarExcecaoAoExportarCsvSemIndicadorFinal() {
        relatorio.setIndicadorInicial(indicadorInicial);
        Path csvPath = tempDir.resolve("relatorio.csv");
        
        assertThrows(IllegalStateException.class, 
            () -> relatorio.exportarParaCsv(csvPath.toString()));
    }

    @Test
    @DisplayName("Deve lancar excecao ao setar indicador inicial null")
    void deveLancarExcecaoAoSetarIndicadorInicialNull() {
        assertThrows(NullPointerException.class, 
            () -> relatorio.setIndicadorInicial(null));
    }

    @Test
    @DisplayName("Deve lancar excecao ao setar indicador final null")
    void deveLancarExcecaoAoSetarIndicadorFinalNull() {
        assertThrows(NullPointerException.class, 
            () -> relatorio.setIndicadorFinal(null));
    }

    @Test
    @DisplayName("Deve calcular diferenca de ganho de peso")
    void deveCalcularDiferencaDeGanhoDePeso() {
        IndicadorBiomedico inicial = new IndicadorBiomedico.Builder()
                .idUsuario(1)
                .pesoKg(70.0)
                .percentualGordura(15.0)
                .percentualMassaMagra(85.0)
                .imc(22.0)
                .data(LocalDate.now().minusMonths(3))
                .build();

        IndicadorBiomedico finalInd = new IndicadorBiomedico.Builder()
                .idUsuario(1)
                .pesoKg(75.0)
                .percentualGordura(14.0)
                .percentualMassaMagra(86.0)
                .imc(23.5)
                .data(LocalDate.now())
                .build();

        relatorio.setIndicadorInicial(inicial);
        relatorio.setIndicadorFinal(finalInd);
        relatorio.calcularDiferencas();
        
        assertEquals(5.0, relatorio.getDiferencaPeso(), 0.01);
        assertEquals(-1.0, relatorio.getDiferencaPercentualGordura(), 0.01);
        assertEquals(1.0, relatorio.getDiferencaPercentualMassaMagra(), 0.01);
        assertEquals(1.5, relatorio.getDiferencaImc(), 0.01);
    }

    @Test
    @DisplayName("Deve calcular diferenca zero quando indicadores sao iguais")
    void deveCalcularDiferencaZeroQuandoIndicadoresSaoIguais() {
        relatorio.setIndicadorInicial(indicadorInicial);
        relatorio.setIndicadorFinal(indicadorInicial); // Mesmo indicador
        relatorio.calcularDiferencas();
        
        assertEquals(0.0, relatorio.getDiferencaPeso(), 0.01);
        assertEquals(0.0, relatorio.getDiferencaPercentualGordura(), 0.01);
        assertEquals(0.0, relatorio.getDiferencaPercentualMassaMagra(), 0.01);
        assertEquals(0.0, relatorio.getDiferencaImc(), 0.01);
    }

    @Test
    @DisplayName("Deve testar getters de diferencas")
    void deveTestarGettersDeDiferencas() {
        relatorio.setIndicadorInicial(indicadorInicial);
        relatorio.setIndicadorFinal(indicadorFinal);
        relatorio.calcularDiferencas();
        
        assertNotEquals(0.0, relatorio.getDiferencaPeso());
        assertNotEquals(0.0, relatorio.getDiferencaPercentualGordura());
        assertNotEquals(0.0, relatorio.getDiferencaPercentualMassaMagra());
        assertNotEquals(0.0, relatorio.getDiferencaImc());
    }
}
