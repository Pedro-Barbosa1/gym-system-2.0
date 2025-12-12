package br.upe.Integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilitario para gerenciar o EntityManagerFactory para testes de integracao.
 * Usa o persistence unit "TestePU" configurado para H2 em memoria.
 */
public class IntegrationTestHelper {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    /**
     * Inicializa o EntityManagerFactory para testes.
     * Deve ser chamado no @BeforeAll dos testes de integracao.
     */
    public static void initEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("TestePU");
        }
    }

    /**
     * Retorna o EntityManagerFactory para testes.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            initEntityManagerFactory();
        }
        return emf;
    }

    /**
     * Cria um novo EntityManager.
     */
    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory.
     * Deve ser chamado no @AfterAll dos testes de integracao.
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Executa uma acao dentro de uma transacao.
     */
    public static void executeInTransaction(TransactionAction action) {
        EntityManager entityManager = createEntityManager();
        try {
            entityManager.getTransaction().begin();
            action.execute(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }
    }

    /**
     * Interface funcional para acoes dentro de transacao.
     */
    @FunctionalInterface
    public interface TransactionAction {
        void execute(EntityManager em);
    }
}
