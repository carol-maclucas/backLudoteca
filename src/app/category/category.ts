import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Category } from './model/category';
//import { CATEGORY_DATA } from './model/mock-categories';

@Injectable({
    providedIn: 'root'
})
export class CategoryService {
    constructor() { }

    protected readonly http = inject(HttpClient);
    private baseUrl = 'http://localhost:8080/category';
    // Configuramos "app.config.ts" para que la URL base de la API sea configurable
    // Permite cambiar la URL de la API sin tener que modificar el código fuente del servicio

    //mockear datos de Categorias
    getCategories(): Observable<Category[]> {
        //return of(CATEGORY_DATA);
        return this.http.get<Category[]>(this.baseUrl);
    }

    //EDITAR CATEGORIA
    saveCategory(category: Category): Observable<Category> {
        const {id} = category;
        // Si el id es nulo o indefinido crea una nueva 
        const url = id ? `${this.baseUrl}/${id}` : this.baseUrl;
        return this.http.put<Category>(url, category);
    }

    //ELIMINAR CATEGORIA
    // Lo de dentro de Observable<any> es el tipo de dato que devuelve la función, 
    // en este caso un observable de cualquier tipo
    deleteCategory(idCategory: number): Observable<any> { 
       return this.http.delete(`${this.baseUrl}/${idCategory}`)
    }

    getAllCategories(): Observable<Category[]> {
            return this.http.get<Category[]>(this.baseUrl);
    }
}