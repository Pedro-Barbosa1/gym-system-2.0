package br.upe.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

import br.upe.model.IndicadorBiomedico;

public class RelatorioDiferencaIndicadores {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private IndicadorBiomedico indicadorInicial;
    private IndicadorBiomedico indicadorFinal;

    private double diferencaPeso;
    private double diferencaPercentualGordura;
    private double diferencaPercentualMassaMagra;
    private double diferencaImc;

    private static final Logger logger = Logger.getLogger(RelatorioDiferencaIndicadores.class.getName());

    // Constantes
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String MSG_INDICADORES_NAO_DEFINIDOS = "Indicadores inicial ou final não estão definidos.";

    public void calcularDiferencas() {
        if (indicadorInicial != null && indicadorFinal != null) {
            this.diferencaPeso = indicadorFinal.getPesoKg() - indicadorInicial.getPesoKg();
            this.diferencaPercentualGordura = indicadorFinal.getPercentualGordura() - indicadorInicial.getPercentualGordura();
            this.diferencaPercentualMassaMagra = indicadorFinal.getPercentualMassaMagra() - indicadorInicial.getPercentualMassaMagra();
            this.diferencaImc = indicadorFinal.getImc() - indicadorInicial.getImc();
        } else {
            logger.warning(MSG_INDICADORES_NAO_DEFINIDOS);
        }
    }

    @Override
    public String toString() {
        if (indicadorInicial == null || indicadorFinal == null) {
            logger.warning(MSG_INDICADORES_NAO_DEFINIDOS);
            return String.format("Relatório de Evolução (%s a %s)%nNenhum dado encontrado no período.",
                    dataInicio != null ? dataInicio.format(DATE_FORMATTER) : "N/A",
                    dataFim != null ? dataFim.format(DATE_FORMATTER) : "N/A");
        }

        IndicadorBiomedico inicial = indicadorInicial;
        IndicadorBiomedico finalObj = indicadorFinal;

        String template = """
        --- Relatório de Evolução: %s a %s ---
        | Indicador              | %-15s | %-15s | %-17s |
        |------------------------|-----------------|-----------------|-------------------|
        | Peso (kg)              | %-15.1f | %-15.1f | %+17.1f |
        | Gordura (%%)            | %-15.1f | %-15.1f | %+17.1f |
        | Massa Magra (%%)       | %-15.1f | %-15.1f | %+17.1f |
        | IMC                    | %-15.2f | %-15.2f | %+17.2f |
        -----------------------------------------------------------------------------------
        """;

        return String.format(Locale.US, template,
                dataInicio.format(DATE_FORMATTER), dataFim.format(DATE_FORMATTER),
                "Inicial", "Final", "Diferença",
                inicial.getPesoKg(), finalObj.getPesoKg(), diferencaPeso,
                inicial.getPercentualGordura(), finalObj.getPercentualGordura(), diferencaPercentualGordura,
                inicial.getPercentualMassaMagra(), finalObj.getPercentualMassaMagra(), diferencaPercentualMassaMagra,
                inicial.getImc(), finalObj.getImc(), diferencaImc
        );
    }

    public void exportarParaCsv(String caminhoArquivo) throws IOException {
        if (indicadorInicial == null || indicadorFinal == null) {
            logger.warning(MSG_INDICADORES_NAO_DEFINIDOS);
            throw new IllegalStateException("Indicadores inicial ou final não estão presentes para exportar.");
        }

        Path path = Paths.get(caminhoArquivo);
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.append("Indicador,Inicial,Final,Diferença\n");

            writer.append(String.format(Locale.US, "Peso (kg),%.1f,%.1f,%+.1f%n",
                    indicadorInicial.getPesoKg(),
                    indicadorFinal.getPesoKg(),
                    diferencaPeso));

            writer.append(String.format(Locale.US, "Gordura (%%),%.1f,%.1f,%+.1f%n",
                    indicadorInicial.getPercentualGordura(),
                    indicadorFinal.getPercentualGordura(),
                    diferencaPercentualGordura));

            writer.append(String.format(Locale.US, "Massa Magra (%%),%.1f,%.1f,%+.1f%n",
                    indicadorInicial.getPercentualMassaMagra(),
                    indicadorFinal.getPercentualMassaMagra(),
                    diferencaPercentualMassaMagra));

            writer.append(String.format(Locale.US, "IMC,%.2f,%.2f,%+.2f%n",
                    indicadorInicial.getImc(),
                    indicadorFinal.getImc(),
                    diferencaImc));
        }
    }

    // Getters e Setters
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public void setIndicadorInicial(IndicadorBiomedico indicadorInicial) {
        this.indicadorInicial = Objects.requireNonNull(indicadorInicial, "Indicador inicial não pode ser null");
    }

    public void setIndicadorFinal(IndicadorBiomedico indicadorFinal) {
        this.indicadorFinal = Objects.requireNonNull(indicadorFinal, "Indicador final não pode ser null");
    }

    public double getDiferencaPeso() {
        return diferencaPeso;
    }

    public double getDiferencaPercentualMassaMagra() {
        return diferencaPercentualMassaMagra;
    }

    public double getDiferencaImc() {
        return diferencaImc;
    }

    public double getDiferencaPercentualGordura() {
        return diferencaPercentualGordura;
    }
}
