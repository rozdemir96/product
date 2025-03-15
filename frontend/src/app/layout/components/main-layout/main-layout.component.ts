import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../../auth/services/auth.service';
import { Router } from '@angular/router';
import { BadgeModule } from 'primeng/badge';
import { CartService } from '../../../orders/services/cart.service';
import { TooltipModule } from 'primeng/tooltip';
import { UserRole } from '../../../auth/models/auth.models';

export interface NavItem {
  label: string;
  icon: string;
  routerLink: string;
  visible?: boolean;
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    BadgeModule,
    TooltipModule
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {
  menuItems: NavItem[] = [];
  cartItemCount: number = 0;
  
  private cartService = inject(CartService);

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.initializeMenu();
    
    // Kullanıcı oturumu değiştiğinde menüyü güncelle
    this.authService.currentUser$.subscribe(() => {
      this.initializeMenu();
    });

    // Subscribe to cart item count
    this.cartService.getCartItemCount().subscribe((count: number) => {
      this.cartItemCount = count;
    });
  }

  private initializeMenu(): void {
    const isAdmin = this.authService.isAdmin();
    const isCustomer = this.authService.isCustomer();
    const isEmployee = this.authService.isEmployee();
    
    console.log('User roles check:', { isAdmin, isCustomer, isEmployee });
    
    this.menuItems = [
      {
        label: 'Ürünler',
        icon: 'pi pi-shopping-bag',
        routerLink: '/products'
      },
      {
        label: 'Profilim',
        icon: 'pi pi-user-edit',
        routerLink: '/profile'
      }
    ];
    
    // Sadece müşteri rolü için sipariş geçmişi menüsü
    if (isCustomer) {
      console.log('Adding order history menu item');
      this.menuItems.push({
        label: 'Sipariş Geçmişim',
        icon: 'pi pi-history',
        routerLink: '/orders/history'
      });
    }
    
    // Admin rolü için kullanıcı yönetimi menüsü
    if (isAdmin) {
      this.menuItems.push({
        label: 'Kullanıcı Yönetimi',
        icon: 'pi pi-users',
        routerLink: '/admin/user-management'
      });
    }
    
    console.log('Final menu items:', this.menuItems);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  goToCart() {
    this.router.navigate(['/orders/cart']);
  }
} 