import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { Book } from '../models/book.models';
import { BookAction, BookActionsListComponent } from './book-actions-list.component';

@Component({
  selector: 'app-book-actions-sheet',
  imports: [BookActionsListComponent],
  templateUrl: './book-actions-sheet.component.html',
  styleUrl: './book-actions-sheet.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookActionsSheetComponent {
  readonly data = inject<Book>(MAT_BOTTOM_SHEET_DATA);
  private readonly ref = inject(MatBottomSheetRef<BookActionsSheetComponent>);
  select(action: BookAction): void {
    this.ref.dismiss(action);
  }
}
