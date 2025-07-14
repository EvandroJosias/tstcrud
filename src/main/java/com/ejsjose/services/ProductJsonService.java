package com.ejsjose.services;

import com.ejsjose.entities.Product;
import com.ejsjose.services.dto.ProductCreateDTO;
import com.ejsjose.services.dto.ProductUpdateDTO;
import com.ejsjose.services.dto.ProductFilterDTO;
import com.ejsjose.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Optional;

/**
 * Service wrapper que trabalha com JSON
 * Todas as entradas e saídas são em formato JSON
 */
public class ProductJsonService {
    
    private final IProductService productService;
    
    public ProductJsonService(IProductService productService) {
        this.productService = productService;
    }
    
    // === OPERAÇÕES BÁSICAS CRUD ===
    
    /**
     * Cria produto a partir de JSON
     * @param createJson JSON com dados para criação
     * @return JSON do produto criado
     * @throws JsonProcessingException se JSON inválido
     */
    public String createProduct(String createJson) throws JsonProcessingException {
        ProductCreateDTO createDTO = JsonUtils.fromJson(createJson, ProductCreateDTO.class);
        Product product = productService.createProduct(createDTO);
        return JsonUtils.toJsonPretty(product);
    }
    
    /**
     * Busca produto por ID
     * @param id ID do produto
     * @return JSON do produto ou null se não encontrado
     * @throws JsonProcessingException se erro na serialização
     */
    public String findProductById(Integer id) throws JsonProcessingException {
        Optional<Product> product = productService.findProductById(id);
        if (product.isPresent()) {
            return JsonUtils.toJsonPretty(product.get());
        }
        return null;
    }
    
    /**
     * Lista todos os produtos
     * @return JSON array com todos os produtos
     * @throws JsonProcessingException se erro na serialização
     */
    public String findAllProducts() throws JsonProcessingException {
        List<Product> products = productService.findAllProducts();
        return JsonUtils.toJsonPretty(products);
    }
    
    /**
     * Atualiza produto
     * @param id ID do produto
     * @param updateJson JSON com dados para atualização
     * @return JSON do produto atualizado
     * @throws JsonProcessingException se JSON inválido
     */
    public String updateProduct(Integer id, String updateJson) throws JsonProcessingException {
        ProductUpdateDTO updateDTO = JsonUtils.fromJson(updateJson, ProductUpdateDTO.class);
        Product product = productService.updateProduct(id, updateDTO);
        return JsonUtils.toJsonPretty(product);
    }
    
    /**
     * Remove produto
     * @param id ID do produto
     * @return JSON com resultado da operação
     * @throws JsonProcessingException se erro na serialização
     */
    public String deleteProduct(Integer id) throws JsonProcessingException {
        productService.deleteProduct(id);
        
        // Retorna resultado da operação
        OperationResult result = new OperationResult(true, "Produto removido com sucesso", id);
        return JsonUtils.toJsonPretty(result);
    }
    
    // === OPERAÇÕES DE BUSCA ===
    
    /**
     * Busca produtos com filtros
     * @param filterJson JSON com filtros
     * @return JSON array com produtos encontrados
     * @throws JsonProcessingException se JSON inválido
     */
    public String findProductsWithFilters(String filterJson) throws JsonProcessingException {
        ProductFilterDTO filterDTO = JsonUtils.fromJson(filterJson, ProductFilterDTO.class);
        List<Product> products = productService.findProductsWithFilters(filterDTO);
        return JsonUtils.toJsonPretty(products);
    }
    
    /**
     * Busca produtos por nome
     * @param name nome do produto
     * @return JSON array com produtos encontrados
     * @throws JsonProcessingException se erro na serialização
     */
    public String findProductsByName(String name) throws JsonProcessingException {
        List<Product> products = productService.findProductsByName(name);
        return JsonUtils.toJsonPretty(products);
    }
    
    /**
     * Busca produtos por status
     * @param status status do produto
     * @return JSON array com produtos encontrados
     * @throws JsonProcessingException se erro na serialização
     */
    public String findProductsByStatus(Boolean status) throws JsonProcessingException {
        List<Product> products = productService.findProductsByStatus(status);
        return JsonUtils.toJsonPretty(products);
    }
    
    /**
     * Busca produtos por faixa de preço
     * @param minPrice preço mínimo
     * @param maxPrice preço máximo
     * @return JSON array com produtos encontrados
     * @throws JsonProcessingException se erro na serialização
     */
    public String findProductsByPriceRange(Double minPrice, Double maxPrice) throws JsonProcessingException {
        List<Product> products = productService.findProductsByPriceRange(minPrice, maxPrice);
        return JsonUtils.toJsonPretty(products);
    }
    
    // === OPERAÇÕES DE NEGÓCIO ===
    
