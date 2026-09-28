import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { BookApiService } from '../services/book-api.service';

export const MAX_PDF_SIZE = 60 * 1024 * 1024;
export function validatePdf(file: File): string | null { if (!file.name.toLowerCase().endsWith('.pdf') || file.type.toLowerCase() !== 'application/pdf') return 'Selecione um arquivo PDF válido.'; if (file.size > MAX_PDF_SIZE) return 'O arquivo deve ter no máximo 60 MB.'; return null; }
@Component({ selector: 'app-book-upload', imports: [MatButtonModule, MatIconModule, RouterLink], templateUrl: './book-upload.component.html', styleUrl: './book-upload.component.css', changeDetection: ChangeDetectionStrategy.OnPush })
export class BookUploadComponent {
  private readonly api = inject(BookApiService); private readonly router = inject(Router); readonly selectedFile = signal<File | null>(null); readonly validationError = signal<string | null>(null); readonly serverError = signal<string | null>(null); readonly saving = signal(false);
  choose(event: Event): void { const file = (event.target as HTMLInputElement).files?.[0] ?? null; this.validationError.set(null); this.serverError.set(null); this.selectedFile.set(null); if (!file) return; const error = validatePdf(file); if (error) this.validationError.set(error); else this.selectedFile.set(file); }
  save(): void { const file = this.selectedFile(); if (!file || this.saving()) return; this.saving.set(true); this.api.upload(file).subscribe({ next: () => { this.saving.set(false); void this.router.navigate(['/books']); }, error: (error: unknown) => { this.saving.set(false); const status = error instanceof HttpErrorResponse ? error.status : 0; this.serverError.set(status === 413 ? 'O arquivo excede o limite permitido.' : status === 400 ? 'O PDF não pôde ser aceito.' : 'Não foi possível salvar o livro. Tente novamente.'); } }); }
  formatSize(bytes: number): string { return `${(bytes / 1024 / 1024).toFixed(1)} MB`; }
}
