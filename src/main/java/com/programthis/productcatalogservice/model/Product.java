package com.programthis.productcatalogservice.model;

import jakarta.persistence.*; // Importa las anotaciones de JPA
import lombok.Data;          // Anotación de Lombok
import lombok.NoArgsConstructor; // Constructor sin argumentos
import lombok.AllArgsConstructor; // Constructor con todos los argumentos

@Data // Lombok: genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Lombok: genera un constructor sin argumentos
@AllArgsConstructor // Lombok: genera un constructor con todos los argumentos
@Entity // Marca esta clase como una entidad JPA
@Table(name = "products") // Mapea esta entidad a la tabla "products" en la BD
public class Product {

    @Id // Marca 'id' como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID es auto-incremental
    private Long id;

    @Column(nullable = false, unique = true) // Mapea a la columna 'name', no nula, y única
    private String name;

    @Column(nullable = false) // Mapea a la columna 'description', no nula
    private String description;

    @Column(nullable = false) // Mapea a la columna 'price', no nula
    private Double price; // Usamos Double en Java, aunque la BD almacene INT/DECIMAL(10)

    @Column(nullable = false) // Mapea a la columna 'stock', no nula
    private Integer stock;

    // Define la relación Many-to-One: Muchos productos pueden tener una Categoría
    @ManyToOne(fetch = FetchType.EAGER) // FetchType.EAGER: la categoría se carga junto con el producto
    @JoinColumn(name = "category_id", nullable = false) // Mapea a la columna 'category_id' que es la FK
    private Category category; // Campo que representa la categoría relacionada
}