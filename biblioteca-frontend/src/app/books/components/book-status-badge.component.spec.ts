import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookStatusBadgeComponent } from './book-status-badge.component';

describe('BookStatusBadgeComponent', () => {
  let fixture: ComponentFixture<BookStatusBadgeComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookStatusBadgeComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(BookStatusBadgeComponent);
    fixture.componentRef.setInput('status', 'COMPLETED');
    fixture.detectChanges();
  });
  it('creates and renders completed status', () => {
    expect(fixture.componentInstance).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Concluído');
  });
});
