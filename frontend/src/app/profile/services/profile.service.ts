import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError, switchMap } from 'rxjs';
import { UserModel } from '../../auth/models/auth.models';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  // Kullanıcı bilgilerini getir
  getCurrentUserProfile(): Observable<UserModel> {
    console.log('Getting current user profile');
    
    // 1. Önce /api/auth/getCurrentUser endpoint'ine istek atarak mevcut kullanıcı ID'sini al
    return this.http.get<any>(`${this.apiUrl}/auth/getCurrentUser`)
      .pipe(
        tap(currentUser => console.log('Current user from API:', currentUser)),
        switchMap(currentUser => {
          if (!currentUser || !currentUser.userId) {
            console.error('User ID not found in API response');
            return throwError(() => new Error('User ID not found'));
          }
          
          const userId = currentUser.userId;
          console.log('Fetching user profile for ID:', userId);
          
          // 2. Sonra /api/users/get/{id} endpoint'ine istek atarak kullanıcı bilgilerini al
          return this.http.get<UserModel>(`${this.apiUrl}/users/get/${userId}`);
        }),
        tap(response => console.log('User profile response:', response)),
        catchError(error => {
          console.error('Error fetching user profile:', error);
          return throwError(() => error);
        })
      );
  }

  // Kullanıcı bilgilerini güncelle
  updateUserProfile(userData: UserModel): Observable<UserModel> {
    console.log('Updating user profile:', userData);
    return this.http.put<UserModel>(`${this.apiUrl}/users/update`, userData)
      .pipe(
        tap(response => console.log('Update response:', response)),
        catchError(error => {
          console.error('Error updating user profile:', error);
          return throwError(() => error);
        })
      );
  }
} 