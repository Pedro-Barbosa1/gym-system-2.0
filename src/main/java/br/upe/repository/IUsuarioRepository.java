package br.upe.repository;

import br.upe.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(int id);
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> listarTodos();
    void editar(Usuario usuario);
    void deletar(int id);
    int gerarProximoId();
}