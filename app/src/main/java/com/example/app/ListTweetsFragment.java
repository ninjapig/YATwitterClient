package com.example.app;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListTweetsFragment extends ListFragment implements
        LoaderCallbacks<Cursor> {
    public static final String TAG = ListTweetsFragment.class.getSimpleName();
    public static final String[] FROM ={StatusContract.Column.USER, StatusContract.Column.MESSAGE,
                                        StatusContract.Column.CREATED_AT,
                                        StatusContract.Column.CREATED_AT};
    public static final int[] TO = {R.id.list_item_text_user, R.id.list_item_text_message,
                                    R.id.list_item_text_created_at, R.id.list_item_text_created_at};
    public SimpleCursorAdapter mAdapter;
    public final static int LOADER_ID = 42;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //binds the Database COLUMNS to ListView variables
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    //******************************************************************
    //                 LoaderCallback runs on Worker Thread
    // NOTE: Use ViewBinder and DateUtils.getRelativeTime()
    //******************************************************************

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null, null,
                StatusContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoaderFinished with cursor: "+ cursor.getCount());
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
