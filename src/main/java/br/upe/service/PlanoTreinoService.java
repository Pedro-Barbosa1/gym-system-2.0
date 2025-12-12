package br.upe.service;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.model.Usuario;
import br.upe.repository.IExercicioRepository;
import br.upe.repository.IPlanoTreinoRepository;
import br.upe.repository.IUsuarioRepository;
import br.upe.repository.impl.ExercicioRepositoryImpl;
import br.upe.repository.impl.PlanoTreinoRepositoryImpl;
import br.upe.repository.impl.UsuarioRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class PlanoTreinoService implements IPlanoTreinoService {

    private final IPlanoTreinoRepository planoTreinoRepository;
    private final IExercicioRepository exercicioRepository;
    private final IUsuarioRepository usuarioRepository;

    public PlanoTreinoService(IPlanoTreinoRepository planoTreinoRepository,
                              IExercicioRepository exercicioRepository, IUsuarioRepository usuarioRepository) {
        this.planoTreinoRepository = planoTreinoRepository;
        this.exercicioRepository = exercicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PlanoTreinoService() {
        this.planoTreinoRepository = new PlanoTreinoRepositoryImpl();
        this.exercicioRepository = new ExercicioRepositoryImpl();
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    @Override
    public PlanoTreino criarPlano(int idUsuario, String nome) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario nao encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        PlanoTreino plano = new PlanoTreino(usuario, nome);
        planoTreinoRepository.salvar(plano);
        return plano;
    }

    @Override
    public void adicionarExercicioAoPlano(int idUsuario, String nomePlano, int idExercicio) {
        Optional<PlanoTreino> planoOpt = planoTreinoRepository.buscarPorNome(nomePlano.trim(), idUsuario);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano nao encontrado ou nao pertence a voce.");
        }
        PlanoTreino plano = planoOpt.get();
        Optional<Exercicio> exercicioOpt = exercicioRepository.buscarPorId(idExercicio);
        if (exercicioOpt.isEmpty() || exercicioOpt.get().getIdUsuario() != idUsuario) {
            throw new IllegalArgumentException("Exercicio nao encontrado ou nao pertence a voce.");
        }
        boolean exercicioJaNoPlano = plano.getItensTreino().stream()
                .anyMatch(item -> item.getIdExercicio() == idExercicio);
        if (exercicioJaNoPlano) {
            throw new IllegalArgumentException("Exercicio ja adicionado a este plano.");
        }
        ItemPlanoTreino newItem = new ItemPlanoTreino(plano.getIdPlano(), idExercicio, 0, 0);
        plano.adicionarItem(newItem);
        planoTreinoRepository.editar(plano);
    }

    @Override
    public void removerExercicioDoPlano(int idUsuario, String nomePlano, int idExercicio) {
        Optional<PlanoTreino> planoOpt = planoTreinoRepository.buscarPorNome(nomePlano.trim(), idUsuario);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano nao encontrado ou nao pertence a voce.");
        }
        PlanoTreino plano = planoOpt.get();
        boolean removido = plano.getItensTreino().removeIf(item -> item.getIdExercicio() == idExercicio);
        if (!removido) {
            throw new IllegalArgumentException("Exercicio nao encontrado neste plano.");
        }
        planoTreinoRepository.editar(plano);
    }

    @Override
    public List<PlanoTreino> listarMeusPlanos(int idUsuario) {
        return planoTreinoRepository.buscarTodosDoUsuario(idUsuario);
    }

    public void editarPlano(int idUsuario, String nomeAtualPlano, String novoNome) {
        Optional<PlanoTreino> planoOpt = planoTreinoRepository.buscarPorNome(nomeAtualPlano.trim(), idUsuario);
        if (planoOpt.isEmpty()) {
            throw new IllegalArgumentException("Plano nao encontrado ou nao pertence a voce.");
        }
        PlanoTreino plano = planoOpt.get();
        if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equalsIgnoreCase(plano.getNome())) {
            Optional<PlanoTreino> nomeExistente = planoTreinoRepository.buscarPorNome(novoNome.trim(), idUsuario);
            if (nomeExistente.isPresent()) {
                throw new IllegalArgumentException("Voce ja possui outro plano com este nome.");
            }
            plano.setNome(novoNome.trim());
        }
        planoTreinoRepository.editar(plano);
    }

    @Override
    public boolean deletarPlano(int idUsuario, String nomePlano) {
        Optional<PlanoTreino> planoOpt = planoTreinoRepository.buscarPorNome(nomePlano.trim(), idUsuario);
        if (planoOpt.isEmpty()) {
            return false;
        }
        planoTreinoRepository.deletar(planoOpt.get().getIdPlano());
        return true;
    }

    @Override
    public Optional<PlanoTreino> buscarPlanoPorId(int idPlano) {
        return planoTreinoRepository.buscarPorId(idPlano);
    }
}
