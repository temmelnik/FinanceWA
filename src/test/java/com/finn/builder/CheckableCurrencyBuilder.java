package com.finn.builder;

import com.finn.domain.CheckableCurrency;
import com.finn.domain.Currency;
import com.finn.domain.User;

import java.time.LocalDate;


public class CheckableCurrencyBuilder {

    private User user;
    private Currency currency;
    private int period;
    private LocalDate lastUpdate;
    private Long id;

    public CheckableCurrencyBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public CheckableCurrencyBuilder setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public CheckableCurrencyBuilder setPeriod(int period) {
        this.period = period;
        return this;
    }

    public CheckableCurrencyBuilder setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CheckableCurrency createCheckableCur() {
        CheckableCurrency checkableCurrency = new CheckableCurrency(user, currency, period, lastUpdate);
        checkableCurrency.setId(id);
        return checkableCurrency;
    }

    public CheckableCurrency createDefaultCheckableCurUSD(){
        CheckableCurrency checkableCurrency = new CheckableCurrency(user, Currency.USD, 1, LocalDate.now());
        checkableCurrency.setId(1L);
        return checkableCurrency;
    }

    public CheckableCurrency createDefaultCheckableCurBGN(){
        CheckableCurrency checkableCurrency = new CheckableCurrency(user, Currency.BGN, 1, LocalDate.now());
        checkableCurrency.setId(2L);
        return checkableCurrency;
    }
}