import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import { LOCALE_ID } from '@angular/core';

// Türkçe locale desteği için
import localeTr from '@angular/common/locales/tr';

// Import Lara theme
import Lara from '@primeng/themes/lara';

import { routes } from './app.routes';
import { authInterceptor } from './auth/interceptors/auth.interceptor';
import { OrdersModule } from './orders/orders.module';

// Türkçe locale'i kaydet
registerLocaleData(localeTr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    providePrimeNG({
      theme: {
        preset: Lara,
        options: {
          mode: 'light',
          colorScheme: 'blue'
        }
      },
      ripple: true
    }),
    importProvidersFrom(OrdersModule),
    { provide: LOCALE_ID, useValue: 'tr' } // Varsayılan locale'i Türkçe olarak ayarla
  ],
};
