import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { BookApiService } from './book-api.service';

describe('BookApiService', () => {
  let api: BookApiService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BookApiService, provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(BookApiService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('uses API paths and resolves absolute media URLs', () => {
    api.list({ page: 1, size: 8 }).subscribe();
    const request = http.expectOne((r) => r.url === '/api/books');
    expect(request.request.params.get('page')).toBe('1');
    request.flush({ data: [] });
    expect(api.resolveMediaUrl('https://cdn.example/cover')).toBe('https://cdn.example/cover');
    expect(api.coverUrl({ id: 'x', coverUrl: '' })).toBe('/api/books/x/cover');
  });
  it('uploads, updates and deletes', () => {
    const file = new File(['pdf'], 'book.pdf', { type: 'application/pdf' });
    api.upload(file).subscribe();
    http.expectOne('/api/books/upload').flush({ data: {} });
    api.update('x', { title: 'Novo' }).subscribe();
    http.expectOne('/api/books/x').flush({ data: {} });
    api.delete('x').subscribe();
    http.expectOne('/api/books/x').flush(null);
  });
});
