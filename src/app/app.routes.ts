import { Routes } from '@angular/router';

//lazy loading -> category list page -> STANDLONE COMPONENTS
export const routes: Routes = [
    //Ruta por defecto /games
    { path: '', redirectTo: '/games', pathMatch: 'full'},
    { path: 'categories', 
        loadComponent: () => import('./category/category-list/category-list.page').then(m => m.CategoryListPage)},
    { path: 'authors',
        loadComponent: () => import('./author/author-list/author-list.page').then(m => m.AuthorListPage)},
    { path: 'games', 
        loadComponent: () => import('./game/game-list/game-list.page').then(m => m.GameListPage)},
    { path: 'clients', 
        loadComponent: () => import('./clients/clients-list/clients-list.page').then(m => m.ClientsListPage)}
    ];