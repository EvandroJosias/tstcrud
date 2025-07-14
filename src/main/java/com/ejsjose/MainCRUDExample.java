package com.ejsjose;

import com.ejsjose.entities.Product;
import com.ejsjose.repositories.IProductRepository;
import com.ejsjose.repositories.ProductRepository;
import com.ejsjose.services.IProductService;
import com.ejsjose.services.ProductService;
import com.ejsjose.services.dto.ProductCreateDTO;
import com.ejsjose.services.dto.ProductUpdateDTO;
import com.ejsjose.services.dto.ProductFilterDTO;
import com.ejsjose.infra.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Exemplo completo de CRUD seguindo as melhores práticas com arquitetura em camadas
 * 
 * Este exemplo demonstra:
 * 1. Arquitetura em camadas (Presentation -> Service -> Repository -> Entity)
 * 2. Uso de DTOs para transferência de dados
 * 3. Lógica de negócio na camada de serviço
 * 4. Validações e regras de negócio
 * 5. Operações CRUD completas
 * 6. Fechamento adequado de recursos
 */
public class MainCRUDExample {
    
    private final IProductService productService;
    private final Scanner scanner;
    
    public MainCRUDExample() {
        // Injeção de dependência manual (em projetos reais usar Spring/CDI)
        IProductRepository productRepository = new ProductRepository();
        this.productService = new ProductService(productRepository);
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        System.out.println("=== EXEMPLO DE CRUD COM MELHORES PRÁTICAS ===");
        System.out.println("Projeto: Java + Hibernate + JPA + SQLite + Maven");
        System.out.println();
        
        MainCRUDExample app = new MainCRUDExample();
        
        try {
            // Demonstrar todas as operações CRUD
            app.demonstrateCRUDOperations();
            
            // Menu interativo
            app.runInteractiveMenu();
            
        } catch (Exception e) {
            System.err.println("Erro na aplicação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            app.cleanup();
        }
    }
    
    /**
     * Demonstra todas as operações CRUD com dados de exemplo
     */
    private void demonstrateCRUDOperations() {
        System.out.println("--- DEMONSTRAÇÃO AUTOMÁTICA DE CRUD ---");
        
        try {
            // 1. CREATE - Criando produtos
            System.out.println("\n1. CREATE - Criando produtos...");
            Product notebook = createProduct("Notebook Dell", 2500.00, 10, true);
            Product mouse = createProduct("Mouse Logitech", 45.90, 25, true);
            Product teclado = createProduct("Teclado Mecânico", 180.00, 5, false);
            
            // 2. READ - Lendo dados
            System.out.println("\n2. READ - Listando todos os produtos:");
            listAllProducts();
            
            System.out.println("\n2.1. READ - Buscando produto por ID:");
            findProductById(notebook.getId());
            
            System.out.println("\n2.2. READ - Buscando produtos por nome:");
            findProductsByName("mouse");
            
            System.out.println("\n2.3. READ - Buscando produtos ativos:");
            findProductsByStatus(true);
            
            System.out.println("\n2.4. READ - Buscando produtos por faixa de preço:");
            findProductsByPriceRange(40.0, 200.0);
            
            // 3. UPDATE - Atualizando produto
            System.out.println("\n3. UPDATE - Atualizando produto...");
            updateProduct(teclado.getId(), "Teclado Mecânico RGB", 220.00, 8, true);
            
            // 4. COUNT e EXISTS
            System.out.println("\n4. CONTAGEM E VERIFICAÇÃO:");
            System.out.println("Total de produtos: " + productService.countProducts());
            System.out.println("Produto com ID " + notebook.getId() + " existe? " + 
                productService.productExists(notebook.getId()));
            
            // 5. OPERAÇÕES DE NEGÓCIO
            System.out.println("\n5. OPERAÇÕES DE NEGÓCIO:");
            System.out.println("Desativando produto...");
            productService.deactivateProduct(mouse.getId());
            System.out.println("Ativando produto novamente...");
            productService.activateProduct(mouse.getId());
            
            // 6. ESTATÍSTICAS
            System.out.println("\n6. ESTATÍSTICAS:");
            showBusinessStatistics();
            
            // 7. DELETE - Removendo produto
            System.out.println("\n7. DELETE - Removendo produto...");
            deleteProduct(mouse.getId());
            
            System.out.println("\nEstado final dos produtos:");
            listAllProducts();
            
        } catch (Exception e) {
            System.err.println("Erro na demonstração: " + e.getMessage());
        }
    }
    
    /**
     * Menu interativo para operações CRUD
     */
    private void runInteractiveMenu() {
        System.out.println("\n\n--- MENU INTERATIVO ---");
        
        while (true) {
            showMenu();
            int option = getIntInput("Escolha uma opção: ");
            
            try {
                switch (option) {
                    case 1 -> interactiveCreate();
                    case 2 -> interactiveList();
                    case 3 -> interactiveFind();
                    case 4 -> interactiveUpdate();
                    case 5 -> interactiveDelete();
                    case 6 -> interactiveSearch();
                    case 7 -> showStatistics();
                    case 8 -> businessOperations();
                    case 0 -> {
                        System.out.println("Encerrando aplicação...");
                        return;
                    }
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
            
            System.out.println("\nPressione Enter para continuar...");
            scanner.nextLine();
        }
    }
    
    private void showMenu() {
        System.out.println("\n=== MENU DE OPERAÇÕES ===");
        System.out.println("1. Criar produto");
        System.out.println("2. Listar todos os produtos");
        System.out.println("3. Buscar produto por ID");
        System.out.println("4. Atualizar produto");
        System.out.println("5. Remover produto");
        System.out.println("6. Buscar produtos (filtros)");
        System.out.println("7. Estatísticas");
        System.out.println("8. Operações de negócio");
        System.out.println("0. Sair");
    }
    
    // === OPERAÇÕES CRUD COM SERVICE ===
    
    private Product createProduct(String name, Double price, Integer quantity, Boolean status) {
        try {
            ProductCreateDTO createDTO = new ProductCreateDTO(name, price, quantity, status);
            Product savedProduct = productService.createProduct(createDTO);
            
            System.out.printf("✓ Produto criado: ID=%d, Nome='%s', Preço=R$%.2f, Qtd=%d, Status=%s%n",
                savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice(), 
                savedProduct.getQuantity(), savedProduct.isStatus() ? "Ativo" : "Inativo");
            
            return savedProduct;
        } catch (Exception e) {
            System.err.println("✗ Erro ao criar produto: " + e.getMessage());
            throw e;
        }
    }
    
    private void listAllProducts() {
        try {
            List<Product> products = productService.findAllProducts();
            
            if (products.isEmpty()) {
                System.out.println("Nenhum produto encontrado.");
                return;
            }
            
            System.out.println("Produtos encontrados:");
            products.forEach(this::displayProduct);
        } catch (Exception e) {
            System.err.println("✗ Erro ao listar produtos: " + e.getMessage());
        }
    }
    
    private void findProductById(Integer id) {
        try {
            Optional<Product> productOpt = productService.findProductById(id);
            
            if (productOpt.isPresent()) {
                System.out.println("Produto encontrado:");
                displayProduct(productOpt.get());
            } else {
                System.out.println("Produto com ID " + id + " não encontrado.");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao buscar produto: " + e.getMessage());
        }
    }
    
    private void findProductsByName(String name) {
        try {
            List<Product> products = productService.findProductsByName(name);
            
            if (products.isEmpty()) {
                System.out.println("Nenhum produto encontrado com o nome '" + name + "'.");
                return;
            }
            
            System.out.println("Produtos encontrados com nome '" + name + "':");
            products.forEach(this::displayProduct);
        } catch (Exception e) {
            System.err.println("✗ Erro ao buscar produtos por nome: " + e.getMessage());
        }
    }
    
    private void findProductsByStatus(Boolean status) {
        try {
            List<Product> products = productService.findProductsByStatus(status);
            String statusText = status ? "ativos" : "inativos";
            
            if (products.isEmpty()) {
                System.out.println("Nenhum produto " + statusText + " encontrado.");
                return;
            }
            
            System.out.println("Produtos " + statusText + ":");
            products.forEach(this::displayProduct);
        } catch (Exception e) {
            System.err.println("✗ Erro ao buscar produtos por status: " + e.getMessage());
        }
    }
    
    private void findProductsByPriceRange(Double minPrice, Double maxPrice) {
        try {
            List<Product> products = productService.findProductsByPriceRange(minPrice, maxPrice);
            
            if (products.isEmpty()) {
                System.out.printf("Nenhum produto encontrado na faixa de R$%.2f a R$%.2f.%n", minPrice, maxPrice);
                return;
            }
            
            System.out.printf("Produtos na faixa de R$%.2f a R$%.2f:%n", minPrice, maxPrice);
            products.forEach(this::displayProduct);
        } catch (Exception e) {
            System.err.println("✗ Erro ao buscar produtos por faixa de preço: " + e.getMessage());
        }
    }
    
    private void updateProduct(Integer id, String name, Double price, Integer quantity, Boolean status) {
        try {
            ProductUpdateDTO updateDTO = new ProductUpdateDTO(name, price, quantity, status);
            Product updatedProduct = productService.updateProduct(id, updateDTO);
            
            System.out.printf("✓ Produto atualizado: ID=%d, Nome='%s', Preço=R$%.2f, Qtd=%d, Status=%s%n",
                updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getPrice(), 
                updatedProduct.getQuantity(), updatedProduct.isStatus() ? "Ativo" : "Inativo");
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao atualizar produto: " + e.getMessage());
        }
    }
    
    private void deleteProduct(Integer id) {
        try {
            productService.deleteProduct(id);
            System.out.println("✓ Produto com ID " + id + " removido com sucesso.");
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao remover produto: " + e.getMessage());
        }
    }
    
    private void showBusinessStatistics() {
        try {
            System.out.println("Total de produtos: " + productService.countProducts());
            System.out.println("Produtos ativos: " + productService.countActiveProducts());
            System.out.println("Produtos inativos: " + productService.countInactiveProducts());
            System.out.printf("Valor total do estoque: R$%.2f%n", productService.calculateTotalStockValue());
            
            Optional<Product> mostExpensive = productService.findMostExpensiveProduct();
            if (mostExpensive.isPresent()) {
                System.out.println("Produto mais caro:");
                displayProduct(mostExpensive.get());
            }
            
            Optional<Product> cheapest = productService.findCheapestProduct();
            if (cheapest.isPresent()) {
                System.out.println("Produto mais barato:");
                displayProduct(cheapest.get());
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao obter estatísticas: " + e.getMessage());
        }
    }
    
    // === OPERAÇÕES INTERATIVAS ===
    
    private void interactiveCreate() {
        System.out.println("\n--- CRIAR PRODUTO ---");
        
        String name = getStringInput("Nome do produto: ");
        Double price = getDoubleInput("Preço: R$ ");
        Integer quantity = getIntInput("Quantidade: ");
        Boolean status = getBooleanInput("Produto ativo? (s/n): ");
        
        createProduct(name, price, quantity, status);
    }
    
    private void interactiveList() {
        System.out.println("\n--- LISTAR PRODUTOS ---");
        listAllProducts();
    }
    
    private void interactiveFind() {
        System.out.println("\n--- BUSCAR PRODUTO ---");
        Integer id = getIntInput("ID do produto: ");
        findProductById(id);
    }
    
    private void interactiveUpdate() {
        System.out.println("\n--- ATUALIZAR PRODUTO ---");
        
        Integer id = getIntInput("ID do produto: ");
        
        Optional<Product> productOpt = productService.findProductById(id);
        if (productOpt.isEmpty()) {
            System.out.println("Produto não encontrado!");
            return;
        }
        
        Product current = productOpt.get();
        System.out.println("Produto atual:");
        displayProduct(current);
        
        System.out.println("\nNovos dados (Enter para manter atual):");
        String name = getStringInputWithDefault("Nome: ", current.getName());
        Double price = getDoubleInputWithDefault("Preço: R$ ", current.getPrice());
        Integer quantity = getIntInputWithDefault("Quantidade: ", current.getQuantity());
        Boolean status = getBooleanInputWithDefault("Ativo? (s/n): ", current.isStatus());
        
        updateProduct(id, name, price, quantity, status);
    }
    
    private void interactiveDelete() {
        System.out.println("\n--- REMOVER PRODUTO ---");
        
        Integer id = getIntInput("ID do produto: ");
        
        Optional<Product> productOpt = productService.findProductById(id);
        if (productOpt.isEmpty()) {
            System.out.println("Produto não encontrado!");
            return;
        }
        
        System.out.println("Produto a ser removido:");
        displayProduct(productOpt.get());
        
        if (getBooleanInput("Confirma remoção? (s/n): ")) {
            deleteProduct(id);
        } else {
            System.out.println("Remoção cancelada.");
        }
    }
    
    private void interactiveSearch() {
        System.out.println("\n--- BUSCAR PRODUTOS ---");
        System.out.println("1. Por nome");
        System.out.println("2. Por status");
        System.out.println("3. Por faixa de preço");
        
        int option = getIntInput("Escolha o tipo de busca: ");
        
        switch (option) {
            case 1 -> {
                String name = getStringInput("Nome (ou parte): ");
                findProductsByName(name);
            }
            case 2 -> {
                Boolean status = getBooleanInput("Buscar produtos ativos? (s/n): ");
                findProductsByStatus(status);
            }
            case 3 -> {
                Double minPrice = getDoubleInput("Preço mínimo: R$ ");
                Double maxPrice = getDoubleInput("Preço máximo: R$ ");
                findProductsByPriceRange(minPrice, maxPrice);
            }
            default -> System.out.println("Opção inválida!");
        }
    }
    
    private void showStatistics() {
        System.out.println("\n--- ESTATÍSTICAS ---");
        showBusinessStatistics();
    }
    
    private void businessOperations() {
        System.out.println("\n--- OPERAÇÕES DE NEGÓCIO ---");
        System.out.println("1. Ativar produto");
        System.out.println("2. Desativar produto");
        System.out.println("3. Atualizar preço com validação");
        System.out.println("4. Aplicar desconto em produtos");
        System.out.println("5. Buscar com filtros avançados");
        
        int option = getIntInput("Escolha uma opção: ");
        
        try {
            switch (option) {
                case 1 -> {
                    Integer id = getIntInput("ID do produto para ativar: ");
                    Product product = productService.activateProduct(id);
                    System.out.println("✓ Produto ativado:");
                    displayProduct(product);
                }
                case 2 -> {
                    Integer id = getIntInput("ID do produto para desativar: ");
                    Product product = productService.deactivateProduct(id);
                    System.out.println("✓ Produto desativado:");
                    displayProduct(product);
                }
                case 3 -> {
                    Integer id = getIntInput("ID do produto: ");
                    Double newPrice = getDoubleInput("Novo preço: R$ ");
                    Product product = productService.updateProductPrice(id, newPrice);
                    System.out.println("✓ Preço atualizado:");
                    displayProduct(product);
                }
                case 4 -> {
                    Double discount = getDoubleInput("Percentual de desconto (0-100): ");
                    ProductFilterDTO filter = new ProductFilterDTO();
                    filter.setStatus(true); // Apenas produtos ativos
                    int updated = productService.applyDiscount(filter, discount);
                    System.out.printf("✓ Desconto aplicado em %d produtos%n", updated);
                }
                case 5 -> advancedFilters();
                default -> System.out.println("Opção inválida!");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro: " + e.getMessage());
        }
    }
    
    private void advancedFilters() {
        System.out.println("\n--- FILTROS AVANÇADOS ---");
        
        ProductFilterDTO filter = new ProductFilterDTO();
        
        System.out.println("Configure os filtros (Enter para pular):");
        
        String name = getStringInput("Nome (ou parte): ");
        if (!name.isEmpty()) {
            filter.setName(name);
        }
        
        String minPriceStr = getStringInput("Preço mínimo: R$ ");
        if (!minPriceStr.isEmpty()) {
            try {
                filter.setMinPrice(Double.parseDouble(minPriceStr));
            } catch (NumberFormatException e) {
                System.out.println("Preço mínimo inválido, ignorando.");
            }
        }
        
        String maxPriceStr = getStringInput("Preço máximo: R$ ");
        if (!maxPriceStr.isEmpty()) {
            try {
                filter.setMaxPrice(Double.parseDouble(maxPriceStr));
            } catch (NumberFormatException e) {
                System.out.println("Preço máximo inválido, ignorando.");
            }
        }
        
        String statusStr = getStringInput("Status (ativo/inativo/todos): ");
        if (!statusStr.isEmpty()) {
            switch (statusStr.toLowerCase()) {
                case "ativo" -> filter.setStatus(true);
                case "inativo" -> filter.setStatus(false);
                // "todos" deixa null
            }
        }
        
        try {
            List<Product> products = productService.findProductsWithFilters(filter);
            
            if (products.isEmpty()) {
                System.out.println("Nenhum produto encontrado com os filtros especificados.");
            } else {
                System.out.printf("Encontrados %d produtos:%n", products.size());
                products.forEach(this::displayProduct);
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao buscar com filtros: " + e.getMessage());
        }
    }
    
    // === UTILITÁRIOS ===
    
    private void displayProduct(Product product) {
        System.out.printf("  ID: %d | Nome: %s | Preço: R$%.2f | Qtd: %d | Status: %s%n",
            product.getId(), product.getName(), product.getPrice(),
            product.getQuantity(), product.isStatus() ? "Ativo" : "Inativo");
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private String getStringInputWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt + "(" + defaultValue + "): ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
    
    private Integer getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }
    
    private Integer getIntInputWithDefault(String prompt, Integer defaultValue) {
        System.out.print(prompt + "(" + defaultValue + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, mantendo o atual.");
            return defaultValue;
        }
    }
    
    private Double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }
    
    private Double getDoubleInputWithDefault(String prompt, Double defaultValue) {
        System.out.print(prompt + "(" + defaultValue + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, mantendo o atual.");
            return defaultValue;
        }
    }
    
    private Boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("sim") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("não") || input.equals("nao") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Por favor, digite 's' para sim ou 'n' para não.");
            }
        }
    }
    
    private Boolean getBooleanInputWithDefault(String prompt, Boolean defaultValue) {
        String defaultText = defaultValue ? "s" : "n";
        System.out.print(prompt + "(" + defaultText + "): ");
        String input = scanner.nextLine().trim().toLowerCase();
        
        if (input.isEmpty()) {
            return defaultValue;
        }
        
        if (input.equals("s") || input.equals("sim") || input.equals("y") || input.equals("yes")) {
            return true;
        } else if (input.equals("n") || input.equals("não") || input.equals("nao") || input.equals("no")) {
            return false;
        } else {
            System.out.println("Valor inválido, mantendo o atual.");
            return defaultValue;
        }
    }
    
    private void cleanup() {
        try {
            scanner.close();
            HibernateUtil.shutdown();
            System.out.println("Recursos liberados com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao fechar recursos: " + e.getMessage());
        }
    }
}
