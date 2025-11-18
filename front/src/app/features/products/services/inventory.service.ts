import { Injectable, inject } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { ApiHttpService } from '../../../core/services/http/api-http.service';
import { Inventory, InventoryProduct, InventoryStatus, InventoryStock } from '../models/inventory.model';
import { environment } from '../../../../environments/environment';
import { MOCK_INVENTORY } from '../mock-inventory';
import { AppSettingsService } from 'src/app/core/services/http/app-settings.service';
import { IApiRes } from 'src/app/shared/models/IAPI.interface';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private api = inject(ApiHttpService);
  private _appSettings = inject(AppSettingsService);

  postIngresoInventory(productId: string, data: Partial<Inventory>): Observable<IApiRes<InventoryStatus>> {

    return this.api.post<IApiRes<InventoryStatus>>(`${this._appSettings.inventarioMS.url.getBase}${productId}/ingreso`, data);
  }


  /**
   *  OBTENER DETALLE POR ID
   */
  getInventaryProductById(productId: string): Observable<InventoryProduct> {
    return this.api.get<IApiRes<InventoryProduct>>(`${this._appSettings.inventarioMS.url.getBase}${productId}`).pipe(
      map((res: IApiRes<InventoryProduct>) => res.data)
    );
  }


  // ------------------------------------------------------
  //  Actualizar inventario después de compra
  // ------------------------------------------------------
  updateAfterPurchase(productId: string, data: Partial<Inventory>): Observable<InventoryStock> {

    return this.api.post<IApiRes<InventoryStock>>(`${this._appSettings.inventarioMS.url.getBase}${productId}/salida`, data).pipe(
      map((res: IApiRes<InventoryStock>) => res.data)
    );

  }
}
