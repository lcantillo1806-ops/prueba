export interface InventoryStatus {
  productId: string;
  cantidad: number;
}

export interface Inventory {
  cantidad: number;
  precioUnitario: number;
}

export interface InventoryProduct {
  productoId: number,
  nombreProducto: string,
  cantidadDisponible: number,
  precioUnitario: number
}

export interface  InventoryStock{
  productoId: number,
  cantidadNueva: number,
} 