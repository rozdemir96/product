import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserManagementService } from '../../services/user-management.service';
import { UserModel, UserRole } from '../../../auth/models/auth.models';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    DropdownModule,
    ToastModule,
    ConfirmDialogModule,
    CheckboxModule,
    TooltipModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss'
})
export class UserManagementComponent implements OnInit {
  users: UserModel[] = [];
  selectedUser: UserModel | null = null;
  userDialog: boolean = false;
  newEmployeeDialog: boolean = false;
  userForm: FormGroup;
  employeeForm: FormGroup;
  roles = [
    { label: 'Admin', value: UserRole.ADMIN },
    { label: 'Çalışan', value: UserRole.EMPLOYEE },
    { label: 'Müşteri', value: UserRole.CUSTOMER }
  ];
  loading: boolean = false;
  passwordTooltip: string = 'Şifre sadece harf ve rakam içerebilir. Boşluk, özel karakter ve Unicode karakterleri kullanılamaz.';

  // Özel şifre validatörü - statik metot olarak tanımlandı
  static passwordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    
    if (!value) {
      return null;
    }
    
    // Sadece harf ve rakam içeren regex
    const validCharsRegex = /^[a-zA-Z0-9]+$/;
    
    if (!validCharsRegex.test(value)) {
      return { invalidPassword: true };
    }
    
    return null;
  }

  constructor(
    private userService: UserManagementService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private fb: FormBuilder
  ) {
    this.userForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      mobilePhone: ['', Validators.required],
      address: [''],
      username: ['', Validators.required],
      role: ['', Validators.required],
      enabled: [true]
    });

    this.employeeForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      mobilePhone: ['', Validators.required],
      address: [''],
      username: ['', Validators.required],
      password: ['', [Validators.required, UserManagementComponent.passwordValidator]],
      role: [UserRole.EMPLOYEE, Validators.required],
      enabled: [true]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Kullanıcılar yüklenirken bir hata oluştu' });
        this.loading = false;
      }
    });
  }

  openNew(): void {
    this.newEmployeeDialog = true;
    this.employeeForm.reset();
    this.employeeForm.patchValue({ 
      role: UserRole.EMPLOYEE,
      enabled: true 
    });
  }

  editUser(user: UserModel): void {
    this.selectedUser = { ...user };
    this.userForm.patchValue(this.selectedUser);
    this.userDialog = true;
  }

  deleteUser(user: UserModel): void {
    this.confirmationService.confirm({
      message: `${user.name} ${user.surname} kullanıcısını silmek istediğinize emin misiniz?`,
      header: 'Silme Onayı',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        if (user.id) {
          this.userService.deleteUser(user.id).subscribe({
            next: () => {
              this.users = this.users.filter(u => u.id !== user.id);
              this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Kullanıcı silindi', life: 3000 });
            },
            error: (error) => {
              this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Kullanıcı silinirken bir hata oluştu', life: 3000 });
            }
          });
        }
      }
    });
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Lütfen tüm zorunlu alanları doldurun', life: 3000 });
      return;
    }

    const user = this.userForm.value;
    this.userService.updateUser(user).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
          this.users = [...this.users];
        }
        this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Kullanıcı güncellendi', life: 3000 });
        this.userDialog = false;
        this.selectedUser = null;
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Kullanıcı güncellenirken bir hata oluştu', life: 3000 });
      }
    });
  }

  saveEmployee(): void {
    if (this.employeeForm.invalid) {
      if (this.employeeForm.get('password')?.errors?.['invalidPassword']) {
        this.messageService.add({ 
          severity: 'error', 
          summary: 'Hata', 
          detail: 'Şifre sadece harf ve rakam içerebilir. Boşluk, özel karakter ve Unicode karakterleri kullanılamaz.', 
          life: 5000 
        });
      } else {
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Lütfen tüm zorunlu alanları doldurun', life: 3000 });
      }
      return;
    }

    const employee = this.employeeForm.value;
    this.userService.addEmployee(employee).subscribe({
      next: (newEmployee) => {
        this.users.push(newEmployee);
        this.users = [...this.users];
        this.messageService.add({ severity: 'success', summary: 'Başarılı', detail: 'Kullanıcı eklendi', life: 3000 });
        this.newEmployeeDialog = false;
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Hata', detail: 'Kullanıcı eklenirken bir hata oluştu', life: 3000 });
      }
    });
  }

  hideDialog(): void {
    this.userDialog = false;
    this.newEmployeeDialog = false;
    this.selectedUser = null;
  }

  getRoleName(role: string): string {
    switch (role) {
      case UserRole.ADMIN:
        return 'Admin';
      case UserRole.EMPLOYEE:
        return 'Çalışan';
      case UserRole.CUSTOMER:
        return 'Müşteri';
      default:
        return role;
    }
  }
} 