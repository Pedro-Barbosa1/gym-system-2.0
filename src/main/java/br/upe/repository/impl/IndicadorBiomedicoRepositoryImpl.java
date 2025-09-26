package br.upe.repository.impl;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.IIndicadorBiomedicoRepository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndicadorBiomedicoRepositoryImpl implements IIndicadorBiomedicoRepository {

    private static final Logger logger = Logger.getLogger(IndicadorBiomedicoRepositoryImpl.class.getName());
    private static final Path CAMINHO_ARQUIVO = Paths.get("src/main/resources/data/indicadores.csv");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final List<IndicadorBiomedico> indicadores;
    private final AtomicInteger proximoId;

    public IndicadorBiomedicoRepositoryImpl() {
        this.indicadores = new ArrayList<>();
        this.proximoId = new AtomicInteger(1);
        carregarDoCsv();
    }

    // Busca os indicadores no arquivo CSV
    private void carregarDoCsv() {
        try {
            Files.createDirectories(CAMINHO_ARQUIVO.getParent());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV", e);
            return;
        }

        if (!Files.exists(CAMINHO_ARQUIVO)) {
            logger.info("Arquivo CSV de indicadores não encontrado. Será criado um novo na primeira inserção.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(CAMINHO_ARQUIVO)) {
            String cabecalho = reader.readLine(); // Lê o cabeçalho
            if (cabecalho == null) {
                // Arquivo vazio
                return;
            }
            int maxId = 0;

            String linha;
            while ((linha = reader.readLine()) != null) {
                IndicadorBiomedico indicador = parseLinhaCsv(linha);
                if (indicador != null) {
                    indicadores.add(indicador);
                    maxId = Math.max(maxId, indicador.getId());
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler indicadores do arquivo CSV", e);
        }
    }

    // Grava os indicadores no arquivo CSV
    private void escreverParaCsv() {
        Path arquivoTemp = CAMINHO_ARQUIVO.resolveSibling("indicadores_temp.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(arquivoTemp)) {
            writer.write("id;idUsuario;data;pesoKg;alturaCm;percentualGordura;percentualMassaMagra;imc\n");
            for (IndicadorBiomedico indicador : indicadores) {
                writer.write(formatarLinhaCsv(indicador));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever indicadores no arquivo CSV", e);
            return;
        }

        try {
            Files.move(arquivoTemp, CAMINHO_ARQUIVO, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao substituir arquivo CSV original", e);
        }
    }

    // Lê uma linha do arquivo CSV
    private IndicadorBiomedico parseLinhaCsv(String linha) {
        String[] partes = linha.split(";");
        if (partes.length != 8) {
            logger.log(Level.WARNING, "Formato inválido de linha CSV de indicador: {0}", linha);
            return null;
        }

        try {
            int id = Integer.parseInt(partes[0]);
            int idUsuario = Integer.parseInt(partes[1]);
            LocalDate data = LocalDate.parse(partes[2], DATE_FORMATTER);
            double pesoKg = Double.parseDouble(partes[3]);
            double alturaCm = Double.parseDouble(partes[4]);
            double percentualGordura = Double.parseDouble(partes[5]);
            double percentualMassaMagra = Double.parseDouble(partes[6]);
            double imc = Double.parseDouble(partes[7]);
            return new IndicadorBiomedico(id, idUsuario, data, pesoKg, alturaCm, percentualGordura, percentualMassaMagra, imc);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erro ao parsear linha CSV de indicador: {0}", linha);
            logger.log(Level.WARNING, "Detalhes da exceção:", e);
            return null;
        }
    }

    // Formata uma linha no arquivo CSV
    private String formatarLinhaCsv(IndicadorBiomedico indicador) {
        return String.join(";",
                String.valueOf(indicador.getId()),
                String.valueOf(indicador.getIdUsuario()),
                indicador.getData().format(DATE_FORMATTER),
                String.valueOf(indicador.getPesoKg()),
                String.valueOf(indicador.getAlturaCm()),
                String.valueOf(indicador.getPercentualGordura()),
                String.valueOf(indicador.getPercentualMassaMagra()),
                String.valueOf(indicador.getImc())
        );
    }

    @Override
    public IndicadorBiomedico salvar(IndicadorBiomedico indicador) {
        if (indicador.getId() == 0) {
            indicador.setId(gerarProximoId());
        } else {
            indicadores.removeIf(i -> i.getId() == indicador.getId());
        }
        indicadores.add(indicador);
        escreverParaCsv();
        return indicador;
    }

    @Override
    public Optional<IndicadorBiomedico> buscarPorId(int id) {
        return indicadores.stream()
                .filter(i -> i.getId() == id)
                .findFirst();
    }

    @Override
    public List<IndicadorBiomedico> listarTodos() {
        return new ArrayList<>(indicadores);
    }

    @Override
    public List<IndicadorBiomedico> listarPorUsuario(int idUsuario) {
        return indicadores.stream()
                .filter(i -> i.getIdUsuario() == idUsuario)
                .toList();
    }

    @Override
    public List<IndicadorBiomedico> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        return indicadores.stream()
                .filter(i -> i.getIdUsuario() == idUsuario &&
                        !i.getData().isBefore(dataInicio) &&
                        !i.getData().isAfter(dataFim))
                .toList();
    }

    @Override
    public void editar(IndicadorBiomedico indicador) {
        if (buscarPorId(indicador.getId()).isPresent()) {
            indicadores.removeIf(i -> i.getId() == indicador.getId());
            indicadores.add(indicador);
            escreverParaCsv();
        } else {
            logger.warning("Indicador com ID " + indicador.getId() + " não encontrado para edição.");
        }
    }

    @Override
    public void deletar(int id) {
        if (indicadores.removeIf(i -> i.getId() == id)) {
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Indicador com ID {0} não encontrado para remoção.", id);
        }
    }


    @Override
    public int gerarProximoId() {
        return proximoId.getAndIncrement();
    }
}