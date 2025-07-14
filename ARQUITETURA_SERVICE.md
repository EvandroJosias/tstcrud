# 🏗️ Arquitetura em Camadas - CRUD Java/Hibernate/JPA com Service Layer

## 📚 Visão Geral da Arquitetura

O projeto agora implementa uma **arquitetura completa em camadas** seguindo as melhores práticas empresariais:

```
┌─────────────────────────────────────┐
│          PRESENTATION LAYER         │  ← MainCRUDExample.java
│  (Interface do usuário, Controllers) │
├─────────────────────────────────────┤
│           SERVICE LAYER             │  ← IProductService, ProductService
│     (Lógica de negócio, DTOs)        │
├─────────────────────────────────────┤
│         REPOSITORY LAYER            │  ← IProductRepository, ProductRepository
│       (Acesso aos dados)             │
├─────────────────────────────────────┤
│            ENTITY LAYER             │  ← Product.java
│      (Modelo de domínio)             │
├─────────────────────────────────────┤
│       INFRASTRUCTURE LAYER          │  ← HibernateUtil, SQLiteDialect
│    (Configuração, Persistência)      │
└─────────────────────────────────────┘
```

## 🎯 Camada de Service - Responsabilidades

### ✅ **O que a Service Layer faz:**

1. **Lógica de Negócio**
   - Implementa regras complexas de validação
   - Coordena operações entre múltiplos repositórios
   - Aplica políticas de negócio

2. **Transformação de Dados**
   - Converte DTOs em entidades
   - Valida dados de entrada
   - Formata dados de saída

3. **Operações Transacionais**
   - Gerencia transações complexas
   - Coordena operações atômicas
   - Rollback em caso de erro

4. **Validações de Regras de Negócio**
   - Nome único de produtos
   - Preços válidos
   - Limitações de desconto

## 📁 Estrutura dos Arquivos

### **Service Layer**
```
src/main/java/com/ejsjose/services/
├── IProductService.java          # Interface do serviço
├── ProductService.java           # Implementação do serviço
├── ProductServiceTest.java       # Testes da camada de service
└── dto/
    ├── ProductCreateDTO.java     # DTO para criação
    ├── ProductUpdateDTO.java     # DTO para atualização
    └── ProductFilterDTO.java     # DTO para filtros
```

### **Repository Layer**
```
src/main/java/com/ejsjose/repositories/
├── IProductRepository.java      # Interface do repositório
├── ProductRepository.java       # Implementação com melhores práticas
├── ProductRepositoryTest.java   # Testes do repositório
└── repoProduct.java            # Repositório legado (mantido para comparação)
```

## 🔧 DTOs (Data Transfer Objects)

### **ProductCreateDTO**
```java
public class ProductCreateDTO {
    private String name;        // Nome do produto (obrigatório)
    private Double price;       // Preço (obrigatório, >= 0)
    private Integer quantity;   // Quantidade (opcional, padrão: 0)
    private Boolean status;     // Status (opcional, padrão: true)
}
```

### **ProductUpdateDTO**
```java
public class ProductUpdateDTO {
    private String name;        // Novo nome (opcional)
    private Double price;       // Novo preço (opcional)
    private Integer quantity;   // Nova quantidade (opcional)
    private Boolean status;     // Novo status (opcional)
    
    // Atualização parcial - apenas campos não-null são atualizados
}
```

### **ProductFilterDTO**
```java
public class ProductFilterDTO {
    private String name;           // Filtro por nome (LIKE)
    private Double minPrice;       // Preço mínimo
    private Double maxPrice;       // Preço máximo
    private Integer minQuantity;   // Quantidade mínima
    private Integer maxQuantity;   // Quantidade máxima
    private Boolean status;        // Filtro por status
    private String sortBy;         // Campo para ordenação
    private String sortDirection;  // ASC ou DESC
}
```

## 🎯 Operações de Serviço

### **CRUD Básico**
```java
// CREATE
ProductCreateDTO createDTO = new ProductCreateDTO("Notebook", 2500.0, 10, true);
Product product = productService.createProduct(createDTO);

// READ
Optional<Product> found = productService.findProductById(1);
List<Product> all = productService.findAllProducts();

// UPDATE
ProductUpdateDTO updateDTO = new ProductUpdateDTO("Notebook Dell", 2300.0, null, null);
Product updated = productService.updateProduct(1, updateDTO);

// DELETE
productService.deleteProduct(1);
```

### **Operações de Negócio**
```java
// Ativar/Desativar produto
Product activated = productService.activateProduct(productId);
Product deactivated = productService.deactivateProduct(productId);

// Atualizar preço com validação
Product updated = productService.updateProductPrice(productId, newPrice);

// Aplicar desconto em lote
ProductFilterDTO filter = new ProductFilterDTO();
filter.setStatus(true); // Apenas produtos ativos
int updated = productService.applyDiscount(filter, 10.0); // 10% desconto
```

