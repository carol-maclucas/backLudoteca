package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author ccsw
 *
 */
public interface LoanRepository extends CrudRepository<Loan, Long> {
    /**
     * Método para recuperar un listado paginado de {@link Loan}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Loan}
     */
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);

    /**
     * Método para recuperar Loans por el id de su Game
     * @return {@link Loan}
     */
    List<Loan> findByGameId(Long id);

    /**
     * Método para recuperar Loans por el id de su Customer
     * @return {@link Loan}
     */
    List<Loan> findByClientId(Long id);
}
