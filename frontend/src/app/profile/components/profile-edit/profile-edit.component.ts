import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserModel, UserRole } from '../../../auth/models/auth.models';
import { ProfileService } from '../../services/profile.service';
import { AuthService } from '../../../auth/services/auth.service';
import { Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextarea } from 'primeng/inputtextarea';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';

@Component({
  selector: 'app-profile-edit',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    ToastModule, 
    ButtonModule, 
    InputTextModule, 
    InputTextarea,
    DropdownModule,
    CheckboxModule
  ],
  providers: [MessageService],
  template: `
    <div class="profile-container">
      <div class="profile-card">
        <h2>Profil Bilgilerim</h2>
        
        <div *ngIf="loading" class="loading-indicator">
          Bilgiler yükleniyor...
        </div>
        
        <form *ngIf="!loading" [formGroup]="profileForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="name">Ad</label>
            <input pInputText id="name" formControlName="name" class="form-control">
            <div *ngIf="profileForm.get('name')?.invalid && profileForm.get('name')?.touched" class="error-message">
              Ad alanı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="surname">Soyad</label>
            <input pInputText id="surname" formControlName="surname" class="form-control">
            <div *ngIf="profileForm.get('surname')?.invalid && profileForm.get('surname')?.touched" class="error-message">
              Soyad alanı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="email">E-posta</label>
            <input pInputText id="email" formControlName="email" class="form-control">
            <div *ngIf="profileForm.get('email')?.invalid && profileForm.get('email')?.touched" class="error-message">
              Geçerli bir e-posta adresi giriniz
            </div>
          </div>
          
          <div class="form-group">
            <label for="mobilePhone">Telefon</label>
            <input pInputText id="mobilePhone" formControlName="mobilePhone" class="form-control">
            <div *ngIf="profileForm.get('mobilePhone')?.invalid && profileForm.get('mobilePhone')?.touched" class="error-message">
              Telefon alanı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="address">Adres</label>
            <textarea pInputTextarea id="address" formControlName="address" class="form-control"></textarea>
          </div>
          
          <div class="form-group">
            <label for="username">Kullanıcı Adı</label>
            <input pInputText id="username" formControlName="username" class="form-control">
            <div *ngIf="profileForm.get('username')?.invalid && profileForm.get('username')?.touched" class="error-message">
              Kullanıcı adı zorunludur
            </div>
          </div>
          
          <div class="form-group">
            <label for="password">Şifre (Değiştirmek istemiyorsanız boş bırakın)</label>
            <input pInputText type="password" id="password" formControlName="password" class="form-control">
            <div *ngIf="profileForm.get('password')?.invalid && profileForm.get('password')?.touched" class="error-message">
              Şifre en az 6 karakter olmalıdır
            </div>
          </div>
          
          <!-- Sadece Admin için rol ve aktiflik alanları -->
          <ng-container *ngIf="isAdmin">
            <div class="form-group">
              <label for="role">Rol</label>
              <select pInputText id="role" formControlName="role" class="form-control">
                <option [value]="UserRole.ADMIN">Admin</option>
                <option [value]="UserRole.EMPLOYEE">Çalışan</option>
                <option [value]="UserRole.CUSTOMER">Müşteri</option>
              </select>
            </div>
            
            <div class="form-group checkbox-group">
              <label>
                <p-checkbox formControlName="enabled" [binary]="true"></p-checkbox>
                <span class="checkbox-label">Aktif Kullanıcı</span>
              </label>
            </div>
          </ng-container>
          
          <!-- Customer ve Employee için rol ve aktiflik bilgisi (salt okunur) -->
          <ng-container *ngIf="!isAdmin">
            <div class="form-group">
              <label>Rol</label>
              <div class="readonly-field">{{ getRoleName(userProfile?.role) }}</div>
            </div>
            
            <div class="form-group">
              <label>Hesap Durumu</label>
              <div class="readonly-field">{{ userProfile?.enabled ? 'Aktif' : 'Pasif' }}</div>
            </div>
          </ng-container>
          
          <div class="button-group">
            <button pButton type="submit" [disabled]="profileForm.invalid || saving" class="p-button-primary">
              {{ saving ? 'Kaydediliyor...' : 'Kaydet' }}
            </button>
            <button pButton type="button" (click)="cancel()" class="p-button-secondary">
              İptal
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .profile-container {
      display: flex;
      justify-content: center;
      padding: 2rem;
    }
    
    .profile-card {
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
    
    .loading-indicator {
      text-align: center;
      padding: 2rem;
      color: #6b7280;
    }
    
    .form-group {
      margin-bottom: 1.5rem;
    }
    
    label {
      display: block;
      margin-bottom: 0.5rem;
      color: #4b5563;
      font-weight: 500;
    }
    
    .form-control {
      width: 100%;
    }
    
    .error-message {
      color: #ef4444;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }
    
    .readonly-field {
      padding: 0.75rem;
      background-color: #f3f4f6;
      border: 1px solid #e5e7eb;
      border-radius: 4px;
      color: #4b5563;
    }
    
    .checkbox-group {
      display: flex;
      align-items: center;
    }
    
    .checkbox-label {
      margin-left: 0.5rem;
    }
    
    .button-group {
      display: flex;
      gap: 1rem;
      margin-top: 1.5rem;
    }
    
    .button-group button {
      flex: 1;
    }
  `]
})
export class ProfileEditComponent implements OnInit {
  profileForm!: FormGroup;
  userProfile: UserModel | null = null;
  loading: boolean = true;
  saving: boolean = false;
  isAdmin: boolean = false;
  UserRole = UserRole; // Template'de enum değerlerine erişim için

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    console.log('ProfileEditComponent initialized');
    this.isAdmin = this.authService.isAdmin();
    console.log('Is admin:', this.isAdmin);
    
