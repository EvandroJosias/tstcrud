package com.ejsjose.repositories;

import com.ejsjose.entities.Product;
import com.ejsjose.infra.HibernateUtil;

import java.util.List;
import java.util.Optional;

/**
 * Exemplo de testes manuais para o repositório
 * Em um projeto real, use JUnit ou TestNG
 */
public class ProductRepositoryTest {
    
    private static final IProductRepository repository = new ProductRepository();
    
    public static void main(String[] args) {
        System.out.println("=== TESTES DO REPOSITÓRIO ===");
        
        try {
            // Limpar dados antes dos testes
            setupTest();
            
            // Executar testes
            testSave();
            testFindById();
            testFindAll();
            testFindByName();
            testFindByStatus();
            testFindByPriceRange();
            testUpdate();
            testCount();
            testExistsById();
            testDelete();
            
            System.out.println("\n✓ Todos os testes passaram!");
            
        } catch (Exception e) {
            System.err.println("✗ Teste falhou: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    private static void setupTest() {
        System.out.println("\n--- SETUP: Limpando dados existentes ---");
        repository.deleteAll();
        System.out.println("✓ Base de dados limpa");
    }
    
    private static void testSave() {
        System.out.println("\n--- TESTE: Save ---");
        
        // Test 1: Salvar novo produto
        Product product = createTestProduct("Produto Teste 1", 100.0, true);
        Product saved = repository.save(product);
        
        assert saved.getId() != null : "ID deve ser gerado automaticamente";
        assert saved.getName().equals("Produto Teste 1") : "Nome deve ser salvo corretamente";
        System.out.println("✓ Teste save (novo produto): PASSOU");
        
        // Test 2: Atualizar produto existente
        saved.setName("Produto Teste 1 Atualizado");
        Product updated = repository.save(saved);
        
        assert updated.getId().equals(saved.getId()) : "ID deve permanecer o mesmo";
        assert updated.getName().equals("Produto Teste 1 Atualizado") : "Nome deve ser atualizado";
        System.out.println("✓ Teste save (atualização): PASSOU");
    }
    
    private static void testFindById() {
        System.out.println("\n--- TESTE: FindById ---");
        
        // Criar produto para teste
        Product product = repository.save(createTestProduct("Produto FindById", 200.0, true));
        
        // Test 1: Buscar produto existente
        Optional<Product> found = repository.findById(product.getId());
        assert found.isPresent() : "Produto deve ser encontrado";
        assert found.get().getName().equals("Produto FindById") : "Dados devem estar corretos";
        System.out.println("✓ Teste findById (existente): PASSOU");
        
        // Test 2: Buscar produto inexistente
        Optional<Product> notFound = repository.findById(99999);
        assert notFound.isEmpty() : "Produto inexistente não deve ser encontrado";
        System.out.println("✓ Teste findById (inexistente): PASSOU");
        
        // Test 3: Buscar com ID nulo
        Optional<Product> nullId = repository.findById(null);
        assert nullId.isEmpty() : "ID nulo deve retornar empty";
        System.out.println("✓ Teste findById (ID nulo): PASSOU");
    }
    
    private static void testFindAll() {
        System.out.println("\n--- TESTE: FindAll ---");
        
        // Limpar e criar produtos para teste
        repository.deleteAll();
        repository.save(createTestProduct("Produto A", 100.0, true));
        repository.save(createTestProduct("Produto B", 200.0, false));
        repository.save(createTestProduct("Produto C", 300.0, true));
        
        List<Product> all = repository.findAll();
        assert all.size() == 3 : "Deve retornar 3 produtos";
        System.out.println("✓ Teste findAll: PASSOU");
    }
    
    private static void testFindByName() {
        System.out.println("\n--- TESTE: FindByName ---");
        
        // Test 1: Buscar por nome existente
        List<Product> found = repository.findByName("Produto A");
        assert !found.isEmpty() : "Deve encontrar produtos";
        System.out.println("✓ Teste findByName (existente): PASSOU");
        
        // Test 2: Buscar por nome inexistente
        List<Product> notFound = repository.findByName("Produto Inexistente");
        assert notFound.isEmpty() : "Não deve encontrar produtos";
        System.out.println("✓ Teste findByName (inexistente): PASSOU");
        
        // Test 3: Buscar com nome nulo
        List<Product> nullName = repository.findByName(null);
        assert nullName.isEmpty() : "Nome nulo deve retornar lista vazia";
        System.out.println("✓ Teste findByName (nome nulo): PASSOU");
        
        // Test 4: Buscar com nome vazio
        List<Product> emptyName = repository.findByName("");
        assert emptyName.isEmpty() : "Nome vazio deve retornar lista vazia";
        System.out.println("✓ Teste findByName (nome vazio): PASSOU");
    }
    
    private static void testFindByStatus() {
        System.out.println("\n--- TESTE: FindByStatus ---");
        
        // Test 1: Buscar produtos ativos
        List<Product> active = repository.findByStatus(true);
        assert active.size() == 2 : "Deve encontrar 2 produtos ativos";
        System.out.println("✓ Teste findByStatus (ativo): PASSOU");
        
        // Test 2: Buscar produtos inativos
        List<Product> inactive = repository.findByStatus(false);
        assert inactive.size() == 1 : "Deve encontrar 1 produto inativo";
        System.out.println("✓ Teste findByStatus (inativo): PASSOU");
        
        // Test 3: Buscar com status nulo
        List<Product> nullStatus = repository.findByStatus(null);
        assert nullStatus.size() == 3 : "Status nulo deve retornar todos";
        System.out.println("✓ Teste findByStatus (status nulo): PASSOU");
    }
    
    private static void testFindByPriceRange() {
        System.out.println("\n--- TESTE: FindByPriceRange ---");
        
        // Test 1: Faixa que inclui produtos
        List<Product> inRange = repository.findByPriceRange(150.0, 250.0);
        assert inRange.size() == 1 : "Deve encontrar 1 produto na faixa";
        System.out.println("✓ Teste findByPriceRange (com resultados): PASSOU");
        
        // Test 2: Faixa que não inclui produtos
        List<Product> outOfRange = repository.findByPriceRange(400.0, 500.0);
        assert outOfRange.isEmpty() : "Não deve encontrar produtos fora da faixa";
        System.out.println("✓ Teste findByPriceRange (sem resultados): PASSOU");
        
        // Test 3: Apenas preço mínimo
        List<Product> minOnly = repository.findByPriceRange(200.0, null);
        assert minOnly.size() == 2 : "Deve encontrar produtos acima do mínimo";
        System.out.println("✓ Teste findByPriceRange (apenas mínimo): PASSOU");
        
        // Test 4: Apenas preço máximo
        List<Product> maxOnly = repository.findByPriceRange(null, 200.0);
        assert maxOnly.size() == 2 : "Deve encontrar produtos abaixo do máximo";
        System.out.println("✓ Teste findByPriceRange (apenas máximo): PASSOU");
    }
    
    private static void testUpdate() {
        System.out.println("\n--- TESTE: Update ---");
        
        // Criar produto para teste
        Product product = repository.save(createTestProduct("Produto Update", 150.0, true));
        
        // Test 1: Atualizar produto existente
        product.setName("Produto Atualizado");
        product.setPrice(180.0);
        Product updated = repository.update(product);
        
        assert updated.getName().equals("Produto Atualizado") : "Nome deve ser atualizado";
        assert updated.getPrice().equals(180.0) : "Preço deve ser atualizado";
        System.out.println("✓ Teste update (existente): PASSOU");
        
        // Test 2: Tentar atualizar produto com ID nulo
        try {
            Product productWithoutId = createTestProduct("Sem ID", 100.0, true);
            repository.update(productWithoutId);
            assert false : "Deve lançar exceção para produto sem ID";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Teste update (sem ID): PASSOU");
        }
        
        // Test 3: Tentar atualizar produto inexistente
        try {
            Product nonExistent = createTestProduct("Inexistente", 100.0, true);
            nonExistent.setId(99999);
            repository.update(nonExistent);
            assert false : "Deve lançar exceção para produto inexistente";
        } catch (RuntimeException e) {
            System.out.println("✓ Teste update (inexistente): PASSOU");
        }
    }
    
    private static void testCount() {
        System.out.println("\n--- TESTE: Count ---");
        
        long count = repository.count();
        assert count == 4 : "Deve contar 4 produtos"; // 3 do setup + 1 do update
        System.out.println("✓ Teste count: PASSOU");
    }
    
    private static void testExistsById() {
        System.out.println("\n--- TESTE: ExistsById ---");
        
        // Obter um produto existente
        Optional<Product> firstProduct = repository.findFirst();
        assert firstProduct.isPresent() : "Deve ter pelo menos um produto";
        
        // Test 1: Verificar produto existente
        boolean exists = repository.existsById(firstProduct.get().getId());
        assert exists : "Produto deve existir";
        System.out.println("✓ Teste existsById (existente): PASSOU");
        
        // Test 2: Verificar produto inexistente
        boolean notExists = repository.existsById(99999);
        assert !notExists : "Produto não deve existir";
        System.out.println("✓ Teste existsById (inexistente): PASSOU");
        
        // Test 3: Verificar com ID nulo
        boolean nullExists = repository.existsById(null);
        assert !nullExists : "ID nulo não deve existir";
        System.out.println("✓ Teste existsById (ID nulo): PASSOU");
    }
    
    private static void testDelete() {
        System.out.println("\n--- TESTE: Delete ---");
        
        // Criar produto para teste de deleção
        Product product = repository.save(createTestProduct("Produto Delete", 100.0, true));
        Integer productId = product.getId();
        
        // Test 1: Deletar por ID
        repository.deleteById(productId);
        assert !repository.existsById(productId) : "Produto deve ter sido removido";
        System.out.println("✓ Teste deleteById: PASSOU");
        
        // Test 2: Tentar deletar produto inexistente
        try {
            repository.deleteById(99999);
            assert false : "Deve lançar exceção para produto inexistente";
        } catch (RuntimeException e) {
            System.out.println("✓ Teste deleteById (inexistente): PASSOU");
        }
        
        // Test 3: Deletar entidade
        Product productToDelete = repository.save(createTestProduct("Produto Delete Entity", 100.0, true));
        repository.delete(productToDelete);
        assert !repository.existsById(productToDelete.getId()) : "Produto deve ter sido removido";
        System.out.println("✓ Teste delete (entity): PASSOU");
        
        // Test 4: Deletar todos
        repository.deleteAll();
        long countAfter = repository.count();
        assert countAfter == 0 : "Todos os produtos devem ter sido removidos";
        System.out.println("✓ Teste deleteAll: PASSOU");
    }
    
    private static Product createTestProduct(String name, Double price, Boolean status) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStatus(status);
        product.setQuantity(10);
        return product;
    }
    
    private static void cleanup() {
        try {
            HibernateUtil.shutdown();
            System.out.println("\n✓ Recursos liberados");
        } catch (Exception e) {
            System.err.println("Erro ao liberar recursos: " + e.getMessage());
        }
    }
}
