package com.finn.controller;

import com.finn.domain.*;
import com.finn.domain.Currency;
import com.finn.exception.CurrencyNotSupportedException;
import com.finn.exception.UserNotFoundException;
import com.finn.service.*;
import com.finn.repository.CheckableRepository;
import com.finn.repository.StatisticsRepository;
import com.finn.service.StatisticsService;
import com.finn.repository.UserRepository;
import com.finn.tasks.UpdateAllCurrencyTask;
import com.finn.tasks.UpdateCurrencyTask;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;


@EnableScheduling
@RestController
public class FinanceController {

    private UserRepository userRepository;
    private StatisticsRepository statisticsRepository;
    private CheckableRepository checkableRepository;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private FinanceService financeService;
    private StatisticsService statisticsService;

    public FinanceController(UserRepository userRepository,
                             StatisticsRepository statisticsRepository,
                             CheckableRepository checkableRepository,
                             ThreadPoolTaskScheduler threadPoolTaskScheduler,
                             FinanceService financeService,
                             StatisticsService statisticsService) {
        this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
        this.checkableRepository = checkableRepository;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.financeService = financeService;
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = "/{userId}/addCurrency")
    public ResponseEntity addCurrency(@PathVariable long userId,
                                 @RequestParam(defaultValue = "1") int period,
                                 @RequestParam String currencyName) {
        User user = userRepository.findOne(userId);
        if(user == null)
            throw new UserNotFoundException();
        if (!Arrays.stream(Currency.values()).anyMatch((t) -> t.name().equals(currencyName)))
            throw new CurrencyNotSupportedException();

        Currency currency = Currency.valueOf(currencyName);
        CheckableCurrency checkableCurrency = checkableRepository.findByUserAndCurrency(user, currency);
        if (checkableCurrency == null) {
            checkableCurrency = new CheckableCurrency(user, currency, period);
        } else checkableCurrency.setPeriod(period);

        LocalDate lastUpdateCurrency = financeService.dateOfLastUpdate();
        if(statisticsRepository.findByDateAndCurrency(lastUpdateCurrency, currency) == null) {
            threadPoolTaskScheduler.execute(
                    new UpdateCurrencyTask(checkableCurrency,
                            checkableRepository,
                            statisticsRepository,
                            financeService,
                            threadPoolTaskScheduler));
        }
        else{
            checkableCurrency.setLastUpdate(lastUpdateCurrency);
        }

        checkableCurrency = checkableRepository.save(checkableCurrency);
        user.setCheckableCurrency(checkableCurrency);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}/getPriceAfterDate")
    public Map<CurrencyDTO, List<StatisticsDTO>> getPriceAfterDate(
            @PathVariable long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after) {
        User user = userRepository.findOne(userId);
        if(user == null)
            throw new UserNotFoundException();

        Set<CheckableCurrency> checkables = user.getCheckable();
        Map<Currency, List<Statistics>> statForUser = new HashMap<>();
        checkables.forEach(checkableCurrency -> {
            List<Statistics> statistics = statisticsRepository
                    .findByDateAfterAndCurrencyOrderByDate(after, checkableCurrency.getCurrency());
            statForUser.put(checkableCurrency.getCurrency(),
                    statisticsService.selectByPeriod(statistics, checkableCurrency.getPeriod()));
        });

        return convertToDTO(statForUser);
    }

    @GetMapping(value = "/{userId}/getPriceBeforeDate")
    public Map<CurrencyDTO, List<StatisticsDTO>> getPriceBeforeDate(
            @PathVariable long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before) {
        User user = userRepository.findOne(userId);
        if(user == null)
            throw new UserNotFoundException();

        Set<CheckableCurrency> checkables = user.getCheckable();
        Map<Currency, List<Statistics>> statForUser = new HashMap<>();
        checkables.forEach(checkableCurrency -> {
            List<Statistics> statistics = statisticsRepository
                    .findByDateBeforeAndCurrencyOrderByDate(before, checkableCurrency.getCurrency());
            statForUser.put(checkableCurrency.getCurrency(),
                    statisticsService.selectByPeriod(statistics, checkableCurrency.getPeriod()));
        });

        return convertToDTO(statForUser);
    }

    @GetMapping(value = "/{userId}/getPriceBetweenDate")
    public Map<CurrencyDTO, List<StatisticsDTO>> getPriceBetweenDate(
            @PathVariable long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate botDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate topDate) {
        User user = userRepository.findOne(userId);
        if(user == null)
            throw new UserNotFoundException();

        Set<CheckableCurrency> checkables = user.getCheckable();
        Map<Currency, List<Statistics>> statForUser = new HashMap<>();
        checkables.forEach(checkableCurrency -> {
            List<Statistics> statistics = statisticsRepository
                    .findByDateBetweenAndCurrencyOrderByDate(botDate, topDate, checkableCurrency.getCurrency());
            statForUser.put(checkableCurrency.getCurrency(),
                    statisticsService.selectByPeriod(statistics, checkableCurrency.getPeriod()));
        });

        return convertToDTO(statForUser);
    }

    @Scheduled(cron = "0 3 16 * * *", zone = "Europe/Rome")
    void updateStats() {
        if (threadPoolTaskScheduler.getActiveCount() > 0) {
            threadPoolTaskScheduler.shutdown();
        }
        threadPoolTaskScheduler.execute(
                new UpdateAllCurrencyTask(checkableRepository,
                        statisticsRepository,
                        financeService,
                        threadPoolTaskScheduler));
    }

    private Map<CurrencyDTO, List<StatisticsDTO>> convertToDTO(Map<Currency, List<Statistics>> statForUser){
        Map<CurrencyDTO, List<StatisticsDTO>> statForUserDTO = new HashMap<>();
        statForUser.keySet().forEach(currency -> {
            List<StatisticsDTO> list = new ArrayList<>();
            statForUser.get(currency).forEach(stat->list.add(
                    new StatisticsDTO(stat.getCurrency().name(),stat.getPrice(),stat.getDate())));
            statForUserDTO.put(new CurrencyDTO(currency.name()), list);
        });

        return statForUserDTO;
    }
}
