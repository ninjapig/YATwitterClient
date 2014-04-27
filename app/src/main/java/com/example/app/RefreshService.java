package com.example.app;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import  winterwell.jtwitter.Twitter;
import  winterwell.jtwitter.Twitter.Status;
import  winterwell.jtwitter.TwitterException;


import java.util.List;

public class RefreshService extends IntentService{
    private static final String TAG = "IntentService";

    public RefreshService(){
        super(TAG);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String username = sharedPrefs.getString("username", "");
        final String password = sharedPrefs.getString("password", "");
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        Twitter yambaClient = new Twitter(username, password);
        yambaClient.setAPIRootUrl("http://yamba.marakana.com/api");

        int count = 0;
        try{
            List<Status> timeLine = yambaClient.getPublicTimeline();
            for(Status status : timeLine){
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser().toString());
                values.put(StatusContract.Column.MESSAGE, status.getText());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
                //CONTENT_URI is a directory----->table is directory
                //table is represented by a directory and records are items
                //items directly accessed by appending /# to the uri
                Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                if(uri != null){
                    count++;
                    Log.d(TAG, status.getUser()+" : " + status.getText());
                }
            }
        } catch(TwitterException e){
            Log.e(TAG, "Failed to fetch the timeline", e);
            e.printStackTrace();
        }
        Log.d(TAG, "Successful Insert Count: "+count);
        return;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
