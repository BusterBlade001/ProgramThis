package com.programthis.productcatalogservice.controller;

import com.programthis.productcatalogservice.model.Product;
import com.programthis.productcatalogservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "Endpoints for managing products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public CollectionModel<EntityModel<Product>> getAllProducts() {
        List<EntityModel<Product>> products = productService.getAllProducts().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by its ID")
    public ResponseEntity<EntityModel<Product>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody ProductCreationRequest request) {
        try {
            Product newProduct = productService.createProduct(
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getStock(),
                    request.getCategoryId());
            return ResponseEntity.status(HttpStatus.CREATED).body(toEntityModel(newProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<EntityModel<Product>> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest request) {
        try {
            return productService.updateProduct(id, request.getProductDetails(), request.getCategoryId())
                    .map(this::toEntityModel)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get all products for a given category ID")
    public CollectionModel<EntityModel<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<EntityModel<Product>> products = productService.getProductsByCategoryId(categoryId).stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getProductsByCategoryId(categoryId)).withSelfRel());
    }

    // Helper para convertir Product a EntityModel
    private EntityModel<Product> toEntityModel(Product product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getProductById(product.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getCategoryById(product.getCategory().getId())).withRel("category"),
                linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
    }
    
    // --- DTOs ---
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