import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, of } from 'rxjs';
import { 
  LoginRequestModel, 
  LoginResponseModel, 
  RegisterRequestModel, 
  RegisterResponseModel,
  JwtPayload,
  UserRole,
  UserModel
} from '../models/auth.models';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'auth_token';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  private currentUserSubject = new BehaviorSubject<JwtPayload | null>(this.getCurrentUserFromToken());
  
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponseModel> {
    const loginRequest: LoginRequestModel = { username, password };
    
    return this.http.post<LoginResponseModel>('http://localhost:8080/api/auth/login', loginRequest)
      .pipe(
        tap(response => {
          console.log('Login response:', response);
          if (response && response.token) {
            this.setToken(response.token);
            this.isAuthenticatedSubject.next(true);
            const decodedToken = this.decodeToken(response.token);
            console.log('Setting current user:', decodedToken);
            this.currentUserSubject.next(decodedToken);
            
            // Rol kontrollerini hemen yapalım
            console.log('Immediate role checks:');
            console.log('- isAdmin:', this.isAdmin());
            console.log('- isEmployee:', this.isEmployee());
            console.log('- isCustomer:', this.isCustomer());
          }
        })
      );
  }

  register(registerData: RegisterRequestModel): Observable<RegisterResponseModel> {
    return this.http.post<RegisterResponseModel>('http://localhost:8080/api/users/save', registerData);
  }

  createEmployee(employeeData: UserModel): Observable<UserModel> {
    return this.http.post<UserModel>('http://localhost:8080/api/users/saveEmployee', employeeData);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  private decodeToken(token: string): JwtPayload | null {
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      console.log('Decoded token:', decoded);
      return decoded;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  private getCurrentUserFromToken(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    
    return this.decodeToken(token);
  }

  hasRole(role: UserRole): boolean {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser) {
      console.log('No user found:', currentUser);
      return false;
    }
    
    console.log('Checking role:', role, 'User data:', currentUser);
    
    // 1. roles array'i varsa kontrol et
    if (currentUser.roles) {
      // 1.1. Doğrudan eşleşme
      if (currentUser.roles.includes(role)) {
        return true;
      }
      
      // 1.2. "ROLE_" prefix'i ile eşleşme (Spring Security genellikle bu formatta döner)
      if (currentUser.roles.includes(`ROLE_${role}`)) {
        return true;
      }
      
      // 1.3. Küçük harfle eşleşme
      if (currentUser.roles.includes(role.toLowerCase())) {
        return true;
      }
      
      // 1.4. Rol bir obje olarak gelebilir
      if (currentUser.roles.some((r: any) => typeof r === 'object' && r.name === role)) {
        return true;
      }
    }
    
    // 2. Tek bir role string'i varsa kontrol et
    if (currentUser.role) {
      // 2.1. Doğrudan eşleşme
      if (currentUser.role === role) {
        return true;
      }
      
      // 2.2. "ROLE_" prefix'i ile eşleşme
      if (currentUser.role === `ROLE_${role}`) {
        return true;
      }
      
      // 2.3. Küçük harfle eşleşme
      if (currentUser.role.toLowerCase() === role.toLowerCase()) {
        return true;
      }
    }
    
    return false;
  }

  isAdmin(): boolean {
    const result = this.hasRole(UserRole.ADMIN);
    console.log('isAdmin check result:', result);
    return result;
  }

  isEmployee(): boolean {
    const result = this.hasRole(UserRole.EMPLOYEE);
    console.log('isEmployee check result:', result);
    return result;
  }

  isCustomer(): boolean {
    const result = this.hasRole(UserRole.CUSTOMER);
    console.log('isCustomer check result:', result);
    return result;
  }

  getCurrentUser(): JwtPayload | null {
    return this.currentUserSubject.value;
  }
} 