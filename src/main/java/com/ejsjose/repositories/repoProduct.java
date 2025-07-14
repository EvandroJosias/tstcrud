package com.ejsjose.repositories;

import com.ejsjose.entities.Product;
import com.ejsjose.infra.HibernateUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class repoProduct {


    public void salvar(Product produto) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(produto);
        em.getTransaction().commit();
        em.close();
    }

    public Product buscarPorId(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        Product produto = em.find(Product.class, id);
        em.close();
        return produto;
    }

    public List<Product> listarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        List<Product> produtos = em.createQuery("from Product", Product.class).getResultList();
        em.close();
        return produtos;
    }

    public Product buscarPrimeiro() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Product produto = em.createQuery("from Product order by id asc", Product.class)
                    .setMaxResults(1)
                    .getSingleResult();
            return produto;
        } catch (Exception e) {
            System.out.println("Nenhum produto encontrado na tabela!");
            return null;
        } finally {
            em.close();
        }
    }

    public void atualizar(Product produto) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(produto);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        Product produto = em.find(Product.class, id);
        if (produto != null) {
            em.remove(produto);
            System.out.println("Produto ID " + id + " removido com sucesso!");
        } else {
            System.out.println("Produto ID " + id + " não encontrado!");
        }
        em.getTransaction().commit();
        em.close();
    }    

}
