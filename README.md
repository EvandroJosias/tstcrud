# CRUD com Melhores Práticas - Java + Hibernate + JPA + SQLite

Este projeto demonstra a implementação de um CRUD completo seguindo as melhores práticas de desenvolvimento Java com Hibernate/JPA e SQLite.

## 🏗️ Arquitetura

### Estrutura do Projeto
```
src/main/java/com/ejsjose/
├── Main.java                    # Aplicação principal original
├── MainCRUDExample.java         # Exemplo completo de CRUD com melhores práticas
├── entities/
│   └── Product.java             # Entidade JPA com Lombok
├── infra/
│   ├── HibernateUtil.java       # Configuração do Hibernate
│   └── SQLiteDialect.java       # Dialect customizado para SQLite
└── repositories/
    ├── IProductRepository.java  # Interface do repositório
    ├── ProductRepository.java   # Implementação do repositório
    └── repoProduct.java         # Repositório legado
```

## 🎯 Melhores Práticas Implementadas

### 1. **Padrão Repository**
- **Interface `IProductRepository`**: Define o contrato das operações
- **Implementação `ProductRepository`**: Implementa a lógica de acesso aos dados
- **Benefícios**: Desacoplamento, testabilidade, flexibilidade

### 2. **Gerenciamento de Transações**
```java
EntityManager em = HibernateUtil.getEntityManager();
try {
    em.getTransaction().begin();
    // operações
    em.getTransaction().commit();
} catch (Exception e) {
    if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
    throw new RuntimeException("Erro: " + e.getMessage(), e);
} finally {
    em.close();
}
```

### 3. **Tratamento de Exceções**
- Rollback automático em caso de erro
- Mensagens de erro claras e contextualizadas
- Propagação adequada de exceções

### 4. **Validação de Dados**
- Verificação de parâmetros nulos
- Validação de entrada do usuário
- Verificação de existência antes de operações

### 5. **Uso do Optional**
```java
public Optional<Product> findById(Integer id) {
    if (id == null) {
        return Optional.empty();
    }
    Product product = em.find(Product.class, id);
    return Optional.ofNullable(product);
}
```

### 6. **Queries Type-Safe**
```java
TypedQuery<Product> query = em.createQuery(
    "SELECT p FROM Product p WHERE p.name LIKE :name", 
    Product.class
);
query.setParameter("name", "%" + name + "%");
```

## 🔧 Funcionalidades Implementadas

### CRUD Básico
- ✅ **CREATE**: Salvar novos produtos
- ✅ **READ**: Buscar por ID, listar todos, primeiro produto
- ✅ **UPDATE**: Atualizar produtos existentes  
- ✅ **DELETE**: Remover por ID, remover entidade, remover todos

### Operações Avançadas
- ✅ **Busca por nome** (com LIKE)
- ✅ **Busca por status** (ativo/inativo)
- ✅ **Busca por faixa de preço**
- ✅ **Contagem de registros**
- ✅ **Verificação de existência**

### Interface Interativa
- Menu completo para todas as operações
- Entrada de dados com validação
- Exibição formatada de resultados
- Confirmação para operações destrutivas

## 🚀 Como Executar

### 1. Compilar o projeto
```bash
mvn clean compile
```

### 2. Executar a aplicação original
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.Main"
```

### 3. Executar o exemplo completo
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.MainCRUDExample"
```

### 4. Para saída limpa (apenas System.out.println)
```bash
mvn -q exec:java -Dexec.mainClass="com.ejsjose.MainCRUDExample" 2>/dev/null
```

## 📊 Configurações

### pom.xml - Dependências Principais
```xml
<!-- Hibernate Core com JPA -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>4.3.11.Final</version>
</dependency>

<!-- SQLite JDBC Driver -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.34.0</version>
</dependency>

<!-- Lombok para reduzir boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.38</version>
    <scope>provided</scope>
</dependency>
```

### persistence.xml
```xml
<persistence-unit name="products-pu">
    <provider>org.hibernate.ejb.HibernatePersistence</provider>
    <class>com.ejsjose.entities.Product</class>
    <properties>
        <property name="javax.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:products.db"/>
        <property name="hibernate.dialect" value="com.ejsjose.infra.SQLiteDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
    </properties>
</persistence-unit>
```

## 🏷️ Entidade Product

```java
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private Double price;
    private int quantity;
    private boolean status;
}
```

## 📋 Operações Disponíveis

### No MainCRUDExample.java

1. **Demonstração Automática**
   - Cria produtos de exemplo
   - Executa todas as operações CRUD
   - Mostra filtros e buscas

2. **Menu Interativo**
   - Criar produto
   - Listar todos os produtos
   - Buscar produto por ID
   - Atualizar produto
   - Remover produto
   - Buscar com filtros (nome, status, preço)
   - Estatísticas

## 🛠️ Padrões Implementados

### 1. Repository Pattern
- Abstração da camada de dados
- Interface para operações CRUD
- Implementação específica para cada tecnologia

### 2. Entity Pattern
- Mapeamento objeto-relacional
- Annotations JPA
- Lombok para reduzir código

### 3. Utility Pattern
- HibernateUtil para configuração
- SQLiteDialect customizado

### 4. Exception Handling
- Try-catch-finally consistente
- Rollback automático
- Mensagens informativas

## 🔍 Benefícios da Arquitetura

1. **Manutenibilidade**: Código organizado e bem estruturado
2. **Testabilidade**: Interfaces permitem mock fácil
3. **Flexibilidade**: Fácil troca de implementação
4. **Robustez**: Tratamento adequado de erros
5. **Performance**: Uso eficiente do EntityManager
6. **Legibilidade**: Código limpo e bem documentado

## 🎨 Exemplo de Uso

```java
// Instanciar repositório
IProductRepository repository = new ProductRepository();

// Criar produto
Product product = new Product();
product.setName("Notebook Dell");
product.setPrice(2500.00);
product.setStatus(true);
Product saved = repository.save(product);

// Buscar por ID
Optional<Product> found = repository.findById(saved.getId());

// Buscar por nome
List<Product> products = repository.findByName("Dell");

// Atualizar
if (found.isPresent()) {
    Product p = found.get();
    p.setPrice(2300.00);
    repository.update(p);
}

// Remover
repository.deleteById(saved.getId());
```

## 🏁 Conclusão

Este projeto demonstra uma implementação completa e robusta de CRUD seguindo as melhores práticas do desenvolvimento Java. A arquitetura permite escalabilidade, manutenibilidade e facilita a implementação de testes automatizados.

A separação de responsabilidades, o uso de interfaces, o tratamento adequado de exceções e a validação de dados tornam este código adequado para ambientes de produção.
