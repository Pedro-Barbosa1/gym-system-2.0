package br.upe.repository.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.model.Exercicio;
import br.upe.repository.IExercicioRepository;

public class ExercicioRepositoryImpl implements IExercicioRepository {

    private static final Logger logger = Logger.getLogger(ExercicioRepositoryImpl.class.getName());
    private static final String ARQUIVO_CSV = "src/main/resources/data/exercicios.csv";
    private final List<Exercicio> exercicios;
    private final AtomicInteger proximoId;

    public ExercicioRepositoryImpl() {
        this.exercicios = new ArrayList<>();
        this.proximoId = new AtomicInteger(0);
        carregarDoCsv();
    }

    // Listar usuario do arquivo csv
    public void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV: {0}", e.getMessage());
            return;
        }

        File file = new File(ARQUIVO_CSV);
        if (!file.exists()) {
            logger.warning("Arquivo CSV não encontrado. Será criado vazio no primeiro salvamento.");
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    logger.warning("Arquivo CSV não foi criado, mas não houve exceção.");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erro ao criar o arquivo CSV vazio: {0}", e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            int maxId = 0;
            while ((linha = br.readLine()) != null) {
                Exercicio exercicio = parseLinhaCsv(linha);
                if (exercicio != null) {
                    exercicios.add(exercicio);
                    if (exercicio.getIdExercicio() > maxId) {
                        maxId = exercicio.getIdExercicio();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler o arquivo CSV de exercícios: {0}", e.getMessage());
        }
    }

    // Gravar exercicio no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            for (Exercicio exercicio : exercicios) {
                bw.write(formatarLinhaCsv(exercicio));
                bw.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever no arquivo CSV de exercícios: {0}", e.getMessage());
        }
    }

    //
    private Exercicio parseLinhaCsv(String linha) {
        String[] partes = linha.split(";");
        if (partes.length == 5) {
            try {
                int id = Integer.parseInt(partes[0]);
                int idUsuario = Integer.parseInt(partes[1]);
                String nome = partes[2];
                String descricao = partes[3];
                String caminhoGif = partes[4];
                return new Exercicio(id, idUsuario, nome, descricao, caminhoGif);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Erro ao converter número em linha CSV: {0}", linha);
                return null;
            }
        }
        logger.log(Level.WARNING, "Formato inválido de linha CSV: {0}", linha);
        return null;
    }

    private String formatarLinhaCsv(Exercicio exercicio) {
        return exercicio.getIdExercicio() + ";" +
                exercicio.getIdUsuario() + ";" +
                exercicio.getNome() + ";" +
                exercicio.getDescricao() + ";" +
                exercicio.getCaminhoGif();
    }

    // Salvar exercicio no arquivo CSV
    @Override
    public Exercicio salvar(Exercicio exercicio) {
        if (exercicio.getIdExercicio() == 0) {
            exercicio.setIdExercicio(proximoId.getAndIncrement());
            exercicios.add(exercicio);
        } else {
            editar(exercicio);
        }
        escreverParaCsv();
        return exercicio;
    }

    // Buscar todos os exercicios pelo id do usuario
    @Override
    public List<Exercicio> buscarTodosDoUsuario(int idUsuario) {
        return exercicios.stream()
                .filter(e -> e.getIdUsuario() == idUsuario)
                .toList();
    }

    // Editar o exercicio no arquivo CSV
    @Override
    public void editar(Exercicio exercicio) {
        Optional<Exercicio> existenteOpt = buscarPorId(exercicio.getIdExercicio());
        if (existenteOpt.isPresent()) {
            exercicios.removeIf(e -> e.getIdExercicio() == exercicio.getIdExercicio());
            exercicios.add(exercicio);
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Erro: Exercício com ID {0} não encontrado para edição.", exercicio.getIdExercicio());
        }
    }

    // Deletar exercicio do arquivo CSV pelo id
    @Override
    public void deletar(int idExercicio) {
        boolean removido = exercicios.removeIf(e -> e.getIdExercicio() == idExercicio);
        if (removido) {
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Erro: Exercício com ID {0} não encontrado para remoção.", idExercicio);
        }
    }

    // Buscar exercicio no arquivo CSV pelo nome
    @Override
    public Optional<Exercicio> buscarPorNome(String nome) {
        return exercicios.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    // Buscar exercicio no arquivo CSV pelo id
    @Override
    public Optional<Exercicio> buscarPorId(int idExercicio) {
        return exercicios.stream()
                .filter(e -> e.getIdExercicio() == idExercicio)
                .findFirst();
    }

    @Override
    public int proximoId() {
        return proximoId.get();
    }

    public void limpar() {
        this.exercicios.clear();
    }

    // Construtor usado nos testes unitários para não carregar CSV
    public ExercicioRepositoryImpl(boolean carregarCsv) {
        this.exercicios = new ArrayList<>();
        this.proximoId = new AtomicInteger(0);
        if (carregarCsv) {
            carregarDoCsv();
        }
    }

}