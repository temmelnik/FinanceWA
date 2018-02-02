package com.finn.repository;

import com.finn.domain.CheckableCurrency;
import com.finn.domain.Currency;
import com.finn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CheckableRepository extends JpaRepository<CheckableCurrency, Long> {
    CheckableCurrency findByUserAndCurrency(User user, Currency currency);
}
