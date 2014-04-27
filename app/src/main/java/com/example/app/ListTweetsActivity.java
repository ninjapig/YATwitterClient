package com.example.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListTweetsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            ListTweetsFragment listTweetsFragment = new ListTweetsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.content, listTweetsFragment , "SomeTag")
                    .commit();
        }
    }
}