    /**
     * Ativa produto
     * @param id ID do produto
     * @return JSON do produto ativado
     * @throws JsonProcessingException se erro na serialização
     */
    public String activateProduct(Integer id) throws JsonProcessingException {
        Product product = productService.activateProduct(id);
        return JsonUtils.toJsonPretty(product);
    }
    
    /**
     * Desativa produto
     * @param id ID do produto
     * @return JSON do produto desativado
     * @throws JsonProcessingException se erro na serialização
     */
    public String deactivateProduct(Integer id) throws JsonProcessingException {
        Product product = productService.deactivateProduct(id);
        return JsonUtils.toJsonPretty(product);
    }
    
    /**
     * Atualiza preço do produto
     * @param id ID do produto
     * @param newPrice novo preço
     * @return JSON do produto atualizado
     * @throws JsonProcessingException se erro na serialização
     */
    public String updateProductPrice(Integer id, Double newPrice) throws JsonProcessingException {
        Product product = productService.updateProductPrice(id, newPrice);
        return JsonUtils.toJsonPretty(product);
    }
    
    /**
     * Aplica desconto nos produtos que correspondem ao filtro
     * @param filterJson JSON com filtros para aplicar desconto
     * @param discountPercentage percentual de desconto
     * @return JSON com resultado da operação
     * @throws JsonProcessingException se erro na serialização
     */
    public String applyDiscount(String filterJson, Double discountPercentage) throws JsonProcessingException {
        ProductFilterDTO filterDTO = JsonUtils.fromJson(filterJson, ProductFilterDTO.class);
        int affectedProducts = productService.applyDiscount(filterDTO, discountPercentage);
        
        OperationResult result = new OperationResult(true, 
            "Desconto aplicado a " + affectedProducts + " produtos", null);
        return JsonUtils.toJsonPretty(result);
    }
    
    // === ESTATÍSTICAS ===
    
    /**
     * Obtém estatísticas dos produtos
     * @return JSON com estatísticas
     * @throws JsonProcessingException se erro na serialização
     */
    public String getStatistics() throws JsonProcessingException {
        long totalProducts = productService.countProducts();
        long activeProducts = productService.countActiveProducts();
        long inactiveProducts = productService.countInactiveProducts();
        Double totalValue = productService.calculateTotalStockValue();
        
        Optional<Product> mostExpensive = productService.findMostExpensiveProduct();
        Optional<Product> cheapest = productService.findCheapestProduct();
        
        ProductStatistics stats = new ProductStatistics(
            totalProducts, activeProducts, inactiveProducts, totalValue,
            mostExpensive.orElse(null), cheapest.orElse(null)
        );
        
        return JsonUtils.toJsonPretty(stats);
    }
    
    // === CLASSES AUXILIARES ===
    
    /**
     * Classe para resultado de operações
     */
    public static class OperationResult {
        private boolean success;
        private String message;
        private Integer id;
        
        public OperationResult() {}
        
        public OperationResult(boolean success, String message, Integer id) {
            this.success = success;
            this.message = message;
            this.id = id;
        }
        
        // Getters e Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }
    
    /**
     * Classe para estatísticas de produtos
     */
    public static class ProductStatistics {
        private long totalProducts;
        private long activeProducts;
        private long inactiveProducts;
        private Double totalStockValue;
        private Product mostExpensiveProduct;
        private Product cheapestProduct;
        
        public ProductStatistics() {}
        
        public ProductStatistics(long totalProducts, long activeProducts, long inactiveProducts, 
                               Double totalStockValue, Product mostExpensiveProduct, Product cheapestProduct) {
            this.totalProducts = totalProducts;
            this.activeProducts = activeProducts;
            this.inactiveProducts = inactiveProducts;
            this.totalStockValue = totalStockValue;
            this.mostExpensiveProduct = mostExpensiveProduct;
            this.cheapestProduct = cheapestProduct;
        }
        
        // Getters e Setters
        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        
        public long getActiveProducts() { return activeProducts; }
        public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }
        
        public long getInactiveProducts() { return inactiveProducts; }
        public void setInactiveProducts(long inactiveProducts) { this.inactiveProducts = inactiveProducts; }
        
        public Double getTotalStockValue() { return totalStockValue; }
        public void setTotalStockValue(Double totalStockValue) { this.totalStockValue = totalStockValue; }
        
        public Product getMostExpensiveProduct() { return mostExpensiveProduct; }
        public void setMostExpensiveProduct(Product mostExpensiveProduct) { this.mostExpensiveProduct = mostExpensiveProduct; }
        
        public Product getCheapestProduct() { return cheapestProduct; }
        public void setCheapestProduct(Product cheapestProduct) { this.cheapestProduct = cheapestProduct; }
    }
}
