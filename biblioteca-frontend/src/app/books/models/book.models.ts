export type ReadingStatus = 'PENDING' | 'COMPLETED';

export interface Book { id: string; title: string; pdfUrl: string; coverUrl: string; status: ReadingStatus; }
export interface ApiMetadata { page: number; size: number; totalElements: number; totalPages: number; }
export interface ApiResponse<T> { data: T; metadata?: ApiMetadata | null; }
export interface ApiErrorResponse { error: string; }
export interface BookCreateRequest { title: string; pdfPath?: string; coverPath?: string; }
export interface BookUpdateRequest { title?: string; status?: ReadingStatus; }
export interface BookUploadRequest { file: File; }
export interface BookListQuery { page?: number; size?: number; title?: string; status?: ReadingStatus; }
export type BookListResponse = ApiResponse<Book[]>;
