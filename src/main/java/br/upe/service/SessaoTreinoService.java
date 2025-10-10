package br.upe.service;

import br.upe.repository.IExercicioRepository;
import br.upe.repository.IPlanoTreinoRepository;
import br.upe.repository.ISessaoTreinoRepository;
import br.upe.repository.impl.ExercicioRepositoryImpl;
import br.upe.repository.impl.PlanoTreinoRepositoryImpl;
import br.upe.repository.impl.SessaoTreinoRepositoryImpl;
import br.upe.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessaoTreinoService {

    private static final Logger logger = Logger.getLogger(SessaoTreinoService.class.getName());

    private final ISessaoTreinoRepository sessaoRepo;
    private final IPlanoTreinoRepository planoRepo;
    private final IExercicioRepository exercicioRepo;

    public SessaoTreinoService() {
        this.sessaoRepo = new SessaoTreinoRepositoryImpl();
        this.planoRepo = new PlanoTreinoRepositoryImpl();
        this.exercicioRepo = new ExercicioRepositoryImpl();
    }

    // Inicia a sessao de um usuario
    public SessaoTreino iniciarSessao(int idUsuario, int idPlano) {
        Optional<PlanoTreino> planoOpt = planoRepo.buscarPorId(idPlano);
        if (planoOpt.isEmpty() || planoOpt.get().getIdUsuario() != idUsuario) {
            throw new IllegalArgumentException("Plano de treino com ID " + idPlano + " não encontrado ou não pertence a você.");
        }

        return new SessaoTreino(idUsuario, idPlano);
    }

    // Registra a execucao de um exercicio
    public void registrarExecucao(SessaoTreino sessao, int idExercicio, int repeticoesRealizadas, double cargaRealizada) {
        ItemSessaoTreino itemExecutado = new ItemSessaoTreino(idExercicio, repeticoesRealizadas, cargaRealizada);
        sessao.adicionarItemExecutado(itemExecutado);
    }

    // Verifica as condições e registra uma sessao de treino
    public void salvarSessao(SessaoTreino sessao) {
        if (sessao.getItensExecutados().isEmpty()) {
            logger.info("Sessão vazia. Não será salva.");
            return;
        }
        sessaoRepo.salvar(sessao);
        logger.log(Level.INFO, "Sessão de treino ID {0} salva com sucesso!", sessao.getIdSessao());
    }

    // Verifica condições e gera sugestões de acordo com sessao de treino
    public List<SugestaoAtualizacaoPlano> verificarAlteracoesEGerarSugestoes(SessaoTreino sessao) {
        Optional<PlanoTreino> planoOpt = planoRepo.buscarPorId(sessao.getIdPlanoTreino());
        if (planoOpt.isEmpty()) {
            logger.log(Level.WARNING, "Plano de treino com ID {0} não encontrado para verificação de alterações.", sessao.getIdPlanoTreino());
            return List.of();
        }
        PlanoTreino plano = planoOpt.get();
        List<SugestaoAtualizacaoPlano> sugestoes = new ArrayList<>();

        for (ItemSessaoTreino executado : sessao.getItensExecutados()) {
            Optional<ItemPlanoTreino> planejadoOpt = plano.getItensTreino().stream()
                    .filter(pItem -> pItem.getIdExercicio() == executado.getIdExercicio())
                    .findFirst();

            if (planejadoOpt.isPresent()) {
                ItemPlanoTreino planejado = planejadoOpt.get();
                boolean mudouRepeticoes = executado.getRepeticoesRealizadas() != planejado.getRepeticoes();
                boolean mudouCarga = executado.getCargaRealizada() != planejado.getCargaKg();

                if (mudouRepeticoes || mudouCarga) {
                    Optional<Exercicio> exercicioDetalhesOpt = exercicioRepo.buscarPorId(executado.getIdExercicio());
                    String nomeExercicio = exercicioDetalhesOpt.map(Exercicio::getNome).orElse("Exercício Desconhecido");

                    sugestoes.add(new SugestaoAtualizacaoPlano(
                            executado.getIdExercicio(),
                            nomeExercicio,
                            planejado.getRepeticoes(),
                            executado.getRepeticoesRealizadas(),
                            planejado.getCargaKg(),
                            executado.getCargaRealizada()
                    ));
                }
            }
        }
        return sugestoes;
    }

    // Verifica condições e atualiza o plano de acordo com a sessao
    public void aplicarAtualizacoesNoPlano(int idPlano, int idExercicio, int novasRepeticoes, double novaCarga) {
        Optional<PlanoTreino> planoOpt = planoRepo.buscarPorId(idPlano);
        if (planoOpt.isEmpty()) {
            logger.log(Level.WARNING, "Plano de treino com ID {0} não encontrado para aplicar atualização.", idPlano);
            return;
        }
        PlanoTreino plano = planoOpt.get();

        Optional<ItemPlanoTreino> itemParaAtualizarOpt = plano.getItensTreino().stream()
                .filter(item -> item.getIdExercicio() == idExercicio)
                .findFirst();

        if (itemParaAtualizarOpt.isPresent()) {
            ItemPlanoTreino item = itemParaAtualizarOpt.get();
            item.setRepeticoes(novasRepeticoes);
            item.setCargaKg((int) novaCarga);
            planoRepo.editar(plano);
            logger.log(Level.INFO, "Plano de treino ID {0} atualizado para o exercício ID {1}.", new Object[]{idPlano, idExercicio});
        } else {
            logger.log(Level.WARNING, "Exercício ID {0} não encontrado no plano ID {1} para atualização.", new Object[]{idExercicio, idPlano});
        }
    }

    public record SugestaoAtualizacaoPlano(
            int idExercicio,
            String nomeExercicio,
            int repPlanejadas,
            int repRealizadas,
            double cargaPlanejada,
            double cargaRealizada
    ) {}

}