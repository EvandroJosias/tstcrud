package com.ejsjose;

import com.ejsjose.entities.Product;
import com.ejsjose.repositories.repoProduct;


public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        Product produto = new Product();
        // produto.setId(1L); // Removido - deixar o Hibernate gerar automaticamente
        produto.setName("Teste");
        produto.setPrice(10.0);
        produto.setQuantity(1);
        produto.setStatus(true);

        repoProduct repository = new repoProduct();
        repository.salvar(produto);
       
    }
}