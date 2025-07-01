package com.programthis.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programthis.productcatalogservice.model.Category;
import com.programthis.productcatalogservice.model.Product;
import com.programthis.productcatalogservice.service.ProductService;
import com.programthis.productcatalogservice.controller.ProductController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Indica que solo cargue ProductController para el test
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON y viceversa

    @MockBean // Crea un mock del ProductService y lo inyecta en el contexto de Spring
    private ProductService productService; //

    private Category testCategory; //
    private Product product1; //
    private Product product2; //

    @BeforeEach
    void setUp() {
        testCategory = new Category(1L, "Electronics", "Devices and gadgets"); //
        product1 = new Product(1L, "Laptop", "High performance laptop", 1200.00, 10, testCategory); //
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50, testCategory); //
    }

    @Test
    @DisplayName("GET /api/products should return all products")
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2)); //

        // When & Then
        mockMvc.perform(get("/api/products") //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
        verify(productService, times(1)).getAllProducts(); //
    }

    @Test
    @DisplayName("GET /api/products/{id} should return product by ID")
    void getProductById_shouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1)); //

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L) //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.id").value(1L));
        verify(productService, times(1)).getProductById(1L); //
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 404 if product not found")
    void getProductById_shouldReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenReturn(Optional.empty()); //

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L) //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(productService, times(1)).getProductById(99L); //
    }

    @Test
    @DisplayName("POST /api/products should create a new product successfully")
    void createProduct_shouldCreateNewProduct() throws Exception {
        // Given
        ProductController.ProductCreationRequest createRequest = //
                new ProductController.ProductCreationRequest("New Phone", "Latest model smartphone", 800.00, 20, 1L); //
        Product createdProduct = new Product(3L, "New Phone", "Latest model smartphone", 800.00, 20, testCategory); //

        when(productService.createProduct(
                eq("New Phone"), eq("Latest model smartphone"), eq(800.00), eq(20), eq(1L))) //
                .thenReturn(createdProduct);

        // When & Then
        mockMvc.perform(post("/api/products") //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated()) //
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("New Phone"));
        verify(productService, times(1)).createProduct(
                eq("New Phone"), eq("Latest model smartphone"), eq(800.00), eq(20), eq(1L)); //
    }

    @Test
    @DisplayName("POST /api/products should return 400 if creation fails (e.g., category not found)")
    void createProduct_shouldReturnBadRequestOnFailure() throws Exception {
        // Given
        ProductController.ProductCreationRequest createRequest = //
                new ProductController.ProductCreationRequest("Invalid Product", "Description", 100.00, 5, 99L); //

        when(productService.createProduct(
                anyString(), anyString(), anyDouble(), anyInt(), anyLong())) //
                .thenThrow(new RuntimeException("Categoría no encontrada con ID: 99")); //

        // When & Then
        mockMvc.perform(post("/api/products") //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest()); //
        verify(productService, times(1)).createProduct(
                anyString(), anyString(), anyDouble(), anyInt(), anyLong()); //
    }

    @Test
    @DisplayName("PUT /api/products/{id} should update an existing product successfully")
    void updateProduct_shouldUpdateProduct() throws Exception {
        // Given
        Product updatedProductDetails = new Product(null, "Laptop Pro", "Updated description", 1500.00, 8, null); //
        ProductController.ProductUpdateRequest updateRequest = //
                new ProductController.ProductUpdateRequest(updatedProductDetails, 1L); //
        Product updatedProduct = new Product(1L, "Laptop Pro", "Updated description", 1500.00, 8, testCategory); //

        when(productService.updateProduct(eq(1L), any(Product.class), eq(1L))) //
                .thenReturn(Optional.of(updatedProduct));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L) //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(1500.00));
        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class), eq(1L)); //
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 404 if product to update is not found")
    void updateProduct_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        // Given
        Product updatedDetails = new Product(null, "NonExistent", "Desc", 100.0, 1, null); //
        ProductController.ProductUpdateRequest updateRequest = //
                new ProductController.ProductUpdateRequest(updatedDetails, 1L); //

        when(productService.updateProduct(eq(99L), any(Product.class), anyLong())) //
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L) //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()); //
        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class), anyLong()); //
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 400 if update fails (e.g., category not found)")
    void updateProduct_shouldReturnBadRequestOnFailure() throws Exception {
        // Given
        Product updatedDetails = new Product(null, "Invalid Update", "Desc", 100.0, 1, null); //
        ProductController.ProductUpdateRequest updateRequest = //
                new ProductController.ProductUpdateRequest(updatedDetails, 99L); //

        when(productService.updateProduct(eq(1L), any(Product.class), eq(99L))) //
                .thenThrow(new RuntimeException("Categoría no encontrada con ID: 99")); //

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L) //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest()); //
        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class), eq(99L)); //
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should delete a product successfully")
    void deleteProduct_shouldDeleteProduct() throws Exception {
        // Given: El método deleteProduct en el servicio es void, no necesitamos un 'when' para su retorno.
        doNothing().when(productService).deleteProduct(1L); //

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L)) //
                .andExpect(status().isNoContent()); //
        verify(productService, times(1)).deleteProduct(1L); //
    }

    @Test
    @DisplayName("GET /api/products/category/{categoryId} should return products by category ID")
    void getProductsByCategoryId_shouldReturnProducts() throws Exception {
        // Given
        when(productService.getProductsByCategoryId(1L)).thenReturn(Arrays.asList(product1, product2)); //

        // When & Then
        mockMvc.perform(get("/api/products/category/{categoryId}", 1L) //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
        verify(productService, times(1)).getProductsByCategoryId(1L); //
    }

    @Test
    @DisplayName("GET /api/products/category/{categoryId} should return empty list if no products in category")
    void getProductsByCategoryId_shouldReturnEmptyList() throws Exception {
        // Given
        when(productService.getProductsByCategoryId(99L)).thenReturn(List.of()); //

        // When & Then
        mockMvc.perform(get("/api/products/category/{categoryId}", 99L) //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(productService, times(1)).getProductsByCategoryId(99L); //
    }
}