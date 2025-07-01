package com.programthis.productcatalogservice.service;

import com.programthis.productcatalogservice.model.Category;
import com.programthis.productcatalogservice.model.Product;
import com.programthis.productcatalogservice.repository.CategoryRepository;
import com.programthis.productcatalogservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Electronics");
        product = new Product(1L, "Laptop", "High-end laptop", 999.99, 10, category);
    }

    // Test para getAllProducts
    @Test
    void getAllProducts_ShouldReturnProductList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    // Test para getProductById
    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(1L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(1L);
    }

    // Test para saveProduct
    @Test
    void saveProduct_ShouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.saveProduct(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    // Test para createProduct
    @Test
    void createProduct_WhenCategoryExists_ShouldCreateAndReturnProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct("Laptop", "High-end laptop", 999.99, 10, 1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(category, result.getCategory());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct("Laptop", "High-end laptop", 999.99, 10, 1L);
        });

        assertEquals("Categoría no encontrada con ID: 1", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // Test para updateProduct
    @Test
    void updateProduct_WhenProductAndCategoryExist_ShouldUpdateAndReturnProduct() {
    // Crear una nueva categoría para el update
    Category newCategory = new Category(2L, "New Category");
    Product updatedDetails = new Product(null, "Updated Laptop", "Updated description", 1099.99, 20, null);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory)); // Nueva categoría
    when(productRepository.save(any(Product.class))).thenReturn(product);

    Optional<Product> result = productService.updateProduct(1L, updatedDetails, 2L); // Usar 2L como nuevo categoryId

    assertTrue(result.isPresent());
    assertEquals("Updated Laptop", result.get().getName());
    assertEquals(1099.99, result.get().getPrice());
    assertEquals(newCategory, result.get().getCategory()); // Verificar que se actualizó la categoría
    verify(productRepository, times(1)).findById(1L);
    verify(categoryRepository, times(1)).findById(2L); // Verificar que se busca la nueva categoría
    verify(productRepository, times(1)).save(any(Product.class));
}

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldReturnEmpty() {
        Product updatedDetails = new Product(null, "Updated Laptop", "Updated description", 1099.99, 20, null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.updateProduct(1L, updatedDetails, 1L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenCategoryDoesNotExist_ShouldThrowException() {
        Product updatedDetails = new Product(null, "Updated Laptop", "Updated description", 1099.99, 20, null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(1L, updatedDetails, 2L);
        });

        assertEquals("Categoría no encontrada con ID: 2", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(2L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // Test para deleteProduct
    @Test
    void deleteProduct_ShouldDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    // Test para getProductsByCategoryId
    @Test
    void getProductsByCategoryId_WhenCategoryExists_ShouldReturnProductList() {
        List<Product> products = Arrays.asList(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByCategory(category)).thenReturn(products);

        List<Product> result = productService.getProductsByCategoryId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findByCategory(category);
    }

    @Test
    void getProductsByCategoryId_WhenCategoryDoesNotExist_ShouldReturnEmptyList() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        List<Product> result = productService.getProductsByCategoryId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).findByCategory(any(Category.class));
    }
}