package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    // Juego
    @Query("""
            SELECT COUNT(l)
            FROM Loan l
            WHERE l.game.id = :gameId
            AND l.id <> :loanId
            AND l.startDate <= :endDate
            AND l.endDate >= :startDate
            """)
    long countOverlappingGameLoans(@Param("gameId") Long gameId, @Param("loanId") Long loanId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Cliente
    @Query("""
    SELECT COUNT(l)
    FROM Loan l
    WHERE l.client.id = :clientId
    AND l.id <> :loanId
    AND l.startDate <= :endDate
    AND l.endDate >= :startDate
""")
    long countOverlappingClientLoans(
            @Param("clientId") Long clientId,
            @Param("loanId") Long loanId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
