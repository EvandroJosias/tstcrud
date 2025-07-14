package com.ejsjose.services;

import com.ejsjose.entities.Product;
import com.ejsjose.services.dto.ProductCreateDTO;
import com.ejsjose.services.dto.ProductUpdateDTO;
import com.ejsjose.services.dto.ProductFilterDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface do serviço de produtos
 * 
 * A camada de serviço é responsável por:
 * 1. Implementar a lógica de negócio
 * 2. Validar regras de negócio
 * 3. Coordenar operações entre múltiplos repositórios
 * 4. Transformar dados (DTOs)
 * 5. Gerenciar transações complexas
 */
public interface IProductService {
    
    // === OPERAÇÕES BÁSICAS CRUD ===
    
    /**
     * Cria um novo produto
     * @param createDTO dados para criação do produto
     * @return produto criado
     * @throws IllegalArgumentException se dados inválidos
     */
    Product createProduct(ProductCreateDTO createDTO);
    
    /**
     * Busca produto por ID
     * @param id ID do produto
     * @return produto encontrado ou empty
     */
    Optional<Product> findProductById(Integer id);
    
    /**
     * Lista todos os produtos
     * @return lista de produtos
     */
    List<Product> findAllProducts();
    
    /**
     * Atualiza produto existente
     * @param id ID do produto
     * @param updateDTO dados para atualização
     * @return produto atualizado
     * @throws IllegalArgumentException se produto não existe ou dados inválidos
     */
    Product updateProduct(Integer id, ProductUpdateDTO updateDTO);
    
    /**
     * Remove produto por ID
     * @param id ID do produto
     * @throws IllegalArgumentException se produto não existe
     */
    void deleteProduct(Integer id);
    
    // === OPERAÇÕES DE BUSCA ===
    
    /**
     * Busca produtos com filtros
     * @param filterDTO filtros para busca
     * @return lista de produtos filtrados
     */
    List<Product> findProductsWithFilters(ProductFilterDTO filterDTO);
    
    /**
     * Busca produtos por nome
     * @param name nome ou parte do nome
     * @return lista de produtos
     */
    List<Product> findProductsByName(String name);
    
    /**
     * Busca produtos por status
     * @param active true para ativos, false para inativos, null para todos
     * @return lista de produtos
     */
    List<Product> findProductsByStatus(Boolean active);
    
    /**
     * Busca produtos por faixa de preço
     * @param minPrice preço mínimo (pode ser null)
     * @param maxPrice preço máximo (pode ser null)
     * @return lista de produtos
     */
    List<Product> findProductsByPriceRange(Double minPrice, Double maxPrice);
    
    // === OPERAÇÕES DE NEGÓCIO ===
    
    /**
     * Ativa um produto
     * @param id ID do produto
     * @return produto ativado
     */
    Product activateProduct(Integer id);
    
    /**
     * Desativa um produto
     * @param id ID do produto
     * @return produto desativado
     */
    Product deactivateProduct(Integer id);
    
    /**
     * Atualiza preço do produto com validação de negócio
     * @param id ID do produto
     * @param newPrice novo preço
     * @return produto com preço atualizado
     */
    Product updateProductPrice(Integer id, Double newPrice);
    
    /**
     * Aplica desconto em produtos por categoria/filtro
     * @param filterDTO filtro para produtos
     * @param discountPercentage percentual de desconto (0-100)
     * @return número de produtos atualizados
     */
    int applyDiscount(ProductFilterDTO filterDTO, Double discountPercentage);
    
    // === ESTATÍSTICAS E RELATÓRIOS ===
    
    /**
     * Conta total de produtos
     * @return número total de produtos
     */
    long countProducts();
    
    /**
     * Conta produtos ativos
     * @return número de produtos ativos
     */
    long countActiveProducts();
    
    /**
     * Conta produtos inativos
     * @return número de produtos inativos
     */
    long countInactiveProducts();
    
    /**
     * Calcula valor total do estoque
     * @return valor total baseado em preço * quantidade
     */
    Double calculateTotalStockValue();
    
    /**
     * Busca produto mais caro
     * @return produto com maior preço
     */
    Optional<Product> findMostExpensiveProduct();
    
    /**
     * Busca produto mais barato
     * @return produto com menor preço
     */
    Optional<Product> findCheapestProduct();
    
    /**
     * Verifica se produto existe
     * @param id ID do produto
     * @return true se existe, false caso contrário
     */
    boolean productExists(Integer id);
    
    /**
     * Verifica se nome do produto já existe
     * @param name nome do produto
     * @param excludeId ID para excluir da verificação (para updates)
     * @return true se nome já existe
     */
    boolean productNameExists(String name, Integer excludeId);
}
