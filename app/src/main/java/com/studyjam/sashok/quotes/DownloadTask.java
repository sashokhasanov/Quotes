package com.studyjam.sashok.quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.studyjam.sashok.quotes.Database.QuotesDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DownloadTask extends AsyncTask<String, Void, Void> {
    // Logging tag
    private static final String TAG = "Quotes app";

    // Prefix of request
    private static final String REQUEST_QUERY_PREFIX = "http://query.yahooapis.com/v1/public/yql?q=";

    // Query for yahoo api
    private static final String REQUEST_QUERY_BODY = "select * from yahoo.finance.quotes where symbol in ";

    // Query arguments
    private static final String REQUEST_QUERY_ARGS = "&format=json&env=http://datatables.org/alltables.env";

    // Appllication context
    private Context applicationContext;

    // Parent activity
    private MainActivity parentActivity;

    // notification id
    private final int NOTIFICATION_ID = 25111991;

    private QuotesDatabase quotesDatabase;

    ArrayList<QuoteInfo> quoteInfos = new ArrayList<>();

    /**
     * Creates new instance of {@link DownloadTask}
     *
     * @param parentActivity parent activity
     */
    public DownloadTask(MainActivity parentActivity) {
        super();

        this.parentActivity = parentActivity;
        this.applicationContext = parentActivity.getApplicationContext();
        this.quotesDatabase = parentActivity.getQuotesDatabase();
    }

    @Override
    protected Void doInBackground(String... urlParameters) {
        Log.i(TAG, "Entered doInBackground()");

        downloadQuotesData(urlParameters);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        quotesDatabase.open();
        for (QuoteInfo quoteInfo : quoteInfos) {
            quotesDatabase.updateRecord(quoteInfo);
        }

        // Call back to the MainActivity to update the quotes display
        if (parentActivity != null) {
            parentActivity.setRefreshed();
        }
    }

    /*
    * If necessary, notifies the user that quotes data download is complete.
    * Sends an ordered broadcast back to BroadcastReceiver in MainActivity
    * to determine whether the notification is necessary.
    */
    private void notify(final boolean downloadSuccessfull) {
        Log.i(TAG, "Entered notify()");

        // Sends an ordered broadcast to determine whether MainActivity is
        // active and in the foreground. Creates a new BroadcastReceiver
        // to receive a result indicating the state of MainActivity

        // The Action for this broadcast Intent is MainActivity.DATA_REFRESHED_ACTION
        // The result MainActivity.IS_ALIVE, indicates that MainActivity is active and in the foreground.

        applicationContext.sendOrderedBroadcast(new Intent(MainActivity.DATA_REFRESHED_ACTION), null, new ResultReceiver(), null, 0, null, null);
    }

    /*
     * Download quotes data from the network
     */
    private void downloadQuotesData(String urlParameters[]) {

        boolean downloadCompleted = false;

        try {
            String rawQuotes = getRawQuotes(urlParameters);

            quoteInfos = parseRawData(rawQuotes);

            downloadCompleted = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // error while parsing JSON data
            e.printStackTrace();
        }

        Log.i(TAG, "Quotes data download completed:" + downloadCompleted);

        // send the respective notification
        notify(downloadCompleted);
    }

    /*
     * Get raw quotes data (in JSON format)
     */
    private String getRawQuotes(String[] symbols) throws IOException {
        String query = makeQuotesQuery(symbols);
        String responseString = "";

        HttpURLConnection connection = null;
        try {
            URL queryUrl = new URL(query);

            // open http connection
            connection = (HttpURLConnection) queryUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(currentLine);
            }

            responseString = stringBuilder.toString();
        } finally {
            //  if managed to make a connection, disconnect
            if (connection != null)
                connection.disconnect();
        }

        return responseString;
    }

    /*
     * Parse raw data (in JSON format) into collection of QuoteInfo instances
     */
    private ArrayList<QuoteInfo> parseRawData(String quotesRawData) throws JSONException {
        JSONObject quotesData = new JSONObject(quotesRawData);

        JSONObject query = quotesData.getJSONObject("query");

        JSONObject results = query.getJSONObject("results");

        JSONArray quotesArray = results.optJSONArray("quote");

        ArrayList<QuoteInfo> quoteInfos = new ArrayList<>();

        for (int i = 0; i < quotesArray.length(); i++) {
            JSONObject currentQuote = quotesArray.getJSONObject(i);

            QuoteInfo quoteInfo = new QuoteInfo(currentQuote);

            quoteInfos.add(quoteInfo);
        }

        return quoteInfos;
    }

    /*
     * Make a request string for receiving quotes data form yahoo.finance.quotes
     */
    private String makeQuotesQuery(String[] symbols) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(REQUEST_QUERY_PREFIX).append(URLEncoder.encode(REQUEST_QUERY_BODY, "UTF-8")).append("(");

        for (int curentSymbolIndex = 0; curentSymbolIndex < symbols.length; curentSymbolIndex++) {
            if (curentSymbolIndex != 0)
                builder = builder.append(",");

            builder = builder.append("\"").append(symbols[curentSymbolIndex]).append("\"");
        }

        builder = builder.append(")").append(REQUEST_QUERY_ARGS);

        return builder.toString();
    }

    /*
     * A BroadcastReceiver to treat as final receiver of a broadcast
     */
    private class ResultReceiver extends BroadcastReceiver {
        // Fail message
        final String failMsg = applicationContext.getResources().getString(R.string.download_fail_msg);

        // Success message
        final String successMsg = applicationContext.getResources().getString(R.string.download_success_msg);

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Entered result receiver's onReceive() method");

            final Intent restartMainActivtyIntent = new Intent(applicationContext, MainActivity.class);

            restartMainActivtyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //  Check whether the result code is not MainActivity.IS_ALIVE
            if (getResultCode() != MainActivity.IS_ALIVE) {
                // If so, create a PendingIntent using the
                // restartMainActivityIntent and set its flags
                // to FLAG_UPDATE_CURRENT
                final PendingIntent pendingIntent = PendingIntent
                        .getActivity(applicationContext, 0, restartMainActivtyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Uses custom_notification layout for notofication view
                RemoteViews mContentView = new RemoteViews(
                        applicationContext.getPackageName(),
                        R.layout.custom_notification);

                // Set the notification View's text to
                // reflect whether the downloadQuotesData completed
                // successfully

                if (quoteInfos.size() > 0) {
                    mContentView.setTextViewText(R.id.text, successMsg);
                } else {
                    mContentView.setTextViewText(R.id.text, failMsg);
                }

                // Use the Notification.Builder class to create the notification
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
                        .setContentIntent(pendingIntent)
                        .setContent(mContentView)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setAutoCancel(true);


                // Send notofication
                NotificationManager mNotificationManager = (NotificationManager) parentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

                Log.i(TAG, "Notification Area Notification sent");
            }
        }

    }

}
