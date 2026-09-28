import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'books', loadComponent: () => import('./books/pages/book-feed.component').then(m => m.BookFeedComponent) },
  { path: 'books/new', loadComponent: () => import('./books/pages/book-upload.component').then(m => m.BookUploadComponent) },
  { path: '', pathMatch: 'full', redirectTo: 'books' },
  { path: '**', redirectTo: 'books' },
];
