package com.finn.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name="statistics")
public class Statistics {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long statId;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable= false)
    @Digits(integer=9, fraction=7)
    private BigDecimal price;

    private LocalDate date;

    public Statistics() {
    }

    public Statistics(Currency currency, BigDecimal price, LocalDate date) {
        this.currency = currency;
        this.price = price;
        this.date = date;
    }

    public Long getStatId() {
        return statId;
    }

    public void setStatId(Long statId) {
        this.statId = statId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistics that = (Statistics) o;

        return statId.equals(that.statId);
    }

    @Override
    public int hashCode() {
        return statId.hashCode();
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "currency=" + currency +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
