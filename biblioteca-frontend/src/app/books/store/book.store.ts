import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { BookApiService } from '../services/book-api.service';
import { ApiMetadata, Book, BookListQuery, BookUpdateRequest, ReadingStatus } from '../models/book.models';

@Injectable({ providedIn: 'root' })
export class BookStore {
  private readonly api = inject(BookApiService);
  readonly books = signal<Book[]>([]);
  readonly metadata = signal<ApiMetadata>({ page: 0, size: 12, totalElements: 0, totalPages: 0 });
  readonly query = signal<BookListQuery>({ page: 0, size: 12 });
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly isEmpty = computed(() => !this.loading() && this.books().length === 0 && !this.error());

  load(query: BookListQuery = this.query()): void {
    this.query.set({ ...query }); this.loading.set(true); this.error.set(null);
    this.api.list(query).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: response => { this.books.set(response.data ?? []); this.metadata.set(response.metadata ?? { page: query.page ?? 0, size: query.size ?? 12, totalElements: response.data?.length ?? 0, totalPages: 1 }); },
      error: error => this.error.set(this.api.errorMessage(error)),
    });
  }
  toggleStatus(book: Book): void { this.update(book.id, { status: book.status === 'PENDING' ? 'COMPLETED' : 'PENDING' }); }
  update(id: string, request: BookUpdateRequest): void {
    this.saving.set(true); this.error.set(null);
    this.api.update(id, request).pipe(finalize(() => this.saving.set(false))).subscribe({ next: response => this.books.update(items => items.map(book => book.id === id ? response.data : book)), error: error => this.error.set(this.api.errorMessage(error)) });
  }
  remove(id: string): void {
    this.saving.set(true); this.api.delete(id).pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => { const meta = this.metadata(); const nextPage = this.books().length === 1 && meta.page > 0 ? meta.page - 1 : meta.page; this.load({ ...this.query(), page: nextPage }); },
      error: error => this.error.set(this.api.errorMessage(error)),
    });
  }
  upload(file: File, done: () => void): void { this.saving.set(true); this.error.set(null); this.api.upload(file).pipe(finalize(() => this.saving.set(false))).subscribe({ next: () => done(), error: error => this.error.set(this.api.errorMessage(error)) }); }
  setStatus(status: ReadingStatus | undefined): void { this.load({ ...this.query(), page: 0, status }); }
}
