package com.programthis.productcatalogservice.service;

import com.programthis.productcatalogservice.model.Product;
import com.programthis.productcatalogservice.model.Category; // Importar Category
import com.programthis.productcatalogservice.repository.ProductRepository;
import com.programthis.productcatalogservice.repository.CategoryRepository; // Importar CategoryRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Indica que esta clase es un componente de servicio de Spring
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired // Necesitamos este para buscar categorías
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Método para guardar un producto (crear o actualizar)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Método para crear un producto, manejando la asignación de categoría por ID
    public Product createProduct(String name, String description, Double price, Integer stock, Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Categoría no encontrada con ID: " + categoryId);
        }
        Category category = categoryOptional.get();
        Product newProduct = new Product(null, name, description, price, stock, category); // ID es null para que se autogenere
        return productRepository.save(newProduct);
    }

    // Método para actualizar un producto existente
    public Optional<Product> updateProduct(Long id, Product productDetails, Long categoryId) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStock(productDetails.getStock());

            if (categoryId != null && !categoryId.equals(product.getCategory().getId())) {
                Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
                if (categoryOptional.isEmpty()) {
                    throw new RuntimeException("Categoría no encontrada con ID: " + categoryId);
                }
                product.setCategory(categoryOptional.get());
            }
            return Optional.of(productRepository.save(product));
        }
        return Optional.empty();
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            // Podrías lanzar una excepción o retornar una lista vacía si la categoría no existe
            return List.of(); // Retorna lista vacía si la categoría no existe
        }
        return productRepository.findByCategory(categoryOptional.get());
    }
}