package com.example.shivamvk.petscatalog.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PetProvider extends ContentProvider {

    private PetDbHelper mDBHelper;

    public static final int PETS = 100;
    public static final int PETS_id = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetsContract.ContentAuthority, PetsContract.PathPets, PETS);
        sUriMatcher.addURI(PetsContract.ContentAuthority, PetsContract.PathPets + "/#", PETS_id);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                cursor = sqLiteDatabase.query(PetsContract.PetsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            case PETS_id:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(PetsContract.PetsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+ uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return PetsContract.PetsEntry.CONTENT_LIST_TYPE;
            case PETS_id:
                return PetsContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        String name = contentValues.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
        if (name == null){
            throw new IllegalArgumentException("Pet requires a valid name");
        }
        String breed = contentValues.getAsString(PetsContract.PetsEntry.COLUMN_PET_BREED);
        if (breed == null){
            throw new IllegalArgumentException("Pet requires a valid breed");
        }
        Integer gender = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
        if (gender == null && !PetsContract.PetsEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender");
        }
        Integer weight = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
        if (weight == null && weight <= 0){
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

        final int match = sUriMatcher.match(uri);
        long id;
        switch (match){
            case PETS:
                SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();
                id = sqLiteDatabase.insert(PetsContract.PetsEntry.TABLE_NAME, null, contentValues);
                if (id == -1)
                    return null;
                break;
            default:
                throw new IllegalArgumentException("Insertion not supported fot URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();
        int rowsDeleted = 0;
        switch (match) {
            case PETS:
                rowsDeleted = sqLiteDatabase.delete(PetsContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
            case PETS_id:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDatabase.delete(PetsContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
            default:
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (contentValues.size() == 0){
            return 0;
        }
        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_NAME)){
            String name = contentValues.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null){
                throw new IllegalArgumentException("Pet requires a valid name");
            }
        }
        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_BREED)){
            String breed = contentValues.getAsString(PetsContract.PetsEntry.COLUMN_PET_BREED);
            if (breed == null){
                throw new IllegalArgumentException("Pet requires a valid breed");
            }
        }
        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_GENDER)){
            Integer gender = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetsContract.PetsEntry.isValidGender(gender)){
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }
        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_WEIGHT)){
            Integer weight = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
            if (weight == null || weight <= 0){
                throw new IllegalArgumentException("Pet requires a valid weight");
            }
        }
        int match = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        int rowsaffected = 0;
        switch (match){
            case PETS:
                rowsaffected = sqLiteDatabase.update(PetsContract.PetsEntry.TABLE_NAME, contentValues, selection, selectionArgs);
            case PETS_id:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsaffected = sqLiteDatabase.update(PetsContract.PetsEntry.TABLE_NAME, contentValues, selection, selectionArgs);
            default:
        }
        if(rowsaffected != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsaffected;
    }
}
