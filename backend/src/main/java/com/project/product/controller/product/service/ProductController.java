package com.project.product.controller.product.service;

import com.project.product.controller.product.model.ProductModel;
import com.project.product.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Ürün Yönetimi", description = "Ürün sayfası CRUD işlemleri yönetilir.")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Yeni ürün ekleme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ProductModel> save(@RequestBody ProductModel productModel) {
        ProductModel savedProduct = productService.save(productModel);
        return ResponseEntity.ok(savedProduct);
    }

    /**
     * Tüm ürünleri listeleme - Herkes erişebilir.
     */
    @GetMapping("/list")
    public ResponseEntity<List<ProductModel>> getList() {
        List<ProductModel> products = productService.getList();
        return ResponseEntity.ok(products);
    }

    /**
     * ID ile ürün getirme - Herkes erişebilir.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<ProductModel> getById(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Ürün güncelleme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ProductModel> update(@RequestBody ProductModel productModel) {
        ProductModel updatedProduct = productService.update(productModel);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Ürün silme - Sadece ADMIN erişebilir.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
