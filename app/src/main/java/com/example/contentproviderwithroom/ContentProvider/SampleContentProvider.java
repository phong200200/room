package com.example.contentproviderwithroom.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.contentproviderwithroom.Dao.MenuDao;
import com.example.contentproviderwithroom.DatabaseHelper.SampleDatabase;
import com.example.contentproviderwithroom.Model.Menu;

import java.util.ArrayList;

public class SampleContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.contentproviderwithroom.ContentProvider";

    public static final Uri URI_MENU = Uri.parse("content://" + AUTHORITY + "/" + Menu.TABLE_NAME);

    private static final int CODE_MENU_DIR = 1;
    private static final int CODE_MENU_ITEM = 2;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, Menu.TABLE_NAME, CODE_MENU_DIR);
        MATCHER.addURI(AUTHORITY, Menu.TABLE_NAME + "/*", CODE_MENU_ITEM);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final int code = MATCHER.match(uri);
        if (code == CODE_MENU_DIR || code == CODE_MENU_ITEM) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            MenuDao menu = SampleDatabase.getInstance(context).menu();
            final Cursor cursor;
            if (code == CODE_MENU_DIR) {
                cursor = menu.selectAll();
            } else {
                cursor = menu.selectById(ContentUris.parseId(uri));
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_MENU_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + Menu.TABLE_NAME;
            case CODE_MENU_ITEM:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + Menu.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        switch (MATCHER.match(uri)) {
            case CODE_MENU_DIR:
                final Context context = getContext();
                if (context == null) {
                    return null;
                }
                final long id = SampleDatabase.getInstance(context).menu()
                        .insert(Menu.fromContentValues(contentValues));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_MENU_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        switch (MATCHER.match(uri)) {
            case CODE_MENU_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_MENU_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final int count = SampleDatabase.getInstance(context).menu()
                        .deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        switch (MATCHER.match(uri)) {
            case CODE_MENU_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_MENU_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final Menu menu = Menu.fromContentValues(contentValues);
                menu.id = ContentUris.parseId(uri);
                final int count = SampleDatabase.getInstance(context).menu()
                        .update(menu);
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final Context context = getContext();
        if (context == null) {
            return new ContentProviderResult[0];
        }
        final SampleDatabase database = SampleDatabase.getInstance(context);
        database.beginTransaction();
        try {
            final ContentProviderResult[] result = super.applyBatch(operations);
            database.setTransactionSuccessful();
            return result;
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        switch (MATCHER.match(uri)) {
            case CODE_MENU_DIR:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final SampleDatabase database = SampleDatabase.getInstance(context);
                final Menu[] menus = new Menu[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    menus[i] = Menu.fromContentValues(valuesArray[i]);
                }
                return database.menu().insertAll(menus).length;
            case CODE_MENU_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
