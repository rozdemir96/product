import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { UserModel, UserRole } from '../../../auth/models/auth.models';
import { AuthService } from '../../../auth/services/auth.service';
import { Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

// Özel şifre validatörü
function passwordValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  
  if (!value) {
    return null;
  }
  
  // Sadece harf ve rakam kontrolü
  const validChars = /^[a-zA-Z0-9]*$/;
  
  if (!validChars.test(value)) {
    return { invalidPassword: true };
  }
  
  return null;
}

@Component({
  selector: 'app-user-add',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ToastModule],
  providers: [MessageService],
  template: `
    <div class="user-add-container">
      <div class="user-add-card">
        <h2>Yeni Kullanıcı Ekle</h2>
        
        <form [formGroup]="userForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="name">Ad</label>
            <input type="text" id="name" formControlName="name" class="form-control">
            <div *ngIf="userForm.get('name')?.invalid && userForm.get('name')?.touched" class="error-message">
              Ad alanı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="surname">Soyad</label>
            <input type="text" id="surname" formControlName="surname" class="form-control">
            <div *ngIf="userForm.get('surname')?.invalid && userForm.get('surname')?.touched" class="error-message">
              Soyad alanı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="email">E-posta</label>
            <input type="email" id="email" formControlName="email" class="form-control">
            <div *ngIf="userForm.get('email')?.invalid && userForm.get('email')?.touched" class="error-message">
              Geçerli bir e-posta adresi giriniz
            </div>
          </div>
          
          <div class="form-group">
            <label for="mobilePhone">Telefon</label>
            <input type="tel" id="mobilePhone" formControlName="mobilePhone" class="form-control">
            <div *ngIf="userForm.get('mobilePhone')?.invalid && userForm.get('mobilePhone')?.touched" class="error-message">
              Telefon alanı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="address">Adres</label>
            <textarea id="address" formControlName="address" class="form-control"></textarea>
          </div>
          
          <div class="form-group">
            <label for="username">Kullanıcı Adı</label>
            <input type="text" id="username" formControlName="username" class="form-control">
            <div *ngIf="userForm.get('username')?.invalid && userForm.get('username')?.touched" class="error-message">
              Kullanıcı adı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="password">Şifre</label>
            <input 
              type="password" 
              id="password" 
              formControlName="password" 
              class="form-control"
              (keydown)="preventSpace($event)"
              (input)="filterPassword($event)"
            >
            <div *ngIf="userForm.get('password')?.invalid && userForm.get('password')?.touched" class="error-message">
              {{ getPasswordErrorMessage() }}
            </div>
            <div *ngIf="!userForm.get('password')?.invalid" class="info-message">
              Şifre sadece harf ve rakam içerebilir (en az 6 karakter).
            </div>
          </div>
          
          <div class="form-group">
            <label for="role">Rol <small class="text-muted">(Sistem tarafından otomatik atanır)</small></label>
            <select id="role" formControlName="role" class="form-control disabled-field">
              <option [value]="UserRole.EMPLOYEE">Çalışan</option>
            </select>
          </div>
          
          <div class="form-group checkbox-group">
            <label>
              <input type="checkbox" formControlName="enabled">
              Aktif Kullanıcı <small class="text-muted">(Sistem tarafından otomatik atanır)</small>
            </label>
          </div>
          
          <button type="submit" [disabled]="userForm.invalid" class="submit-button">
            Kullanıcı Ekle
          </button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .user-add-container {
      display: flex;
      justify-content: center;
      padding: 2rem;
    }
    
    .user-add-card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      padding: 2rem;
      width: 100%;
      max-width: 600px;
    }
    
    h2 {
      color: #1f2937;
      margin-bottom: 1.5rem;
      text-align: center;
    }
    
    .form-group {
      margin-bottom: 1rem;
    }
    
    label {
      display: block;
      margin-bottom: 0.5rem;
      color: #4b5563;
      font-weight: 500;
    }
    
    .text-muted {
      color: #6b7280;
      font-weight: normal;
    }
    
    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #e2e8f0;
      border-radius: 4px;
      font-size: 1rem;
      transition: border-color 0.2s;
    }
    
    .form-control:focus {
      border-color: #3B82F6;
      outline: none;
    }
    
    .disabled-field {
      background-color: #f3f4f6;
      cursor: not-allowed;
    }
    
    .error-message {
      color: #ef4444;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }
    
    .info-message {
      color: #3b82f6;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }
    
    .checkbox-group {
      display: flex;
      align-items: center;
    }
    
    .checkbox-group label {
      display: flex;
      align-items: center;
      margin-bottom: 0;
    }
    
    .checkbox-group input {
      margin-right: 0.5rem;
      cursor: not-allowed;
    }
    
    .submit-button {
      width: 100%;
      padding: 0.75rem;
      background-color: #3B82F6;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s;
      margin-top: 1rem;
    }
    
    .submit-button:hover {
      background-color: #2563eb;
    }
    
    .submit-button:disabled {
      background-color: #93c5fd;
      cursor: not-allowed;
    }
  `]
})
export class UserAddComponent implements OnInit {
  userForm!: FormGroup;
  UserRole = UserRole; // Template'de enum değerlerine erişim için

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      mobilePhone: ['', Validators.required],
      address: [''],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6), passwordValidator]],
      role: [{value: UserRole.EMPLOYEE, disabled: true}, Validators.required],
      enabled: [{value: true, disabled: true}]
    });
  }

  // Boşluk tuşuna basılmasını engelle
  preventSpace(event: KeyboardEvent): void {
    if (event.key === ' ' || event.code === 'Space') {
      event.preventDefault();
    }
  }

  // Input değerini filtrele - boşluk ve özel karakterleri kaldır
  filterPassword(event: Event): void {
    const input = event.target as HTMLInputElement;
    const filteredValue = input.value.replace(/[^a-zA-Z0-9]/g, '');
    
    // Eğer değer değiştiyse, input değerini güncelle
    if (input.value !== filteredValue) {
      input.value = filteredValue;
      // FormControl değerini manuel olarak güncelle
      this.userForm.get('password')?.setValue(filteredValue);
    }
  }

  // Şifre hata mesajını tek bir mesaj olarak döndür
  getPasswordErrorMessage(): string {
    const passwordControl = this.userForm.get('password');
    
    if (passwordControl?.errors?.['required']) {
      return 'Şifre alanı zorunludur';
    }
    
    if (passwordControl?.errors?.['minlength']) {
      return 'Şifre en az 6 karakter olmalıdır';
    }
    
    if (passwordControl?.errors?.['invalidPassword']) {
      return 'Şifre sadece harf ve rakam içerebilir';
    }
    
    return '';
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      return;
    }

    // Form değerlerini al ve disabled alanları ekle
    const formValues = this.userForm.getRawValue();
    const userData: UserModel = {
      ...formValues,
      role: UserRole.EMPLOYEE,
      enabled: true
    };
    
    this.authService.createEmployee(userData).subscribe({
      next: (response) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Başarılı',
          detail: 'Kullanıcı başarıyla oluşturuldu'
        });
        
        // Başarılı oluşturma sonrası kullanıcı listesine yönlendir
        setTimeout(() => {
          this.router.navigate(['/admin/users']);
        }, 1500);
      },
      error: (error) => {
        console.error('Kullanıcı oluşturma hatası:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Kullanıcı oluşturulurken bir hata oluştu'
        });
      }
    });
  }
} 