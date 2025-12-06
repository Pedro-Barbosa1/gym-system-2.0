package br.upe.repository.impl;

import br.upe.model.Usuario;
import br.upe.repository.IUsuarioRepository;
import br.upe.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UsuarioRepositoryImpl implements IUsuarioRepository {

    @Override
    public Usuario salvar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        if (usuario.getId() == 0) {
            em.persist(usuario);
        } else {
            usuario = em.merge(usuario);
        }

        em.getTransaction().commit();
        em.close();
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        Usuario usuario = em.find(Usuario.class, id);
        em.close();
        return Optional.ofNullable(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();

        TypedQuery<Usuario> query =
                em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
        query.setParameter("email", email);

        List<Usuario> result = query.getResultList();
        em.close();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Usuario> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Usuario> lista =
                em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                        .getResultList();
        em.close();
        return lista;
    }

    @Override
    public void editar(Usuario usuario) {
        salvar(usuario); // merge já faz edição
    }

    @Override
    public void deletar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        Usuario usuario = em.find(Usuario.class, id);
        if (usuario != null) {
            em.remove(usuario);
        }

        em.getTransaction().commit();
        em.close();
    }
}