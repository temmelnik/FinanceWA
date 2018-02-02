package com.finn;

import com.finn.builder.CheckableCurrencyBuilder;
import com.finn.builder.StatisticsBuilder;
import com.finn.builder.UserBuilder;
import com.finn.controller.FinanceController;
import com.finn.domain.CheckableCurrency;
import com.finn.domain.Statistics;
import com.finn.domain.User;
import com.finn.service.FinanceService;
import com.finn.repository.CheckableRepository;
import com.finn.repository.StatisticsRepository;
import com.finn.repository.UserRepository;
import com.finn.service.StatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FinanceControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private FinanceController financeControllerMock;

    @Mock
    private StatisticsRepository statisticsRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CheckableRepository checkableRepositoryMock;

    @Mock
    private FinanceService financeServiceMock;

    @Mock
    private StatisticsService statisticsServiceMock;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(financeControllerMock)
                .build();
    }

    @Test
    public void getPriceBetweenDateTest() {
        Statistics one = new StatisticsBuilder().createDefaultStatisticsUSD();
        Statistics two = new StatisticsBuilder().createDefaultStatisticsBGN();
        CheckableCurrency oneCheck = new CheckableCurrencyBuilder().createDefaultCheckableCurUSD();
        CheckableCurrency twoCheck = new CheckableCurrencyBuilder().createDefaultCheckableCurBGN();
        User user = new UserBuilder().createDefaultUserVasya();
        user.setCheckableCurrency(oneCheck);
        user.setCheckableCurrency(twoCheck);
        oneCheck.setUser(user);
        twoCheck.setUser(user);

        when(statisticsRepositoryMock.
                findByDateBetweenAndCurrencyOrderByDate(any(), any(), any()))
                .thenReturn(Arrays.asList(one))
                .thenReturn(Arrays.asList(two));
        when(userRepositoryMock.findOne(any(Long.class))).thenReturn(user);
        when(statisticsServiceMock
                .selectByPeriod(any(), any(Integer.class)))
                .thenReturn(Arrays.asList(one))
                .thenReturn(Arrays.asList(two));

        try {
            mockMvc.perform(get("/1/getPriceBetweenDate?botDate=2018-01-23&topDate=2018-01-26"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$[\'USD\'].[0].currency", is("USD")))
                    .andExpect(jsonPath("$[\'USD\'].[0].price", is(0)))
                    .andExpect(jsonPath("$[\'BGN\'].[0].currency", is("BGN")))
                    .andExpect(jsonPath("$[\'BGN\'].[0].price", is(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(userRepositoryMock, times(1)).findOne(1L);
        verify(statisticsRepositoryMock, times(2))
                .findByDateBetweenAndCurrencyOrderByDate(any(), any(), any());
        verifyNoMoreInteractions(userRepositoryMock);
        verifyNoMoreInteractions(statisticsRepositoryMock);
    }

    @Test
    public void addCurrencyTest() {
        User user = new UserBuilder().createDefaultUserVasya();

        when(userRepositoryMock.findOne(any(Long.class))).thenReturn(user);
        when(checkableRepositoryMock.findByUserAndCurrency(any(), any())).thenReturn(null);
        when(checkableRepositoryMock.save(any(CheckableCurrency.class)))
                .thenReturn(new CheckableCurrencyBuilder().createDefaultCheckableCurUSD());
        when(statisticsRepositoryMock.findByDateAndCurrency(any(), any()))
                .thenReturn(new StatisticsBuilder().createDefaultStatisticsUSD());
        when(financeServiceMock.dateOfLastUpdate()).thenReturn(LocalDate.now());

        try {
            mockMvc.perform(get("/1/addCurrency?currencyName=USD"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(userRepositoryMock, times(1)).findOne(1L);
        verify(statisticsRepositoryMock, times(1))
                .findByDateAndCurrency(any(), any());
        verify(checkableRepositoryMock, times(1)).findByUserAndCurrency(any(), any());
        verifyNoMoreInteractions(userRepositoryMock);
        verifyNoMoreInteractions(statisticsRepositoryMock);
    }

    @Test
    public void addCurrencyExceptionUserTest(){
        when(userRepositoryMock.findOne(any(Long.class))).thenReturn(null);

        try {
            mockMvc.perform(get("/1/addCurrency?currencyName=USD"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(userRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void addCurrencyExceptionCurrencyTest(){
        when(userRepositoryMock.findOne(any(Long.class)))
                .thenReturn(new UserBuilder().createDefaultUserVasya());

        try {
            mockMvc.perform(get("/1/addCurrency?currencyName=XXX"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(userRepositoryMock, times(1)).findOne(1L);
        verifyNoMoreInteractions(userRepositoryMock);
    }
}
