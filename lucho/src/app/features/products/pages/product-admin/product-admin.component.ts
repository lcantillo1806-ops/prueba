import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { InventoryService } from '../../services/inventory.service';

@Component({
  selector: 'app-product-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-admin.component.html'
})
export class ProductAdminComponent {

  private productService = inject(ProductService);
  private inventarioService = inject(InventoryService);

  products: Product[] = [];
  hasScrolled = false;

  editingProduct: Product | null = null;

  form: { id?: string; nombre: string; descripcion: string; precio: number | null, cantidad?: number | null } = {
    nombre: '',
    descripcion: '',
    precio: null
  };

  isCreating = true;
  isModalOpen = false;

  // Paginación
  page = 0;
  pageSize = 5;
  totalItems = 0;

  ngOnInit(): void {
    this.loadProducts();
  }

  // --------------------------------------------------------------------------------
  //  Cargar productos desde API
  // --------------------------------------------------------------------------------
  loadProducts(): void {
    this.productService.getProducts(this.page, this.pageSize).subscribe({
      next: (resp) => {
        this.products = resp.items;
        this.totalItems = resp.totalItems;
      },
      error: () => {
        console.error('Error cargando productos');
      }
    });
  }

  // --------------------------------------------------------------------------------
  //  Paginación
  // --------------------------------------------------------------------------------
  get totalPages(): number {
    return this.totalItems ? Math.ceil(this.totalItems / this.pageSize) : 1;
  }

  get paginatedProducts(): Product[] {
    return this.products;
  }

  onPageChange(newPage: number): void {
    if (newPage < 1 || newPage > this.totalPages) return;
    this.page = newPage;
    this.loadProducts();  // ← recarga desde API
  }

  // Scroll del contenedor
  onInnerScroll(event: Event): void {
    const target = event.target as HTMLElement;
    this.hasScrolled = target.scrollTop > 10;
  }

  // --------------------------------------------------------------------------------
  // Modal / Form
  // --------------------------------------------------------------------------------
  private getEmptyForm() {
    return { id: undefined, nombre: '', descripcion: '', precio: null, cantidad: undefined };
  }

  private resetForm(): void {
    this.isCreating = true;
    this.editingProduct = null;
    this.form = this.getEmptyForm();
  }

  openCreateModal(): void {
    this.isCreating = true;
    this.editingProduct = null;
    this.form = this.getEmptyForm();
    this.isModalOpen = true;
  }

  openEditModal(product: Product): void {
    this.isCreating = false;
    this.editingProduct = product;
    this.form = { ...product };
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetForm();
  }

  // --------------------------------------------------------------------------------
  //  Guardar (crear o actualizar)
  // --------------------------------------------------------------------------------
  save() {

    debugger
    if (!this.form.nombre || this.form.precio == null) return;

    if (this.isCreating) {
      const payload = {
        nombre: this.form.nombre,
        descripcion: this.form.descripcion,
        precio: this.form.precio,
        disponible: true
      };

      this.productService.createProduct(payload).subscribe({
        next: () => {
          this.closeModal();
          this.loadProducts();
        }
      });

    } else if (this.editingProduct) {
      const payload = {
        nombre: this.form.nombre,
        descripcion: this.form.descripcion,
        precio: this.form.precio,
        disponible: true
      };
      const payload2 = {
        cantidad: this.form.cantidad || 0,
        precioUnitario: this.form.precio,
      };

      this.productService.updateProduct(this.editingProduct.id!, payload).subscribe({
        next: () => {
          this.closeModal();
          this.loadProducts();
        }
      }); 
      this.inventarioService.postIngresoInventory(this.editingProduct.id!, payload2).subscribe({}); 
    }
  }

  // --------------------------------------------------------------------------------
  //  Eliminar
  // --------------------------------------------------------------------------------
  delete(product: Product): void {
    if (!confirm(`¿Eliminar el producto "${product.nombre}"?`)) return;

    this.productService.deleteProduct(product.id!).subscribe({
      next: () => this.loadProducts()
    });
  }
}
