package br.upe.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.upe.model.IndicadorBiomedico;

class RelatorioDiferencaIndicadoresTest {

    private RelatorioDiferencaIndicadores relatorio;

    @BeforeEach
    void setup() {
        relatorio = new RelatorioDiferencaIndicadores();

        relatorio.setDataInicio(LocalDate.of(2025, 1, 1));
        relatorio.setDataFim(LocalDate.of(2025, 1, 31));

        // Indicadores podem ser variáveis locais
        IndicadorBiomedico inicial = new IndicadorBiomedico.Builder()
                .id(0)
                .idUsuario(0)
                .data(null)
                .pesoKg(70)
                .alturaCm(0)
                .percentualGordura(20)
                .percentualMassaMagra(75)
                .imc(22)
                .build();

        IndicadorBiomedico finalObj = new IndicadorBiomedico.Builder()
                .id(0)
                .idUsuario(0)
                .data(null)
                .pesoKg(68)
                .alturaCm(0)
                .percentualGordura(18)
                .percentualMassaMagra(77)
                .imc(21.5)
                .build();

        relatorio.setIndicadorInicial(inicial);
        relatorio.setIndicadorFinal(finalObj);

        relatorio.calcularDiferencas();
    }

     @Test
     void testCalcularDiferencasCorretamente() {
         assertEquals(-2.0, relatorio.getDiferencaPeso(), 0.01);
         assertEquals(-2.0, relatorio.getDiferencaPercentualGordura(), 0.01);
         assertEquals(2.0, relatorio.getDiferencaPercentualMassaMagra(), 0.01);
         assertEquals(-0.5, relatorio.getDiferencaImc(), 0.01);
     }

     @Test
     void testToStringConteudoFormatado() {
         String relatorioStr = relatorio.toString();
         assertTrue(relatorioStr.contains("Relatório de Evolução"));
         assertTrue(relatorioStr.contains("Peso (kg)"));
         assertTrue(relatorioStr.contains("Inicial"));
         assertTrue(relatorioStr.contains("Final"));
         assertTrue(relatorioStr.contains("-2.0")); // diferença peso negativa
     }

     @Test
    void testExportarParaCsvCriaArquivo() throws IOException {
        String caminho = "test-relatorio.csv";
        Path path = Paths.get(caminho);

        // Garante que o arquivo não exista antes
        Files.deleteIfExists(path);

        relatorio.exportarParaCsv(caminho);

        assertTrue(Files.exists(path));

        // Ler conteúdo e verificar algumas linhas
        String conteudo = Files.readString(path);
        assertTrue(conteudo.contains("Peso (kg)"));
        assertTrue(conteudo.contains("-2.0"));

        Files.deleteIfExists(path);
    }

}
