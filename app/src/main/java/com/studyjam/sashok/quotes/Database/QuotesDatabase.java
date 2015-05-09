package com.studyjam.sashok.quotes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.studyjam.sashok.quotes.QuoteInfo;
import com.studyjam.sashok.quotes.R;

/**
 * Helper class that incapsulates interaction with database
 */
public class QuotesDatabase {

    private static QuotesDatabase instance;

    /**
     * Get instance of {@link QuotesDatabase}
     *
     * @param context {@link Context}
     * @return insatnce of {@link QuotesDatabase}
     */
    public static QuotesDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new QuotesDatabase(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Database name
     */
    public static final String DATABASE_NAME = "quotes.db";

    // Current database version
    private static final int DATABASE_VERSION = 1;

    // Application context
    private final Context context;

    // Helper class
    private QuotesDbHelper quotesDbHelper;

    // SQLite database
    private SQLiteDatabase sqliteDatabase;

    /**
     * Creates a new instance of {@link QuotesDatabase}
     *
     * @param context context
     */
    private QuotesDatabase(Context context) {
        this.context = context;
    }

    /**
     * Open database connection
     */
    public void open() {
        if (quotesDbHelper == null) {
            quotesDbHelper = new QuotesDbHelper(context);
        }
        sqliteDatabase = quotesDbHelper.getWritableDatabase();
    }

    /**
     * Close database connection
     */
    public void close() {
        if (quotesDbHelper != null) quotesDbHelper.close();
    }

    /**
     * Get all data from TABLE_NAME table
     *
     * @return {@link Cursor}
     */
    public Cursor getAllData() {
        return sqliteDatabase.query(QuoteEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Add record to database
     *
     * @param info {@link QuoteInfo} to be added to database
     */
    public void addRecord(QuoteInfo info) {
        ContentValues contentValues = makeContentValues(info);

        sqliteDatabase.insert(QuoteEntry.TABLE_NAME, null, contentValues);
    }

    /**
     * Update record in database
     *
     * @param info {@link QuoteInfo}, representing ticker info for update
     */
    public void updateRecord(QuoteInfo info) {
        ContentValues contentValues = makeContentValues(info);

        sqliteDatabase.update(QuoteEntry.TABLE_NAME, contentValues, "symbol = ?", new String[]{info.getSymbol()});
    }

    /*
     * Make ContentValues based on respective QuoteInfo
     */
    private ContentValues makeContentValues(QuoteInfo info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuoteEntry.COLUMN_SYMBOL, info.getSymbol());
        contentValues.put(QuoteEntry.COLUMN_LAST_TRADE_DATE, info.getLastTradeDate());
        contentValues.put(QuoteEntry.COLUMN_LAST_TRADE_TIME, info.getLastTradeTime());
        contentValues.put(QuoteEntry.COLUMN_LAST_TRADE_PRICE, (Double.valueOf(info.getLastTradePrice()).isNaN() ? -1.0 : info.getLastTradePrice()));
        contentValues.put(QuoteEntry.COLUMN_CHANGEIN_PERCENT, info.getChangeinPercent());
        contentValues.put(QuoteEntry.COLUMN_ASK, (Double.valueOf(info.getAsk()).isNaN() ? -1.0 : info.getAsk()));
        contentValues.put(QuoteEntry.COLUMN_BID, (Double.valueOf(info.getBid()).isNaN() ? -1.0 : info.getBid()));
        contentValues.put(QuoteEntry.COLUMN_DAYS_LOW, (Double.valueOf(info.getDaysLow()).isNaN() ? -1.0 : info.getDaysLow()));
        contentValues.put(QuoteEntry.COLUMN_DAYS_HIGH, (Double.valueOf(info.getDaysHigh()).isNaN() ? -1.0 : info.getDaysHigh()));
        contentValues.put(QuoteEntry.COLUMN_OPEN, (Double.valueOf(info.getOpen()).isNaN() ? -1.0 : info.getOpen()));
        contentValues.put(QuoteEntry.COLUMN_PREVIOUS_CLOSE, (Double.valueOf(info.getPreviousClose()).isNaN() ? -1.0 : info.getPreviousClose()));

        return contentValues;
    }

    /*
     * Make ContentValues for single ticker with respective name and ticker symbol
     */
    private ContentValues makeEmptyTickerValue(String symbol, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuoteEntry.COLUMN_SYMBOL, symbol);
        contentValues.put(QuoteEntry.COLUMN_TICKER_NAME, name);
        contentValues.put(QuoteEntry.COLUMN_LAST_TRADE_DATE, "");
        contentValues.put(QuoteEntry.COLUMN_LAST_TRADE_TIME, "");
        contentValues.put(QuoteEntry.COLUMN_LAST_TRADE_PRICE, -1.0);
        contentValues.put(QuoteEntry.COLUMN_CHANGEIN_PERCENT, "");
        contentValues.put(QuoteEntry.COLUMN_ASK, -1.0);
        contentValues.put(QuoteEntry.COLUMN_BID, -1.0);
        contentValues.put(QuoteEntry.COLUMN_DAYS_LOW, -1.0);
        contentValues.put(QuoteEntry.COLUMN_DAYS_HIGH, -1.0);
        contentValues.put(QuoteEntry.COLUMN_OPEN, -1.0);
        contentValues.put(QuoteEntry.COLUMN_PREVIOUS_CLOSE, -1.0);

        return contentValues;
    }

    /*
     * Helper class for interaction with database
     */
    private class QuotesDbHelper extends SQLiteOpenHelper {
        /**
         * Creates a new instance of {@link QuotesDbHelper}
         *
         * @param context {@link Context}
         */
        private QuotesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String SQL_CREATE_QUOTES_TABLE = "CREATE TABLE " + QuoteEntry.TABLE_NAME + " (" +

                    QuoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    QuoteEntry.COLUMN_SYMBOL + " TEXT NOT NULL," +
                    QuoteEntry.COLUMN_TICKER_NAME + " TEXT NOT NULL," +
                    QuoteEntry.COLUMN_LAST_TRADE_DATE + " TEXT NOT NULL," +
                    QuoteEntry.COLUMN_LAST_TRADE_TIME + " TEXT NOT NULL," +
                    QuoteEntry.COLUMN_LAST_TRADE_PRICE + " REAL NOT NULL," +
                    QuoteEntry.COLUMN_CHANGEIN_PERCENT + " TEXT NOT NULL," +
                    QuoteEntry.COLUMN_ASK + " REAL NOT NULL," +
                    QuoteEntry.COLUMN_BID + " REAL NOT NULL," +
                    QuoteEntry.COLUMN_DAYS_LOW + " REAL NOT NULL," +
                    QuoteEntry.COLUMN_DAYS_HIGH + " REAL NOT NULL," +
                    QuoteEntry.COLUMN_OPEN + " REAL NOT NULL," +
                    QuoteEntry.COLUMN_PREVIOUS_CLOSE + " REAL NOT NULL" + ");";

            sqLiteDatabase.execSQL(SQL_CREATE_QUOTES_TABLE);

            String[] tickersShort = context.getResources().getStringArray(R.array.tickers_short);
            String[] tickersFull = context.getResources().getStringArray(R.array.tickers_full);

            for (int i = 0; i < tickersShort.length; i++) {
                sqLiteDatabase.insert(QuoteEntry.TABLE_NAME, null, makeEmptyTickerValue(tickersShort[i], tickersFull[i]));
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuoteEntry.TABLE_NAME);

            onCreate(sqLiteDatabase);
        }
    }

    public static class QuoteEntry implements BaseColumns {
        // Database table name
        public static final String TABLE_NAME = "quotes";

        // Table columns names
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TICKER_NAME = "tickerName";
        public static final String COLUMN_LAST_TRADE_DATE = "LastTradeDate";
        public static final String COLUMN_LAST_TRADE_TIME = "LastTradeTime";
        public static final String COLUMN_LAST_TRADE_PRICE = "LstTradePriceOnly";
        public static final String COLUMN_CHANGEIN_PERCENT = "ChangeinPercent";
        public static final String COLUMN_ASK = "Ask";
        public static final String COLUMN_BID = "Bid";
        public static final String COLUMN_DAYS_LOW = "DaysLow";
        public static final String COLUMN_DAYS_HIGH = "DaysHigh";
        public static final String COLUMN_OPEN = "Open";
        public static final String COLUMN_PREVIOUS_CLOSE = "PreviousClose";
    }
}