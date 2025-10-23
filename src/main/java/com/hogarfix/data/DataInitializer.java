package com.hogarfix.data;

import com.hogarfix.model.Categoria;
import com.hogarfix.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    // Método para inicializar categorías
    private Categoria initializeCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    // Cargar datos iniciales en la base de datos
    @Bean
    CommandLineRunner initDatabase(CategoriaRepository categoriaRepository) {
        return args -> {
            Categoria plomeria = initializeCategoria("Plomería");
            Categoria electricidad = initializeCategoria("Electricidad");
            Categoria carpinteria = initializeCategoria("Carpintería");
            Categoria pintura = initializeCategoria("Pintura");
            Categoria reparacionesGenerales = initializeCategoria("Reparaciones Generales");

            categoriaRepository.saveAll(List.of(plomeria, electricidad, carpinteria, pintura, reparacionesGenerales));
        };
    }
}