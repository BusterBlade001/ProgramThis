package com.programthis.productcatalogservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programthis.productcatalogservice.model.Category;
import com.programthis.productcatalogservice.service.CategoryService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class) // Indica que solo cargue CategoryController para el test
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @MockBean // Crea un mock del CategoryService y lo inyecta
    private CategoryService categoryService; //

    private Category category1; //
    private Category category2; //

    @BeforeEach
    void setUp() {
        category1 = new Category(1L, "Electronics", "Devices and gadgets"); //
        category2 = new Category(2L, "Books", "Reading materials"); //
    }

    @Test
    @DisplayName("GET /api/categories should return all categories")
    void getAllCategories_shouldReturnListOfCategories() throws Exception {
        // Given
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category1, category2)); //

        // When & Then
        mockMvc.perform(get("/api/categories") //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Books"));
        verify(categoryService, times(1)).getAllCategories(); //
    }

    @Test
    @DisplayName("GET /api/categories/{id} should return category by ID")
    void getCategoryById_shouldReturnCategory() throws Exception {
        // Given
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category1)); //

        // When & Then
        mockMvc.perform(get("/api/categories/{id}", 1L) //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"));
        verify(categoryService, times(1)).getCategoryById(1L); //
    }

    @Test
    @DisplayName("GET /api/categories/{id} should return 404 if category not found")
    void getCategoryById_shouldReturnNotFound() throws Exception {
        // Given
        when(categoryService.getCategoryById(99L)).thenReturn(Optional.empty()); //

        // When & Then
        mockMvc.perform(get("/api/categories/{id}", 99L) //
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(categoryService, times(1)).getCategoryById(99L); //
    }

    @Test
    @DisplayName("POST /api/categories should create a new category successfully")
    void createCategory_shouldCreateNewCategory() throws Exception {
        // Given
        Category newCategory = new Category(null, "New Category", "Description for new category"); //
        Category savedCategory = new Category(3L, "New Category", "Description for new category"); //

        when(categoryService.saveCategory(any(Category.class))).thenReturn(savedCategory); //

        // When & Then
        mockMvc.perform(post("/api/categories") //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated()) //
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("New Category"));
        verify(categoryService, times(1)).saveCategory(any(Category.class)); //
    }

    @Test
    @DisplayName("PUT /api/categories/{id} should update an existing category successfully")
    void updateCategory_shouldUpdateCategory() throws Exception {
        // Given
        Category updatedDetails = new Category(null, "Updated Electronics", "Updated description"); //
        Category existingCategory = new Category(1L, "Electronics", "Devices and gadgets"); //
        Category savedUpdatedCategory = new Category(1L, "Updated Electronics", "Updated description"); //

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(existingCategory)); //
        when(categoryService.saveCategory(any(Category.class))).thenReturn(savedUpdatedCategory); //

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", 1L) //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Electronics"))
                .andExpect(jsonPath("$.description").value("Updated description"));
        verify(categoryService, times(1)).getCategoryById(1L); //
        verify(categoryService, times(1)).saveCategory(any(Category.class)); //
    }

    @Test
    @DisplayName("PUT /api/categories/{id} should return 404 if category to update is not found")
    void updateCategory_shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
        // Given
        Category updatedDetails = new Category(null, "NonExistent", "Desc"); //
        when(categoryService.getCategoryById(99L)).thenReturn(Optional.empty()); //

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", 99L) //
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound()); //
        verify(categoryService, times(1)).getCategoryById(99L); //
        verify(categoryService, never()).saveCategory(any(Category.class)); //
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} should delete a category successfully")
    void deleteCategory_shouldDeleteCategory() throws Exception {
        // Given: El método deleteCategory en el servicio es void
        doNothing().when(categoryService).deleteCategory(1L); //

        // When & Then
        mockMvc.perform(delete("/api/categories/{id}", 1L)) //
                .andExpect(status().isNoContent()); //
        verify(categoryService, times(1)).deleteCategory(1L); //
    }
}