package br.upe.repository;

import br.upe.model.PlanoTreino;

import java.util.List;
import java.util.Optional;

public interface IPlanoTreinoRepository {

    PlanoTreino salvar(PlanoTreino plano);

    void editar(PlanoTreino plano);

    void deletar(int idPlano);

    Optional<PlanoTreino> buscarPorId(int idPlano);

    List<PlanoTreino> buscarTodosDoUsuario(int idUsuario);

    Optional<PlanoTreino> buscarPorNome(String nome, int idUsuario);

}
