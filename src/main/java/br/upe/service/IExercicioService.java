package br.upe.service;

import br.upe.model.Exercicio;

import java.util.List;
import java.util.Optional;

public interface IExercicioService {
    Exercicio cadastrarExercicio(int idUsuario, String nome, String descricao, String caminhoGif);
    boolean deletarExercicioForcado(int idUsuario, String nomeExercicio);
    List<Exercicio> listarExerciciosDoUsuario(int idUsuario);
    Optional<Exercicio> buscarExercicioDoUsuarioPorNome(int idUsuario, String nomeExercicio);
    Optional<Exercicio> buscarExercicioPorIdGlobal(int idExercicio);
    boolean deletarExercicioPorNome(int idUsuario, String nomeExercicio);
    void atualizarExercicio(int idUsuario, String nomeAtualExercicio, String novoNome, String novaDescricao, String novoCaminhoGif);
}