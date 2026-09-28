import { ChangeDetectionStrategy, Component, inject, input, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Book } from '../models/book.models';
import { BookApiService } from '../services/book-api.service';
import { BookStatusBadgeComponent } from './book-status-badge.component';

@Component({
  selector: 'app-book-card',
  imports: [MatButtonModule, MatIconModule, BookStatusBadgeComponent],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookCardComponent {
  readonly book = input.required<Book>();
  readonly actions = output<void>();
  readonly api = inject(BookApiService);
  readonly imageFailed = signal(false);
  readonly imageLoaded = signal(false);
  onImageLoad(): void {
    this.imageLoaded.set(true);
    this.imageFailed.set(false);
  }
  onImageError(): void {
    this.imageFailed.set(true);
    this.imageLoaded.set(false);
  }
}
