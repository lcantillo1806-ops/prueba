import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { InventoryService } from '../../services/inventory.service';
import { Product } from '../../models/product.model';
import { InventoryProduct, InventoryStatus, InventoryStock } from '../../models/inventory.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-detail.component.html'
})
export class ProductDetailComponent {
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);
  private inventoryService = inject(InventoryService);
  private location = inject(Location);

  producto: Product;
  product?: InventoryProduct;
  productoId: string;
  inventory: InventoryStock;
  isLoading = false;
  isUpdating = false;
  errorMessage = '';
  successMessage = '';
  quantity = 1;
  hasScrolled = false;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.errorMessage = 'Producto inválido.';
      return;
    }
    this.productoId = id;
    this.loadData(id);
  }

  goBack(): void {
    this.location.back();
  }

  // -------------------------------------------------------------
  //  Cargar información del producto + inventario desde API
  // -------------------------------------------------------------
  private loadData(productId: string): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.inventoryService.getInventaryProductById(productId).subscribe({
      next: (product) => {
        this.product = product;
        this.isLoading = false;


        this.inventory = {
          productoId: product.productoId,
          cantidadNueva: product.cantidadDisponible
        }

      },
      error: () => {
        this.errorMessage = 'No se pudo cargar la información del producto.';
        this.isLoading = false;
      }
    });

    this.productService.getProductById(productId).subscribe({
      next: (product) => {
        this.producto = product;
      }
    });
  }


  // -------------------------------------------------------------
  //  Compra (actualiza inventario real)
  // -------------------------------------------------------------
  purchase(): void {
    if (!this.product) return;
    if (this.quantity <= 0) return;

    if (this.quantity > this.product.cantidadDisponible) {
      this.errorMessage = 'La cantidad supera el stock disponible.';
      this.successMessage = '';
      return;
    }

    this.isUpdating = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload2 = {
      cantidad: this.quantity || 0,
      precioUnitario: this.product.precioUnitario,
    };


    this.inventoryService.updateAfterPurchase(this.productoId!, payload2).subscribe({
      next: (updatedInventory) => {
        this.inventory = updatedInventory;
        this.isUpdating = false;
        this.successMessage = 'Compra realizada y stock actualizado.';
      },
      error: () => {
        this.isUpdating = false;
        this.errorMessage = 'No se pudo completar la compra.';
      }
    });
  }

  // -------------------------------------------------------------
  //  Scroll del área de detalle
  // -------------------------------------------------------------
  onInnerScroll(event: Event): void {
    const target = event.target as HTMLElement;
    this.hasScrolled = target.scrollTop > 10;
  }
}
