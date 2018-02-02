package com.finn.tasks;

import com.finn.domain.CheckableCurrency;
import com.finn.domain.Statistics;
import com.finn.service.FinanceService;
import com.finn.repository.CheckableRepository;
import com.finn.repository.StatisticsRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


public class UpdateCurrencyTask implements Runnable {
    private CheckableCurrency checkableCurrency;
    private StatisticsRepository statisticsRepository;
    private FinanceService financeService;
    private CheckableRepository checkableRepository;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public UpdateCurrencyTask(CheckableCurrency checkableCurrency,
                              CheckableRepository checkableRepository,
                              StatisticsRepository statisticsRepository,
                              FinanceService financeService,
                              ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.checkableCurrency = checkableCurrency;
        this.checkableRepository = checkableRepository;
        this.statisticsRepository = statisticsRepository;
        this.financeService = financeService;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    @Override
    public void run() {
        Statistics statistics = financeService.getPriceOfCurrency(checkableCurrency.getCurrency());
        if(statistics == null){
            threadPoolTaskScheduler.scheduleWithFixedDelay(this, 1000);
            return;
        }
        statisticsRepository.save(statistics);
        checkableCurrency.setLastUpdate(statistics.getDate());
        checkableRepository.save(checkableCurrency);
    }
}
