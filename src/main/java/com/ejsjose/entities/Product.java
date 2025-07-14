package com.ejsjose.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id", "name", "price", "quantity", "status"})
@Table(name = "products")
@Entity
public class Product {
  
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    private String name;

    private Double price;

    private int quantity;

    private boolean status;

}
