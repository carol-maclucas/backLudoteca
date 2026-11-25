package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ClientRepository clientRepository;

    private static final int MAX_LOAN_DAYS = 14;
    private static final int MAX_LOAN_PER_CLIENT = 2;

    /**
     * {@inheritDoc}
     */
    @Override
    public Loan get(Long id) {
        return this.loanRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Loan> findAll() {
        return (List<Loan>) this.loanRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Page<Loan> findPage(LoanSearchDto dto) {
        //
        Specification<Loan> spec = Specification.where(null);

        if (dto.getGameId() != null) {
            LoanSpecification gameSpec = new LoanSpecification(new SearchCriteria("game.id", ":", dto.getGameId()));
            spec = spec.and(gameSpec);
        }

        if (dto.getClientId() != null) {
            LoanSpecification clientSpec = new LoanSpecification(new SearchCriteria("client.id", ":", dto.getClientId()));
            spec = spec.and(clientSpec);
        }

        if (dto.getDate() != null) {
            LoanSpecification startDateSpec = new LoanSpecification(new SearchCriteria("startDate", "<=", dto.getDate()));
            LoanSpecification endDateSpec = new LoanSpecification(new SearchCriteria("endDate", ">=", dto.getDate()));
            spec = spec.and(startDateSpec).and(endDateSpec);
        }

        return this.loanRepository.findAll(spec, dto.getPageable().getPageable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, LoanDto dto) {
        Loan loan;
        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id).orElse(null);
        }

        if (loan == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan does not exist");
        }
        // BeanUtils:- Para copiar más de un dato de una clase a otra
        BeanUtils.copyProperties(dto, loan, "id", "game", "client");

        if (dto.getStartDate() == null || dto.getEndDate() == null || !dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango de fechas del préstamo no es válido.");
        }

        // Miramos si el rango de fechas es correcto (máximo 14 días + fecha de inicio > fecha de devolución)
        long diffDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        if (diffDays > MAX_LOAN_DAYS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El préstamo no puede superar los 14 días");
        }
        loan.setStartDate(dto.getStartDate());
        loan.setEndDate(dto.getEndDate());

        // -------------------------------------------------------------
        // Validamos el cliente y el juego
        if (dto.getClient() == null || dto.getClient().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente es obligatorio");
        }

        if (dto.getGame() == null || dto.getGame().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El juego es obligatorio");
        }

        // Guardamos el cliente y el juego con el préstamo
        Client client = this.clientRepository.findById(dto.getClient().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        Game game = this.gameRepository.findById(dto.getGame().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        loan.setClient(client);
        loan.setGame(game);
        // ------------------------------------------------------------

        // Además, un JUEGO no puede estar prestado a dos clientes distintos un mismo día
        long overlapCount = loanRepository.countOverlappingGameLoans(dto.getGame().getId(), id == null ? -1L : id, dto.getStartDate(), dto.getEndDate());

        if (overlapCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El juego ya está prestado en esas fechas escogidas");
        }

        // Y un CLIENTE no pue tener más de un préstamo a la vez
        long overlappingLoans = loanRepository.countOverlappingClientLoans(dto.getClient().getId(), id == null ? -1L : id, dto.getStartDate(), dto.getEndDate());

        if (overlappingLoans >= MAX_LOAN_PER_CLIENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente ya tiene dos préstamos simultáneos");
        }
        this.loanRepository.save(loan);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {
        if (this.get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan does not exist");
        }
        this.loanRepository.deleteById(id);
    }
}


