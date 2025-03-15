import { BaseModel } from '../../products/models/product.models';

// Product type enum
export enum OrderStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

// Payment status enum
export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

// Sipariş kalemi oluşturma modeli
export interface OrderItemCreateModel {
  productId: number;
  quantity: number;
  priceAtPurchase?: number;
}

// Sipariş kalemi modeli
export interface OrderItemModel extends BaseModel {
  productId: number;
  productName?: string;
  quantity: number;
  priceAtPurchase: number;
  orderId: number;
}

// Sipariş oluşturma modeli
export interface OrderCreateModel {
  userId?: number;
  orderItems: OrderItemCreateModel[];
}

// Sipariş modeli
export interface OrderModel extends BaseModel {
  userId: number;
  userName?: string;
  orderDate: string;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  totalPrice: number;
  orderItems?: OrderItemModel[];
}

// Sepet kalemi modeli (UI için)
export interface CartItem {
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  totalPrice: number;
} 