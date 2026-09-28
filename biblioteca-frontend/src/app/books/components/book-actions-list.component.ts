import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Book } from '../models/book.models';

export type BookAction = 'status' | 'edit' | 'delete';
@Component({
  selector: 'app-book-actions-list',
  imports: [MatButtonModule, MatIconModule],
  templateUrl: './book-actions-list.component.html',
  styleUrl: './book-actions-list.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookActionsListComponent {
  readonly book = input.required<Book>();
  readonly selected = output<BookAction>();
}
