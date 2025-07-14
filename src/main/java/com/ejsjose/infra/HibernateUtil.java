package com.ejsjose.infra;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

public class HibernateUtil {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("crudHibernatePU");

    static {
        // Criar a tabela products se ela não existir
        createTableIfNotExists();
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {
        emf.close();
    }

    private static void createTableIfNotExists() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery(
                "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "price REAL, " +
                "quantity INTEGER NOT NULL, " +
                "status BOOLEAN NOT NULL" +
                ")"
            ).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Tabela já existe ou erro ao criar: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}