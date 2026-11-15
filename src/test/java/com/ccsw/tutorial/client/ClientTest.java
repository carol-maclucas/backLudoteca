package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ------------- TEST UNITARIOS -----------
@ExtendWith(MockitoExtension.class)
public class ClientTest {
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private ClientServiceImpl clientService;

    //-------------- LISTA de CLIENTES ------------
    @Test
    public void findAllShouldReturnAllClients() {

        List<Client> list = new ArrayList<>();
        list.add(mock(Client.class));

        when(clientRepository.findAll()).thenReturn(list);

        List<Client> clients = clientService.findAll();

        // Como añado un cliente, compruebo que el tamaño es 1
        assertNotNull(clients);
        assertEquals(1, clients.size());
    }

    //-------------- CREACIÓN de CLIENT ------------
    public static final String CLIENT_NAME = "Cliente_1";

    @Test
    public void saveNotExistsClientIdShouldInsert() {
        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        // Para poder usar el get(), set()...
        ArgumentCaptor<Client> client = ArgumentCaptor.forClass(Client.class);

        clientService.save(null, clientDto);
        // Comprobar que se llamó al "save()" del repositorio
        // "capture()" guarda lo que se le ha pasado
        verify(clientRepository).save(client.capture());
        assertEquals(CLIENT_NAME, client.getValue().getName());
    }

    //-------------- MODIFICACIÓN de CLIENTES ------------
    public static final Long EXISTS_CLIENT_ID = 1L;

    @Test
    public void saveExistsClientIdShouldUpdate() {
        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        Client client = mock(Client.class);

        // Cuando se busque el "id", devuelve un cliente existente
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.save(EXISTS_CLIENT_ID, clientDto);
        verify(clientRepository).save(client);
    }

    //-------------- ELIMINACIÓN de CLIENTES ------------
    @Test
    public void deleteExistsClientIdShouldDelete() throws Exception {
        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.delete(EXISTS_CLIENT_ID);
        verify(clientRepository).deleteById(EXISTS_CLIENT_ID);
    }

    //-------------- LLAMAR a CLIENTES ------------
    public static final Long NOT_EXISTS_CLIENT_ID = 0L;

    // Con Id
    @Test
    public void getExistsClientIdShouldReturnClient() {

        Client client = mock(Client.class);
        when(client.getId()).thenReturn(EXISTS_CLIENT_ID);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        Client clientResponse = clientService.get(EXISTS_CLIENT_ID);

        assertNotNull(clientResponse);
        assertEquals(EXISTS_CLIENT_ID, client.getId());
    }

    // Sin Id
    @Test
    public void getNotExistsClientIdShouldReturnNull() {
        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        Client client = clientService.get(NOT_EXISTS_CLIENT_ID);

        assertNull(client);
    }
}