package id.net.gmedia.perkasaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPreferences {
    private static final String LOGIN_PREF = "login_status";
    private static final String ID_PREF = "id_user";

    private static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void Login(Context context, String id){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, true);
        editor.putString(ID_PREF, id);

        //Melakukan perubahan terhadap Shared Preferences secara asinkron
        //lebih cepat, namun tidak memiliki nilai balikan
        editor.apply();
    }

    public static void LogOut(Context context){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, false);
        editor.putString(ID_PREF, "");

        editor.apply();
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static String getUserId(Context context){
        return getPreferences(context).getString(ID_PREF, "");
    }
}
