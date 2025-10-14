package br.upe.repository.impl;

import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.repository.IPlanoTreinoRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlanoTreinoRepositoryImpl implements IPlanoTreinoRepository {

    private static final String ARQUIVO_CSV = "src/main/resources/data/planos_treino.csv";
    private final List<PlanoTreino> planos;
    private final AtomicInteger proximoId;
    private static final Logger logger = Logger.getLogger(PlanoTreinoRepositoryImpl.class.getName());

    public PlanoTreinoRepositoryImpl() {
        this.planos = new ArrayList<>();
        this.proximoId = new AtomicInteger(0);
        carregarDoCsv();
    }

    // Listar usuario do arquivo CSV
    private void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV", e);
            return;
        }

        File file = new File(ARQUIVO_CSV);
        if (!file.exists()) {
            logger.warning("Arquivo " + ARQUIVO_CSV + " não encontrado. Será criado vazio no primeiro salvamento.");
            try {
                boolean criado = file.createNewFile();
                if (criado) {
                    logger.info("Arquivo CSV criado: " + ARQUIVO_CSV);
                } else {
                    logger.info("Arquivo CSV já existia: " + ARQUIVO_CSV);
                }

            }
            catch (IOException e) {
                logger.log(Level.SEVERE, "Erro ao criar o arquivo CSV vazio", e);
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            int maxId = 0;
            while ((linha = br.readLine()) != null) {
                PlanoTreino plano = parseLinhaCsv(linha);
                if (plano != null) {
                    planos.add(plano);
                    if (plano.getIdPlano() > maxId) {
                        maxId = plano.getIdPlano();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler o arquivo CSV de planos de treino", e);
        }
    }

    // Gravar plano de treino no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            for (PlanoTreino plano : planos) {
                bw.write(formatarLinhaCsv(plano));
                bw.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever no arquivo CSV de planos de treino", e);
        }
    }

    // Ler uma linha do arquivo CSV
    private PlanoTreino parseLinhaCsv(String linha) {
        String[] partes = linha.split(";", 4);
        if (partes.length == 4) {
            try {
                int idPlano = Integer.parseInt(partes[0]);
                int idUsuario = Integer.parseInt(partes[1]);
                String nome = partes[2];
                List<ItemPlanoTreino> itensTreino = new ArrayList<>();

                String itensString = partes[3];
                if (!itensString.isEmpty()) {
                    String[] itensSeparados = itensString.split("\\|");
                    for (String itemStr : itensSeparados) {
                        String[] itemPartes = itemStr.split(",");
                        if (itemPartes.length == 3) {
                            int idExercicio = Integer.parseInt(itemPartes[0]);
                            int cargaKg = Integer.parseInt(itemPartes[1]);
                            int repeticoes = Integer.parseInt(itemPartes[2]);
                            itensTreino.add(new ItemPlanoTreino(idExercicio, cargaKg, repeticoes));
                        } else {
                            logger.log(Level.WARNING, "Formato inválido de item de treino: {0}", itemStr);
                        }
                    }
                }
                return new PlanoTreino(idPlano, idUsuario, nome, itensTreino);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Erro ao converter número em linha CSV de plano: {0}", new Object[]{linha});
                logger.log(Level.WARNING, "Detalhes da exceção:", e);
                return null;
            }
        }
        logger.log(Level.WARNING, "Formato inválido de linha CSV de plano: {0}", linha);
        return null;
    }

    // Formata para uma linha no arquivo CSV
    private String formatarLinhaCsv(PlanoTreino plano) {
        String itensString = plano.getItensTreino().stream()
                .map(item -> item.getIdExercicio() + "," + item.getCargaKg() + "," + item.getRepeticoes())
                .collect(Collectors.joining("|"));

        return plano.getIdPlano() + ";" +
                plano.getIdUsuario() + ";" +
                plano.getNome() + ";" +
                itensString;
    }

    // Salvar plano de treino no arquivo CSV
    @Override
    public PlanoTreino salvar(PlanoTreino plano) {
        if (plano.getIdPlano() == 0) {
            plano.setIdPlano(proximoId.getAndIncrement());
            planos.add(plano);
        } else {
            editar(plano);
        }
        escreverParaCsv();
        return plano;
    }

    // Listar todos os planos de treino do usuario
    @Override
    public List<PlanoTreino> buscarTodosDoUsuario(int idUsuario) {
        return planos.stream()
                .filter(p -> p.getIdUsuario() == idUsuario)
                .toList();
    }

    // Verifica condições e altera o plano de treino
    @Override
    public void editar(PlanoTreino plano) {
        Optional<PlanoTreino> existenteOpt = buscarPorId(plano.getIdPlano());
        if (existenteOpt.isPresent()) {
            planos.removeIf(p -> p.getIdPlano() == plano.getIdPlano());
            planos.add(plano);
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Plano de treino com ID {0} não encontrado para edição.", plano.getIdPlano());
        }
    }

    // Atualiza o plano de treino de acordo com as alterações
    @Override
    public void atualizar(PlanoTreino plano) {
        editar(plano);
    }

    // Verifica as condições e deleta o plano de treino
    @Override
    public void deletar(int idPlano) {
        boolean removido = planos.removeIf(p -> p.getIdPlano() == idPlano);
        if (removido) {
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Plano de treino com ID {0} não encontrado para remoção.", idPlano);
        }
    }

    // Lista o plano de treino pelo nome e id do usuario
    @Override
    public Optional<PlanoTreino> buscarPorNomeEUsuario(int idUsuario, String nomePlano) {
        return planos.stream()
                .filter(p -> p.getIdUsuario() == idUsuario && p.getNome().equalsIgnoreCase(nomePlano))
                .findFirst();
    }

    // Lista o plano de treino pelo id
    @Override
    public Optional<PlanoTreino> buscarPorId(int idPlano) {
        return planos.stream()
                .filter(p -> p.getIdPlano() == idPlano)
                .findFirst();
    }

    @Override
    public int proximoId() {
        return proximoId.get();
    }

    public void limpar() {
        this.planos.clear();
    }

}