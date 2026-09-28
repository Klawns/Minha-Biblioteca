import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookActionsListComponent } from './book-actions-list.component';

describe('BookActionsListComponent', () => {
  let fixture: ComponentFixture<BookActionsListComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookActionsListComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(BookActionsListComponent);
    fixture.componentRef.setInput('book', {
      id: '1',
      title: 'Livro',
      pdfUrl: '',
      coverUrl: '',
      status: 'PENDING',
    });
    fixture.detectChanges();
  });
  it('creates', () => expect(fixture.componentInstance).toBeTruthy());
  it('emits selected action', () => {
    const spy = vi.fn();
    fixture.componentInstance.selected.subscribe(spy);
    fixture.nativeElement.querySelectorAll('button')[1].click();
    expect(spy).toHaveBeenCalledWith('edit');
  });
});
