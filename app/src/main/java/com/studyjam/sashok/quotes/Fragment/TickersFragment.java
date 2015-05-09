package com.studyjam.sashok.quotes.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.studyjam.sashok.quotes.Database.QuotesDatabase;
import com.studyjam.sashok.quotes.R;
import com.studyjam.sashok.quotes.SelectionListener;

/**
 * A simple {@link ListFragment} subclass, which is used to
 * display a list of quotes tickers.
 */
public class TickersFragment extends ListFragment {
    // Selection listener callback
    private SelectionListener selectionListener;

    // Cursor adapter
    private SimpleCursorAdapter adapter;

    /**
     * Update adapter's cursor
     *
     * @param cursor {@link Cursor}
     */
    public void updateCursor(Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // формируем столбцы сопоставления
        String[] from = new String[]{QuotesDatabase.QuoteEntry.COLUMN_TICKER_NAME};
        int[] to = new int[]{android.R.id.text1};

        // создааем адаптер и настраиваем список
        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, null, from, to, 0);

        // set list adapter
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the selectionListener interface. If not, it throws an exception
        try {
            selectionListener = (SelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SelectionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // When using two-pane layout, configure the ListView
        // to highlight the selected list item
        if (isTwoPaneMode())
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        // Send the event to the host activity
        selectionListener.onItemSelected(position);
    }

    /**
     * Detect if application is in two-pane mode
     *
     * @return <code>true</code>, if application is in two-pane mode, <code>false</code> otherwise
     */
    public boolean isTwoPaneMode() {
        return getFragmentManager().findFragmentById(R.id.details_frag) != null;
    }
}
