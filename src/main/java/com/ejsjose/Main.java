package com.ejsjose;

import com.ejsjose.entities.Product;
import com.ejsjose.repositories.repoProduct;
import com.ejsjose.infra.HibernateUtil;


public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        try {
            Product produto = new Product();
            // produto.setId(1L); // Removido - deixar o Hibernate gerar automaticamente
            produto.setName("Teste");
            produto.setPrice(10.0);
            produto.setQuantity(1);
            produto.setStatus(true);

            repoProduct repository = new repoProduct();
            repository.salvar(produto);

            System.out.println("=== PRODUTOS ANTES DA REMOÇÃO ===");
            repository.listarTodos().forEach(p -> {
                System.out.println("ID: " + p.getId() + ", Name: " + p.getName() + ", Price: " + p.getPrice() + ", Quantity: " + p.getQuantity() + ", Status: " + p.isStatus());
            });
            
            System.out.println("\n=== REMOVENDO PRODUTOS ===");
            repository.remover(1);
            repository.remover(2);
            
            System.out.println("\n=== PRODUTOS APÓS A REMOÇÃO ===");
            repository.listarTodos().forEach(p -> {
                System.out.println("ID: " + p.getId() + ", Name: " + p.getName() + ", Price: " + p.getPrice() + ", Quantity: " + p.getQuantity() + ", Status: " + p.isStatus());
            });
            
            System.out.println("\n=== BUSCANDO PRIMEIRO PRODUTO ===");
            Product primeiro = repository.buscarPrimeiro();
            if (primeiro != null) {
                System.out.println("Primeiro produto: ID: " + primeiro.getId() + ", Name: " + primeiro.getName() + ", Price: " + primeiro.getPrice());
            } else {
                System.out.println("Nenhum produto encontrado!");
            }
            
            System.out.println("Produto salvo com sucesso!");
            
        } finally {
            // IMPORTANTE: Fechar o EntityManagerFactory para terminar a aplicação
            HibernateUtil.shutdown();
            System.out.println("Aplicação finalizada.");
        }
    }
}