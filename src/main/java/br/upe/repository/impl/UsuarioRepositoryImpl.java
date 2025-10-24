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

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.repository.IUsuarioRepository;

public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private static final String CAMINHO_ARQUIVO = "src/main/resources/data/usuarios.csv";
    private final List<Usuario> usuarios;
    private final AtomicInteger proximoId;

    private static final Logger logger = Logger.getLogger(UsuarioRepositoryImpl.class.getName());

    public UsuarioRepositoryImpl() {
        this.usuarios = new ArrayList<>();
        this.proximoId = new AtomicInteger(1);
        carregarDoCsv();
    }

    // Lista o usuario do arquivo CSV
    private void carregarDoCsv() {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório para CSV", e);
            return;
        }

        File file = new File(CAMINHO_ARQUIVO);
        if (!file.exists()) {
            logger.info("Arquivo CSV de usuários não encontrado. Criando usuário 'adm' inicial...");
            Usuario adminUser = new Usuario(gerarProximoId(), "Administrador", "adm", "adm", TipoUsuario.ADMIN);
            usuarios.add(adminUser);
            escreverParaCsv();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            int linhaNum = 0;
            int maxId = 0;
            while ((linha = reader.readLine()) != null) {
                linhaNum++;
                if (linhaNum == 1) continue; // pula cabeçalho

                Usuario usuario = parseLinhaCsv(linha);
                if (usuario != null) {
                    usuarios.add(usuario);
                    if (usuario.getId() > maxId) {
                        maxId = usuario.getId();
                    }
                }
            }
            proximoId.set(maxId + 1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler usuários do arquivo CSV", e);
        }
    }

    // Grava o usuario no arquivo CSV
    private void escreverParaCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            writer.write("id;nome;email;senha;tipo\n");
            for (Usuario usuario : usuarios) {
                writer.write(formatarLinhaCsv(usuario));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao escrever usuários no arquivo CSV", e);
        }
    }

    // Ler uma linha do arquivo CSV
    private Usuario parseLinhaCsv(String linha) {
        String[] partes = linha.split(";");
        if (partes.length == 5) {
            try {
                int id = Integer.parseInt(partes[0]);
                String nome = partes[1];
                String email = partes[2];
                String senha = partes[3];
                TipoUsuario tipo = TipoUsuario.valueOf(partes[4]);
                return new Usuario(id, nome, email, senha, tipo);
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Erro ao parsear linha CSV de usuário: {0}", new Object[]{linha});
                logger.log(Level.WARNING, "Exceção capturada", e);
                return null;
            }
        }
        logger.log(Level.WARNING, "Formato inválido de linha CSV de usuário: {0}", new Object[]{linha});
        return null;
    }

    // Formata para uma linha do arquivo CSV
    private String formatarLinhaCsv(Usuario usuario) {
        return usuario.getId() + ";" +
                usuario.getNome() + ";" +
                usuario.getEmail() + ";" +
                usuario.getSenha() + ";" +
                usuario.getTipo().name();
    }

    // Salva o usuario no arquivo CSV
    @Override
    public Usuario salvar(Usuario usuario) {
        Optional<Usuario> existenteOpt = buscarPorId(usuario.getId());
        if (existenteOpt.isEmpty() || usuario.getId() == 0) {
            usuario.setId(gerarProximoId());
            usuarios.add(usuario);
        } else {
            editar(usuario);
        }
        escreverParaCsv();
        return usuario;
    }

    // Lista o usuario por id
    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    // Lista o usuario por email
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // Lista todos os usuarios
    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    // Verifica condições e altera o usuario
    @Override
    public void editar(Usuario usuario) {
        Optional<Usuario> existenteOpt = buscarPorId(usuario.getId());
        if (existenteOpt.isPresent()) {
            usuarios.removeIf(u -> u.getId() == usuario.getId());
            usuarios.add(usuario);
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Usuário com ID {0} não encontrado para edição.", new Object[]{usuario.getId()});
        }
    }

    //Verifica condições e deleta o usuario
    @Override
    public void deletar(int id) {
        boolean removido = usuarios.removeIf(u -> u.getId() == id);
        if (removido) {
            escreverParaCsv();
        } else {
            logger.log(Level.WARNING, "Usuário com ID {0} não encontrado para remoção.", new Object[]{id});
        }
    }

    @Override
    public int gerarProximoId() {
        return proximoId.getAndIncrement();
    }
}