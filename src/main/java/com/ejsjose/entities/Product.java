package com.ejsjose.entities;

import jakarta.persistence.*;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Table(name = "products")
@Entity
public class Product {
  
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private String name;

    private Double price;

    private int quantity;

    private boolean status;

}
