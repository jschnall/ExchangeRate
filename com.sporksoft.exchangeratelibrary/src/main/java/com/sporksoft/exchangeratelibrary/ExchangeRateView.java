package com.sporksoft.exchangeratelibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jschnall on 5/7/16.
 */
public class ExchangeRateView extends RecyclerView {
    private static final String LOGTAG = ExchangeRateView.class.getSimpleName();
    private static final String CURRENCY_LAYER_URL = "http://apilayer.net/api/live";

    ExchangeRateAdapter mListAdapter;
    String mApiKey;
    boolean mShowFlags = true;
    int mUpdateInterval = 0; // Update interval in seconds
    List<String> mCountries;

    private Handler mHandler;

    class GetRatesTask extends AsyncTask<String, String, ExchangeRateResponse> {
        @Override
        protected ExchangeRateResponse doInBackground(String... params) {
            ExchangeRateResponse result = null;
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(CURRENCY_LAYER_URL + "?access_key=" + mApiKey);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                result = new Gson().fromJson(br, ExchangeRateResponse.class);
            } catch (Exception e) {
                Log.e(LOGTAG, "Can't get exchange rates.");
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    // Do nothing
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ExchangeRateResponse result) {
            super.onPostExecute(result);
            mListAdapter.update(result, mShowFlags, mCountries);


            if (mUpdateInterval > 0) {
                try {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mGetRatesTask = new GetRatesTask();
                            mGetRatesTask.execute(mApiKey, String.valueOf(mUpdateInterval));
                        }
                    }, 1000 * mUpdateInterval);
                } catch (Exception e) {
                    Log.e(LOGTAG, "Can't post to handler");
                }
            }
        }
    }
    GetRatesTask mGetRatesTask;


    public ExchangeRateView(Context context, String apiKey) {
        super(context);
        init(null, 0);
        mApiKey = apiKey;
    }

    public ExchangeRateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ExchangeRateView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mCountries = new ArrayList<>();

        if (attrs != null) {
            try {
                TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExchangeRateView, defStyle, 0);
                mApiKey = a.getString(R.styleable.ExchangeRateView_apiKey);
                mShowFlags = a.getBoolean(R.styleable.ExchangeRateView_showFlags, true);
                mUpdateInterval = a.getInteger(R.styleable.ExchangeRateView_updateInterval, 0);
                a.recycle();
            } catch (Exception e) {
                Log.e(LOGTAG, "Can't parse attributes");
            }
        }

        Context context = getContext();
        setLayoutManager(new LinearLayoutManager(context));
        mListAdapter = new ExchangeRateAdapter(context);
        setAdapter(mListAdapter);
    }

    public void setCountries(List countries) {
        mCountries = countries;
    }


    @Override
    protected void onWindowVisibilityChanged (int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            if (mHandler == null) {
                mHandler = new Handler();

                mGetRatesTask = new GetRatesTask();
                mGetRatesTask.execute();
            }
        } else {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
    }

    public void onScreenStateChanged (int screenState) {
        super.onScreenStateChanged(screenState);
        if (screenState == View.SCREEN_STATE_ON) {
            if (mHandler == null) {
                mHandler = new Handler();

                mGetRatesTask = new GetRatesTask();
                mGetRatesTask.execute();
            }
        } else {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
    }
}
