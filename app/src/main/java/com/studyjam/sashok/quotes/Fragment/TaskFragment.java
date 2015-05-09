package com.studyjam.sashok.quotes.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;

import com.studyjam.sashok.quotes.DownloadTask;
import com.studyjam.sashok.quotes.MainActivity;
import com.studyjam.sashok.quotes.R;

/**
 * This Fragment manages a single background task and retains itself across configuration changes.
 */
public class TaskFragment extends Fragment {
    // Parent MainActivity instance
    private MainActivity parentActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        parentActivity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        new DownloadTask(parentActivity).execute(parentActivity.getApplicationContext().getResources().getStringArray(R.array.tickers_short));
    }
}
