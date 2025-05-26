package com.programthis.productcatalogservice.repository;

import com.programthis.productcatalogservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interfaz es un repositorio de Spring
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // JpaRepository ya te da métodos CRUD básicos para Category: save, findById, findAll, deleteById, etc.
    // Puedes añadir métodos personalizados si los necesitas, ej: Optional<Category> findByName(String name);
}