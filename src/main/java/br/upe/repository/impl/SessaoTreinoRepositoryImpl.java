package br.upe.repository.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import br.upe.model.ItemSessaoTreino;
import br.upe.model.SessaoTreino;
import br.upe.repository.ISessaoTreinoRepository;

public class SessaoTreinoRepositoryImpl implements ISessaoTreinoRepository {

    private static final String ARQUIVO_CSV = "src/main/resources/data/sessoes_treino.csv";
    private final List<SessaoTreino> sessoes;
    private final AtomicInteger proximoId;
    private static final Logger logger = Logger.getLogger(SessaoTreinoRepositoryImpl.class.getName());

    public SessaoTreinoRepositoryImpl() {
        this.sessoes = new ArrayList<>();
        this.proximoId = new AtomicInteger(0);
        carregarDoCsv();
    }

    // Lista o usuario do arquivo CSV
    private void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV: {0}", e.getMessage());
            return;
        }

        File file = new File(ARQUIVO_CSV);
        if (!file.exists()) {
            logger.log(Level.INFO, "Arquivo {0} não encontrado. Será criado vazio no primeiro salvamento.", ARQUIVO_CSV);
            try {
                boolean criado = file.createNewFile();
                if (criado){
                    logger.log(Level.INFO, "Arquivo {0} criado com sucesso.", ARQUIVO_CSV);
                } else{
                    logger.log(Level.WARNING, "Falha ao criar o arquivo {0}.", ARQUIVO_CSV);
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
                SessaoTreino sessao = parseLinhaCsv(linha);
                if (sessao != null) {
                    sessoes.add(sessao);
                    if (sessao.getIdSessao() > maxId) {
                        maxId = sessao.getIdSessao();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler o arquivo CSV de sessões de treino: {0}", e.getMessage());
        }
    }

    public List<SessaoTreino> listarPorUsuario(int idUsuario) {
    return sessoes.stream()
        .filter(s -> s.getIdUsuario() == idUsuario)
        .collect(Collectors.toList());
    }

    // Grava a sessao de treino no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            for (SessaoTreino sessao : sessoes) {
                bw.write(formatarLinhaCsv(sessao));
                bw.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever no arquivo CSV de sessões de treino: {0}", e.getMessage());
        }
    }

    // Ler uma linha do arquivo CSV
    private SessaoTreino parseLinhaCsv(String linha) {
        String[] partes = linha.split(";", 5);
        if (partes.length == 5) {
            try {
                int idSessao = Integer.parseInt(partes[0]);
                int idUsuario = Integer.parseInt(partes[1]);
                int idPlanoTreino = Integer.parseInt(partes[2]);
                LocalDate dataSessao = LocalDate.parse(partes[3]);
                List<ItemSessaoTreino> itensExecutados = new ArrayList<>();

                String itensString = partes[4];
                if (!itensString.isEmpty()) {
                    String[] itensSeparados = itensString.split("\\|");
                    for (String itemStr : itensSeparados) {
                        String[] itemPartes = itemStr.split(",");
                        if (itemPartes.length == 3) {
                            int idExercicio = Integer.parseInt(itemPartes[0]);
                            int repeticoesRealizadas = Integer.parseInt(itemPartes[1]);
                            double cargaRealizada = Double.parseDouble(itemPartes[2]);
                            itensExecutados.add(new ItemSessaoTreino(idSessao, idExercicio, repeticoesRealizadas, cargaRealizada));
                        } else {
                            logger.log(Level.SEVERE, "Formato inválido de item de sessão de treino: {0}", itemStr);
                        }
                    }
                }
                return new SessaoTreino(idSessao, idUsuario, idPlanoTreino, dataSessao, itensExecutados);
            } catch (NumberFormatException | DateTimeParseException e) {
                logger.log(Level.SEVERE, "Erro ao converter número ou data em linha CSV de sessão: {0} - {1}", new Object[]{linha, e.getMessage()});
                return null;
            }
        }
        logger.log(Level.SEVERE, "Formato inválido de linha CSV de sessão: {0}", linha);
        return null;
    }

    // Formata para uma linha do arquivo CSV
    private String formatarLinhaCsv(SessaoTreino sessao) {
        String itensString = sessao.getItensExecutados().stream()
                .map(item -> item.getIdExercicio() + "," + item.getRepeticoesRealizadas() + "," + item.getCargaRealizada())
                .collect(Collectors.joining("|"));

        return sessao.getIdSessao() + ";" +
                sessao.getIdUsuario() + ";" +
                sessao.getIdPlanoTreino() + ";" +
                sessao.getDataSessao().toString() + ";" +
                itensString;
    }

    // Salva a sessao de treino no arquivo CSV
    @Override
    public SessaoTreino salvar(SessaoTreino sessao) {
        if (sessao.getIdSessao() == 0) {
            sessao.setIdSessao(proximoId.getAndIncrement());
            sessoes.add(sessao);
        } else {
            editar(sessao);
        }
        escreverParaCsv();
        return sessao;
    }

    // Lista a sessao de treino pelo id
    @Override
    public Optional<SessaoTreino> buscarPorId(int idSessao) {
        return sessoes.stream()
                .filter(s -> s.getIdSessao() == idSessao)
                .findFirst();
    }

    // Verifica as condições e altera a sessao de treino
    @Override
    public void editar(SessaoTreino sessao) {
        Optional<SessaoTreino> existenteOpt = buscarPorId(sessao.getIdSessao());
        if (existenteOpt.isPresent()) {
            sessoes.removeIf(s -> s.getIdSessao() == sessao.getIdSessao());
            sessoes.add(sessao);
            escreverParaCsv();
        } else {
            logger.log(Level.SEVERE, "Erro: Sessão de treino com ID {0} não encontrado para edição.", sessao.getIdSessao());
        }
    }

    // Verifica as condições e deleta a sessao de treino
    @Override
    public void deletar(int idSessao) {
        boolean removido = sessoes.removeIf(s -> s.getIdSessao() == idSessao);
        if (removido) {
            escreverParaCsv();
        } else {
            logger.log(Level.SEVERE, "Erro: Sessão de treino com ID {0} não encontrada para remoção.", idSessao);
        }
    }

}