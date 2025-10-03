package br.upe.service;

import java.util.List;
import java.util.Optional;

import br.upe.model.Exercicio;
import br.upe.repository.IExercicioRepository;
import br.upe.repository.impl.ExercicioRepositoryImpl;

public class ExercicioService implements IExercicioService {

    private IExercicioRepository exercicioRepository;

    public ExercicioService(IExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }

    public ExercicioService() {
        this.exercicioRepository = new ExercicioRepositoryImpl();
    }

    // Verifica condições e cadastra exercicios no repositorio
    @Override
    public Exercicio cadastrarExercicio(int idUsuario, String nome, String descricao, String caminhoGif) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exercício não pode ser vazio.");
        }

        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);
        boolean nomeJaExiste = exerciciosDoUsuario.stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(nome.trim()));
        if (nomeJaExiste) {
            throw new IllegalArgumentException("Você já possui um exercício com o nome '" + nome + "'.");
        }

        Exercicio novoExercicio = new Exercicio(idUsuario, nome.trim(), descricao, caminhoGif);
        return exercicioRepository.salvar(novoExercicio);
    }

    // Lista exercicios do usuario
    @Override
    public List<Exercicio> listarExerciciosDoUsuario(int idUsuario) {
        return exercicioRepository.buscarTodosDoUsuario(idUsuario);
    }


    // Busca o exercicio pelo nome
    @Override
    public Optional<Exercicio> buscarExercicioDoUsuarioPorNome(int idUsuario, String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            return Optional.empty();
        }

        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);

        return exerciciosDoUsuario.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nomeExercicio.trim()))
                .findFirst();
    }

    // Busca o exercicio pelo id
    @Override
    public Optional<Exercicio> buscarExercicioPorIdGlobal(int idExercicio) {
        return exercicioRepository.buscarPorId(idExercicio);
    }

    // verifica condições e deleta o exercicio pelo nome
    @Override
    public boolean deletarExercicioPorNome(int idUsuario, String nomeExercicio) {
        if (nomeExercicio == null || nomeExercicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exercício para deletar não pode ser vazio.");
        }

        Optional<Exercicio> exercicioParaDeletarOpt = buscarExercicioDoUsuarioPorNome(idUsuario, nomeExercicio);

        if (exercicioParaDeletarOpt.isPresent()) {
            Exercicio exercicioParaDeletar = exercicioParaDeletarOpt.get();
            exercicioRepository.deletar(exercicioParaDeletar.getIdExercicio());
            return true;
        } else {
            System.out.println("Exercício com nome '" + nomeExercicio + "' não encontrado entre os seus exercícios.");
            return false;
        }
    }

    // Alterar exercicios
    
    private boolean verificarNomeDuplicado(int idUsuario, String novoNome) {
        List<Exercicio> exerciciosDoUsuario = exercicioRepository.buscarTodosDoUsuario(idUsuario);
        return exerciciosDoUsuario.stream()
            .anyMatch(e -> e.getNome().equalsIgnoreCase(novoNome.trim()));
    }

    public void atualizarExercicio(int idUsuario, String nomeAtualExercicio, String novoNome, String novaDescricao, String novoCaminhoGif) {
        if (nomeAtualExercicio == null || nomeAtualExercicio.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome atual do exercício não pode ser vazio.");
        }

        Optional<Exercicio> exercicioOpt = buscarExercicioDoUsuarioPorNome(idUsuario, nomeAtualExercicio);

        if (exercicioOpt.isPresent()) {
            Exercicio exercicio = exercicioOpt.get();

            if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equalsIgnoreCase(exercicio.getNome())) {
                if (verificarNomeDuplicado(idUsuario, novoNome)) {
                    throw new IllegalArgumentException("Você já possui um exercício com o novo nome '" + novoNome + "'.");
                }
            }

            if (novoNome != null && !novoNome.trim().isEmpty()) {
                exercicio.setNome(novoNome.trim());
            }
            if (novaDescricao != null) {
                exercicio.setDescricao(novaDescricao);
            }
            if (novoCaminhoGif != null) {
                exercicio.setCaminhoGif(novoCaminhoGif);
            }

            exercicioRepository.editar(exercicio);
        } else {
            throw new IllegalArgumentException("Erro: Exercício '" + nomeAtualExercicio + "' não encontrado entre os seus exercícios para atualização.");
        }
    }

    public void limparDados() {
        if(exercicioRepository instanceof ExercicioRepositoryImpl exerciciorepositoryimpl) {
            exerciciorepositoryimpl.limpar();
        }
    }
}