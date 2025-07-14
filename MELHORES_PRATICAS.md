# 🎯 Guia de Melhores Práticas - CRUD Java/Hibernate/JPA

## 📚 Resumo das Melhores Práticas Implementadas

Este projeto demonstra a implementação completa de um CRUD seguindo as melhores práticas do desenvolvimento Java empresarial.

### 🏛️ Arquitetura em Camadas

```
┌─────────────────────────────────────┐
│          PRESENTATION LAYER         │  ← Main.java, MainCRUDExample.java
├─────────────────────────────────────┤
│           SERVICE LAYER             │  ← (Opcional) Business Logic
├─────────────────────────────────────┤
│         REPOSITORY LAYER            │  ← IProductRepository, ProductRepository
├─────────────────────────────────────┤
│            ENTITY LAYER             │  ← Product.java
├─────────────────────────────────────┤
│       INFRASTRUCTURE LAYER          │  ← HibernateUtil, SQLiteDialect
└─────────────────────────────────────┘
```

## 🔧 1. Padrão Repository

### ✅ Implementação Correta

```java
// Interface define o contrato
public interface IProductRepository {
    Product save(Product product);
    Optional<Product> findById(Integer id);
    List<Product> findAll();
    void deleteById(Integer id);
    // ... outros métodos
}

// Implementação específica
public class ProductRepository implements IProductRepository {
    @Override
    public Product save(Product product) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (product.getId() == null) {
                em.persist(product);  // INSERT
            } else {
                product = em.merge(product);  // UPDATE
            }
            em.getTransaction().commit();
            return product;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
```

### 🎯 Benefícios
- **Abstração**: Camada de abstração entre lógica de negócio e persistência
- **Testabilidade**: Fácil criação de mocks para testes
- **Flexibilidade**: Trocar implementação sem afetar outras camadas
- **Reutilização**: Métodos comuns para todas as entidades

## 🛡️ 2. Gerenciamento de Transações

### ✅ Padrão Try-Catch-Finally

```java
EntityManager em = HibernateUtil.getEntityManager();
try {
    em.getTransaction().begin();
    
    // Operações de persistência
    
    em.getTransaction().commit();
} catch (Exception e) {
    if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();  // Desfaz mudanças
    }
    throw new RuntimeException("Erro: " + e.getMessage(), e);
} finally {
    em.close();  // Sempre fecha o EntityManager
}
```

### 🎯 Benefícios
- **Consistência**: Garante que operações sejam atômicas
- **Recuperação**: Rollback automático em caso de erro
- **Recursos**: Liberação garantida do EntityManager

## 🔍 3. Uso do Optional

### ✅ Para Valores que Podem Não Existir

```java
public Optional<Product> findById(Integer id) {
    if (id == null) {
        return Optional.empty();
    }
    Product product = em.find(Product.class, id);
    return Optional.ofNullable(product);
}

// Uso correto
Optional<Product> productOpt = repository.findById(1);
if (productOpt.isPresent()) {
    Product product = productOpt.get();
    // usar produto
} else {
    System.out.println("Produto não encontrado");
}
```

### 🎯 Benefícios
- **Segurança**: Evita NullPointerException
- **Expressividade**: Código mais claro sobre possibilidade de ausência
- **API Fluente**: Métodos como map(), filter(), orElse()

## 🔒 4. Validação de Entrada

### ✅ Validação Defensiva

```java
public List<Product> findByName(String name) {
    if (name == null || name.trim().isEmpty()) {
        return List.of();  // Lista vazia ao invés de null
    }
    
    EntityManager em = HibernateUtil.getEntityManager();
    try {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name)", 
            Product.class
        );
        query.setParameter("name", "%" + name.trim() + "%");
        return query.getResultList();
    } finally {
        em.close();
    }
}
```

### 🎯 Benefícios
- **Robustez**: Previne erros de entrada inválida
- **Previsibilidade**: Comportamento consistente com dados inválidos
- **Debugging**: Facilita identificação de problemas

## 🎯 5. Queries Type-Safe

