import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { ProductService } from '../../services/product.service';
import { ProductModel, ProductType, ProductTypeLabels } from '../../models/product.models';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    ButtonModule,
    CardModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.scss'
})
export class ProductFormComponent implements OnInit {
  productForm!: FormGroup;
  isEditMode: boolean = false;
  productId?: number;
  loading: boolean = false;
  submitting: boolean = false;
  
  productTypes = Object.entries(ProductTypeLabels).map(([key, label]) => ({
    label: label,
    value: key
  }));

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.initForm();
    
    // Check if we're in edit mode
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.productId = +params['id'];
        this.loadProduct(this.productId);
      }
    });
  }

  initForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      price: [0, [Validators.required, Validators.min(0)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      type: [null, [Validators.required]]
    });
  }

  loadProduct(id: number): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          stock: product.stock,
          type: product.type
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading product:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Ürün yüklenemedi'
        });
        this.loading = false;
        this.router.navigate(['/products']);
      }
    });
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const productData: ProductModel = this.productForm.value;
    
    if (this.isEditMode && this.productId) {
      productData.id = this.productId;
      this.updateProduct(productData);
    } else {
      this.createProduct(productData);
    }
  }

  createProduct(product: ProductModel): void {
    this.productService.createProduct(product).subscribe({
      next: (createdProduct) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Başarılı',
          detail: 'Ürün başarıyla oluşturuldu'
        });
        this.submitting = false;
        setTimeout(() => {
          this.router.navigate(['/products']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error creating product:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Ürün oluşturulamadı'
        });
        this.submitting = false;
      }
    });
  }

  updateProduct(product: ProductModel): void {
    this.productService.updateProduct(product).subscribe({
      next: (updatedProduct) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Başarılı',
          detail: 'Ürün başarıyla güncellendi'
        });
        this.submitting = false;
        setTimeout(() => {
          this.router.navigate(['/products']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error updating product:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Ürün güncellenemedi'
        });
        this.submitting = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }
} 