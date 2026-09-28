import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BookActionsDialogComponent } from './book-actions-dialog.component';

describe('BookActionsDialogComponent', () => {
  it('creates and closes with action', async () => {
    const ref = { close: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [BookActionsDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { id: '1', title: 'Livro', status: 'PENDING' } },
        { provide: MatDialogRef, useValue: ref },
      ],
    }).compileComponents();
    const component = TestBed.createComponent(BookActionsDialogComponent).componentInstance;
    component.select('edit');
    expect(ref.close).toHaveBeenCalledWith('edit');
  });
});
