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
import { RegisterRequestModel, RegisterResponseModel } from '../../models/auth.models';

@Component({
  selector: 'app-register',
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
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      mobilePhone: ['', [Validators.required]],
      address: ['', [Validators.required]],
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    
    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    const { name, surname, email, mobilePhone, address, username, password } = this.registerForm.value;
    
    const registerData: RegisterRequestModel = {
      name,
      surname,
      email,
      mobilePhone,
      address,
      username,
      password
    };
    
    console.log('Attempting registration with:', registerData);

    this.authService.register(registerData).subscribe({
      next: (response: RegisterResponseModel) => {
        console.log('Registration successful:', response);
        this.messageService.add({
          severity: 'success',
          summary: 'Registration Successful',
          detail: response.message || 'You can now login with your credentials'
        });
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Registration failed:', error);
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Registration Failed',
          detail: error.error?.message || 'An error occurred during registration. Please try again.'
        });
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }
} 