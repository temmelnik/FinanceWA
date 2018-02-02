package com.finn.service;

import com.finn.domain.Currency;
import com.finn.domain.Statistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public interface FinanceService {
    Statistics getPriceOfCurrency(Currency currency);
    List<Statistics> getPriceOfCurrency(Set<Currency> currencySet);
    LocalDate dateOfLastUpdate();
}
