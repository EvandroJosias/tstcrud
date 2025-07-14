package com.ejsjose.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO para atualização de produtos
 * 
 * Permite atualização parcial dos campos
 */
@JsonPropertyOrder({"name", "price", "quantity", "status"})
public class ProductUpdateDTO {
    
    private String name;
    private Double price;
    private Integer quantity;
    private Boolean status;
    
    // Construtores
    public ProductUpdateDTO() {}
    
    @JsonCreator
    public ProductUpdateDTO(
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
    
    /**
     * Verifica se algum campo foi definido para atualização
     * @return true se pelo menos um campo não é null
     */
    public boolean hasAnyField() {
        return name != null || price != null || quantity != null || status != null;
    }
    
    @Override
    public String toString() {
        return "ProductUpdateDTO{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", status=" + status +
                '}';
    }
}
