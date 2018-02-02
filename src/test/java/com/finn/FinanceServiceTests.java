package com.finn;

import com.finn.domain.Currency;
import com.finn.domain.Statistics;
import com.finn.service.FinanceServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FinanceServiceTests {
	FinanceServiceImpl financeService = new FinanceServiceImpl();

	@Test
	public void financeServiceOneCurrencyTest() {
		assertNotNull(financeService.getPriceOfCurrency(Currency.valueOf("USD")));
	}

	@Test
	public void financeServiceManyCurrencyTest() {
		Set<Currency> currencies = new HashSet<>();
		currencies.add(Currency.valueOf("USD"));
		currencies.add(Currency.valueOf("EUR"));
		currencies.add(Currency.valueOf("CZK"));
		List<Statistics> result = financeService.getPriceOfCurrency(currencies);
		assertNotNull(result);
		assertTrue(result.size() == 3);
	}
}
