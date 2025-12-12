package br.upe.repository.impl;

import br.upe.model.SessaoTreino;
import br.upe.repository.ISessaoTreinoRepository;
import br.upe.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class SessaoTreinoRepositoryImpl implements ISessaoTreinoRepository {

    private EntityManager em() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public SessaoTreino salvar(SessaoTreino sessao) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            
            if (sessao.getIdSessao() == 0) {
                em.persist(sessao);
            } else {
                sessao = em.merge(sessao);
            }
            
            em.getTransaction().commit();
            return sessao;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<SessaoTreino> buscarPorId(int idSessao) {
        EntityManager em = em();
        try {
            SessaoTreino sessao = em.createQuery(
                    """
                    SELECT s FROM SessaoTreino s
                    LEFT JOIN FETCH s.itensExecutados
                    WHERE s.idSessao = :idSessao
                    """, SessaoTreino.class)
                    .setParameter("idSessao", idSessao)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            return Optional.ofNullable(sessao);
        } finally {
            em.close();
        }
    }

    @Override
    public List<SessaoTreino> listarPorUsuario(int idUsuario) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    """
                    SELECT s FROM SessaoTreino s
                    LEFT JOIN FETCH s.itensExecutados
                    WHERE s.usuario.id = :idUsuario
                    ORDER BY s.dataSessao DESC
                    """, SessaoTreino.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void editar(SessaoTreino sessao) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.merge(sessao);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void deletar(int idSessao) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            SessaoTreino sessao = em.find(SessaoTreino.class, idSessao);
            if (sessao != null) {
                em.remove(sessao);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}