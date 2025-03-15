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
import { CardModule } from 'primeng/card';
import { AccordionModule } from 'primeng/accordion';
import { DividerModule } from 'primeng/divider';

import { OrderService } from '../../services/order.service';
import { OrderModel, OrderStatus, PaymentStatus } from '../../models/order.models';

// Define the valid severity types for p-tag
type TagSeverity = 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined;

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    ToastModule,
    ConfirmDialogModule,
    TagModule,
    TooltipModule,
    CardModule,
    AccordionModule,
    DividerModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './order-history.component.html',
  styleUrl: './order-history.component.scss'
})
export class OrderHistoryComponent implements OnInit {
  orders: OrderModel[] = [];
  loading: boolean = true;
  expandedOrderId: number | null = null;
  
  constructor(
    private orderService: OrderService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getUserOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Siparişler yüklenemedi'
        });
        this.loading = false;
      }
    });
  }

  toggleOrderDetails(orderId: number): void {
    if (this.expandedOrderId === orderId) {
      this.expandedOrderId = null;
    } else {
      this.expandedOrderId = orderId;
    }
  }

  getOrderStatusLabel(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING:
        return 'Beklemede';
      case OrderStatus.PROCESSING:
        return 'İşleniyor';
      case OrderStatus.SHIPPED:
        return 'Kargoya Verildi';
      case OrderStatus.DELIVERED:
        return 'Teslim Edildi';
      case OrderStatus.CANCELLED:
        return 'İptal Edildi';
      default:
        return 'Bilinmiyor';
    }
  }

  getOrderStatusSeverity(status: OrderStatus): TagSeverity {
    switch (status) {
      case OrderStatus.PENDING:
        return 'warn';
      case OrderStatus.PROCESSING:
        return 'info';
      case OrderStatus.SHIPPED:
        return 'info';
      case OrderStatus.DELIVERED:
        return 'success';
      case OrderStatus.CANCELLED:
        return 'danger';
      default:
        return 'secondary';
    }
  }

  getPaymentStatusLabel(status: PaymentStatus): string {
    switch (status) {
      case PaymentStatus.PENDING:
        return 'Ödeme Bekliyor';
      case PaymentStatus.PAID:
        return 'Ödendi';
      case PaymentStatus.FAILED:
        return 'Başarısız';
      case PaymentStatus.REFUNDED:
        return 'İade Edildi';
      default:
        return 'Bilinmiyor';
    }
  }

  getPaymentStatusSeverity(status: PaymentStatus): TagSeverity {
    switch (status) {
      case PaymentStatus.PENDING:
        return 'warn';
      case PaymentStatus.PAID:
        return 'success';
      case PaymentStatus.FAILED:
        return 'danger';
      case PaymentStatus.REFUNDED:
        return 'info';
      default:
        return 'secondary';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  goToProducts(): void {
    this.router.navigate(['/products']);
  }
} 