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

    @Override
    public boolean exercicioEstaSendoUsadoEmPlano(int idExercicio) {
        EntityManager em = em();
        try {
            // Verificar uso em ItemPlanoTreino
            Long countPlano = em.createQuery(
                "SELECT COUNT(i) FROM ItemPlanoTreino i WHERE i.exercicio.idExercicio = :idExercicio",
                Long.class)
                .setParameter("idExercicio", idExercicio)
                .getSingleResult();
                
            // Verificar uso em ItemSessaoTreino (sessões de treino realizadas)
            Long countSessao = em.createQuery(
                "SELECT COUNT(ist) FROM ItemSessaoTreino ist WHERE ist.exercicio.idExercicio = :idExercicio",
                Long.class)
                .setParameter("idExercicio", idExercicio)
                .getSingleResult();
            
            // Log para debug
            System.out.println("DEBUG - Exercício " + idExercicio + 
                             ": countPlano=" + countPlano + 
                             ", countSessao=" + countSessao);
                
            return (countPlano + countSessao) > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public String verificarUsodoExercicio(int idExercicio) {
        EntityManager em = em();
        try {
            // Verificar uso em ItemPlanoTreino
            Long countPlano = em.createQuery(
                "SELECT COUNT(i) FROM ItemPlanoTreino i WHERE i.exercicio.idExercicio = :idExercicio",
                Long.class)
                .setParameter("idExercicio", idExercicio)
                .getSingleResult();
                
            // Verificar uso em ItemSessaoTreino (sessões de treino realizadas)
            Long countSessao = em.createQuery(
                "SELECT COUNT(ist) FROM ItemSessaoTreino ist WHERE ist.exercicio.idExercicio = :idExercicio",
                Long.class)
                .setParameter("idExercicio", idExercicio)
                .getSingleResult();
            
            if (countPlano > 0 && countSessao > 0) {
                return "Não é possível excluir este exercício porque:\n\n" +
                       "• Está em " + countPlano + " plano(s) de treino\n" +
                       "• Está registrado em " + countSessao + " sessão(ões) de treino\n\n" +
                       "Para excluir, remova-o de todos os planos e limpe o histórico de treinos.";
            } else if (countPlano > 0) {
                return "Não é possível excluir este exercício porque está em " + countPlano + " plano(s) de treino.\n\n" +
                       "Remova-o dos planos de treino primeiro.";
            } else if (countSessao > 0) {
                return "Não é possível excluir este exercício porque está registrado em " + countSessao + " sessão(ões) de treino (histórico).\n\n" +
                       "O exercício já foi usado em treinos anteriores.";
            }
            
            return null; // Pode ser excluído
        } finally {
            em.close();
        }
    }

    @Override
    public void deletarComReferencias(int idExercicio) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            
            // 1. Remover de ItemSessaoTreino (histórico de sessões)
            int deletedSessoes = em.createQuery(
                "DELETE FROM ItemSessaoTreino ist WHERE ist.exercicio.idExercicio = :idExercicio")
                .setParameter("idExercicio", idExercicio)
                .executeUpdate();
            
            // 2. Remover de ItemPlanoTreino (planos de treino)
            int deletedPlanos = em.createQuery(
                "DELETE FROM ItemPlanoTreino ipt WHERE ipt.exercicio.idExercicio = :idExercicio")
                .setParameter("idExercicio", idExercicio)
                .executeUpdate();
            
            // 3. Remover o exercício
            Exercicio exercicio = em.find(Exercicio.class, idExercicio);
            if (exercicio != null) {
                em.remove(exercicio);
            }
            
            em.getTransaction().commit();
            
            System.out.println("Exercício " + idExercicio + " excluído forçadamente:");
            System.out.println("  - " + deletedSessoes + " referência(s) removida(s) de sessões de treino");
            System.out.println("  - " + deletedPlanos + " referência(s) removida(s) de planos de treino");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}