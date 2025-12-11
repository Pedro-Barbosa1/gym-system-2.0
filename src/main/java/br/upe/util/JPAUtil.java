package br.upe.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());
    
    public static final EntityManagerFactory emf = criarEntityManagerFactory();

    /**
     * Cria o EntityManagerFactory com tratamento de erro
     */
    private static EntityManagerFactory criarEntityManagerFactory() {
        try {
            logger.info("Inicializando EntityManagerFactory...");
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("AcademiaPU");
            logger.info("EntityManagerFactory inicializado com sucesso!");
            return factory;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao criar EntityManagerFactory", e);
            throw e; // Relança a exceção para que a aplicação saiba que houve erro
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory não está disponível. Verifique a conexão com o banco de dados.");
        }
        return emf.createEntityManager();
    }
    
    /**
     * Fecha o EntityManagerFactory quando a aplicação terminar
     */
    public static void fecharFactory() {
        if (emf != null && emf.isOpen()) {
            logger.info("Fechando EntityManagerFactory...");
            emf.close();
        }
    }
}

