package com.ejsjose.repositories;

import com.ejsjose.entities.Product;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de produtos seguindo as melhores práticas
 */
public interface IProductRepository {
    
    // CREATE
    Product save(Product product);
    
    // READ
    Optional<Product> findById(Integer id);
    List<Product> findAll();
    Optional<Product> findFirst();
    List<Product> findByName(String name);
    List<Product> findByStatus(Boolean status);
    List<Product> findByPriceRange(Double minPrice, Double maxPrice);
    long count();
    boolean existsById(Integer id);
    
    // UPDATE
    Product update(Product product);
    
    // DELETE
    void deleteById(Integer id);
    void delete(Product product);
    void deleteAll();
}
