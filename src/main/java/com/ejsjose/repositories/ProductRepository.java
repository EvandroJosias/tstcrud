package com.ejsjose.repositories;

import com.ejsjose.entities.Product;
import com.ejsjose.infra.HibernateUtil;

import javax.persistence.EntityManager;
// import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de produtos seguindo as melhores práticas
 */
public class ProductRepository implements IProductRepository {

    @Override
    public Product save(Product product) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (product.getId() == null) {
                // Novo produto - INSERT
                em.persist(product);
            } else {
                // Produto existente - UPDATE
                product = em.merge(product);
            }
            
            em.getTransaction().commit();
            return product;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Product> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Product product = em.find(Product.class, id);
            return Optional.ofNullable(product);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Product> findFirst() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p ORDER BY p.id ASC", Product.class);
            query.setMaxResults(1);
            List<Product> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name) ORDER BY p.id", 
                Product.class
            );
            query.setParameter("name", "%" + name.trim() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByStatus(Boolean status) {
        if (status == null) {
            return findAll();
        }
        
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.status = :status ORDER BY p.id", 
                Product.class
            );
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT p FROM Product p WHERE 1=1");
            
            if (minPrice != null) {
                jpql.append(" AND p.price >= :minPrice");
            }
            if (maxPrice != null) {
                jpql.append(" AND p.price <= :maxPrice");
            }
            jpql.append(" ORDER BY p.price");
            
            TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
            
            if (minPrice != null) {
                query.setParameter("minPrice", minPrice);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsById(Integer id) {
        if (id == null) {
            return false;
        }
        return findById(id).isPresent();
    }

    @Override
    public Product update(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Produto ou ID não pode ser nulo para atualização");
        }
        
        if (!existsById(product.getId())) {
            throw new RuntimeException("Produto com ID " + product.getId() + " não encontrado");
        }
        
        return save(product);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
                throw new RuntimeException("Produto com ID " + id + " não encontrado");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Produto ou ID não pode ser nulo");
        }
        deleteById(product.getId());
    }

    @Override
    public void deleteAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Product").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao remover todos os produtos: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
