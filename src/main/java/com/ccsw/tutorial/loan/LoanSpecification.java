package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.loan.model.Loan;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class LoanSpecification implements Specification<Loan> {
    private static final long serialVersionUID = 1L;
    private final SearchCriteria criteria;

    public LoanSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Loan> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        // Comparacion ":"
        if (criteria.getOperation().equalsIgnoreCase(":") && criteria.getValue() != null) {
            Path<String> path = getPath(root);
            // Predicate
            if (path.getJavaType() == String.class) {
                // Si es texto
                return builder.like(path, "%" + criteria.getValue() + "%");
            } else {
                // Si es número/fecha
                return builder.equal(path, criteria.getValue());
            }
        }

        // Comparación "<="
        if (criteria.getOperation().equalsIgnoreCase("<=") && criteria.getValue() != null) {
            Path<LocalDate> path = root.get(criteria.getKey());
            return builder.lessThanOrEqualTo(path, (LocalDate) criteria.getValue());
        }

        // Comparación ">="
        if (criteria.getOperation().equalsIgnoreCase(">=") && criteria.getValue() != null) {
            Path<LocalDate> path = root.get(criteria.getKey());
            return builder.greaterThanOrEqualTo(path, (LocalDate) criteria.getValue());
        }
        return null;
    }

    // Para mirar las sub entidades
    private Path<String> getPath(Root<Loan> root) {
        String key = criteria.getKey();
        String[] split = key.split("[.]", 0);

        Path<String> expression = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            expression = expression.get(split[i]);
        }
        return expression;
    }
}