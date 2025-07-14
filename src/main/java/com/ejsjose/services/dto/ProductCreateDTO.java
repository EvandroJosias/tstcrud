package com.ejsjose.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO para criação de produtos
 * 
 * Data Transfer Object usado para transportar dados
 * entre a camada de apresentação e serviço
 */
@JsonPropertyOrder({"name", "price", "quantity", "status"})
public class ProductCreateDTO {
    
    private String name;
    private Double price;
    private Integer quantity;
    private Boolean status;
    
    // Construtores
    public ProductCreateDTO() {}
    
    @JsonCreator
    public ProductCreateDTO(
            @JsonProperty("name") String name, 
            @JsonProperty("price") Double price, 
            @JsonProperty("quantity") Integer quantity, 
            @JsonProperty("status") Boolean status) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }
    
    // Getters e Setters
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    @JsonProperty("quantity")
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "ProductCreateDTO{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", status=" + status +
                '}';
    }
}
