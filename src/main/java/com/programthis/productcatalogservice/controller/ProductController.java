package com.programthis.productcatalogservice.controller;

import com.programthis.productcatalogservice.model.Product;
import com.programthis.productcatalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Importaciones de Lombok para las clases internas
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreationRequest request) {
        try {
            Product newProduct = productService.createProduct(
                request.getName(),          // <-- Asegúrate de que esta línea exista y sea .getName()
                request.getDescription(),   // <-- .getDescription()
                request.getPrice(),         // <-- .getPrice()
                request.getStock(),         // <-- .getStock()
                request.getCategoryId()     // <-- .getCategoryId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest request) {
        try {
            Optional<Product> updatedProduct = productService.updateProduct(
                id,
                request.getProductDetails(), // <-- .getProductDetails()
                request.getCategoryId()      // <-- .getCategoryId()
            );
            return updatedProduct.map(ResponseEntity::ok)
                                 .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    // --- Clases DTO (Data Transfer Object) para Request Bodies ---
    // ASEGÚRATE DE QUE ESTAS CLASES TENGAN LAS ANOTACIONES DE LOMBOK
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCreationRequest {
        private String name;
        private String description;
        private Double price;
        private Integer stock;
        private Long categoryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductUpdateRequest {
        private Product productDetails;
        private Long categoryId;
    }
}