    // Kullanıcı bilgilerini kontrol et
    const currentUser = this.authService.getCurrentUser();
    console.log('Current user in component:', currentUser);
    
    this.initForm();
    this.loadUserProfile();
  }

  initForm(): void {
    this.profileForm = this.fb.group({
      id: [''],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      mobilePhone: ['', Validators.required],
      address: [''],
      username: ['', Validators.required],
      password: ['', Validators.minLength(6)],
      role: [{ value: '', disabled: !this.isAdmin }],
      enabled: [{ value: true, disabled: !this.isAdmin }]
    });
  }

  loadUserProfile(): void {
    this.loading = true;
    this.profileService.getCurrentUserProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.updateForm(profile);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Profil bilgileri yüklenirken bir hata oluştu'
        });
        this.loading = false;
      }
    });
  }

  updateForm(profile: UserModel): void {
    // Şifre alanını boş bırak, sadece değiştirilmek istenirse doldurulacak
    const formData = {
      ...profile,
      password: ''
    };
    
    this.profileForm.patchValue(formData);
    
    // Admin değilse rol ve aktiflik alanlarını disable et
    if (!this.isAdmin) {
      this.profileForm.get('role')?.disable();
      this.profileForm.get('enabled')?.disable();
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.saving = true;
    
    // Form değerlerini al
    const formValues = this.profileForm.getRawValue();
    
    // Şifre alanı boşsa, mevcut şifreyi korumak için null olarak gönder
    // Backend tarafında null şifre değiştirilmeyecek şekilde işlenecek
    if (!formValues.password) {
      formValues.password = null;
    }
    
    // Admin değilse, rol ve aktiflik değerlerini mevcut değerlerle doldur
    if (!this.isAdmin && this.userProfile) {
      formValues.role = this.userProfile.role;
      formValues.enabled = this.userProfile.enabled;
    }
    
    this.profileService.updateUserProfile(formValues).subscribe({
      next: (response) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Başarılı',
          detail: 'Profil bilgileriniz başarıyla güncellendi'
        });
        this.saving = false;
        
        // Profil güncellemesi başarılı olduğunda ana sayfaya yönlendir
        setTimeout(() => {
          this.router.navigate(['/products']);
        }, 1500);
      },
      error: (error) => {
        console.error('Profil güncelleme hatası:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Hata',
          detail: 'Profil bilgileriniz güncellenirken bir hata oluştu'
        });
        this.saving = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }

  getRoleName(role?: UserRole): string {
    if (!role) return '';
    
    switch (role) {
      case UserRole.ADMIN:
        return 'Admin';
      case UserRole.EMPLOYEE:
        return 'Çalışan';
      case UserRole.CUSTOMER:
        return 'Müşteri';
      default:
        return '';
    }
  }
} 