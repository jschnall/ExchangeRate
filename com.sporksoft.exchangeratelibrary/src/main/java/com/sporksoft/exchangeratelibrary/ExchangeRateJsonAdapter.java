package com.sporksoft.exchangeratelibrary;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jschnall on 5/7/16.
 */
public class ExchangeRateJsonAdapter extends TypeAdapter<ExchangeRateResponse> {
    private static final String SUCCESS = "success";
    private static final String TIMESTAMP = "timestamp";
    private static final String SOURCE = "source";
    private static final String QUOTES = "quotes";

    @Override
    public void write(JsonWriter out, ExchangeRateResponse value) throws IOException {
        throw new UnsupportedOperationException("Can't write ExchangeRateResponse.");
    }

    @Override
    public ExchangeRateResponse read(JsonReader in) throws IOException {
        ExchangeRateResponse response = new ExchangeRateResponse();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();

            if (SUCCESS.equals(key)) {
                response.mSuccess = in.nextBoolean();
            } else if (TIMESTAMP.equals(key)) {
                response.mTimestamp = in.nextLong();
            } else if (SOURCE.equals(key)) {
                response.mSource = in.nextString();
            } else if (QUOTES.equals(key)) {
                response.mQuotes = readQuotes(in);
            } else {
                in.skipValue(); //avoid unhandled events
            }
        }
        in.endObject();

        return response;
    }

    private Map<String, Double> readQuotes(JsonReader in) throws IOException {
        Map<String, Double> quotes = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            quotes.put(in.nextName(), in.nextDouble());
        }
        in.endObject();

        return quotes;
    }
}
