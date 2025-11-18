import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { PaginatedResult } from '../../../../shared/models/pagination.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-list.component.html'
})
export class ProductListComponent {
  private productService = inject(ProductService);
  private router = inject(Router);

  // Para el blur/sombra del encabezado
  hasScrolled = false;

  products: Product[] = [];
  isLoading = false;
  errorMessage = '';
  page = 0;
  pageSize = 10;
  totalItems = 0;

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    debugger
    this.productService.getProducts(this.page, this.pageSize).subscribe({
      next: (result: PaginatedResult<Product>) => {
        this.products = result.items;
        this.totalItems = result.totalItems;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Ocurrió un error al cargar los productos.';
        this.isLoading = false;
      }
    });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.loadProducts();
  }

  goToDetail(product: Product): void {
    this.router.navigate(['/products', product.id]);
  }

  // Scroll
  onInnerScroll(event: Event): void {
    const target = event.target as HTMLElement;
    this.hasScrolled = target.scrollTop > 10;
  }
}
