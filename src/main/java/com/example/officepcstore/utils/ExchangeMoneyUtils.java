package com.example.officepcstore.utils;

import com.example.officepcstore.excep.AppException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeMoneyUtils {
    public static final String ACCESS_KEY = "oOpQ0k4DC6H6jPFsQUPUf9XOHUSYIQrH";
    public static final String BASE_URL = "https://api.apilayer.com/currency_data/convert";

    public static double exchange(BigDecimal amount) throws IOException {
        String url_str = BASE_URL + "?from=VND&to=USD&amount=" + amount;

        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestMethod("GET");
        request.setRequestProperty("apikey", ACCESS_KEY);
        request.connect();
        if (request.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Failed when exchange money");
        JsonObject jsonObject = JsonParser.parseReader(
                new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
        String result = jsonObject.get("result").getAsString();
        request.disconnect();
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
