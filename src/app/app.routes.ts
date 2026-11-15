import { Routes } from '@angular/router';

export const routes: Routes = [
    //lazy loading -> category list page -> STANDLONE COMPONENTS
    { path: 'categories', 
        loadComponent: () => import('./category/category-list/category-list.page').then(m => m.CategoryListPage)},
    { path: 'authors',
        loadComponent: () => import('./author/author-list/author-list.page').then(m => m.AuthorListPage)},
    ];