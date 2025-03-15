import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DividerModule } from 'primeng/divider';

import { CartService } from '../../services/cart.service';
import { CartItem } from '../../models/order.models';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    TableModule,
    ButtonModule,
    InputNumberModule,
    CardModule,
    ToastModule,
    ConfirmDialogModule,
    DividerModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  cartTotal: number = 0;
  
  constructor(
    private cartService: CartService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadCartItems();
    this.updateCartTotal();
  }
  
  loadCartItems(): void {
    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
    });
  }
  
  updateCartTotal(): void {
    this.cartService.getCartTotal().subscribe(total => {
      this.cartTotal = total;
    });
  }
  
  updateQuantity(item: CartItem, quantity: number): void {
    this.cartService.updateQuantity(item.productId, quantity);
    this.updateCartTotal();
    this.messageService.add({
      severity: 'success',
      summary: 'Sepet Güncellendi',
      detail: `${item.productName} için miktar güncellendi`
    });
  }
  
  removeItem(item: CartItem): void {
    this.confirmationService.confirm({
      message: `${item.productName} ürününü sepetinizden çıkarmak istediğinize emin misiniz?`,
      header: 'Ürünü Çıkar',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.cartService.removeFromCart(item.productId);
        this.updateCartTotal();
        this.messageService.add({
          severity: 'success',
          summary: 'Ürün Çıkarıldı',
          detail: `${item.productName} sepetinizden çıkarıldı`
        });
      }
    });
  }
  
  clearCart(): void {
    this.confirmationService.confirm({
      message: 'Sepetinizdeki tüm ürünleri silmek istediğinize emin misiniz?',
      header: 'Sepeti Temizle',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.cartService.clearCart();
        this.updateCartTotal();
        this.messageService.add({
          severity: 'success',
          summary: 'Sepet Temizlendi',
          detail: 'Tüm ürünler sepetinizden çıkarıldı'
        });
      }
    });
  }
  
  proceedToCheckout(): void {
    if (this.cartItems.length === 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Boş Sepet',
        detail: 'Sepetiniz boş. Lütfen ödeme öncesi ürün ekleyin.'
      });
      return;
    }
    
    this.router.navigate(['/orders/checkout']);
  }
  
  continueShopping(): void {
    this.router.navigate(['/products']);
  }
} 