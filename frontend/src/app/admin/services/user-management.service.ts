import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { UserModel } from '../../auth/models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) { }

  // Tüm kullanıcıları getir
  getAllUsers(): Observable<UserModel[]> {
    console.log('Tüm kullanıcılar getiriliyor');
    return this.http.get<UserModel[]>(`${this.apiUrl}/list`)
      .pipe(
        tap(users => console.log('Kullanıcılar getirildi:', users)),
        catchError(error => {
          console.error('Kullanıcılar getirilirken hata oluştu:', error);
          return throwError(() => error);
        })
      );
  }

  // Kullanıcı detaylarını getir
  getUserById(id: number): Observable<UserModel> {
    return this.http.get<UserModel>(`${this.apiUrl}/get/${id}`)
      .pipe(
        tap(user => console.log('Kullanıcı getirildi:', user)),
        catchError(error => {
          console.error('Kullanıcı getirilirken hata oluştu:', error);
          return throwError(() => error);
        })
      );
  }

  // Kullanıcı güncelle
  updateUser(user: UserModel): Observable<UserModel> {
    return this.http.put<UserModel>(`${this.apiUrl}/update`, user)
      .pipe(
        tap(updatedUser => console.log('Kullanıcı güncellendi:', updatedUser)),
        catchError(error => {
          console.error('Kullanıcı güncellenirken hata oluştu:', error);
          return throwError(() => error);
        })
      );
  }

  // Yeni kullanıcı ekle
  addEmployee(user: UserModel): Observable<UserModel> {
    // Eğer rol EMPLOYEE ise saveEmployee endpoint'ini kullan
    const endpoint = user.role === 'EMPLOYEE' ? 'saveEmployee' : 'save';
    
    return this.http.post<UserModel>(`${this.apiUrl}/${endpoint}`, user)
      .pipe(
        tap(newUser => console.log('Kullanıcı eklendi:', newUser)),
        catchError(error => {
          console.error('Kullanıcı eklenirken hata oluştu:', error);
          return throwError(() => error);
        })
      );
  }

  // Kullanıcı sil
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`)
      .pipe(
        tap(() => console.log('Kullanıcı silindi, ID:', id)),
        catchError(error => {
          console.error('Kullanıcı silinirken hata oluştu:', error);
          return throwError(() => error);
        })
      );
  }
} 