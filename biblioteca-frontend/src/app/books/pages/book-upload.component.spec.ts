import { validatePdf, MAX_PDF_SIZE } from './book-upload.component';

describe('book upload validation', () => {
  it('accepts a PDF under 60 MB', () => {
    expect(validatePdf(new File(['pdf'], 'book.pdf', { type: 'application/pdf' }))).toBeNull();
  });
  it('rejects wrong extension or MIME', () => {
    expect(validatePdf(new File(['x'], 'book.txt', { type: 'text/plain' }))).toContain('PDF');
  });
  it('rejects files above the limit', () => {
    const file = new File([new Uint8Array(MAX_PDF_SIZE + 1)], 'book.pdf', { type: 'application/pdf' });
    expect(validatePdf(file)).toContain('60 MB');
  });
});
