package com.studyjam.sashok.quotes;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class represetnting company quote information
 */
public class QuoteInfo {
    // String keys for JSONObject fields
    private static final String SYMBOL_KEY = "symbol";
    private static final String TICKER_NAME_KEY = "Name";

    private static final String LAST_TARDE_DATE_KEY = "LastTradeDate";
    private static final String LAST_TRADE_TIME_KEY = "LastTradeTime";
    private static final String LAST_TRADE_PRICE_KEY = "LastTradePriceOnly";

    private static final String CHANGEIN_PERCENT_KEY = "ChangeinPercent";

    private static final String ASK_KEY = "Ask";
    private static final String BID_KEY = "Bid";

    private static final String DAYS_LOW_KEY = "DaysLow";
    private static final String DAYS_HIGH_KEY = "DaysHigh";

    private static final String OPEN_KEY = "Open";
    private static final String PREVIOUS_CLOSE_KEY = "PreviousClose";

    // Ticker symbol
    private String symbol;

    // Full ticker name
    private String tickerName;

    // Last trade date
    private String lastTradeDate;
    // Last trade time
    private String lastTradeTime;
    // Last trade price
    private double lastTradePrice;

    // Changein percent
    private String changeinPercent;

    // Ask
    private double ask;
    // Bid
    private double bid;

    // Day's low
    private double daysLow;
    // Day's high
    private double daysHigh;

    // Open
    private double open;
    // Previous close
    private double previousClose;

    /**
     * Creates a new instance of {@link QuoteInfo}
     */
    public QuoteInfo() {
    }

    /**
     * Creates new instance of {@link QuoteInfo}
     *
     * @param jsonQuote qoute info in JSON format
     */
    public QuoteInfo(JSONObject jsonQuote) {
        symbol = jsonQuote.optString(SYMBOL_KEY);
        tickerName = jsonQuote.optString(TICKER_NAME_KEY);
        lastTradeDate = jsonQuote.optString(LAST_TARDE_DATE_KEY);
        lastTradeTime = jsonQuote.optString(LAST_TRADE_TIME_KEY);
        lastTradePrice = jsonQuote.optDouble(LAST_TRADE_PRICE_KEY);

        changeinPercent = jsonQuote.optString(CHANGEIN_PERCENT_KEY);

        ask = jsonQuote.optDouble(ASK_KEY);
        bid = jsonQuote.optDouble(BID_KEY);
        daysLow = jsonQuote.optDouble(DAYS_LOW_KEY);
        daysHigh = jsonQuote.optDouble(DAYS_HIGH_KEY);
        open = jsonQuote.optDouble(OPEN_KEY);
        previousClose = jsonQuote.optDouble(PREVIOUS_CLOSE_KEY);
    }

    /**
     * Get JSON representation of quote info
     *
     * @return {@link JSONObject} representing a quote info
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject quoteInfo = new JSONObject();
        quoteInfo.put(SYMBOL_KEY, symbol);
        quoteInfo.put(TICKER_NAME_KEY, tickerName);
        quoteInfo.put(LAST_TARDE_DATE_KEY, lastTradeDate);
        quoteInfo.put(LAST_TRADE_TIME_KEY, lastTradeTime);
        quoteInfo.put(LAST_TRADE_PRICE_KEY, lastTradePrice);
        quoteInfo.put(CHANGEIN_PERCENT_KEY, changeinPercent);
        quoteInfo.put(ASK_KEY, ask);
        quoteInfo.put(BID_KEY, bid);
        quoteInfo.put(DAYS_LOW_KEY, daysLow);
        quoteInfo.put(DAYS_HIGH_KEY, daysHigh);
        quoteInfo.put(OPEN_KEY, open);
        quoteInfo.put(PREVIOUS_CLOSE_KEY, previousClose);

        return quoteInfo;
    }

    /**
     * Check if ticker data is empty
     *
     * @return <code>true</code>, if no tcker data is set, <code>false</code>  otrewise
     */
    public boolean isEmptyQuoteData() {
        return TextUtils.isEmpty(lastTradeDate)
                && TextUtils.isEmpty(lastTradeTime)
                && lastTradePrice <= 0.0
                && TextUtils.isEmpty(changeinPercent)
                && ask <= 0.0
                && bid <= 0.0
                && daysLow <= 0.0
                && daysHigh <= 0.0
                && open <= 0.0
                && previousClose <= 0.0;
    }

    public double getAsk() {
        return ask;
    }

    public double getBid() {
        return bid;
    }

    public double getDaysLow() {
        return daysLow;
    }

    public double getDaysHigh() {
        return daysHigh;
    }

    public double getOpen() {
        return open;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public String getChangeinPercent() {
        return changeinPercent;
    }

    public String getLastTradeDate() {
        return lastTradeDate;
    }

    public String getLastTradeTime() {
        return lastTradeTime;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getTickerName() {
        return tickerName;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setLastTradeDate(String lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    public void setLastTradeTime(String lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public void setChangeinPercent(String changeinPercent) {
        this.changeinPercent = changeinPercent;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public void setDaysLow(double daysLow) {
        this.daysLow = daysLow;
    }

    public void setDaysHigh(double daysHigh) {
        this.daysHigh = daysHigh;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public void setTickerName(String tickerName) {
        this.tickerName = tickerName;
    }
}
