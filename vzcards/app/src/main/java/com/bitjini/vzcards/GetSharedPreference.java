package com.bitjini.vzcards;

import android.content.Context;

import static com.bitjini.vzcards.Constants.IS_ORGANIZATION_KEY;
import static com.bitjini.vzcards.Constants.PHONE_KEY;
import static com.bitjini.vzcards.Constants.TOKEN_KEY;
import static com.bitjini.vzcards.Constants.VZCARD_PREFS;
import static com.bitjini.vzcards.Constants.VZ_ID_KEY;
import static com.bitjini.vzcards.Constants.is_organization_sharedPreference;
import static com.bitjini.vzcards.Constants.phone_sharedPreference;
import static com.bitjini.vzcards.Constants.sharedPreferences;
import static com.bitjini.vzcards.Constants.token_sharedPreference;
import static com.bitjini.vzcards.Constants.vz_id_sharedPreference;
import static com.bitjini.vzcards.Constants.isFetched_sharedPreference;
/**
 * Created by bitjini on 25/1/17.
 */

public class GetSharedPreference {

    public static void getSharePreferenceValue(Context context) {
        sharedPreferences = context.getSharedPreferences(VZCARD_PREFS, 0);
        token_sharedPreference=sharedPreferences.getString(TOKEN_KEY,null);
        vz_id_sharedPreference=sharedPreferences.getString(VZ_ID_KEY,null);
        phone_sharedPreference=sharedPreferences.getString(PHONE_KEY,null);
        is_organization_sharedPreference=sharedPreferences.getString(IS_ORGANIZATION_KEY,null);
        isFetched_sharedPreference=sharedPreferences.getBoolean("isFetched",false);


    }

    public static boolean isOrganisation()
    {
        if(is_organization_sharedPreference.equals("false"))
        {
            return false;
        }else
        {
            return true;
        }
    }
}
