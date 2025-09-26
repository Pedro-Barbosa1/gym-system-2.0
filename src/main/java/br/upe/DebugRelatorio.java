package br.upe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import br.upe.service.RelatorioDiferencaIndicadores;
import br.upe.model.IndicadorBiomedico;

public class DebugRelatorio {
    public static void main(String[] args) throws IOException {
        RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
        relatorio.dataInicio = LocalDate.of(2025,1,1);
        relatorio.dataFim = LocalDate.of(2025,1,31);

        IndicadorBiomedico inicial = new IndicadorBiomedico(0, null, 0, 0, 0, 0, 0);
        inicial.setPesoKg(70.0);
        inicial.setPercentualGordura(20.0);
        inicial.setPercentualMassaMagra(75.0);
        inicial.setImc(22.0);

        IndicadorBiomedico finalObj = new IndicadorBiomedico(0, null, 0, 0, 0, 0, 0);
        finalObj.setPesoKg(68.0);
        finalObj.setPercentualGordura(18.0);
        finalObj.setPercentualMassaMagra(77.0);
        finalObj.setImc(21.5);

        relatorio.indicadorInicial = Optional.of(inicial);
        relatorio.indicadorFinal = Optional.of(finalObj);
        relatorio.calcularDiferencas();

        String s = relatorio.toString();
        System.out.println("---- toString output ----");
        System.out.println(s);
        System.out.println("---- end ----");

        String caminho = "test-relatorio.csv";
        relatorio.exportarParaCsv(caminho);
        System.out.println("Wrote CSV to: " + Paths.get(caminho).toAbsolutePath());
        System.out.println("Exists? " + Files.exists(Paths.get(caminho)));
    }
}
