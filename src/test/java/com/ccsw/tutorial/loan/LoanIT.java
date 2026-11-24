package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class LoanIT {
    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    public static final Long DELETE_LOAN_ID = 1L;
    public static final Long MODIFY_LOAN_ID = 1L;

    private static final int TOTAL_LOANS = 6;
    private static final int PAGE_SIZE = 5;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    //------------- PAGINADO ------------
    // Primera página
    @Test
    public void findFirstPageWithFiveSizeShouldReturnFirstFiveResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    // Segunda página
    @Test
    public void findSecondPageWithFiveSizeShouldReturnLastResults() {

        int elementsCount = TOTAL_LOANS - PAGE_SIZE;

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(1, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(elementsCount, response.getBody().getContent().size());
    }

    //-------------- LISTA de PRÉSTAMOS ------------
    // Sin filtros
    @Test
    public void findWithoutFiltersShouldReturnAllLoansInDB() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(TOTAL_LOANS, response.getBody().getContent().size());
    }

    // Con filtro:- Juegos
    @Test
    public void findWithGameFilterShouldReturnOnlyMatchingLoans() {
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setGameId(1L);
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(2, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getContent().size());
    }

    // Con filtro:- Clientes
    @Test
    public void findWithExistsClientIdShouldReturnLoansOfClient() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setClientId(1L);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(3, response.getBody().getTotalElements());
    }

    // Con filtro:- Juego + Cliente
    @Test
    public void findWithGameAndClientFilterShouldReturnOneResult() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setGameId(1L);
        searchDto.setClientId(1L);
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(2, response.getBody().getTotalElements());
    }

    // Con filtro:- Clientes + Fecha
    @Test
    public void findWithClientAndDateFilterShouldReturnTwoResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setClientId(1L);
        searchDto.setDate(LocalDate.of(2020, 1, 3));
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(2, response.getBody().getTotalElements());
    }

    //-------------- CREACIÓN de PRÉSTAMOS ------------
    @Test
    public void saveWithoutIdShouldCreateNewLoan() {
        long newLoanId = TOTAL_LOANS + 1;
        long newLoanSize = TOTAL_LOANS + 1;

        LoanDto dto = new LoanDto();
        ClientDto clientDto = new ClientDto();
        GameDto gameDto = new GameDto();

        clientDto.setId(1L);
        gameDto.setId(3L);

        dto.setClient(clientDto);
        dto.setGame(gameDto);
        dto.setStartDate(LocalDate.parse("2026-11-05"));
        dto.setEndDate(LocalDate.of(2026, 11, 12));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, (int) newLoanSize));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }

    //-------------- MODIFICACIÓN de JUEGOS ------------
    @Test
    public void modifyWithExistIdShouldModifyLoan() {

        LoanDto dto = new LoanDto();
        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        GameDto gameDto = new GameDto();
        gameDto.setId(1L);

        dto.setId(MODIFY_LOAN_ID);
        dto.setClient(clientDto);
        dto.setGame(gameDto);
        dto.setStartDate(LocalDate.of(2020, 1, 15));
        dto.setEndDate(LocalDate.of(2020, 1, 20));

        ResponseEntity<Void> saveResponse = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_LOAN_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());

        LoanDto loan = response.getBody().getContent().stream().filter(item -> item.getId().equals(MODIFY_LOAN_ID)).findFirst().orElse(null);
        assertNotNull(loan);
        assertEquals(LocalDate.of(2020, 1, 15), loan.getStartDate());
        assertEquals(LocalDate.of(2020, 1, 20), loan.getEndDate());
    }

    //------------- ELIMINACIÓN de PRÉSTAMOS ------------
    // Con Id existente
    @Test
    public void deleteWithExistsIdShouldDeleteLoan() {
        long newLoanSize = TOTAL_LOANS - 1;

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_LOAN_ID, HttpMethod.DELETE, null, Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }

    // Sin Id existente
    @Test
    public void deleteWithNoExistsIdShouldThrowException() {
        long deleteLoanId = TOTAL_LOANS + 1;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + deleteLoanId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ------------- PROBAR ERRORES -------------
    // Con filtro :- Fecha incorrecta
    @Test
    public void findWithDateFilterShouldReturnZeroResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setDate(LocalDate.of(2020, 1, 20));
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(0, response.getBody().getTotalElements());
    }

    // Rango de fechas de más 14 días
    @Test
    public void saveWhenLoanMoreThan14DaysShouldReturnFail() {
        LoanDto dto = new LoanDto();
        GameDto game = new GameDto();
        game.setId(6L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setStartDate(LocalDate.of(2020, 1, 1));
        dto.setEndDate(LocalDate.of(2020, 1, 25));

        ResponseEntity<String> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Cuando un Juego ya está prestado
    @Test
    public void saveWhenGameIsLoanedInTheSameDateShouldReturnFail() {
        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(2L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setStartDate(LocalDate.of(2020, 1, 2));
        dto.setEndDate(LocalDate.of(2020, 1, 5));

        ResponseEntity<String> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    // El cliente tiene más de 2 préstamos durante esas fechas
    @Test
    public void saveShouldFailWhenClientHasMoreThanTwoLoansInSamePeriod() {
        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(4L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setStartDate(LocalDate.of(2020, 1, 3));
        dto.setEndDate(LocalDate.of(2020, 1, 7));

        ResponseEntity<String> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    // Comprobar creación si no existe el juego
    @Test
    public void saveWithNotExistingGameShouldFail() {
        LoanDto dto = new LoanDto();

        GameDto gameDto = new GameDto();
        gameDto.setId(67L);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);

        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setStartDate(LocalDate.of(2020, 1, 15));
        dto.setEndDate(LocalDate.of(2020, 1, 20));

        ResponseEntity<String> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Comprobar creación si no existe el cliente
    @Test
    public void saveWithNotExistingClientShouldFail() {
        LoanDto dto = new LoanDto();

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(67L);

        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setStartDate(LocalDate.of(2020, 1, 15));
        dto.setEndDate(LocalDate.of(2020, 1, 20));

        ResponseEntity<String> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
