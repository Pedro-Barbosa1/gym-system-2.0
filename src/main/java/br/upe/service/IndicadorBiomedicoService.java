package br.upe.service;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.IIndicadorBiomedicoRepository;
import br.upe.repository.impl.IndicadorBiomedicoRepositoryImpl;
import br.upe.util.CalculadoraIMC;

import java.io.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndicadorBiomedicoService implements IIndicadorBiomedicoService {

    private static final Logger logger = Logger.getLogger(IndicadorBiomedicoService.class.getName());

    private final IIndicadorBiomedicoRepository indicadorRepository;

    public IndicadorBiomedicoService() {
        this.indicadorRepository = new IndicadorBiomedicoRepositoryImpl();
    }

    // Verifica condições e cadastra os indicadores
    @Override
    public IndicadorBiomedico cadastrarIndicador(int idUsuario, LocalDate data, double pesoKg, double alturaCm,
                                                 double percentualGordura, double percentualMassaMagra) {
        if (pesoKg <= 0 || alturaCm <= 0) {
            throw new IllegalArgumentException("Peso e altura devem ser maiores que zero.");
        }
        if (percentualGordura < 0 || percentualMassaMagra < 0) {
            throw new IllegalArgumentException("Percentuais de gordura e massa magra não podem ser negativos.");
        }
        if (data == null) {
            data = LocalDate.now();
        }

        double imc = CalculadoraIMC.calcular(pesoKg, alturaCm);

        IndicadorBiomedico novoIndicador = new IndicadorBiomedico.Builder()
                .idUsuario(idUsuario)
                .data(data)
                .pesoKg(pesoKg)
                .alturaCm(alturaCm)
                .percentualGordura(percentualGordura)
                .percentualMassaMagra(percentualMassaMagra)
                .imc(imc)
                .build();

        return indicadorRepository.salvar(novoIndicador);
    }


    // Verifica as condições e gera o relatorio pela data
    @Override
    public List<IndicadorBiomedico> gerarRelatorioPorData(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de início e fim não podem ser nulas.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }
        List<IndicadorBiomedico> resultados = indicadorRepository.buscarPorPeriodo(idUsuario, dataInicio, dataFim);
        resultados.sort(Comparator.comparing(IndicadorBiomedico::getData));
        return resultados;
    }

    // Verifica as condições e gera um relatorio da diferenca entre duas datas
    @Override
    public RelatorioDiferencaIndicadores gerarRelatorioDiferenca(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de início e fim não podem ser nulas.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }

        List<IndicadorBiomedico> indicadoresNoPeriodo = indicadorRepository.buscarPorPeriodo(idUsuario, dataInicio, dataFim);
        indicadoresNoPeriodo.sort(Comparator.comparing(IndicadorBiomedico::getData));

        RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);

        if (!indicadoresNoPeriodo.isEmpty()) {
            relatorio.setIndicadorInicial(Optional.of(indicadoresNoPeriodo.get(0)));
            relatorio.setIndicadorFinal(Optional.of(indicadoresNoPeriodo.get(indicadoresNoPeriodo.size() - 1)));
            relatorio.calcularDiferencas();
        }
        return relatorio;
    }

    // Lista todos os indicadores do usuario
    @Override
    public List<IndicadorBiomedico> listarTodosDoUsuario(int idUsuario) {
        return indicadorRepository.listarPorUsuario(idUsuario);
    }

    public void exportarRelatorioPorDataParaCsv(int idUsuario, LocalDate dataInicio, LocalDate dataFim, String caminhoArquivo) {
        List<IndicadorBiomedico> relatorio = gerarRelatorioPorData(idUsuario, dataInicio, dataFim);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            writer.write("Data;Peso (Kg);Altura (cm);Gordura (%);Massa Magra (%);IMC");
            writer.newLine();

            for (IndicadorBiomedico i : relatorio) {
                writer.write(
                        i.getData() + ";" +
                                i.getPesoKg() + ";" +
                                i.getAlturaCm() + ";" +
                                i.getPercentualGordura() + ";" +
                                i.getPercentualMassaMagra() + ";" +
                                String.format("%.2f", i.getImc())
                );
                writer.newLine();
            }

            logger.log(Level.INFO, "Relatório exportado com sucesso para: {0}", caminhoArquivo);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao exportar relatório: {0}", e.getMessage());
        }
    }
}