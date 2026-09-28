import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ReadingStatus } from '../models/book.models';

@Component({
  selector: 'app-book-status-badge',
  templateUrl: './book-status-badge.component.html',
  styleUrl: './book-status-badge.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookStatusBadgeComponent {
  readonly status = input.required<ReadingStatus>();
}
