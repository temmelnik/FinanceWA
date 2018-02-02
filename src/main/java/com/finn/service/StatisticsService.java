package com.finn.service;

import com.finn.domain.Statistics;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class StatisticsService {

    public List<Statistics> selectByPeriod(List<Statistics> statistics, int period){
        if(statistics.size()<=1){
            return statistics;
        }
        List<Statistics> periodStatistics = new ArrayList<>();
        LocalDate last = statistics.get(0).getDate().plusDays(period);
        periodStatistics.add(statistics.get(0));
        for(Statistics stat:statistics){
            if(last.compareTo(stat.getDate()) <=0){
                periodStatistics.add(stat);
                last = stat.getDate().plusDays(period);
            }
        }
        return periodStatistics;
    }


}
