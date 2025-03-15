import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';

import { ProductService } from '../../services/product.service';
import { ProductModel, ProductType, ProductTypeLabels } from '../../models/product.models';
import { CartService } from '../../../orders/services/cart.service';
import { AuthService } from '../../../auth/services/auth.service';

// Define the valid severity types for p-tag
type TagSeverity = 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined;

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    CardModule,
    ToastModule,
    TagModule,
    TooltipModule,
    InputNumberModule,
    FormsModule
  ],
  providers: [MessageService],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {
  product?: ProductModel;
  loading: boolean = true;
  quantity: number = 1;
  
  private cartService = inject(CartService);
  
  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        const productId = +params['id'];
        this.loadProduct(productId);
      } else {
        this.router.navigate(['/products']);
      }
    });
  }

  loadProduct(id: number): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.product = product;
        // Fiyatı iki ondalık basamağa yuvarla
        if (this.product && this.product.price) {
          this.product.price = Number(this.product.price.toFixed(2));
        }
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

  getProductTypeLabel(type: ProductType): string {
    return ProductTypeLabels[type] || type;
  }

  getProductTypeSeverity(type: ProductType): TagSeverity {
    switch (type) {
      case ProductType.ELECTRONICS:
        return 'info';
      case ProductType.CLOTHING:
        return 'success';
      case ProductType.FOOD:
        return 'warn';
      case ProductType.FURNITURE:
        return 'danger';
      default:
        return 'info';
    }
  }

  // Düzenleme yetkisi kontrolü - sadece admin ve çalışanlar düzenleyebilir
  canEditProduct(): boolean {
    return this.authService.isAdmin() || this.authService.isEmployee();
  }

  addToCart(): void {
    if (this.product) {
      this.cartService.addToCart(this.product, this.quantity);
      this.messageService.add({
        severity: 'success',
        summary: 'Sepete Eklendi',
        detail: `${this.product.name} sepetinize eklendi`
      });
    }
  }

  editProduct(): void {
    if (this.product?.id) {
      this.router.navigate(['/products/edit', this.product.id]);
    }
  }

  backToList(): void {
    this.router.navigate(['/products']);
  }
} 