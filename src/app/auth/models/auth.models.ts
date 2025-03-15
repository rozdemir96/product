// Login request model
export interface LoginRequestModel {
  username: string;
  password: string;
}

// Login response model
export interface LoginResponseModel {
  token: string;
  username: string;
  roles?: string[];
}

// Register request model
export interface RegisterRequestModel {
  name: string;
  surname: string;
  email: string;
  mobilePhone: string;
  address: string;
  username: string;
  password: string;
}

// Register response model
export interface RegisterResponseModel {
  success: boolean;
  message: string;
}

// User Roles
export enum UserRole {
  ADMIN = 'ADMIN',
  EMPLOYEE = 'EMPLOYEE',
  CUSTOMER = 'CUSTOMER'
}

// JWT Payload interface
export interface JwtPayload {
  sub: string; // username
  roles?: any[]; // user roles - array olarak gelirse
  role?: string; // user role - string olarak gelirse
  userId?: number;
  name?: string;
  exp: number; // expiration time
  iat: number; // issued at time
}

// User Model for employee creation
export interface UserModel {
  id?: number;
  name: string;
  surname: string;
  email: string;
  mobilePhone: string;
  address?: string;
  username: string;
  password: string;
  role: UserRole;
  enabled: boolean;
} 