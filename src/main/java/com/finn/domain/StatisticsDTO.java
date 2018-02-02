package com.finn.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDate;


public class StatisticsDTO {

    private String currency;

    @Digits(integer=9, fraction=7)
    private BigDecimal price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    public StatisticsDTO(String currency, BigDecimal price, LocalDate date) {
        this.currency = currency;
        this.price = price;
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StatisticsDTO{" +
                "currency='" + currency + '\'' +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
