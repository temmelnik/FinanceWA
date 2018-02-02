package com.finn.service;

import com.finn.domain.Currency;
import com.finn.domain.Statistics;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class FinanceServiceImpl implements FinanceService {

    private String baseUrl = "https://api.fixer.io/latest";

    public FinanceServiceImpl() {
    }

    public FinanceServiceImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Statistics getPriceOfCurrency(Currency currency) {
        String url = baseUrl + "?base=RUB&symbols=" + currency.name();
        try {
            JSONObject json = getJsonResponseFromUrl(url);
            if (json == null) return null;

            Map<String, Double> rates = (Map<String, Double>) json.get("rates");
            BigDecimal price = new BigDecimal(rates.get(currency.name()));
            price = price.setScale(7, BigDecimal.ROUND_HALF_EVEN);
            LocalDate date = LocalDate.parse((String) json.get("date"));
            return new Statistics(currency, price, date);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Statistics> getPriceOfCurrency(Set<Currency> currencySet) {
        StringBuilder url = new StringBuilder(baseUrl + "?base=RUB&symbols=");
        currencySet.forEach(currency -> url.append(currency.name() + ","));
        List<Statistics> result = new ArrayList<>();

        try {
            JSONObject json = getJsonResponseFromUrl(url.toString());
            if (json == null) return result;

            Map<String, Double> rates = (Map<String, Double>) json.get("rates");
            LocalDate date = LocalDate.parse((String) json.get("date"));
            currencySet.forEach(currency -> {
                BigDecimal price = new BigDecimal(rates.get(currency.name()));
                price = price.setScale(7, BigDecimal.ROUND_HALF_EVEN);
                result.add(new Statistics(currency, price, date));
            });
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JSONObject getJsonResponseFromUrl(String urlString) throws IOException, ParseException {
        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return null;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(response.toString());
    }

    @Override
    public LocalDate dateOfLastUpdate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("CET"));
        LocalDateTime sixteenPM = LocalDate.now(ZoneId.of("CET")).atTime(16, 0);
        if (now.isBefore(sixteenPM)) {
            return LocalDate.now().minusDays(1);
        }
        return LocalDate.now();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
