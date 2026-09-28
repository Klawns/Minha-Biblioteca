import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookCardComponent } from './book-card.component';
import { BookApiService } from '../services/book-api.service';

describe('BookCardComponent', () => {
  let fixture: ComponentFixture<BookCardComponent>;
  const book = { id: '1', title: 'Livro', pdfUrl: '', coverUrl: '', status: 'PENDING' as const };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookCardComponent],
      providers: [
        {
          provide: BookApiService,
          useValue: { coverUrl: () => '/api/books/1/cover', pdfUrl: () => '/api/books/1/pdf' },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(BookCardComponent);
    fixture.componentRef.setInput('book', book);
    fixture.detectChanges();
  });
  it('renders cover URL and loading state', () => {
    const image = fixture.nativeElement.querySelector('img');
    expect(image.src).toContain('/api/books/1/cover');
    expect(fixture.componentInstance.imageLoaded()).toBe(false);
    image.dispatchEvent(new Event('load'));
    fixture.detectChanges();
    expect(fixture.componentInstance.imageLoaded()).toBe(true);
  });
  it('shows fallback after image failure', () => {
    fixture.nativeElement.querySelector('img').dispatchEvent(new Event('error'));
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.fallback').classList.contains('visible')).toBe(
      true,
    );
  });
});
