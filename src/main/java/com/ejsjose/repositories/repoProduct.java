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

    public Product buscarPorId(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        Product produto = em.find(Product.class, id);
        em.close();
        return produto;
    }

    public List<Product> listarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        List<Product> produtos = em.createQuery("from Produto", Product.class).getResultList();
        em.close();
        return produtos;
    }

    public void atualizar(Product produto) {
        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(produto);
        em.getTransaction().commit();
        em.close();
    }

    public void remover(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        Product produto = em.find(Product.class, id);
        if (produto != null) {
            em.getTransaction().begin();
            em.remove(produto);
            em.getTransaction().commit();
        }
        em.close();
    }    

}
