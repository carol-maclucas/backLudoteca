import { Component, inject, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Clients } from '../model/Clients';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ClientsService } from '../clients';
import { MatDialog } from '@angular/material/dialog';
import { ClientsEdit } from '../clients-edit/clients-edit';
import { DialogConfirmation } from '../../core/dialog-confirmation/dialog-confirmation';

@Component({
  selector: 'app-clients-list',
  standalone: true,
  imports: [  
    CommonModule,
    MatTableModule,
    MatIconModule,
    MatButtonModule],
  
  styleUrl: './clients-list.page.scss',
  templateUrl: './clients-list.page.html',
})
export class ClientsListPage implements OnInit {
  dataSource = new MatTableDataSource<Clients>();
  displayedColumns: string[] = ['id', 'name', 'action'];

  // Inyección
  protected readonly clientsService = inject(ClientsService);
  protected readonly dialog = inject(MatDialog);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.clientsService.getClients().subscribe(
      clients => {
        this.dataSource.data = clients;
      }
    );
  }

  // Crear cliente
    createClient() {    
      // Abrir el diálogo para crear una nueva categoría
      const dialogRef = this.dialog.open(ClientsEdit, {
        data: {}
      });

      dialogRef.afterClosed().subscribe(result => {
        if(!result) return; // Si no hay resultado, no hacer nada

        this.loadData();
      });
    }   

    // Editar cliente
    editClient(clients: Clients) {
      const dialogRef = this.dialog.open(ClientsEdit, {
        data: { clients }
      });
      dialogRef.afterClosed().subscribe(result => {
        if(!result) return;
        this.loadData();
      });
    }    

    // Eliminar cliente
    deleteClient(clients: Clients) {    
      const dialogRef = this.dialog.open(DialogConfirmation, {
        data: { title: "Eliminar cliente", 
        description: "Atención si borra el cliente se perderán sus datos.<br> ¿Desea eliminar el cliente?" }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.clientsService.deleteClient(clients.id).subscribe(result => {
            this.loadData();
          }); 
        }
      });    
    }
}
