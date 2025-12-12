package br.upe.service;

import br.upe.model.IndicadorBiomedico;

import java.time.LocalDate;
import java.util.List;

public interface IIndicadorBiomedicoService {

    IndicadorBiomedico cadastrarIndicador(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra);

    List<IndicadorBiomedico> gerarRelatorioPorData(int idUsuario, LocalDate dataInicio, LocalDate dataFim);

    RelatorioDiferencaIndicadores gerarRelatorioDiferenca(int idUsuario, LocalDate dataInicio, LocalDate dataFim);

    List<IndicadorBiomedico> listarTodosDoUsuario(int idUsuario);

    void deletarIndicador(int idIndicador);

    void editarIndicador(int idIndicador, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra);

}
