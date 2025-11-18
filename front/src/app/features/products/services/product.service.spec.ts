import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { PaginatedResult } from '../../../shared/models/pagination.model';
import { Product } from '../models/product.model';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:3000';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('debe obtener productos paginados', () => {
    const mockResponse: PaginatedResult<Product> = {
      items: [{ id: '1', nombre: 'Prod 1', descripcion: '', precio: 1000 }],
      totalItems: 1,
      page: 1,
      pageSize: 10
    };

    service.getProducts(1, 10).subscribe(result => {
      expect(result.items.length).toBe(1);
      expect(result.page).toBe(1);
    });

    const req = httpMock.expectOne(`${baseUrl}/products?page=1&pageSize=10`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
