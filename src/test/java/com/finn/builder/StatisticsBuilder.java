package com.finn.builder;

import com.finn.domain.Currency;
import com.finn.domain.Statistics;

import java.math.BigDecimal;
import java.time.LocalDate;


public class StatisticsBuilder {
    private Currency currency;
    private BigDecimal price;
    private LocalDate date;

    public StatisticsBuilder setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public StatisticsBuilder setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public StatisticsBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public Statistics createStatistics() {
        return new Statistics(currency, price, date);
    }

    public Statistics createDefaultStatisticsUSD(){
        return new Statistics(Currency.USD, BigDecimal.ZERO, LocalDate.now());
    }

    public Statistics createDefaultStatisticsBGN(){
        return new Statistics(Currency.BGN, BigDecimal.ONE, LocalDate.now());
    }
}