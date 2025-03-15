import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { AuthService } from '../../services/auth.service';
import { LoginResponseModel } from '../../models/auth.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    CardModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    const { username, password } = this.loginForm.value;
    
    console.log('Attempting login with:', { username });

    this.authService.login(username, password).subscribe({
      next: (response: LoginResponseModel) => {
        console.log('Login successful:', response);
        this.messageService.add({
          severity: 'success',
          summary: 'Login Successful',
          detail: `Welcome back, ${response.username}!`
        });
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Login failed:', error);
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Login Failed',
          detail: error.error?.message || 'Invalid credentials. Please try again.'
        });
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/auth/register']);
  }
} 