package com.kongzue.takephoto.util.imagechooser.api;

import android.content.Context;
import android.content.SharedPreferences;

import static com.kongzue.takephoto.TakePhotoUtil.CACHE_FOLDER_NAME;

/**
 * Preferences for ICL
 * Created by kbibek on 5/22/15.
 */
public class BChooserPreferences {
    private final static String FILE = "b_chooser_prefs";
    private final static String FOLDER_NAME = "folder_name";
    private SharedPreferences preferences;

    public BChooserPreferences(Context context) {
        preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    /**
     * Set the folder name to be used for all files or temporary files
     * @param folderName
     */
    public void setFolderName(String folderName){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FOLDER_NAME, folderName);
        editor.commit();
    }

    public String getFolderName(){
        return preferences.getString(FOLDER_NAME, CACHE_FOLDER_NAME);
    }

}
