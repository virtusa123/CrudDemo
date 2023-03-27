package com.interview.prep.controller;

import com.interview.prep.exception.ProductAlreadyExistsException;
import com.interview.prep.model.Product;
import com.interview.prep.exception.ProductNotFoundException;
import com.interview.prep.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    public List<Product> getAll() {
        return repository.findAll();
    }

    @PostMapping("/products")
    public Product create(@RequestBody Product product) {
        repository.findByName(product.getName())
                .ifPresent(p -> {
                    try {
                        throw new ProductAlreadyExistsException("product already exists");
                    } catch (ProductAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                });
        return repository.save(product);
    }

    @GetMapping("/products/{id}")
    public Product getById(@PathVariable Long id) throws ProductNotFoundException {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not available"));
    }

    @PutMapping("/products/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) throws ProductNotFoundException {
        return repository.findById(id)
                .map(p -> {
                    p.setName(product.getName());
                    p.setPrice(product.getPrice());
                    return repository.save(p);
                })
                .orElseThrow(() -> new ProductNotFoundException("Product Not available"));
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

}

