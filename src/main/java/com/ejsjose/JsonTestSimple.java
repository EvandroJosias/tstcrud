package com.ejsjose;

import com.ejsjose.repositories.IProductRepository;
import com.ejsjose.repositories.ProductRepository;
import com.ejsjose.services.IProductService;
import com.ejsjose.services.ProductService;
import com.ejsjose.services.ProductJsonService;
import com.ejsjose.infra.HibernateUtil;

/**
 * Teste simples do sistema JSON
 */
public class JsonTestSimple {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE SIMPLES JSON ===");
        
        // Configurar dependências
        IProductRepository repository = new ProductRepository();
        IProductService service = new ProductService(repository);
        ProductJsonService jsonService = new ProductJsonService(service);
        
        try {
            // Teste 1: Criar produto via JSON
            System.out.println("\n📝 1. CRIANDO PRODUTO VIA JSON:");
            
            String createJson = """
                {
                    "name": "iPhone 15 Pro",
                    "price": 8999.99,
                    "quantity": 50,
                    "status": true
                }
                """;
            
            System.out.println("🔹 Input JSON:");
            System.out.println(createJson);
            
            String resultCreate = jsonService.createProduct(createJson);
            System.out.println("🔹 Output JSON:");
            System.out.println(resultCreate);
            
            // Teste 2: Listar produtos
            System.out.println("\n📋 2. LISTANDO PRODUTOS (JSON):");
            String resultList = jsonService.findAllProducts();
            System.out.println(resultList);
            
            // Teste 3: Buscar por nome
            System.out.println("\n🔍 3. BUSCANDO POR NOME (JSON):");
            String resultSearch = jsonService.findProductsByName("iPhone");
            System.out.println(resultSearch);
            
            // Teste 4: Filtro via JSON
            System.out.println("\n🔍 4. FILTRO AVANÇADO (JSON):");
            
            String filterJson = """
                {
                    "name": "iPhone",
                    "status": true,
                    "minPrice": 5000.0
                }
                """;
            
            System.out.println("🔹 Input JSON (filtro):");
            System.out.println(filterJson);
            
            String resultFilter = jsonService.findProductsWithFilters(filterJson);
            System.out.println("🔹 Output JSON (resultado):");
            System.out.println(resultFilter);
            
            // Teste 5: Estatísticas
            System.out.println("\n📊 5. ESTATÍSTICAS (JSON):");
            String resultStats = jsonService.getStatistics();
            System.out.println(resultStats);
            
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
            System.out.println("\n✅ Teste concluído!");
        }
    }
}
