import { Component, OnInit, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ClientsService } from '../clients';
import { Clients } from "../model/Clients";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  selector: 'app-clients-edit',
  standalone: true,
  styleUrl: './clients-edit.scss',
  templateUrl: './clients-edit.html',
})
export class ClientsEdit implements OnInit {
  // Inyección
  protected readonly dialogRef = inject(MatDialogRef<ClientsEdit>);
  protected readonly data = inject(MAT_DIALOG_DATA) as { client: Clients };
  protected readonly clientsService = inject(ClientsService);

  protected readonly id = signal<number | null>(null);
  protected readonly name = signal<string | null>(null);

  // Al iniciar, se cargan los datos del formulario
  ngOnInit(): void {
    this.loadFormData(this.data.client ?? null);
  }

  // Cargar datos
  loadFormData(initialData: Clients | null): void {
    this.id.set(initialData?.id ?? null);
    this.name.set(initialData?.name ?? null);
  }

  // Guardar datos
  onSave() {
    const id = this.id();
    const name = this.name();
    if(!name) { // Si el nombre es nulo, no hacer nada
      return;
    }

    const client = { id, name } as Clients;
    
    this.clientsService.saveClient(client).subscribe(() => {
    this.dialogRef.close(true);
    });
  }
  
  onClose() {
      this.dialogRef.close();
  }
  
}
