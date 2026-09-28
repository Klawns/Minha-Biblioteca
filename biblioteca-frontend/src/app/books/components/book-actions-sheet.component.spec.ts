import { TestBed } from '@angular/core/testing';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { BookActionsSheetComponent } from './book-actions-sheet.component';

describe('BookActionsSheetComponent', () => {
  it('creates and dismisses with action', async () => {
    const ref = { dismiss: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [BookActionsSheetComponent],
      providers: [
        {
          provide: MAT_BOTTOM_SHEET_DATA,
          useValue: { id: '1', title: 'Livro', status: 'PENDING' },
        },
        { provide: MatBottomSheetRef, useValue: ref },
      ],
    }).compileComponents();
    TestBed.createComponent(BookActionsSheetComponent).componentInstance.select('status');
    expect(ref.dismiss).toHaveBeenCalledWith('status');
  });
});
