import { TestBed } from '@angular/core/testing';
import { BookFeedComponent } from './book-feed.component';
import { BookStore } from '../store/book.store';
import { provideRouter } from '@angular/router';

describe('BookFeedComponent', () => { it('creates and loads books', async () => { const store = { load: vi.fn(), error: () => null, loading: () => false, isEmpty: () => true, books: () => [], metadata: () => ({ page: 0, size: 12, totalElements: 0, totalPages: 0 }), query: () => ({}) }; await TestBed.configureTestingModule({ imports: [BookFeedComponent], providers: [provideRouter([]), { provide: BookStore, useValue: store }] }).compileComponents(); const fixture = TestBed.createComponent(BookFeedComponent); fixture.detectChanges(); expect(store.load).toHaveBeenCalled(); }); });
