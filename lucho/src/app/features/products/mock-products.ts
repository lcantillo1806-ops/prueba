import { Product } from './models/product.model';

export const MOCK_PRODUCTS: Product[] = Array.from({ length: 50 }, (_, index) => {
  const id = (index + 1).toString();

  return {
    id,
    nombre: `Producto ${id}`,
    descripcion: `Descripción del producto ${id}.`,
    precio: 10000 * (index + 1),
    availableQuantity: Math.floor(Math.random() * 20) 
  } as Product;
});