### ✅ TypedQuery e Criteria API

```java
// TypedQuery - compilação verifica tipos
TypedQuery<Product> query = em.createQuery(
    "SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max", 
    Product.class
);
query.setParameter("min", minPrice);
query.setParameter("max", maxPrice);
List<Product> results = query.getResultList();

// Named Parameters evitam SQL Injection
```

### ❌ Evitar String Concatenation

```java
// NUNCA FAÇA ISSO!
String sql = "SELECT * FROM products WHERE name = '" + name + "'";
// Vulnerável a SQL Injection
```

## 🏷️ 6. Entidades Bem Definidas

### ✅ Uso do Lombok

```java
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    private int quantity;
    private boolean status;
}
```

### 🎯 Benefícios
- **Menos Código**: Lombok gera getters/setters automaticamente
- **Manutenibilidade**: Menos código para manter
- **Consistência**: Implementação padrão de equals/hashCode

## 🔧 7. Configuração Centralizada

### ✅ HibernateUtil

```java
public class HibernateUtil {
    private static EntityManagerFactory emf;
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory("products-pu");
            createTableIfNotExists();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
```

### 🎯 Benefícios
- **Singleton**: Uma única instância do EntityManagerFactory
- **Configuração**: Centralizada em persistence.xml
- **Recursos**: Shutdown adequado da aplicação

## 📊 8. Operações CRUD Completas

### Create/Update Inteligente
```java
public Product save(Product product) {
    if (product.getId() == null) {
        em.persist(product);        // INSERT
    } else {
        product = em.merge(product); // UPDATE
    }
    return product;
}
```

### Read com Múltiplas Opções
```java
Optional<Product> findById(Integer id);
List<Product> findAll();
List<Product> findByName(String name);
List<Product> findByStatus(Boolean status);
List<Product> findByPriceRange(Double min, Double max);
```

### Delete Seguro
```java
public void deleteById(Integer id) {
    Product product = em.find(Product.class, id);
    if (product != null) {
        em.remove(product);
    } else {
        throw new RuntimeException("Produto não encontrado");
    }
}
```

## 🧪 9. Testabilidade

### ✅ Testes com Assertions

```java
// Test básico
Product saved = repository.save(product);
assert saved.getId() != null : "ID deve ser gerado";

// Test com Optional
Optional<Product> found = repository.findById(id);
assert found.isPresent() : "Produto deve ser encontrado";

// Test de exceção
try {
    repository.deleteById(null);
    assert false : "Deve lançar exceção";
} catch (IllegalArgumentException e) {
    // Esperado
}
```

## 🚀 10. Como Executar

### Compilação
```bash
mvn clean compile
```

### Aplicação Principal
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.Main"
```

### Exemplo Completo de CRUD
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.MainCRUDExample"
```

### Testes
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.repositories.ProductRepositoryTest"
```

### Saída Limpa (apenas System.out.println)
```bash
mvn -q exec:java -Dexec.mainClass="com.ejsjose.MainCRUDExample" 2>/dev/null
```

## 📈 Próximos Passos

1. **Testes Automatizados**: Implementar JUnit/TestNG
2. **Service Layer**: Adicionar camada de serviços
3. **DTO Pattern**: Data Transfer Objects para APIs
4. **Caching**: Implementar cache de segundo nível
5. **Connection Pooling**: Para ambientes de produção
6. **Metrics**: Monitoramento de performance

## 🎓 Lições Aprendidas

### ✅ Faça
- Use interfaces para abstrair implementações
- Sempre gerencie transações adequadamente
- Valide entrada de dados
- Use Optional para valores opcionais
- Trate exceções de forma específica
- Feche recursos adequadamente

### ❌ Evite
- Concatenação de strings em queries
- Ignorar tratamento de exceções
- Não validar parâmetros
- Retornar null quando Optional é melhor
- Não fechar EntityManager
- Transações muito longas

Este projeto demonstra uma base sólida para desenvolvimento Java empresarial com Hibernate/JPA, seguindo as melhores práticas da indústria.
