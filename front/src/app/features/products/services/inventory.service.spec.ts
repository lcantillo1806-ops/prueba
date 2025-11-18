import { TestBed } from '@angular/core/testing';
import { InventoryService } from './inventory.service';
import { ApiHttpService } from 'src/app/core/services/http/api-http.service';
import { environment } from 'src/environments/environment';
import { MOCK_INVENTORY } from '../mock-inventory';
import { of } from 'rxjs';

describe('InventoryService', () => {
  let service: InventoryService;
  let apiHttpSpy: jasmine.SpyObj<ApiHttpService>;

  beforeEach(() => {
    apiHttpSpy = jasmine.createSpyObj('ApiHttpService', ['get', 'patch']);

    TestBed.configureTestingModule({
      providers: [
        InventoryService,
        { provide: ApiHttpService, useValue: apiHttpSpy }
      ]
    });

    service = TestBed.inject(InventoryService);
  });

  describe('getInventory', () => {
    it('should return mock inventory when useMockApi = true', (done) => {
      environment.useMockApi = true;
      MOCK_INVENTORY.push({ productId: 'p1', cantidad: 5 });

      service.getInventory('p1').subscribe(result => {
        expect(result.cantidad).toBe(5);
        done();
      });
    });

    it('should call api.get when useMockApi = false', () => {
      environment.useMockApi = false;
      apiHttpSpy.get.and.returnValue(of({ productId: 'p2', availableQuantity: 10 }));

      service.getInventory('p2').subscribe(result => {
        expect(apiHttpSpy.get).toHaveBeenCalledWith('/inventory/p2');
        expect(result.cantidad).toBe(10);
      });
    });
  });

  describe('updateAfterPurchase', () => {
    it('should create new inventory if product does not exist', (done) => {
      environment.useMockApi = true;
      const productId = 'newProd';

      service.updateAfterPurchase(productId, 3).subscribe(result => {
        expect(result.cantidad).toBe(0);
        expect(MOCK_INVENTORY.find(i => i.productId === productId)).toBeTruthy();
        done();
      });
    });

    it('should update inventory without going negative', (done) => {
      environment.useMockApi = true;
      MOCK_INVENTORY.push({ productId: 'p3', cantidad: 2 });

      service.updateAfterPurchase('p3', 5).subscribe(result => {
        expect(result.cantidad).toBe(0);
        done();
      });
    });

    it('should call api.patch when useMockApi = false', () => {
      environment.useMockApi = false;
      apiHttpSpy.patch.and.returnValue(of({ productId: 'p4', availableQuantity: 7 }));

      service.updateAfterPurchase('p4', 2).subscribe(result => {
        expect(apiHttpSpy.patch).toHaveBeenCalledWith('/inventory/p4/purchase', { quantity: 2 });
        expect(result.cantidad).toBe(7);
      });
    });
  });
});