package br.upe.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    public static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("AcademiaPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

