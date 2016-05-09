package com.sporksoft.exchangeratelibrary;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by jschnall on 5/7/16.
 */
public class ExchangeRateAdapter extends RecyclerView.Adapter<ExchangeRateAdapter.ViewHolder> {
    Context mContext;
    String mSource;
    boolean mShowFlags;
    List<String> mCountries; // iso codes for countries to display
    Map<String, Double> mRates;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Note Butterknife does not work in library projects, so do this the old fashioned way
        RelativeLayout layout;
        ImageView flag;
        TextView symbol;
        TextView name;
        TextView rate;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
            flag = (ImageView) itemView.findViewById(R.id.flag);
            symbol = (TextView) itemView.findViewById(R.id.symbol);
            name = (TextView) itemView.findViewById(R.id.name);
            rate = (TextView) itemView.findViewById(R.id.rate);
        }
    }

    public ExchangeRateAdapter(Context context) {
        mContext = context;
        mCountries = new ArrayList<>();
        mRates = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_rate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String country =  mCountries.get(position);
        Locale locale = new Locale("", country);
        Currency currency = Currency.getInstance(locale);
        String currencyCode = currency.getCurrencyCode();

        double rate = mRates.get(mSource + currencyCode);

        if (mShowFlags) {
            holder.flag.setVisibility(View.VISIBLE);
            Resources resources = mContext.getResources();
            String fileName = locale.getDisplayName().toLowerCase().replace(" ", "_");
            final int resourceId = resources.getIdentifier(fileName, "drawable",
                    mContext.getPackageName());
            holder.flag.setImageResource(resourceId);
        } else {
            holder.flag.setVisibility(View.GONE);
        }
        holder.name.setText(currencyCode);
        holder.symbol.setText(currency.getSymbol());
        holder.rate.setText(String.valueOf(rate));
        holder.layout.setBackgroundResource(position % 2 == 0 ? R.color.transparent : R.color.lightGrey);
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public void update(ExchangeRateResponse exchangeRateResponse, boolean showFlags, List<String> countries) {
        mShowFlags = showFlags;
        mCountries = countries;

        if (exchangeRateResponse == null || !exchangeRateResponse.mSuccess) {
            mRates.clear();
            notifyDataSetChanged();
            return;
        }
        mSource = exchangeRateResponse.mSource;
        mRates = exchangeRateResponse.mQuotes;
        notifyDataSetChanged();
    }
}
