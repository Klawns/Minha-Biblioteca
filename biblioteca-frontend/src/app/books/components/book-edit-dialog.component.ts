import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Book, BookUpdateRequest, ReadingStatus } from '../models/book.models';

@Component({
  selector: 'app-book-edit-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './book-edit-dialog.component.html',
  styleUrl: './book-edit-dialog.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookEditDialogComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<BookEditDialogComponent>);
  readonly data = inject<Book>(MAT_DIALOG_DATA);
  readonly form = this.formBuilder.nonNullable.group({
    title: [this.data.title, [Validators.required, Validators.minLength(1)]],
    status: [this.data.status],
  });
  save(): void {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    const title = value.title.trim();
    if (!title) return;
    const result: BookUpdateRequest = {};
    if (title !== this.data.title) result.title = title;
    if (value.status !== this.data.status) result.status = value.status as ReadingStatus;
    this.dialogRef.close(result);
  }
}
