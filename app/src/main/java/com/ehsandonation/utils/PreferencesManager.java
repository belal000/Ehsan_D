package com.ehsandonation.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager
{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;


    public PreferencesManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Const.PREF_NAME, Const.PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Const.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(Const.IS_FIRST_TIME_LAUNCH, true);
    }

}
