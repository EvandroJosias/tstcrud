package com.ejsjose.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO para filtros de busca de produtos
 */
@JsonPropertyOrder({"name", "minPrice", "maxPrice", "minQuantity", "maxQuantity", "status", "sortBy", "sortDirection"})
public class ProductFilterDTO {
    
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Boolean status;
    private String sortBy;
    private String sortDirection; // ASC ou DESC
    
    // Construtores
    public ProductFilterDTO() {}
    
    // Getters e Setters
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty("minPrice")
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    @JsonProperty("maxPrice")
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    @JsonProperty("minQuantity")
    public Integer getMinQuantity() {
        return minQuantity;
    }
    
    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }
    
    @JsonProperty("maxQuantity")
    public Integer getMaxQuantity() {
        return maxQuantity;
    }
    
    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
    
    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    @JsonProperty("sortBy")
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    @JsonProperty("sortDirection")
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    /**
     * Verifica se algum filtro foi definido
     * @return true se pelo menos um filtro não é null
     */
    public boolean hasAnyFilter() {
        return name != null || minPrice != null || maxPrice != null || 
               minQuantity != null || maxQuantity != null || status != null;
    }
    
    /**
     * Verifica se tem ordenação definida
     * @return true se sortBy não é null e não está vazio
     */
    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "ProductFilterDTO{" +
                "name='" + name + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", minQuantity=" + minQuantity +
                ", maxQuantity=" + maxQuantity +
                ", status=" + status +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
