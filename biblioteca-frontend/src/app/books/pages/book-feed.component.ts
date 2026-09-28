import { BreakpointObserver } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { RouterLink } from '@angular/router';
import { Book } from '../models/book.models';
import { BookActionsDialogComponent } from '../components/book-actions-dialog.component';
import { BookActionsSheetComponent } from '../components/book-actions-sheet.component';
import { BookAction } from '../components/book-actions-list.component';
import { BookCardComponent } from '../components/book-card.component';
import { BookEditDialogComponent } from '../components/book-edit-dialog.component';
import { BookStore } from '../store/book.store';

@Component({ selector: 'app-book-feed', imports: [MatButtonModule, MatIconModule, MatPaginatorModule, RouterLink, BookCardComponent], templateUrl: './book-feed.component.html', styleUrl: './book-feed.component.css', changeDetection: ChangeDetectionStrategy.OnPush })
export class BookFeedComponent implements OnInit {
  readonly store = inject(BookStore); private readonly dialog = inject(MatDialog); private readonly sheet = inject(MatBottomSheet); private readonly breakpoint = inject(BreakpointObserver); readonly skeletons = [1, 2, 3, 4, 5, 6];
  ngOnInit(): void { this.store.load(); }
  reload(): void { this.store.load(); }
  page(event: PageEvent): void { this.store.load({ ...this.store.query(), page: event.pageIndex, size: event.pageSize }); }
  openActions(book: Book): void { if (this.breakpoint.isMatched('(max-width: 699px)')) this.sheet.open(BookActionsSheetComponent, { data: book, panelClass: 'book-actions-sheet' }).afterDismissed().subscribe(action => this.handleAction(action, book)); else this.dialog.open(BookActionsDialogComponent, { data: book, ariaLabel: 'Ações do livro' }).afterClosed().subscribe(action => this.handleAction(action, book)); }
  private handleAction(action: BookAction | undefined, book: Book): void { if (action === 'status') this.store.toggleStatus(book); if (action === 'delete' && confirm(`Apagar “${book.title}”?`)) this.store.remove(book.id); if (action === 'edit') this.dialog.open(BookEditDialogComponent, { data: book, ariaLabel: 'Editar informações do livro' }).afterClosed().subscribe(result => { if (result && Object.keys(result).length) this.store.update(book.id, result); }); }
}
