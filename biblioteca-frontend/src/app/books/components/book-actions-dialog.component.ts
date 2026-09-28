import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Book } from '../models/book.models';
import { BookAction, BookActionsListComponent } from './book-actions-list.component';

@Component({
  selector: 'app-book-actions-dialog',
  imports: [MatDialogModule, BookActionsListComponent],
  templateUrl: './book-actions-dialog.component.html',
  styleUrl: './book-actions-dialog.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookActionsDialogComponent {
  readonly data = inject<Book>(MAT_DIALOG_DATA);
  private readonly ref = inject(MatDialogRef<BookActionsDialogComponent>);
  select(action: BookAction): void {
    this.ref.close(action);
  }
}
