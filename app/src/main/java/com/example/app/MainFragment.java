package com.example.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import  winterwell.jtwitter.Twitter;
import  winterwell.jtwitter.TwitterException;

public class MainFragment extends Fragment implements OnClickListener{
    private final static String TAG = "MainFragment";
    private EditText editStatus;
    private Button buttonTweet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        editStatus = (EditText) view.findViewById(R.id.editStatus);
        buttonTweet = (Button) view.findViewById(R.id.buttonTweet);

        buttonTweet.setOnClickListener(this);
        return view;
    }
    public String getFragmentTag(){
        return TAG;
    }
    @Override
    public void onClick(View view) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked with status: " + status );
        new PostingTask().execute(status);
    }
    private final class PostingTask extends
            AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Twitter yambaClient = new Twitter("student", "password");//has to be student-pass
            yambaClient.setAPIRootUrl("http://yamba.marakana.com/api");
            try{
                yambaClient.setStatus(params[0]);
                return "Successfully posted";
            }catch(TwitterException e){
                e.printStackTrace();
                return "Failed to post to yamba service";
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Toast.makeText(MainFragment.this.getActivity(), result, Toast.LENGTH_LONG).show();
        }
    }
}
