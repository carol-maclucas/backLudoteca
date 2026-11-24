package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// ------------- TEST DE INTEGRACIÓN -----------
//Inicializamos el contexto de Spring
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//Test transaccionales + dejar la BBDD igual que antes
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {
    // GET del ENDPOINT:
    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/clients";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<ClientDto>> responseType = new ParameterizedTypeReference<List<ClientDto>>() {
    };

    //-------------- LISTA de CLIENTES ------------
    @Test
    public void findAllShouldReturnAllClients() {
        // GET
        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(5, response.getBody().size());
    }

    //-------------- CREACIÓN de CLIENTES ------------
    public static final Long NEW_CLIENT_ID = 6L;
    public static final Long NEW_CLIENT_ID_2 = 1L;
    public static final String NEW_CLIENT_NAME = "Cliente_6";

    @Test
    public void saveWithoutIdShouldCreateNewClient() {
        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);
        // PUT
        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(6, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream().filter(item -> item.getId().equals(NEW_CLIENT_ID)).findFirst().orElse(null);
        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());
    }

    // -- Evitando nombres duplicados --
    public static final String DUPLICATED_NAME = "Cliente 2";

    @Test
    public void saveWithoutIdAndExistsNameShouldNotCreateNewClient() {
        ClientDto dto = new ClientDto();
        dto.setName(DUPLICATED_NAME);
        // PUT
        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        assertEquals(5, response.getBody().size());
    }

    //-------------- MODIFICACIÓN de CLIENTES ------------
    public static final Long MODIFY_CLIENT_ID = 3L;

    @Test
    public void modifyWithExistIdShouldModifyClient() {
        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);
        // PUT
        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_CLIENT_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(5, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream().filter(item -> item.getId().equals(MODIFY_CLIENT_ID)).findFirst().orElse(null);
        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());
    }

    //-------------- MODIFICACIÓN de CLIENTES: probar ERROR ------------
    // Modificando un ID que no existiría
    @Test
    public void modifyWithNotExistIdShouldInternalError() {
        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);
        // PUT
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CLIENT_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    //Modificando un NOMBRE que ya existe
    @Test
    public void modifyWithExistsNameShouldInternalError() {
        ClientDto dto = new ClientDto();
        dto.setName(DUPLICATED_NAME);
        // PUT
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CLIENT_ID_2, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    //-------------- ELIMINACIÓN de CLIENTE ------------
    public static final Long DELETE_CLIENT_ID = 5L;

    // Eliminación y comprobación del tamaño
    @Test
    public void deleteWithExistsIdShouldDeleteClient() {
        // DELETE
        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_CLIENT_ID, HttpMethod.DELETE, null, Void.class);
        // GET
        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(4, response.getBody().size());
    }

    // Intentar eliminar con un ID que no existe
    @Test
    public void deleteWithNotExistsIdShouldInternalError() {
        // DELETE
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CLIENT_ID, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
