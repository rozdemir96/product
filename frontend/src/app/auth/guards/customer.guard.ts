import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';
import { UserRole } from '../models/auth.models';

export const customerGuard: CanActivateFn = (route, state) => {
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