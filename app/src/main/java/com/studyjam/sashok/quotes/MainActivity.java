package com.studyjam.sashok.quotes;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.studyjam.sashok.quotes.Database.QuotesDatabase;
import com.studyjam.sashok.quotes.Fragment.DetailsFragment;
import com.studyjam.sashok.quotes.Fragment.TaskFragment;
import com.studyjam.sashok.quotes.Fragment.TickersFragment;

public class MainActivity extends ActionBarActivity implements SelectionListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DATA_REFRESHED_ACTION = "com.studyjam.sashok.quotes.DATA_REFRESHED";

    // Indicates that MainActivity is active and in the foreground
    public static final int IS_ALIVE = Activity.RESULT_FIRST_USER;

    // Tag for marking non-retain fragment
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    // Logging tag
    private static final String TAG = "Quotes app";

    // Unselected item index
    private static final int ITEM_INDEX_UNSELECTED = -1;

    // Object for interaction with database
    private QuotesDatabase quotesDatabase;

    // Current cursor with data
    private Cursor cursor;

    // Fragment manager
    private FragmentManager fragmentManager;

    // Quotes fragment
    private TickersFragment tickersFragment;

    // Detailed info fragment
    private DetailsFragment detailsFragment;

    private TaskFragment taskFragment;

    // Refresh receiver
    private BroadcastReceiver refreshReceiver;

    // unselected item index
    private int selectedItemIndex = ITEM_INDEX_UNSELECTED;

    /**
     * Refresh displayed quotes data
     */
    public void setRefreshed() {
        if (getLoaderManager().getLoader(0) != null)
            getLoaderManager().getLoader(0).forceLoad();
    }

    /**
     * Get database
     *
     * @return {@link QuotesDatabase}
     */
    public QuotesDatabase getQuotesDatabase() {
        return quotesDatabase;
    }

    /**
     * Update currently selected quote's details
     */
    public void updateQuoteDetails() {
        // Calls DetailsFragement.update(), passing in the
        // the data for the currently selected list item

        if (detailsFragment != null && selectedItemIndex >= 0) {
            if (cursor.moveToPosition(selectedItemIndex)) {
                QuoteInfo info = new QuoteInfo();

                info.setSymbol(cursor.getString(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_SYMBOL)));
                info.setTickerName(cursor.getString(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_TICKER_NAME)));
                info.setAsk(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_ASK)));
                info.setBid(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_BID)));
                info.setLastTradeDate(cursor.getString(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_LAST_TRADE_DATE)));
                info.setLastTradeTime(cursor.getString(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_LAST_TRADE_TIME)));
                info.setLastTradePrice(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_LAST_TRADE_PRICE)));
                info.setChangeinPercent(cursor.getString(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_CHANGEIN_PERCENT)));
                info.setDaysLow(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_DAYS_LOW)));
                info.setDaysHigh(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_DAYS_HIGH)));
                info.setOpen(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_OPEN)));
                info.setPreviousClose(cursor.getDouble(cursor.getColumnIndex(QuotesDatabase.QuoteEntry.COLUMN_PREVIOUS_CLOSE)));

                detailsFragment.update(info);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isTwoPaneMode() && fragmentManager.getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // Notify user that quotes data is now downloading
                Toast.makeText(getApplicationContext(), "Downloading data", Toast.LENGTH_SHORT).show();
                new DownloadTask(this).execute(this.getApplicationContext().getResources().getStringArray(R.array.tickers_short));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Called when a list item is clicked on
     */
    @Override
    public void onItemSelected(int position) {
        selectedItemIndex = position;

        if (!isTwoPaneMode())
            detailsFragment = addDetailsFragment();

        updateQuoteDetails();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.cursor = cursor;
        tickersFragment.updateCursor(cursor);
        updateQuoteDetails();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make a datatabase and open connetcion
        quotesDatabase = QuotesDatabase.getInstance(this);
        quotesDatabase.open();

        getLoaderManager().initLoader(0, null, this);

        // Get fragment manager
        fragmentManager = getFragmentManager();

        // Add fragment, containing list of tickers
        addTickersFragment();

        ensureData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the BroadcastReceiver to receive a
        // DATA_REFRESHED_ACTION broadcast

        IntentFilter filter = new IntentFilter();
        filter.addAction(DATA_REFRESHED_ACTION);
        registerReceiver(refreshReceiver, filter);

        updateQuoteDetails();
    }

    @Override
    protected void onPause() {
        // Unregister the BroadcastReceiver if it has been registered
        if (refreshReceiver != null) {
            unregisterReceiver(refreshReceiver);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("SelectedItemIndex", selectedItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (isTwoPaneMode()) {
            selectedItemIndex = savedInstanceState.getInt("SelectedItemIndex");

            if (tickersFragment != null) {
                tickersFragment.getListView().setItemChecked(selectedItemIndex, true);
            }

            updateQuoteDetails();
        }
    }

    /*
     * Detect whether application is in two-pane mode
     */
    private boolean isTwoPaneMode() {
        // If there is no fragment_container ID,
        // then the application is in two-pane mode
        return findViewById(R.id.fragment_container) == null;
    }

    /*
     * Add TickersFragment to the activity
     */
    private void addTickersFragment() {
        // If the layout is single-pane, create the FriendsFragment
        // and add it to the Activity

        if (!isTwoPaneMode()) {
            tickersFragment = new TickersFragment();
            tickersFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, tickersFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // Otherwise, save a reference to for later use
            detailsFragment = (DetailsFragment) fragmentManager.findFragmentById(R.id.details_frag);
            tickersFragment = (TickersFragment) fragmentManager.findFragmentById(R.id.tickers_frag);
        }
    }

    /*
     * Add DetailsFragment to the activity
     */
    private DetailsFragment addDetailsFragment() {
        DetailsFragment detailsFragment = new DetailsFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        fragmentManager.executePendingTransactions();

        return detailsFragment;
    }

    // If stored Tweets are not fresh, reload them from network
    // Otherwise, load them from file
    private void ensureData() {
        // Show a Toast Notification to inform user that
        // the app is "Downloading Tweets from Network"
        Log.i(TAG, "Issuing Toast Message");

        // Try to find respective fragment by it's tag
        taskFragment = (TaskFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (taskFragment == null) {
            // Notify user that quotes data is now downloading
            Toast.makeText(getApplicationContext(), "Downloading data", Toast.LENGTH_SHORT).show();

            // Create fragment, contaning AsyncTask for downloading data
            taskFragment = new TaskFragment();
            fragmentManager.beginTransaction().add(taskFragment, TAG_TASK_FRAGMENT).commit();
        }

        // Set up a BroadcastReceiver to receive an Intent when download finishes
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.i(TAG, "BroadcastIntent received in MainActivity");

                // Check to make sure this is an ordered broadcast
                // Let sender know that the Intent was received
                // by setting result code to MainActivity.IS_ALIVE

                if (isOrderedBroadcast())
                    refreshReceiver.setResultCode(MainActivity.IS_ALIVE);
            }
        };

    }

    /**
     * {@link CursorLoader} subclass managing to get {@link Cursor} for all tickers' data
     */
    static class MyCursorLoader extends CursorLoader {
        public MyCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return QuotesDatabase.getInstance(this.getContext()).getAllData();
        }
    }
}
