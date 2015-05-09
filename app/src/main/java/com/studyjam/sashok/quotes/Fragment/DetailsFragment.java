package com.studyjam.sashok.quotes.Fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.studyjam.sashok.quotes.QuoteInfo;
import com.studyjam.sashok.quotes.R;

import java.text.DecimalFormat;

/**
 * Subclass of {@link Fragment} class,
 * made for displaying detailed quote information.
 */
public class DetailsFragment extends Fragment {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    // TextView for displaying company full name
    private TextView companyNameFullTextView;

    // TextView for displaying company ticker symbol
    private TextView companyNameShortTextView;

    // TextView for displaying previous trade price
    private TextView prevTradePriceTextView;

    // TextView for displaying changein
    private TextView changeinTextView;

    // TextView for displaying open price
    private TextView openTextView;

    // TextView for displaying previous close price
    private TextView prevCloseTextView;

    // TextViewfor displaying Ask
    private TextView askTextView;

    // TextVew for displaying Bid
    private TextView bidTextView;

    // TextView for displaying warning that data has not been downloaded
    private TextView warningTextView;

    // Table layout for displaying Ask, Bid, Open, Prev. Close
    private TableLayout tableLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get respective views
        companyNameFullTextView = (TextView) getView().findViewById(R.id.companyNameFull);
        companyNameShortTextView = (TextView) getView().findViewById(R.id.companyNameShort);

        prevTradePriceTextView = (TextView) getView().findViewById(R.id.prevTradePrice);
        changeinTextView = (TextView) getView().findViewById(R.id.changein);

        warningTextView = (TextView) getView().findViewById(R.id.warning);

        openTextView = (TextView) getView().findViewById(R.id.open);
        prevCloseTextView = (TextView) getView().findViewById(R.id.prevClose);

        askTextView = (TextView) getView().findViewById(R.id.ask);
        bidTextView = (TextView) getView().findViewById(R.id.bid);

        tableLayout = (TableLayout) getView().findViewById(R.id.table);

        // Make warining view visible
        warningTextView.setVisibility(View.VISIBLE);

        // On the other hand make oter views invisible
        // except company name and ticker symbol
        prevTradePriceTextView.setVisibility(View.INVISIBLE);
        changeinTextView.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        companyNameFullTextView.setVisibility(View.INVISIBLE);
        companyNameShortTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Update fragment data
     *
     * @param quoteInfo update data
     */
    public void update(QuoteInfo quoteInfo) {
        companyNameFullTextView.setVisibility(View.VISIBLE);
        companyNameFullTextView.setText(quoteInfo.getTickerName());

        companyNameShortTextView.setVisibility(View.VISIBLE);
        companyNameShortTextView.setText("(" + quoteInfo.getSymbol() + ")");

        if (quoteInfo.isEmptyQuoteData()) {
            // Make warining view visible
            warningTextView.setVisibility(View.VISIBLE);

            // On the other hand make oter views invisible
            // except company name and ticker symbol
            prevTradePriceTextView.setVisibility(View.INVISIBLE);
            changeinTextView.setVisibility(View.INVISIBLE);
            tableLayout.setVisibility(View.INVISIBLE);
        } else {
            warningTextView.setVisibility(View.INVISIBLE);
            prevTradePriceTextView.setVisibility(View.VISIBLE);
            changeinTextView.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);

            // Fill respective views with data
            {
                String prevTradePriceText = "N/A";
                if (quoteInfo.getLastTradePrice() > 0) {
                    prevTradePriceText = roundValue(quoteInfo.getLastTradePrice());
                }

                prevTradePriceTextView.setText(prevTradePriceText);
            }

            {
                double diff = quoteInfo.getLastTradePrice() - quoteInfo.getPreviousClose();
                String changein = (diff >= 0 ? "▲" : "▼") + roundValue(diff);
                String changeinText = changein + "(" + quoteInfo.getChangeinPercent() + ")";

                changeinTextView.setText(changeinText);
            }

            {
                String openText = "N/A";
                if (quoteInfo.getOpen() > 0)
                    openText = roundValue(quoteInfo.getOpen());

                openTextView.setText(openText);
            }

            {
                String prevCloseText = "N/A";
                if (quoteInfo.getPreviousClose() > 0)
                    prevCloseText = roundValue(quoteInfo.getPreviousClose());

                prevCloseTextView.setText(prevCloseText);
            }

            {
                String askText = "N/A";
                if (quoteInfo.getAsk() > 0)
                    askText = roundValue(quoteInfo.getAsk());

                askTextView.setText(askText);
            }

            {
                String bidText = "N/A";
                if (quoteInfo.getBid() > 0)
                    bidText = roundValue(quoteInfo.getBid());

                bidTextView.setText(bidText);
            }
        }
    }

    private static String roundValue(double value) {
        return DECIMAL_FORMAT.format(value);
    }

}