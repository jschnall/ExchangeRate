package com.sporksoft.exchangeratelibrary;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by jschnall on 5/7/16.
 */
@JsonAdapter(ExchangeRateJsonAdapter.class)
public class ExchangeRateResponse {
    public Boolean mSuccess;
    public String mTerms;
    public String mPrivacy;
    public Long mTimestamp;
    public String mSource;
    public Map<String, Double> mQuotes;

    public ExchangeRateResponse() {
    }
}
