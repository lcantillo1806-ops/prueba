export interface Product {
  id: string;
  nombre: string;
  descripcion: string;
  precio: number;
  imageBase64?: string;
  availableQuantity?: number;
}
