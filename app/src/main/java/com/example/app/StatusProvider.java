package com.example.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StatusProvider extends ContentProvider{
    private static final String TAG = StatusProvider.class.getSimpleName();
    private DbHelper dbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE+"/#", StatusContract.STATUS_ITEM);
    }

    @Override
    public String getType(Uri uri){//CHECKS IF URI HAS A NUMBER IN IT OR NOT

        if(sURIMatcher.match(uri) == StatusContract.STATUS_DIR) {
            Log.d(TAG, "got Type:" + StatusContract.STATUS_TYPE_DIR);
            return StatusContract.STATUS_TYPE_DIR;
        }
        if(sURIMatcher.match(uri) == StatusContract.STATUS_ITEM){
            Log.d(TAG, "got Type:" + StatusContract.STATUS_TYPE_ITEM);
            return StatusContract.STATUS_TYPE_ITEM;
        }
        throw new IllegalArgumentException("Illegal uri: " + uri);
    }
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    //(1) Uses SQLiteQueryBuilder to build query string
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor cursor;
        String orderBy = TextUtils.isEmpty(sortOrder) ? StatusContract.DEFAULT_SORT : sortOrder;

        qb.setTables(StatusContract.TABLE);
        switch(sURIMatcher.match(uri)){
            case(StatusContract.STATUS_DIR):
                break;
            case(StatusContract.STATUS_ITEM):
                qb.appendWhere(StatusContract.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        Log.d(TAG,"Queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    //(1) Inserts row (2)insert returns -1 on fail  (3) Notifies observers that content changed
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        if(sURIMatcher.match(uri) != StatusContract.STATUS_DIR)
            throw new IllegalArgumentException("Illegal uri:" + uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(StatusContract.TABLE, null,values, SQLiteDatabase.CONFLICT_IGNORE);

        if(rowID != -1) {
            long id = values.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);
            //notifies all observers of this content that a change has occurred
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return ret;
    }

    @Override
    //same as update
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        switch(sURIMatcher.match(uri)){
            case(StatusContract.STATUS_DIR):
                where = (selection == null)? "1" : selection;
                break;
            case(StatusContract.STATUS_ITEM):
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID + "="+ id+
                        (TextUtils.isEmpty(selection) ? "" : " and ("+ selection+ ")");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(StatusContract.TABLE, where, selectionArgs);
        if(ret>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "deleted Records: " + ret);
        return ret;
    }

    @Override
    //(1)Checks Uri creates WHERE argument (2) Updates WHERE selection (3) update returns >0 on pass
    //(4) Notify content observers
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;
        switch(sURIMatcher.match(uri)){
            case(StatusContract.STATUS_DIR):
                where = selection;
                break;
            case(StatusContract.STATUS_ITEM):
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID + "="+ id+
                        (TextUtils.isEmpty(selection) ? "" : " and ("+ selection+ ")");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(StatusContract.TABLE, values, where, selectionArgs);
        if(ret>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "Updated Records: " + ret);
        return ret;
    }
}
