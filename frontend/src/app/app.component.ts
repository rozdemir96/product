import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OrdersModule } from './orders/orders.module';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, OrdersModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'product-ui';
}
