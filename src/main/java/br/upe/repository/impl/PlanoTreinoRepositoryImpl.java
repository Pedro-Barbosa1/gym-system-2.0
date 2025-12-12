package br.upe.repository.impl;

import br.upe.model.PlanoTreino;
import br.upe.repository.IPlanoTreinoRepository;
import br.upe.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanoTreinoRepositoryImpl implements IPlanoTreinoRepository {

    private static final Logger logger = Logger.getLogger(PlanoTreinoRepositoryImpl.class.getName());

    private EntityManager em() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public PlanoTreino salvar(PlanoTreino plano) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(plano);
            em.getTransaction().commit();
            return plano;
        } finally {
            em.close();
        }
    }

    @Override
    public void editar(PlanoTreino plano) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.merge(plano);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void deletar(int idPlano) {
        EntityManager em = em();
        try {
            PlanoTreino plano = em.find(PlanoTreino.class, idPlano);
            if (plano != null) {
                em.getTransaction().begin();
                em.remove(plano);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<PlanoTreino> buscarPorId(int idPlano) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(PlanoTreino.class, idPlano));
        } finally {
            em.close();
        }
    }

    @Override
    public List<PlanoTreino> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = em();
        try {
            logger.log(Level.FINE, "Buscando planos do usuário: {0}", idUsuario);
            return em.createQuery(
                            """
                                SELECT p FROM PlanoTreino p
                                LEFT JOIN FETCH p.itensTreino
                                WHERE p.usuario.id = :idUsuario
                            """,
                            PlanoTreino.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<PlanoTreino> buscarPorNome(String nome, int idUsuario) {
        EntityManager em = em();
        try {
            List<PlanoTreino> lista = em.createQuery(
                            """
                                SELECT p FROM PlanoTreino p
                                LEFT JOIN FETCH p.itensTreino
                                WHERE p.nome = :nome AND p.usuario.id = :idUsuario
                            """,
                            PlanoTreino.class)
                    .setParameter("nome", nome)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();

            return lista.stream().findFirst();
        } finally {
            em.close();
        }
    }

}