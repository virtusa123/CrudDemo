package com.interview.prep.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.prep.model.Product;
import com.interview.prep.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductRepository repository;

    @Test
    public void getAll_ShouldReturnListOfProducts() throws Exception {
        List<Product> expectedProducts = Arrays.asList(
                new Product(1L, "Product 1", 10.0),
                new Product(2L, "Product 2", 20.0)
        );
        when(repository.findAll()).thenReturn(expectedProducts);

        this.mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedProducts)));
    }

    @Test
    public void create_ShouldCreateNewProduct() throws Exception {
        Product expectedProduct = new Product(5L, "Product 1", 10.0);
        when(repository.findByName(expectedProduct.getName()))
                .thenReturn(Optional.empty());
        when(repository.save(expectedProduct)).thenReturn(expectedProduct);

        mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expectedProduct)))
                .andExpect(status().isOk());
    }

    @Test
    public void create_ShouldReturnconflict_WhenProductAlreadyExists() throws Exception {
        Product expectedProduct = new Product(100L, "Product1", 10.0);
        when(repository.findByName(expectedProduct.getName()))
                .thenReturn(Optional.of(expectedProduct));

        mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expectedProduct)))
                .andExpect(status().isConflict());
    }

    @Test
    public void getById_ShouldReturnProduct_WhenProductExists() throws Exception {
        Product expectedProduct = new Product(5L, "Product 1", 10.0);
        when(repository.findById(expectedProduct.getId()))
                .thenReturn(Optional.of(expectedProduct));

        mvc.perform(get("/products/{id}", expectedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedProduct)));
    }

    @Test
    public void getById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        mvc.perform(get("/products/{id}", 5L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_ShouldUpdateProduct_WhenProductExists() throws Exception {
        Product expectedProduct = new Product(5L, "Product 1", 10.0);
        when(repository.findById(expectedProduct.getId()))
                .thenReturn(Optional.of(expectedProduct));
        when(repository.save(expectedProduct)).thenReturn(expectedProduct);
        mvc.perform(put("/products/{id}", expectedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expectedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedProduct)));
    }

    @Test
    public void update_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        Product expectedProduct = new Product(5L, "Product 1", 10.0);
        when(repository.findById(expectedProduct.getId()))
                .thenReturn(Optional.empty());

        mvc.perform(put("/products/{id}", expectedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expectedProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_ShouldDeleteProduct() throws Exception {
        Mockito.doNothing().when(repository).deleteById(5L);

        mvc.perform(delete("/products/{id}", 5L))
                .andExpect(status().isOk());
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}



