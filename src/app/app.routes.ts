import { Routes } from '@angular/router';
import { authGuard, loginGuard, adminGuard } from './auth/guards/auth.guard';

// Customer guard'ı inline olarak tanımlayalım
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from './auth/services/auth.service';
import { map, take } from 'rxjs/operators';

const customerGuard = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  
  return authService.isAuthenticated$.pipe(
    take(1),
    map(isAuthenticated => {
      if (isAuthenticated) {
        const isCustomer = authService.isCustomer();
        if (isCustomer) {
          return true;
        } else {
          router.navigate(['/']);
          return false;
        }
      } else {
        router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
        return false;
      }
    })
  );
};

export const routes: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/components/main-layout/main-layout.component').then(
        (m) => m.MainLayoutComponent
      ),
    children: [
      {
        path: '',
        redirectTo: 'products',
        pathMatch: 'full'
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./profile/components/profile-edit/profile-edit.component').then(
            (m) => m.ProfileEditComponent
          ),
      },
      {
        path: 'products',
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./products/components/product-list/product-list.component').then(
                (m) => m.ProductListComponent
              ),
          },
          {
            path: 'create',
            loadComponent: () =>
              import('./products/components/product-form/product-form.component').then(
                (m) => m.ProductFormComponent
              ),
          },
          {
            path: 'edit/:id',
            loadComponent: () =>
              import('./products/components/product-form/product-form.component').then(
                (m) => m.ProductFormComponent
              ),
          },
          {
            path: 'view/:id',
            loadComponent: () =>
              import('./products/components/product-detail/product-detail.component').then(
                (m) => m.ProductDetailComponent
              ),
          }
        ]
      },
      {
        path: 'orders',
        children: [
          {
            path: 'cart',
            canActivate: [customerGuard],
            loadComponent: () =>
              import('./orders/components/cart/cart.component').then(
                (m) => m.CartComponent
              ),
          },
          {
            path: 'checkout',
            canActivate: [customerGuard],
            loadComponent: () =>
              import('./orders/components/checkout/checkout.component').then(
                (m) => m.CheckoutComponent
              ),
          },
          {
            path: 'history',
            canActivate: [customerGuard],
            loadComponent: () =>
              import('./orders/components/order-history/order-history.component').then(
                (m) => m.OrderHistoryComponent
              ),
          }
        ]
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          {
            path: 'user-add',
            loadComponent: () =>
              import('./admin/components/user-add/user-add.component').then(
                (m) => m.UserAddComponent
              ),
          },
          {
            path: 'user-management',
            loadComponent: () =>
              import('./admin/components/user-management/user-management.component').then(
                (m) => m.UserManagementComponent
              ),
          }
        ]
      }
    ],
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        canActivate: [loginGuard],
        loadComponent: () =>
          import('./auth/components/login/login.component').then(
            (m) => m.LoginComponent
          ),
      },
      {
        path: 'register',
        canActivate: [loginGuard],
        loadComponent: () =>
          import('./auth/components/register/register.component').then(
            (m) => m.RegisterComponent
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: ''
  }
];
