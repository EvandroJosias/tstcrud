# 🚀 Sistema CRUD com Transferência JSON

## 🎯 Visão Geral

O projeto agora possui **transferência de dados via JSON** tanto para entradas quanto saídas, implementando uma arquitetura moderna e pronta para APIs REST.

## 📋 Funcionalidades JSON Implementadas

### ✅ **Entrada JSON**
- **Criação** de produtos via JSON
- **Atualização** de produtos via JSON  
- **Filtros** avançados via JSON
- **Validação** de JSON de entrada

### ✅ **Saída JSON**
- **Produtos** serializados em JSON
- **Listas** de produtos em JSON
- **Estatísticas** em JSON
- **Resultados** de operações em JSON

## 🏗️ Arquitetura JSON

```
┌─────────────────────────────────────────┐
│             JSON INPUT                  │  ← Entrada via JSON
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          ProductJsonService             │  ← Conversão JSON ↔ Objects
│     (Jackson Serialization)             │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          ProductService                 │  ← Lógica de Negócio
│        (Business Logic)                 │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         ProductRepository               │  ← Acesso aos Dados
│         (Data Access)                   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│             JSON OUTPUT                 │  ← Saída via JSON
└─────────────────────────────────────────┘
```

## 🔧 Componentes Implementados

### 1. **JsonUtils** - Utilitário para JSON
```java
// Conversão para JSON
String json = JsonUtils.toJsonPretty(object);

// Conversão de JSON
Product product = JsonUtils.fromJson(json, Product.class);

// Validação de JSON
boolean valid = JsonUtils.isValidJson(jsonString);
```

### 2. **ProductJsonService** - Serviço JSON
```java
// Criar produto via JSON
String result = jsonService.createProduct(createJson);

// Buscar produtos via JSON
String products = jsonService.findAllProducts();

// Filtrar via JSON
String filtered = jsonService.findProductsWithFilters(filterJson);
```

### 3. **DTOs com Anotações Jackson**
```java
@JsonPropertyOrder({"name", "price", "quantity", "status"})
public class ProductCreateDTO {
    @JsonProperty("name")
    public String getName() { return name; }
    // ...
}
```

## 📝 Exemplos de Uso

### 🔹 **Criar Produto**

**Input JSON:**
```json
{
    "name": "iPhone 15 Pro",
    "price": 8999.99,
    "quantity": 50,
    "status": true
}
```

**Output JSON:**
```json
{
  "id" : 44,
  "name" : "iPhone 15 Pro",
  "price" : 8999.99,
  "quantity" : 50,
  "status" : true
}
```

### 🔹 **Atualizar Produto**

**Input JSON:**
```json
{
    "price": 7999.99,
    "quantity": 75
}
```

### 🔹 **Filtrar Produtos**

**Input JSON:**
```json
{
    "name": "iPhone",
    "status": true,
    "minPrice": 5000.0,
    "maxPrice": 10000.0
}
```

**Output JSON:**
```json
[ {
  "id" : 44,
  "name" : "iPhone 15 Pro",
  "price" : 8999.99,
  "quantity" : 50,
  "status" : true
} ]
```

### 🔹 **Estatísticas**

**Output JSON:**
```json
{
  "totalProducts" : 9,
  "activeProducts" : 8,
  "inactiveProducts" : 1,
  "totalStockValue" : 538858.735,
  "mostExpensiveProduct" : {
    "id" : 44,
    "name" : "iPhone 15 Pro",
    "price" : 8999.99,
    "quantity" : 50,
    "status" : true
  },
  "cheapestProduct" : {
    "id" : 38,
    "name" : "Mouse Gamer",
    "price" : 89.99,
    "quantity" : 25,
    "status" : true
  }
}
```

## 🚀 Como Executar

### **Teste Simples JSON:**
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.JsonTestSimple"
```

### **Menu Interativo JSON:**
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.MainJsonExample"
```

### **Aplicação Original:**
```bash
mvn exec:java -Dexec.mainClass="com.ejsjose.MainCRUDExample"
```

## 📦 Dependências Adicionadas

```xml
<!-- Jackson para JSON -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.1</version>
</dependency>
```

## 🎯 Benefícios da Implementação JSON

### ✅ **Padronização**
- Formato universal para APIs
- Fácil integração com frontend
- Compatível com REST APIs

### ✅ **Flexibilidade**
- Entrada e saída estruturadas
- Validação automática
- Campos opcionais

### ✅ **Produtividade**
- Menos código boilerplate
- Serialização automática
- Formatação consistente

### ✅ **Escalabilidade**
- Pronto para microserviços
- Compatível com Spring Boot
- Fácil documentação com Swagger

## 🔄 Fluxo de Dados

```
🔹 JSON Input → 🔄 Parsing → 📋 DTO → ⚙️ Service → 💾 Repository
                                                         ↓
🔹 JSON Output ← 🔄 Serialization ← 📋 Entity ← ⚙️ Service ← 💾 Database
```

## 🎉 Resultado Final

✅ **Arquitetura Completa:**
- Entity + Repository + Service + JSON Service
- DTOs com anotações Jackson  
- Entrada e saída via JSON
- Validações de negócio
- Testes funcionais

✅ **Pronto para Evoluir:**
- 🌐 **API REST** (Spring Boot Controllers)
- 📊 **Swagger** (Documentação automática)
- 🧪 **Testes** (JUnit + MockMvc) 
- 🔄 **CI/CD** (Jenkins, GitHub Actions)

O projeto agora possui uma **arquitetura empresarial completa** com transferência JSON, pronto para ser usado em aplicações modernas! 🚀
