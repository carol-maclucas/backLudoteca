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
import org.springframework.stereotype.Service;

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
            throw new RuntimeException("Loan does not exist");
        }
        // BeanUtils:- Para copiar más de un dato de una clase a otra
        BeanUtils.copyProperties(dto, loan, "id", "game", "client");

        if (dto.getStartDate() == null || dto.getEndDate() == null || dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new RuntimeException("El rango de fechas del préstamo no es válido.");
        }

        // Miramos si el rango de fechas es correcto (máximo 14 días + fecha de inicio > fecha de devolución)
        long diffDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        if (diffDays > 14) {
            throw new RuntimeException("El préstamo no puede superar los 14 días");
        }
        loan.setStartDate(dto.getStartDate());
        loan.setEndDate(dto.getEndDate());

        // Guardamos el cliente y el juego con el préstamo
        Client client = this.clientRepository.findById(dto.getClient().getId()).orElse(null);
        Game game = this.gameRepository.findById(dto.getGame().getId()).orElse(null);

        loan.setClient(client);
        loan.setGame(game);

        // Además, un juego no puede estar prestado a dos clientes distintos un mismo día
        List<Loan> loans = loanRepository.findByGame(dto.getGame().getId());
        for (Loan existingloan : loans) {
            if (id != null && existingloan.getId() == id) {
                continue;
            }
            boolean overlap = !dto.getEndDate().isBefore(existingloan.getStartDate()) && !dto.getStartDate().isAfter(existingloan.getEndDate());
            if (overlap) {
                throw new RuntimeException("El juego ya está prestado en esas fechas escogidas");
            }
        }

        // Y un cliente no pue tener más de un préstamo a la vez
        List<Loan> clientLoans = loanRepository.findByClient(dto.getClient().getId());

        int overlappingLoans = 0;
        for (Loan existingLoan : clientLoans) {
            if (id != null && existingLoan.getId() == id) {
                continue;
            }
            boolean overlap = !dto.getEndDate().isBefore(existingLoan.getStartDate()) && !dto.getStartDate().isAfter(existingLoan.getEndDate());
            if (overlap) {
                overlappingLoans++;
            }
        }
        if (overlappingLoans >= 2) {
            throw new RuntimeException("El cliente ya tiene dos préstamos simultáneos");
        }

        this.loanRepository.save(loan);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {
        if (this.get(id) == null) {
            throw new RuntimeException("Loan no existe");
        }
        this.loanRepository.deleteById(id);
    }
}


