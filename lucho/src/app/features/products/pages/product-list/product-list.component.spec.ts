import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { ProductListComponent } from './product-list.component';
import { ProductService } from '../../services/product.service';

describe('ProductListComponent', () => {
  let component: ProductListComponent;
  let fixture: ComponentFixture<ProductListComponent>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;

  beforeEach(async () => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProducts']);

    await TestBed.configureTestingModule({
      imports: [ProductListComponent, RouterTestingModule],
      providers: [
        { provide: ProductService, useValue: productServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
  });

  it('debe cargar productos correctamente', () => {
    productServiceSpy.getProducts.and.returnValue(of({
      items: [],
      totalItems: 0,
      page: 1,
      pageSize: 10
    }));

    component.ngOnInit();
    expect(productServiceSpy.getProducts).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('debe manejar error de API', () => {
    productServiceSpy.getProducts.and.returnValue(throwError(() => new Error('API error')));

    component.ngOnInit();
    expect(component.isLoading).toBeFalse();
    expect(component.errorMessage).toBe('Ocurrió un error al cargar los productos.');
  });
});
