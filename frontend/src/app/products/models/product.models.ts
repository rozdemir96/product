// Product type enum
export enum ProductType {
  ELECTRONICS = 'ELECTRONICS', // Elektronik
  CLOTHING = 'CLOTHING',       // Giyim
  FOOD = 'FOOD',               // Gıda
  FURNITURE = 'FURNITURE'      // Mobilya
}

// Ürün tipi etiketleri
export const ProductTypeLabels = {
  [ProductType.ELECTRONICS]: 'Elektronik',
  [ProductType.CLOTHING]: 'Giyim',
  [ProductType.FOOD]: 'Gıda',
  [ProductType.FURNITURE]: 'Mobilya'
};

// Base model interface
export interface BaseModel {
  id?: number;
  createdDate?: string;
  updatedDate?: string;
}

// Product model interface
export interface ProductModel extends BaseModel {
  name: string;
  description: string;
  price: number;
  stock: number;
  type: ProductType;
}

// Product response interface
export interface ProductResponse {
  success: boolean;
  message?: string;
  data?: ProductModel;
}

// Products list response interface
export interface ProductsListResponse {
  success: boolean;
  message?: string;
  data?: ProductModel[];
} 