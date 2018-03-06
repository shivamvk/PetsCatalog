package com.example.shivamvk.petscatalog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shivamvk.petscatalog.data.PetsContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private int mGender = 0;
    private Uri mUri;
    private static final int PetCursorID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        Intent intent = getIntent();
        mUri = intent.getData();

        if (mUri == null){
            setTitle("Add a Pet");
        } else {
            setTitle("Edit Pet");
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(PetCursorID, null, this);
        }
    }


    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.PetsEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsContract.PetsEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetsContract.PetsEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetsContract.PetsEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    public void insertPet(){
        String nameString = mNameEditText.getText().toString();
        String breedString = mBreedEditText.getText().toString();
        String weightString = mWeightEditText.getText().toString();
        int genderInteger = mGender;

        if (nameString.isEmpty() && breedString.isEmpty() && weightString.isEmpty() ){
            Toast.makeText(this, "No Pet Inserted", Toast.LENGTH_SHORT).show();
            return;
        }

        int weightInteger = 0;
        if (!weightString.isEmpty()) {
            weightInteger = Integer.parseInt(weightString);
        }
        if (breedString.isEmpty()){
            breedString = "Unknown";
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_NAME, nameString);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_BREED, breedString);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, genderInteger);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, weightInteger);

        Uri newuri = getContentResolver().insert(PetsContract.PetsEntry.ContentURI, contentValues);
        if (newuri == null){
            Toast.makeText(this, "Insertion Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "New Pet Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePet(){
        String nameString = mNameEditText.getText().toString();
        String breedString = mBreedEditText.getText().toString();
        int weightInteger = Integer.parseInt(mWeightEditText.getText().toString());
        int genderInteger = mGender;

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_NAME, nameString);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_BREED, breedString);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, genderInteger);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, weightInteger);

        int rowsUpdated = getContentResolver().update(mUri, contentValues, null, null);
        if (rowsUpdated == 1){
            Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void finished(){
        Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if (mUri == null){
                    insertPet();
                } else {
                    updatePet();
                }
                finished();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PetsContract.PetsEntry._ID,
                PetsContract.PetsEntry.COLUMN_PET_NAME,
                PetsContract.PetsEntry.COLUMN_PET_BREED,
                PetsContract.PetsEntry.COLUMN_PET_GENDER,
                PetsContract.PetsEntry.COLUMN_PET_WEIGHT
        };
        return new CursorLoader(this, mUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);

            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));

            switch (gender) {
                case PetsContract.PetsEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetsContract.PetsEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
