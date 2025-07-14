package com.ejsjose.services;

import com.ejsjose.entities.Product;
import com.ejsjose.repositories.IProductRepository;
import com.ejsjose.services.dto.ProductCreateDTO;
import com.ejsjose.services.dto.ProductUpdateDTO;
import com.ejsjose.services.dto.ProductFilterDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de produtos
 * 
 * Esta camada implementa a lógica de negócio e coordena
 * as operações entre a camada de apresentação e repositório
 */
public class ProductService implements IProductService {
    
    private final IProductRepository productRepository;
    
    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // === OPERAÇÕES BÁSICAS CRUD ===
    
    @Override
    public Product createProduct(ProductCreateDTO createDTO) {
        // Validações de negócio
        validateProductCreate(createDTO);
        
        // Verificar se nome já existe
        if (productNameExists(createDTO.getName(), null)) {
            throw new IllegalArgumentException("Já existe um produto com o nome: " + createDTO.getName());
        }
        
        // Converter DTO para entidade
        Product product = new Product();
        product.setName(createDTO.getName().trim());
        product.setPrice(createDTO.getPrice());
        product.setQuantity(createDTO.getQuantity() != null ? createDTO.getQuantity() : 0);
        product.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : true);
        
        // Salvar
        return productRepository.save(product);
    }
    
    @Override
    public Optional<Product> findProductById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return productRepository.findById(id);
    }
    
    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Product updateProduct(Integer id, ProductUpdateDTO updateDTO) {
        // Validações
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do produto é obrigatório e deve ser maior que zero");
        }
        
        if (updateDTO == null || !updateDTO.hasAnyField()) {
            throw new IllegalArgumentException("Pelo menos um campo deve ser fornecido para atualização");
        }
        
        // Buscar produto existente
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto com ID " + id + " não encontrado");
        }
        
        Product product = productOpt.get();
        
        // Validar nome único se fornecido
        if (updateDTO.getName() != null) {
            String newName = updateDTO.getName().trim();
            if (productNameExists(newName, id)) {
                throw new IllegalArgumentException("Já existe outro produto com o nome: " + newName);
            }
            product.setName(newName);
        }
        
        // Validar e atualizar preço
        if (updateDTO.getPrice() != null) {
            validatePrice(updateDTO.getPrice());
            product.setPrice(updateDTO.getPrice());
        }
        
        // Atualizar quantidade
        if (updateDTO.getQuantity() != null) {
            validateQuantity(updateDTO.getQuantity());
            product.setQuantity(updateDTO.getQuantity());
        }
        
        // Atualizar status
        if (updateDTO.getStatus() != null) {
            product.setStatus(updateDTO.getStatus());
        }
        
        return productRepository.update(product);
    }
    
    @Override
    public void deleteProduct(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do produto é obrigatório e deve ser maior que zero");
        }
        
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto com ID " + id + " não encontrado");
        }
        
        productRepository.deleteById(id);
    }
    
    // === OPERAÇÕES DE BUSCA ===
    
    @Override
    public List<Product> findProductsWithFilters(ProductFilterDTO filterDTO) {
        if (filterDTO == null || !filterDTO.hasAnyFilter()) {
            return findAllProducts();
        }
        
        List<Product> products = findAllProducts();
        
        // Aplicar filtros
        return products.stream()
                .filter(product -> matchesNameFilter(product, filterDTO.getName()))
                .filter(product -> matchesPriceFilter(product, filterDTO.getMinPrice(), filterDTO.getMaxPrice()))
                .filter(product -> matchesQuantityFilter(product, filterDTO.getMinQuantity(), filterDTO.getMaxQuantity()))
                .filter(product -> matchesStatusFilter(product, filterDTO.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        return productRepository.findByName(name.trim());
    }
    
    @Override
    public List<Product> findProductsByStatus(Boolean active) {
        return productRepository.findByStatus(active);
    }
    
    @Override
    public List<Product> findProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    // === OPERAÇÕES DE NEGÓCIO ===
    
    @Override
    public Product activateProduct(Integer id) {
        Optional<Product> productOpt = findProductById(id);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto com ID " + id + " não encontrado");
        }
        
        Product product = productOpt.get();
        product.setStatus(true);
        return productRepository.update(product);
    }
    
    @Override
    public Product deactivateProduct(Integer id) {
        Optional<Product> productOpt = findProductById(id);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto com ID " + id + " não encontrado");
        }
        
        Product product = productOpt.get();
        product.setStatus(false);
        return productRepository.update(product);
    }
    
    @Override
    public Product updateProductPrice(Integer id, Double newPrice) {
        validatePrice(newPrice);
        
        Optional<Product> productOpt = findProductById(id);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto com ID " + id + " não encontrado");
        }
        
        Product product = productOpt.get();
        Double oldPrice = product.getPrice();
        
        // Regra de negócio: não permitir redução de preço maior que 50%
        if (oldPrice != null && newPrice < oldPrice * 0.5) {
            throw new IllegalArgumentException(
                String.format("Nova preço (R$%.2f) não pode ser menor que 50%% do preço atual (R$%.2f)", 
                newPrice, oldPrice));
        }
        
        product.setPrice(newPrice);
        return productRepository.update(product);
    }
    
    @Override
    public int applyDiscount(ProductFilterDTO filterDTO, Double discountPercentage) {
        if (discountPercentage == null || discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Percentual de desconto deve estar entre 0 e 100");
        }
        
        List<Product> products = findProductsWithFilters(filterDTO);
        int updatedCount = 0;
        
        for (Product product : products) {
            if (product.getPrice() != null && product.getPrice() > 0) {
                Double newPrice = product.getPrice() * (1 - discountPercentage / 100);
                product.setPrice(newPrice);
                productRepository.update(product);
                updatedCount++;
            }
        }
        
        return updatedCount;
    }
    
    // === ESTATÍSTICAS E RELATÓRIOS ===
    
    @Override
    public long countProducts() {
        return productRepository.count();
    }
    
    @Override
    public long countActiveProducts() {
        return productRepository.findByStatus(true).size();
    }
    
    @Override
    public long countInactiveProducts() {
        return productRepository.findByStatus(false).size();
    }
    
    @Override
    public Double calculateTotalStockValue() {
        return findAllProducts().stream()
                .filter(product -> product.getPrice() != null && product.getQuantity() > 0)
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }
    
    @Override
    public Optional<Product> findMostExpensiveProduct() {
        return findAllProducts().stream()
                .filter(product -> product.getPrice() != null)
                .max((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
    }
    
    @Override
    public Optional<Product> findCheapestProduct() {
        return findAllProducts().stream()
                .filter(product -> product.getPrice() != null)
                .min((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
    }
    
    @Override
    public boolean productExists(Integer id) {
        return productRepository.existsById(id);
    }
    
    @Override
    public boolean productNameExists(String name, Integer excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        List<Product> products = productRepository.findByName(name.trim());
        
        if (excludeId == null) {
            return !products.isEmpty();
        }
        
        return products.stream().anyMatch(product -> !product.getId().equals(excludeId));
    }
    
    // === MÉTODOS DE VALIDAÇÃO PRIVADOS ===
    
    private void validateProductCreate(ProductCreateDTO createDTO) {
        if (createDTO == null) {
            throw new IllegalArgumentException("Dados do produto são obrigatórios");
        }
        
        // Validar nome
        if (createDTO.getName() == null || createDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (createDTO.getName().trim().length() > 255) {
            throw new IllegalArgumentException("Nome do produto deve ter no máximo 255 caracteres");
        }
        
        // Validar preço
        validatePrice(createDTO.getPrice());
        
        // Validar quantidade
        if (createDTO.getQuantity() != null) {
            validateQuantity(createDTO.getQuantity());
        }
    }
    
    private void validatePrice(Double price) {
        if (price == null) {
            throw new IllegalArgumentException("Preço é obrigatório");
        }
        
        if (price < 0) {
            throw new IllegalArgumentException("Preço deve ser maior ou igual a zero");
        }
        
        if (price > 999999.99) {
            throw new IllegalArgumentException("Preço deve ser menor que R$ 999.999,99");
        }
    }
    
    private void validateQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior ou igual a zero");
        }
        
        if (quantity > 999999) {
            throw new IllegalArgumentException("Quantidade deve ser menor que 999.999");
        }
    }
    
    private boolean matchesNameFilter(Product product, String nameFilter) {
        if (nameFilter == null || nameFilter.trim().isEmpty()) {
            return true;
        }
        
        return product.getName() != null && 
               product.getName().toLowerCase().contains(nameFilter.trim().toLowerCase());
    }
    
    private boolean matchesPriceFilter(Product product, Double minPrice, Double maxPrice) {
        if (product.getPrice() == null) {
            return false;
        }
        
        if (minPrice != null && product.getPrice() < minPrice) {
            return false;
        }
        
        if (maxPrice != null && product.getPrice() > maxPrice) {
            return false;
        }
        
        return true;
    }
    
    private boolean matchesQuantityFilter(Product product, Integer minQuantity, Integer maxQuantity) {
        if (minQuantity != null && product.getQuantity() < minQuantity) {
            return false;
        }
        
        if (maxQuantity != null && product.getQuantity() > maxQuantity) {
            return false;
        }
        
        return true;
    }
    
    private boolean matchesStatusFilter(Product product, Boolean statusFilter) {
        if (statusFilter == null) {
            return true;
        }
        
        return product.isStatus() == statusFilter;
    }
}
