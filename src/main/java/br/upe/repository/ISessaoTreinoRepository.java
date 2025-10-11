package br.upe.repository;

import br.upe.model.SessaoTreino;
import java.util.Optional;

public interface ISessaoTreinoRepository {
    SessaoTreino salvar(SessaoTreino sessao);
    Optional<SessaoTreino> buscarPorId(int idSessao);
    void editar(SessaoTreino sessao);
    void deletar(int idSessao);
}