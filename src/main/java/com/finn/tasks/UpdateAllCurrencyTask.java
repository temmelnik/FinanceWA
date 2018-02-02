package com.finn.tasks;

import com.finn.domain.CheckableCurrency;
import com.finn.domain.Currency;
import com.finn.domain.Statistics;
import com.finn.service.FinanceService;
import com.finn.repository.CheckableRepository;
import com.finn.repository.StatisticsRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UpdateAllCurrencyTask implements Runnable {
    private CheckableRepository checkableRepository;
    private StatisticsRepository statisticsRepository;
    private FinanceService financeService;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public UpdateAllCurrencyTask(CheckableRepository checkableRepository,
                                 StatisticsRepository statisticsRepository,
                                 FinanceService financeService,
                                 ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.checkableRepository = checkableRepository;
        this.statisticsRepository = statisticsRepository;
        this.financeService = financeService;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    @Override
    public void run() {
        Set<Currency> needUpdate = new HashSet<>();
        List<CheckableCurrency> needCorrect = new ArrayList<>();
        LocalDate lastUpdate = financeService.dateOfLastUpdate();
        checkableRepository.findAll().forEach(checkableCur -> {
            if (checkableCur.getLastUpdate().plusDays(checkableCur.getPeriod())
                    .compareTo(lastUpdate) <= 0) {
                needUpdate.add(checkableCur.getCurrency());
                needCorrect.add(checkableCur);
            }

        });
        if (needUpdate.size() == 0) {
            return;
        }
        List<Statistics> update = financeService.getPriceOfCurrency(needUpdate);
        if (update.size() == 0 ||
                update.get(0).getDate().isBefore(financeService.dateOfLastUpdate())) {
            threadPoolTaskScheduler.scheduleWithFixedDelay(this, 2000);
            return;
        }
        statisticsRepository.save(update);
        needCorrect.forEach(checkableCur -> {
            checkableCur.setLastUpdate(update.get(0).getDate());
            checkableRepository.save(checkableCur);
        });
    }
}
