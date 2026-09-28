import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BookEditDialogComponent } from './book-edit-dialog.component';

describe('BookEditDialogComponent', () => {
  it('returns changed title and status', async () => {
    const ref = { close: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [BookEditDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { id: '1', title: 'Livro', status: 'PENDING' } },
        { provide: MatDialogRef, useValue: ref },
      ],
    }).compileComponents();
    const component = TestBed.createComponent(BookEditDialogComponent).componentInstance;
    component.form.setValue({ title: 'Novo', status: 'COMPLETED' });
    component.save();
    expect(ref.close).toHaveBeenCalledWith({ title: 'Novo', status: 'COMPLETED' });
  });
});
