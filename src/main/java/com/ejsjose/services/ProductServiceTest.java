package com.ejsjose.services;

import com.ejsjose.entities.Product;
import com.ejsjose.repositories.IProductRepository;
import com.ejsjose.repositories.ProductRepository;
import com.ejsjose.services.dto.ProductCreateDTO;
import com.ejsjose.services.dto.ProductUpdateDTO;
import com.ejsjose.services.dto.ProductFilterDTO;
import com.ejsjose.infra.HibernateUtil;

import java.util.List;
import java.util.Optional;

/**
 * Teste da camada de service para demonstrar as funcionalidades
 */
public class ProductServiceTest {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DA CAMADA DE SERVICE ===");
        System.out.println("Demonstração das funcionalidades da arquitetura em camadas");
        System.out.println();
        
        // Configurar dependências
        IProductRepository repository = new ProductRepository();
        IProductService service = new ProductService(repository);
        
        try {
            // Limpar dados
            System.out.println("--- LIMPEZA INICIAL ---");
            cleanupData(service);
            
            // Teste 1: Criação com DTOs
            System.out.println("\n--- TESTE 1: CRIAÇÃO COM DTOs ---");
            testProductCreation(service);
            
            // Teste 2: Validações de negócio
            System.out.println("\n--- TESTE 2: VALIDAÇÕES DE NEGÓCIO ---");
            testBusinessValidations(service);
            
            // Teste 3: Operações de negócio
            System.out.println("\n--- TESTE 3: OPERAÇÕES DE NEGÓCIO ---");
            testBusinessOperations(service);
            
            // Teste 4: Filtros avançados
            System.out.println("\n--- TESTE 4: FILTROS AVANÇADOS ---");
            testAdvancedFilters(service);
            
            // Teste 5: Estatísticas
            System.out.println("\n--- TESTE 5: ESTATÍSTICAS ---");
            testStatistics(service);
            
            System.out.println("\n✅ TODOS OS TESTES PASSARAM!");
            
        } catch (Exception e) {
            System.err.println("❌ TESTE FALHOU: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
            System.out.println("\nRecursos liberados.");
        }
    }
    
    private static void cleanupData(IProductService service) {
        List<Product> allProducts = service.findAllProducts();
        for (Product product : allProducts) {
            service.deleteProduct(product.getId());
        }
        System.out.printf("✓ %d produtos removidos%n", allProducts.size());
    }
    
    private static void testProductCreation(IProductService service) {
        // Criação normal
        ProductCreateDTO dto1 = new ProductCreateDTO("Smartphone", 899.99, 15, true);
        Product product1 = service.createProduct(dto1);
        System.out.printf("✓ Produto criado: %s (ID: %d)%n", product1.getName(), product1.getId());
        
        // Criação com valores padrão
        ProductCreateDTO dto2 = new ProductCreateDTO("Tablet", 449.99, null, null);
        Product product2 = service.createProduct(dto2);
        System.out.printf("✓ Produto criado com padrões: %s (Qtd: %d, Status: %s)%n", 
            product2.getName(), product2.getQuantity(), product2.isStatus() ? "Ativo" : "Inativo");
        
        // Teste de nome duplicado
        try {
            ProductCreateDTO dto3 = new ProductCreateDTO("Smartphone", 999.99, 10, true);
            service.createProduct(dto3);
            System.err.println("❌ Deveria ter falhado por nome duplicado");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validação de nome duplicado funcionou: " + e.getMessage());
        }
    }
    
    private static void testBusinessValidations(IProductService service) {
        // Validação de preço negativo
        try {
            ProductCreateDTO dto = new ProductCreateDTO("Produto Inválido", -100.0, 5, true);
            service.createProduct(dto);
            System.err.println("❌ Deveria ter falhado por preço negativo");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validação de preço negativo funcionou: " + e.getMessage());
        }
        
        // Validação de nome vazio
        try {
            ProductCreateDTO dto = new ProductCreateDTO("", 100.0, 5, true);
            service.createProduct(dto);
            System.err.println("❌ Deveria ter falhado por nome vazio");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validação de nome vazio funcionou: " + e.getMessage());
        }
        
        // Validação de atualização
        try {
            ProductUpdateDTO updateDto = new ProductUpdateDTO();
            service.updateProduct(1, updateDto);
            System.err.println("❌ Deveria ter falhado por DTO vazio");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validação de DTO vazio funcionou: " + e.getMessage());
        }
    }
    
