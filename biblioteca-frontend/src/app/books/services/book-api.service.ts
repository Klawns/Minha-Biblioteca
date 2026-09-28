import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  Book,
  BookCreateRequest,
  BookListQuery,
  BookListResponse,
  BookUpdateRequest,
} from '../models/book.models';

@Injectable({ providedIn: 'root' })
export class BookApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl.replace(/\/$/, '');

  list(query: BookListQuery = {}): Observable<BookListResponse> {
    let params = new HttpParams();
    if (query.page !== undefined) params = params.set('page', query.page);
    if (query.size !== undefined) params = params.set('size', query.size);
    if (query.title?.trim()) params = params.set('title', query.title.trim());
    if (query.status) params = params.set('status', query.status);
    return this.http.get<BookListResponse>(`${this.baseUrl}/books`, { params });
  }
  get(id: string): Observable<ApiResponse<Book>> {
    return this.http.get<ApiResponse<Book>>(`${this.baseUrl}/books/${id}`);
  }
  create(request: BookCreateRequest): Observable<ApiResponse<Book>> {
    return this.http.post<ApiResponse<Book>>(`${this.baseUrl}/books`, request);
  }
  upload(file: File): Observable<ApiResponse<Book>> {
    const form = new FormData();
    form.append('file', file, file.name);
    return this.http.post<ApiResponse<Book>>(`${this.baseUrl}/books/upload`, form);
  }
  update(id: string, request: BookUpdateRequest): Observable<ApiResponse<Book>> {
    return this.http.patch<ApiResponse<Book>>(`${this.baseUrl}/books/${id}`, request);
  }
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/books/${id}`);
  }
  resolveMediaUrl(url: string): string {
    if (/^https?:\/\//i.test(url)) return url;
    return `${this.baseUrl}/${url.replace(/^\/+/, '')}`;
  }
  coverUrl(book: Pick<Book, 'id' | 'coverUrl'>): string {
    return this.resolveMediaUrl(`/books/${book.id}/cover`);
  }
  pdfUrl(book: Pick<Book, 'id' | 'pdfUrl'>): string {
    return this.resolveMediaUrl(`/books/${book.id}/pdf`);
  }
  errorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) return error.error?.error ?? error.message;
    return 'Não foi possível concluir a operação.';
  }
}
