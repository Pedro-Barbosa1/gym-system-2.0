package br.upe.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.IIndicadorBiomedicoRepository;
import br.upe.repository.impl.IndicadorBiomedicoRepositoryImpl;
import br.upe.util.CalculadoraIMC;

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

        List<IndicadorBiomedico> resultadosOriginais = indicadorRepository.buscarPorPeriodo(idUsuario, dataInicio, dataFim);

        // cria cópia mutável
        List<IndicadorBiomedico> resultados = new java.util.ArrayList<>(resultadosOriginais);

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

        // 🔒 Cria cópia mutável da lista para evitar UnsupportedOperationException
        List<IndicadorBiomedico> indicadoresNoPeriodo = new java.util.ArrayList<>(
                indicadorRepository.buscarPorPeriodo(idUsuario, dataInicio, dataFim)
        );

        // Ordena pela data (do mais antigo ao mais recente)
        indicadoresNoPeriodo.sort(Comparator.comparing(IndicadorBiomedico::getData));

        // Cria o relatório e define o período
        RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);

        // Se houver dados, calcula as diferenças
        if (!indicadoresNoPeriodo.isEmpty()) {
            relatorio.setIndicadorInicial(indicadoresNoPeriodo.get(0));
            relatorio.setIndicadorFinal(indicadoresNoPeriodo.get(indicadoresNoPeriodo.size() - 1));
            relatorio.calcularDiferencas();
        } else {
            logger.log(Level.WARNING, "Nenhum indicador encontrado no período.");
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

    @Override
    public void deletarIndicador(int idIndicador) {
        indicadorRepository.deletar(idIndicador);
    }

    @Override
    public void editarIndicador(int idIndicador, LocalDate data, double pesoKg, double alturaCm,
                                double percentualGordura, double percentualMassaMagra) {
        if (pesoKg <= 0 || alturaCm <= 0) {
            throw new IllegalArgumentException("Peso e altura devem ser maiores que zero.");
        }
        if (percentualGordura < 0 || percentualMassaMagra < 0) {
            throw new IllegalArgumentException("Percentuais de gordura e massa magra não podem ser negativos.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula.");
        }

        double imc = CalculadoraIMC.calcular(pesoKg, alturaCm);

        // Buscar indicador existente para preservar ID do usuário
        IndicadorBiomedico indicadorExistente = indicadorRepository.buscarPorId(idIndicador)
                .orElseThrow(() -> new IllegalArgumentException("Indicador não encontrado."));

        IndicadorBiomedico indicadorAtualizado = new IndicadorBiomedico.Builder()
                .id(idIndicador)
                .idUsuario(indicadorExistente.getIdUsuario())
                .data(data)
                .pesoKg(pesoKg)
                .alturaCm(alturaCm)
                .percentualGordura(percentualGordura)
                .percentualMassaMagra(percentualMassaMagra)
                .imc(imc)
                .build();

        indicadorRepository.editar(indicadorAtualizado);
    }
}
