package com.programthis.productcatalogservice.repository;

import com.programthis.productcatalogservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.programthis.productcatalogservice.model.Category; // Importar Category

@Repository // Indica que esta interfaz es un repositorio de Spring
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Métodos CRUD básicos de JpaRepository
    // Puedes añadir métodos personalizados, por ejemplo, para buscar productos por categoría:
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryId(Long categoryId); // Buscar por ID de categoría directamente
}