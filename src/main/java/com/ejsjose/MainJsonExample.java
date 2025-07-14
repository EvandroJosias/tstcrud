package com.ejsjose;

import com.ejsjose.repositories.IProductRepository;
import com.ejsjose.repositories.ProductRepository;
import com.ejsjose.services.IProductService;
import com.ejsjose.services.ProductService;
import com.ejsjose.services.ProductJsonService;
import com.ejsjose.utils.JsonUtils;
import com.ejsjose.infra.HibernateUtil;

import java.util.Scanner;

/**
 * Exemplo de uso do serviço JSON
 * Demonstra entrada e saída em formato JSON
 */
public class MainJsonExample {
    
    private final ProductJsonService jsonService;
    private final Scanner scanner;
    
    public MainJsonExample() {
        // Configurar dependências
        IProductRepository repository = new ProductRepository();
        IProductService service = new ProductService(repository);
        this.jsonService = new ProductJsonService(service);
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        System.out.println("=== CRUD COM TRANSFERÊNCIA JSON ===");
        System.out.println("Demonstração de entrada e saída via JSON");
        System.out.println();
        
        MainJsonExample app = new MainJsonExample();
        
        try {
            // Demonstração automática
            app.demonstrateJsonOperations();
            
            // Menu interativo
            app.runInteractiveJsonMenu();
            
        } catch (Exception e) {
            System.err.println("Erro na aplicação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            app.cleanup();
        }
    }
    
    /**
     * Demonstra operações CRUD usando JSON
     */
    private void demonstrateJsonOperations() {
        System.out.println("--- DEMONSTRAÇÃO JSON ---");
        
        try {
            // 1. CREATE - Criar produto via JSON
            System.out.println("\n1. CREATE - Criando produto via JSON:");
            
            String createJson = """
                {
                    "name": "Smartphone Galaxy",
                    "price": 1299.99,
                    "quantity": 25,
                    "status": true
                }
                """;
            
            System.out.println("📥 INPUT JSON:");
            System.out.println(createJson);
            
            String createdProductJson = jsonService.createProduct(createJson);
            System.out.println("📤 OUTPUT JSON:");
            System.out.println(createdProductJson);
            
            // 2. READ - Listar todos os produtos
            System.out.println("\n2. READ - Listando produtos (JSON):");
            String allProductsJson = jsonService.findAllProducts();
            System.out.println("📤 OUTPUT JSON:");
            System.out.println(allProductsJson);
            
            // 3. UPDATE - Atualizar produto via JSON
            System.out.println("\n3. UPDATE - Atualizando produto via JSON:");
            
            String updateJson = """
                {
                    "price": 1199.99,
                    "quantity": 30
                }
                """;
            
            System.out.println("📥 INPUT JSON:");
            System.out.println(updateJson);
            
            // Assumindo que o produto criado tem ID 1 (ou extrair do JSON retornado)
            String updatedProductJson = jsonService.updateProduct(1, updateJson);
            System.out.println("📤 OUTPUT JSON:");
            System.out.println(updatedProductJson);
            
            // 4. FILTROS - Buscar com filtros via JSON
            System.out.println("\n4. FILTROS - Buscando com filtros via JSON:");
            
            String filterJson = """
                {
                    "name": "Smartphone",
                    "status": true,
                    "minPrice": 1000.0,
                    "maxPrice": 2000.0
                }
                """;
            
            System.out.println("📥 INPUT JSON:");
            System.out.println(filterJson);
            
            String filteredProductsJson = jsonService.findProductsWithFilters(filterJson);
            System.out.println("📤 OUTPUT JSON:");
            System.out.println(filteredProductsJson);
            
            // 5. ESTATÍSTICAS - Via JSON
            System.out.println("\n5. ESTATÍSTICAS - Via JSON:");
            String statisticsJson = jsonService.getStatistics();
            System.out.println("📤 OUTPUT JSON:");
            System.out.println(statisticsJson);
            
        } catch (Exception e) {
            System.err.println("Erro na demonstração JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Menu interativo com JSON
     */
    private void runInteractiveJsonMenu() {
        System.out.println("\n\n--- MENU INTERATIVO JSON ---");
        
        while (true) {
            showJsonMenu();
            int option = getIntInput("Escolha uma opção: ");
            
            try {
                switch (option) {
                    case 1 -> interactiveCreateJson();
                    case 2 -> interactiveListJson();
                    case 3 -> interactiveFindJson();
                    case 4 -> interactiveUpdateJson();
                    case 5 -> interactiveDeleteJson();
                    case 6 -> interactiveFilterJson();
                    case 7 -> interactiveStatisticsJson();
                    case 8 -> validateJsonInput();
                    case 0 -> {
                        System.out.println("Encerrando aplicação...");
                        return;
                    }
                    default -> System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
            }
            
            System.out.println("\nPressione Enter para continuar...");
            scanner.nextLine();
        }
    }
    
    private void showJsonMenu() {
        System.out.println("\n=== MENU JSON ===");
        System.out.println("1. Criar produto (JSON)");
        System.out.println("2. Listar produtos (JSON)");
        System.out.println("3. Buscar produto por ID (JSON)");
        System.out.println("4. Atualizar produto (JSON)");
        System.out.println("5. Remover produto (JSON)");
        System.out.println("6. Filtrar produtos (JSON)");
        System.out.println("7. Estatísticas (JSON)");
        System.out.println("8. Validar JSON");
        System.out.println("0. Sair");
    }
    
    // === OPERAÇÕES INTERATIVAS JSON ===
    
    private void interactiveCreateJson() {
        System.out.println("\n--- CRIAR PRODUTO VIA JSON ---");
        System.out.println("Digite o JSON para criação do produto:");
        System.out.println("Exemplo:");
        System.out.println("""
            {
                "name": "Nome do Produto",
                "price": 99.99,
                "quantity": 10,
                "status": true
            }
            """);
        
        String createJson = getMultilineInput("JSON: ");
        
        try {
            String result = jsonService.createProduct(createJson);
            System.out.println("✅ Produto criado:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar produto: " + e.getMessage());
        }
    }
    
    private void interactiveListJson() {
        System.out.println("\n--- LISTAR PRODUTOS (JSON) ---");
        
        try {
            String result = jsonService.findAllProducts();
            System.out.println("📤 Produtos (JSON):");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
        }
    }
    
    private void interactiveFindJson() {
        System.out.println("\n--- BUSCAR PRODUTO POR ID (JSON) ---");
        Integer id = getIntInput("ID do produto: ");
        
        try {
            String result = jsonService.findProductById(id);
            if (result != null) {
                System.out.println("📤 Produto encontrado (JSON):");
                System.out.println(result);
            } else {
                System.out.println("❌ Produto não encontrado");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar produto: " + e.getMessage());
        }
    }
    
    private void interactiveUpdateJson() {
        System.out.println("\n--- ATUALIZAR PRODUTO VIA JSON ---");
        Integer id = getIntInput("ID do produto: ");
        
        System.out.println("Digite o JSON para atualização (apenas campos a alterar):");
        System.out.println("Exemplo:");
        System.out.println("""
            {
                "price": 149.99,
                "quantity": 15
            }
            """);
        
        String updateJson = getMultilineInput("JSON: ");
        
        try {
            String result = jsonService.updateProduct(id, updateJson);
            System.out.println("✅ Produto atualizado:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
        }
    }
    
    private void interactiveDeleteJson() {
        System.out.println("\n--- REMOVER PRODUTO (JSON) ---");
        Integer id = getIntInput("ID do produto: ");
        
        try {
            String result = jsonService.deleteProduct(id);
            System.out.println("✅ Resultado:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover produto: " + e.getMessage());
        }
    }
    
    private void interactiveFilterJson() {
        System.out.println("\n--- FILTRAR PRODUTOS VIA JSON ---");
        System.out.println("Digite o JSON com filtros:");
        System.out.println("Exemplo:");
        System.out.println("""
            {
                "name": "Smartphone",
                "status": true,
                "minPrice": 500.0,
                "maxPrice": 2000.0,
                "minQuantity": 5
            }
            """);
        
        String filterJson = getMultilineInput("JSON: ");
        
        try {
            String result = jsonService.findProductsWithFilters(filterJson);
            System.out.println("📤 Produtos filtrados (JSON):");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Erro ao filtrar produtos: " + e.getMessage());
        }
    }
    
    private void interactiveStatisticsJson() {
        System.out.println("\n--- ESTATÍSTICAS (JSON) ---");
        
        try {
            String result = jsonService.getStatistics();
            System.out.println("📤 Estatísticas (JSON):");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter estatísticas: " + e.getMessage());
        }
    }
    
    private void validateJsonInput() {
        System.out.println("\n--- VALIDADOR JSON ---");
        System.out.println("Digite um JSON para validar:");
        
        String json = getMultilineInput("JSON: ");
        
        if (JsonUtils.isValidJson(json)) {
            System.out.println("✅ JSON válido!");
            try {
                System.out.println("📤 JSON formatado:");
                System.out.println(JsonUtils.toJsonPretty(JsonUtils.fromJson(json, Object.class)));
            } catch (Exception e) {
                System.out.println("⚠️ JSON válido mas não pode ser formatado: " + e.getMessage());
            }
        } else {
            System.out.println("❌ JSON inválido!");
        }
    }
    
    // === UTILITÁRIOS ===
    
    private String getMultilineInput(String prompt) {
        System.out.print(prompt);
        StringBuilder input = new StringBuilder();
        String line;
        
        System.out.println("(Digite 'END' em uma linha separada para finalizar)");
        
        while (!(line = scanner.nextLine()).equals("END")) {
            input.append(line).append("\n");
        }
        
        return input.toString().trim();
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
