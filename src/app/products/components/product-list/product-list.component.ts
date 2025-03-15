import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { InputNumberModule } from 'primeng/inputnumber';

import { ProductService } from '../../services/product.service';
import { ProductModel, ProductType, ProductTypeLabels } from '../../models/product.models';
import { CartService } from '../../../orders/services/cart.service';
import { AuthService } from '../../../auth/services/auth.service';
import { UserRole } from '../../../auth/models/auth.models';

// Define the valid severity types for p-tag
type TagSeverity = 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined;

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    ToastModule,
    ConfirmDialogModule,
    TagModule,
    TooltipModule,
    InputNumberModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit {
  products: ProductModel[] = [];
  loading: boolean = true;
  first: number = 0;
  rows: number = 10;
  
  // Rol kontrolleri için değişkenler
  isAdmin: boolean = false;
  isEmployee: boolean = false;
  isCustomer: boolean = false;
  
  constructor(
    private productService: ProductService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public router: Router,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.checkUserRoles();
  }

  // Kullanıcı rollerini kontrol et
  checkUserRoles(): void {
    this.isAdmin = this.authService.isAdmin();
    this.isEmployee = this.authService.isEmployee();
    this.isCustomer = this.authService.isCustomer();
    console.log('User roles:', { isAdmin: this.isAdmin, isEmployee: this.isEmployee, isCustomer: this.isCustomer });
  }

  // Düzenleme ve silme butonları için yetki kontrolü
  canEditOrDelete(): boolean {
    return this.isAdmin || this.isEmployee;
  }

  // Sepete ekleme butonu için yetki kontrolü
  canAddToCart(): boolean {
    return this.isCustomer && !this.isAdmin && !this.isEmployee;
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load products'
        });
        this.loading = false;
      }
    });
  }

  onPageChange(event: any): void {
    this.first = event.first;
    this.rows = event.rows;
  }

  createProduct(): void {
    this.router.navigate(['/products/create']);
  }

  editProduct(product: ProductModel): void {
    this.router.navigate(['/products/edit', product.id]);
  }

  viewProduct(product: ProductModel): void {
    this.router.navigate(['/products/view', product.id]);
  }

  confirmDelete(product: ProductModel): void {
    this.confirmationService.confirm({
      message: `${product.name} ürününü silmek istediğinizden emin misiniz?`,
      header: 'Silme İşlemini Onaylayın',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteProduct(product);
      }
    });
  }

  deleteProduct(product: ProductModel): void {
    if (product.id) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Başarılı',
            detail: `${product.name} silindi`
          });
          this.loadProducts();
        },
        error: (error) => {
          console.error('Error deleting product:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Hata',
            detail: 'Ürün silinemedi'
          });
        }
      });
    }
  }

  addToCart(product: ProductModel, quantity: number = 1): void {
    this.cartService.addToCart(product, quantity);
    this.messageService.add({
      severity: 'success',
      summary: 'Sepete Eklendi',
      detail: `${product.name} sepetinize eklendi`
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
} 