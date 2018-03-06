package com.example.shivamvk.petscatalog.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetsContract {

    public static final String ContentAuthority = "com.example.shivamvk.petscatalog";
    public static final Uri BaseURI = Uri.parse("content://" + ContentAuthority);
    public static final String PathPets = "pets";

    public static abstract class PetsEntry implements BaseColumns{

        public static final Uri ContentURI = Uri.withAppendedPath(BaseURI, PathPets);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ContentAuthority + "/" + PathPets;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ContentAuthority + "/" + PathPets;

        public static final String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

        public static boolean isValidGender(int gender){
            if (gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN ){
                return true;
            }else {
                return false;
            }
        }

    }

}
