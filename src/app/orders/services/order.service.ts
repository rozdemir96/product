import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderCreateModel, OrderItemModel, OrderModel } from '../models/order.models';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private baseUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  // Sipariş oluşturma
  createOrder(order: OrderCreateModel): Observable<OrderCreateModel> {
    return this.http.post<OrderCreateModel>(`${this.baseUrl}/save`, order);
  }

  // Kullanıcının kendi siparişlerini getirme
  getUserOrders(): Observable<OrderModel[]> {
    return this.http.get<OrderModel[]>(`${this.baseUrl}/list`);
  }

  // Tüm siparişleri getirme (admin ve çalışanlar için)
  getAllOrders(): Observable<OrderModel[]> {
    return this.http.get<OrderModel[]>(`${this.baseUrl}/all`);
  }

  // Sipariş detayını getirme
  getOrderById(id: number): Observable<OrderModel> {
    return this.http.get<OrderModel>(`${this.baseUrl}/get/${id}`);
  }

  // Sipariş kalemlerini getirme
  getOrderItems(orderId: number): Observable<OrderItemModel[]> {
    return this.http.get<OrderItemModel[]>(`${this.baseUrl}/getOrderItemsByOrderId/${orderId}`);
  }

  // Sipariş güncelleme
  updateOrder(order: OrderModel): Observable<OrderModel> {
    return this.http.put<OrderModel>(`${this.baseUrl}/update`, order);
  }

  // Sipariş silme
  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }
} 