    private static void testBusinessOperations(IProductService service) {
        // Buscar um produto para testar
        List<Product> products = service.findAllProducts();
        if (products.isEmpty()) {
            System.out.println("❌ Nenhum produto para testar operações de negócio");
            return;
        }
        
        Product product = products.get(0);
        Integer productId = product.getId();
        
        // Teste de ativação/desativação
        Product deactivated = service.deactivateProduct(productId);
        System.out.printf("✓ Produto desativado: %s (Status: %s)%n", 
            deactivated.getName(), deactivated.isStatus() ? "Ativo" : "Inativo");
        
        Product activated = service.activateProduct(productId);
        System.out.printf("✓ Produto ativado: %s (Status: %s)%n", 
            activated.getName(), activated.isStatus() ? "Ativo" : "Inativo");
        
        // Teste de atualização de preço
        Double oldPrice = product.getPrice();
        Double newPrice = oldPrice * 1.1; // Aumento de 10%
        Product updatedPrice = service.updateProductPrice(productId, newPrice);
        System.out.printf("✓ Preço atualizado: R$%.2f -> R$%.2f%n", oldPrice, updatedPrice.getPrice());
        
        // Teste de regra de negócio: redução maior que 50%
        try {
            Double invalidPrice = oldPrice * 0.4; // Redução de 60%
            service.updateProductPrice(productId, invalidPrice);
            System.err.println("❌ Deveria ter falhado por redução maior que 50%");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Regra de redução de preço funcionou: " + e.getMessage());
        }
    }
    
    private static void testAdvancedFilters(IProductService service) {
        // Criar produtos para teste
        service.createProduct(new ProductCreateDTO("Notebook Gaming", 3500.0, 3, true));
        service.createProduct(new ProductCreateDTO("Mouse Gamer", 89.99, 25, true));
        service.createProduct(new ProductCreateDTO("Teclado Gamer", 199.99, 10, false));
        
        // Teste filtro por nome
        ProductFilterDTO nameFilter = new ProductFilterDTO();
        nameFilter.setName("Gamer");
        List<Product> gamerProducts = service.findProductsWithFilters(nameFilter);
        System.out.printf("✓ Produtos 'Gamer' encontrados: %d%n", gamerProducts.size());
        
        // Teste filtro por faixa de preço
        ProductFilterDTO priceFilter = new ProductFilterDTO();
        priceFilter.setMinPrice(50.0);
        priceFilter.setMaxPrice(500.0);
        List<Product> midRangeProducts = service.findProductsWithFilters(priceFilter);
        System.out.printf("✓ Produtos na faixa R$50-500: %d%n", midRangeProducts.size());
        
        // Teste filtro por status
        ProductFilterDTO statusFilter = new ProductFilterDTO();
        statusFilter.setStatus(true);
        List<Product> activeProducts = service.findProductsWithFilters(statusFilter);
        System.out.printf("✓ Produtos ativos: %d%n", activeProducts.size());
        
        // Teste filtro combinado
        ProductFilterDTO combinedFilter = new ProductFilterDTO();
        combinedFilter.setName("Notebook");
        combinedFilter.setStatus(true);
        combinedFilter.setMinPrice(1000.0);
        List<Product> expensiveNotebooks = service.findProductsWithFilters(combinedFilter);
        System.out.printf("✓ Notebooks ativos caros: %d%n", expensiveNotebooks.size());
    }
    
    private static void testStatistics(IProductService service) {
        long totalProducts = service.countProducts();
        long activeProducts = service.countActiveProducts();
        long inactiveProducts = service.countInactiveProducts();
        Double totalValue = service.calculateTotalStockValue();
        
        System.out.printf("✓ Total de produtos: %d%n", totalProducts);
        System.out.printf("✓ Produtos ativos: %d%n", activeProducts);
        System.out.printf("✓ Produtos inativos: %d%n", inactiveProducts);
        System.out.printf("✓ Valor total do estoque: R$%.2f%n", totalValue);
        
        Optional<Product> mostExpensive = service.findMostExpensiveProduct();
        if (mostExpensive.isPresent()) {
            System.out.printf("✓ Produto mais caro: %s (R$%.2f)%n", 
                mostExpensive.get().getName(), mostExpensive.get().getPrice());
        }
        
        Optional<Product> cheapest = service.findCheapestProduct();
        if (cheapest.isPresent()) {
            System.out.printf("✓ Produto mais barato: %s (R$%.2f)%n", 
                cheapest.get().getName(), cheapest.get().getPrice());
        }
        
        // Teste de existência
        if (totalProducts > 0) {
            Product firstProduct = service.findAllProducts().get(0);
            boolean exists = service.productExists(firstProduct.getId());
            System.out.printf("✓ Produto %d existe: %s%n", firstProduct.getId(), exists);
            
            boolean nameExists = service.productNameExists(firstProduct.getName(), null);
            System.out.printf("✓ Nome '%s' existe: %s%n", firstProduct.getName(), nameExists);
        }
    }
}
