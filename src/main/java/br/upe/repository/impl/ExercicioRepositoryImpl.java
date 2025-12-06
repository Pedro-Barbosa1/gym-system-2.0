package br.upe.repository.impl;

import br.upe.util.JPAUtil;
import br.upe.model.Exercicio;
import br.upe.repository.IExercicioRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class ExercicioRepositoryImpl implements IExercicioRepository {

    private EntityManager em() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public Exercicio salvar(Exercicio exercicio) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(exercicio);
            em.getTransaction().commit();
            return exercicio;
        } finally {
            em.close();
        }
    }

    @Override
    public void editar(Exercicio exercicio) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.merge(exercicio);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void deletar(int idExercicio) {
        EntityManager em = em();
        try {
            Exercicio exercicio = em.find(Exercicio.class, idExercicio);
            if (exercicio != null) {
                em.getTransaction().begin();
                em.remove(exercicio);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Exercicio> buscarPorId(int idExercicio) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Exercicio.class, idExercicio));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Exercicio> buscarPorNome(String nome) {
        EntityManager em = em();
        try {
            List<Exercicio> lista = em
                    .createQuery("SELECT e FROM Exercicio e WHERE LOWER(e.nome) = LOWER(:nome)",
                            Exercicio.class)
                    .setParameter("nome", nome)
                    .getResultList();

            return lista.stream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Exercicio> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = em();
        try {
            return em
                    .createQuery("SELECT e FROM Exercicio e WHERE e.usuario.id = :idUsuario",
                            Exercicio.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int proximoId() {
        return 0; // banco cuida disso
    }
}