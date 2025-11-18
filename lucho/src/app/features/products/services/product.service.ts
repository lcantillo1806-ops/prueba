import { Injectable, inject } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { ApiHttpService } from '../../../core/services/http/api-http.service';
import { Product } from '../models/product.model';
import { PaginatedResult, PaginatedRoot } from '../../../shared/models/pagination.model';
import { environment } from '../../../../environments/environment';
import { MOCK_PRODUCTS } from '../mock-products';
import { AppSettingsService } from 'src/app/core/services/http/app-settings.service';
import { IApiRes } from 'src/app/shared/models/IAPI.interface';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private api = inject(ApiHttpService);
  private _appSettings = inject(AppSettingsService);

  /**
   *  LISTAR PRODUCTOS (Paginado)
   */
  getProducts(page = 0, size = 10, sortDirection = 'asc', sortBy = 'id'): Observable<PaginatedResult<Product>> {
    if (environment.useMockApi) {
      const start = (page - 1) * size;
      const items = MOCK_PRODUCTS.slice(start, start + size);

      const result: PaginatedResult<Product> = {
        items,
        totalItems: MOCK_PRODUCTS.length,
        page,
        pageSize: size
      };

      return of(result);
    }

    return this.api.get<IApiRes<PaginatedRoot>>(
      `${this._appSettings.productoMS.url.getBase}`,
      {
        page,
        size,
        sortDirection,
        sortBy
      }
    ).pipe(
      map((response: IApiRes<PaginatedRoot>) => {
        // Aquí transformas la respuesta del API al formato esperado
        const result: PaginatedResult<Product> = {
          items: response.data.content,          // Ajusta según la estructura real de tu API
          totalItems: response.data.totalElements,
          page: response.data.pageable.pageNumber,
          pageSize: response.data.pageable.pageSize
        };
        return result;
      })
    );
  }

  /**
   *  OBTENER DETALLE POR ID
   */
  getProductById(productId: string): Observable<Product> {
    if (environment.useMockApi) {
      const found = MOCK_PRODUCTS.find(p => p.id === productId);

      if (!found) throw new Error(`Producto con ID ${productId} no existe (mock)`);

      return of(found as Product);
    }

    return this.api.get<IApiRes<Product>>(`${this._appSettings.productoMS.url.getBase2}${productId}`)
    .pipe( map((res: IApiRes<Product>) => res.data));
  }

  /**
   *  Crear nuevo producto (Usado en ADMIN)
   */
  createProduct(product: Omit<Product, 'id'>): Observable<Product> {
    if (environment.useMockApi) {
      // Simular creación
      const newId = (MOCK_PRODUCTS.length + 1).toString();
      const newProduct = { id: newId, ...product };

      MOCK_PRODUCTS.push(newProduct);

      return of(newProduct);
    }

    return this.api.post<Product>(`${this._appSettings.productoMS.url.getBase}`, product);
  }

  /**
   *  Actualizar producto (ADMIN)
   */
  updateProduct(productId: string, data: Partial<Product>): Observable<Product> {
    if (environment.useMockApi) {
      const index = MOCK_PRODUCTS.findIndex(p => p.id === productId);

      if (index === -1) throw new Error(`Producto ${productId} no encontrado (mock)`);

      MOCK_PRODUCTS[index] = { ...MOCK_PRODUCTS[index], ...data };

      return of(MOCK_PRODUCTS[index]);
    }

    return this.api.patch<Product>(`${this._appSettings.productoMS.url.getBase2}${productId}`, data);
  }

  /**
   *  Eliminar producto (ADMIN)
   */
  deleteProduct(id: string): Observable<void> {
    if (environment.useMockApi) {
      const index = MOCK_PRODUCTS.findIndex(p => p.id === id);
      if (index >= 0) MOCK_PRODUCTS.splice(index, 1);

      return of(void 0);
    }

    return this.api.delete<void>(`${this._appSettings.productoMS.url.getBase2}`,{
      id
    });
  }
}
