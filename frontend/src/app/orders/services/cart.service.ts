import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { CartItem } from '../models/order.models';
import { ProductModel } from '../../products/models/product.models';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartKey = 'shopping_cart';
  private cartItemsSubject = new BehaviorSubject<CartItem[]>(this.getCartItems());
  
  cartItems$ = this.cartItemsSubject.asObservable();
  
  constructor() {}
  
  // Sepetteki ürünleri localStorage'dan getir
  private getCartItems(): CartItem[] {
    const cartJson = localStorage.getItem(this.cartKey);
    return cartJson ? JSON.parse(cartJson) : [];
  }
  
  // Sepeti localStorage'a kaydet
  private saveCartItems(items: CartItem[]): void {
    localStorage.setItem(this.cartKey, JSON.stringify(items));
    this.cartItemsSubject.next(items);
  }
  
  // Sepete ürün ekle
  addToCart(product: ProductModel, quantity: number = 1): void {
    const currentItems = this.getCartItems();
    const existingItemIndex = currentItems.findIndex(item => item.productId === product.id);
    
    if (existingItemIndex > -1) {
      // Ürün zaten sepette, miktarı artır
      currentItems[existingItemIndex].quantity += quantity;
      currentItems[existingItemIndex].totalPrice = 
        currentItems[existingItemIndex].price * currentItems[existingItemIndex].quantity;
    } else {
      // Yeni ürün ekle
      currentItems.push({
        productId: product.id!,
        productName: product.name,
        price: product.price,
        quantity: quantity,
        totalPrice: product.price * quantity
      });
    }
    
    this.saveCartItems(currentItems);
  }
  
  // Sepetten ürün çıkar
  removeFromCart(productId: number): void {
    const currentItems = this.getCartItems();
    const updatedItems = currentItems.filter(item => item.productId !== productId);
    this.saveCartItems(updatedItems);
  }
  
  // Sepetteki ürün miktarını güncelle
  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    
    const currentItems = this.getCartItems();
    const itemIndex = currentItems.findIndex(item => item.productId === productId);
    
    if (itemIndex > -1) {
      currentItems[itemIndex].quantity = quantity;
      currentItems[itemIndex].totalPrice = currentItems[itemIndex].price * quantity;
      this.saveCartItems(currentItems);
    }
  }
  
  // Sepeti temizle
  clearCart(): void {
    localStorage.removeItem(this.cartKey);
    this.cartItemsSubject.next([]);
  }
  
  // Sepetteki toplam ürün sayısı
  getCartItemCount(): Observable<number> {
    return new Observable<number>(observer => {
      this.cartItems$.subscribe(items => {
        const count = items.reduce((total, item) => total + item.quantity, 0);
        observer.next(count);
      });
    });
  }
  
  // Sepet toplamı
  getCartTotal(): Observable<number> {
    return new Observable<number>(observer => {
      this.cartItems$.subscribe(items => {
        const total = items.reduce((sum, item) => sum + item.totalPrice, 0);
        observer.next(total);
      });
    });
  }
} 