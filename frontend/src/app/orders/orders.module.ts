import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from './services/cart.service';
import { OrderService } from './services/order.service';

@NgModule({
  imports: [
    CommonModule
  ],
  providers: [
    CartService,
    OrderService
  ],
  exports: []
})
export class OrdersModule { } 