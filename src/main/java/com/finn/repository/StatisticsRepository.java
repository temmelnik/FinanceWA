package com.finn.repository;

import com.finn.domain.Currency;
import com.finn.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findByDateBetweenAndCurrencyOrderByDate(LocalDate bot, LocalDate top, Currency currency);
    List<Statistics> findByDateAfterAndCurrencyOrderByDate(LocalDate after, Currency currency);
    List<Statistics> findByDateBeforeAndCurrencyOrderByDate(LocalDate before, Currency currency);
    Statistics findByDateAndCurrency(LocalDate date, Currency currency);
}
