import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { DividerModule } from 'primeng/divider';
import { StepsModule } from 'primeng/steps';
import { MenuItem } from 'primeng/api';
import { TableModule } from 'primeng/table';

import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { CartItem, OrderCreateModel, OrderItemCreateModel } from '../../models/order.models';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    CardModule,
    ToastModule,
    DividerModule,
    StepsModule,
    TableModule
  ],
  providers: [MessageService],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit {
  cartItems: CartItem[] = [];
  cartTotal: number = 0;
  steps: MenuItem[] = [];
  activeIndex: number = 0;
  submitting: boolean = false;
  
  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private messageService: MessageService,
    public router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadCartItems();
    this.updateCartTotal();
    this.initSteps();
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
  
  getTotalQuantity(): number {
    return this.cartItems.reduce((sum, item) => sum + item.quantity, 0);
  }
  
  initSteps(): void {
    this.steps = [
      {
        label: 'Sepeti İncele',
        command: () => {
          this.activeIndex = 0;
        }
      },
      {
        label: 'Siparişi Onayla',
        command: () => {
          this.activeIndex = 1;
        }
      }
    ];
  }
  
  nextStep(): void {
    if (this.activeIndex < this.steps.length - 1) {
      this.activeIndex++;
    }
  }
  
  prevStep(): void {
    if (this.activeIndex > 0) {
      this.activeIndex--;
    }
  }
  
  placeOrder(): void {
    if (this.cartItems.length === 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Boş Sepet',
        detail: 'Sepetiniz boş. Lütfen sipariş vermeden önce ürün ekleyin.'
      });
      return;
    }
    
    this.submitting = true;
    
    // Create order items from cart items
    const orderItems: OrderItemCreateModel[] = this.cartItems.map(item => ({
      productId: item.productId,
      quantity: item.quantity,
      priceAtPurchase: item.price
    }));
    
    // Create order model
    const orderModel: OrderCreateModel = {
      orderItems: orderItems
    };
    
    console.log('Placing order with data:', orderModel);
    
    this.orderService.createOrder(orderModel).subscribe({
      next: (response) => {
        console.log('Order placed successfully:', response);
        this.messageService.add({
          severity: 'success',
          summary: 'Sipariş Verildi',
          detail: 'Siparişiniz başarıyla oluşturuldu!'
        });
        
        // Clear cart after successful order
        this.cartService.clearCart();
        
        // Redirect to home page after a delay
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error placing order:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Sipariş Hatası',
          detail: 'Siparişiniz oluşturulurken bir hata oluştu. Lütfen tekrar deneyin.'
        });
        this.submitting = false;
      },
      complete: () => {
        this.submitting = false;
      }
    });
  }
  
  cancelOrder(): void {
    this.router.navigate(['/orders/cart']);
  }
} 