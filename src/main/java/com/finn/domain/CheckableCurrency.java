package com.finn.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "checkcurrency")
public class CheckableCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    Currency currency;

    int period;

    LocalDate lastUpdate;

    public CheckableCurrency() {
    }

    public CheckableCurrency(User user, Currency currency, int period) {
        this.user = user;
        this.currency = currency;
        this.period = period;
        lastUpdate = LocalDate.ofEpochDay(0);
    }

    public CheckableCurrency(User user, Currency currency, int period, LocalDate lastUpdate) {
        this.user = user;
        this.currency = currency;
        this.period = period;
        this.lastUpdate = lastUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckableCurrency that = (CheckableCurrency) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