### **Buscas Avançadas**
```java
// Buscar por nome
List<Product> products = productService.findProductsByName("Notebook");

// Buscar por status
List<Product> active = productService.findProductsByStatus(true);

// Buscar por faixa de preço
List<Product> inRange = productService.findProductsByPriceRange(100.0, 500.0);

// Filtros combinados
ProductFilterDTO filter = new ProductFilterDTO();
filter.setName("Gaming");
filter.setMinPrice(200.0);
filter.setStatus(true);
List<Product> gamingProducts = productService.findProductsWithFilters(filter);
```

### **Estatísticas e Relatórios**
```java
// Contadores
long total = productService.countProducts();
long active = productService.countActiveProducts();
long inactive = productService.countInactiveProducts();

// Cálculos
Double totalValue = productService.calculateTotalStockValue();
Optional<Product> mostExpensive = productService.findMostExpensiveProduct();
Optional<Product> cheapest = productService.findCheapestProduct();

// Verificações
boolean exists = productService.productExists(productId);
boolean nameExists = productService.productNameExists("Notebook", excludeId);
```

## 🛡️ Validações Implementadas

### **Validações de Criação**
- ✅ Nome obrigatório e não vazio
- ✅ Nome único (não pode repetir)
- ✅ Preço obrigatório e >= 0
- ✅ Preço máximo R$ 999.999,99
- ✅ Quantidade >= 0 (se fornecida)
- ✅ Quantidade máxima 999.999

### **Validações de Atualização**
- ✅ Pelo menos um campo deve ser fornecido
- ✅ Nome único (excluindo o próprio produto)
- ✅ Todas as validações de valores

### **Regras de Negócio**
- ✅ Redução de preço limitada a 50% do valor atual
- ✅ Desconto entre 0% e 100%
- ✅ Produto deve existir para operações

## 🚀 Como Executar

### **Aplicação Principal com Service**
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.MainCRUDExample"
```

### **Teste da Camada de Service**
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.services.ProductServiceTest"
```

### **Teste do Repositório**
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.repositories.ProductRepositoryTest"
```

## 🎨 Exemplo Prático de Uso

```java
// Configurar dependências (em projetos reais, usar Spring/CDI)
IProductRepository repository = new ProductRepository();
IProductService service = new ProductService(repository);

// Criar produto
ProductCreateDTO createDTO = new ProductCreateDTO();
createDTO.setName("Smartphone Galaxy");
createDTO.setPrice(1299.99);
createDTO.setQuantity(50);
createDTO.setStatus(true);

Product product = service.createProduct(createDTO);
System.out.println("Produto criado: " + product.getId());

// Atualizar apenas o preço
ProductUpdateDTO updateDTO = new ProductUpdateDTO();
updateDTO.setPrice(1199.99);

Product updated = service.updateProduct(product.getId(), updateDTO);
System.out.println("Preço atualizado para: " + updated.getPrice());

// Buscar produtos caros
ProductFilterDTO filter = new ProductFilterDTO();
filter.setMinPrice(1000.0);
filter.setStatus(true);

List<Product> expensiveProducts = service.findProductsWithFilters(filter);
System.out.println("Produtos caros encontrados: " + expensiveProducts.size());

// Aplicar desconto de 5% em produtos ativos
ProductFilterDTO activeFilter = new ProductFilterDTO();
activeFilter.setStatus(true);

int discountedProducts = service.applyDiscount(activeFilter, 5.0);
System.out.println("Desconto aplicado em " + discountedProducts + " produtos");
```

## 📊 Benefícios da Arquitetura em Camadas

### **1. Separação de Responsabilidades**
- **Presentation**: Interface do usuário
- **Service**: Lógica de negócio
- **Repository**: Acesso aos dados
- **Entity**: Modelo de domínio

### **2. Testabilidade**
- Cada camada pode ser testada independentemente
- Fácil criação de mocks
- Testes unitários específicos

### **3. Manutenibilidade**
- Código organizado e bem estruturado
- Mudanças isoladas por camada
- Fácil adição de novas funcionalidades

### **4. Reusabilidade**
- Services podem ser usados por múltiplas interfaces
- Repositórios reutilizáveis
- DTOs padronizados

### **5. Flexibilidade**
- Fácil troca de implementações
- Suporte a múltiplas fontes de dados
- Adaptação a diferentes interfaces

## 🔄 Fluxo de Dados

```
User Input → MainCRUDExample → ProductService → ProductRepository → Database
                ↓                    ↓               ↓
              DTOs              Business Logic    Data Access
                ↓                    ↓               ↓
            Validation         Domain Rules     SQL Queries
```

## 🎓 Próximos Passos

1. **Injeção de Dependência**: Implementar Spring Framework
2. **Aspect-Oriented Programming**: Logs e auditoria
3. **Caching**: Redis ou cache de aplicação
4. **API REST**: Controllers REST com Spring Boot
5. **Testes Automatizados**: JUnit + Mockito
6. **Documentação**: Swagger/OpenAPI

Esta arquitetura em camadas com Service Layer representa uma **base sólida para desenvolvimento empresarial** em Java, seguindo padrões reconhecidos pela indústria!
