package br.upe.service;

import br.upe.model.PlanoTreino;

import java.util.List;
import java.util.Optional;

public interface IPlanoTreinoService {
    PlanoTreino criarPlano(int id_usuario, String nome);
    void adicionarExercicioAoPlano(int idUsuario, String nomePlano, int idExercicio);
    void removerExercicioDoPlano(int idUsuario, String nomePlano, int idExercicio);
    List<PlanoTreino> listarMeusPlanos(int idUsuario);
    boolean deletarPlano(int idUsuario, String nomePlano);
    Optional<PlanoTreino> buscarPlanoPorId(int idPlanoEscolhido);
}