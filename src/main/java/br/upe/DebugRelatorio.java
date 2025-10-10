package br.upe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.upe.service.RelatorioDiferencaIndicadores;
import br.upe.model.IndicadorBiomedico;

public class DebugRelatorio {
    private static final Logger logger = Logger.getLogger(DebugRelatorio.class.getName());
    public static void main(String[] args) throws IOException {
        RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
        relatorio.setDataInicio(LocalDate.of(2025, 1, 1));
        relatorio.setDataFim(LocalDate.of(2025, 1, 31));

        IndicadorBiomedico inicial = new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(0)
        .data(null)
        .pesoKg(0)
        .alturaCm(0)
        .percentualGordura(0)
        .percentualMassaMagra(0)
        .imc(0)
        .build();
        inicial.setPesoKg(70.0);
        inicial.setPercentualGordura(20.0);
        inicial.setPercentualMassaMagra(75.0);
        inicial.setImc(22.0);

        IndicadorBiomedico finalObj = new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(0)
        .data(null)
        .pesoKg(0)
        .alturaCm(0)
        .percentualGordura(0)
        .percentualMassaMagra(0)
        .imc(0)
        .build();
        finalObj.setPesoKg(68.0);
        finalObj.setPercentualGordura(18.0);
        finalObj.setPercentualMassaMagra(77.0);
        finalObj.setImc(21.5);

        relatorio.setIndicadorInicial(inicial);
        relatorio.setIndicadorFinal(finalObj);
        relatorio.calcularDiferencas();

        String s = relatorio.toString();
        logger.info("---- toString output ----");
        logger.info(s);
        logger.info("---- end ----");

        String caminho = "test-relatorio.csv";
        relatorio.exportarParaCsv(caminho);

        Path path = Paths.get(caminho);
        logger.log(Level.INFO, "Wrote CSV to: {0}", path.toAbsolutePath());
        logger.log(Level.INFO, "Exists? {0}", Files.exists(path));

    }
}
