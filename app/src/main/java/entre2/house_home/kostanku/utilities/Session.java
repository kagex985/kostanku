package entre2.house_home.kostanku.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import entre2.house_home.kostanku.models.User;

/**
 * Created by chris on 11/03/2017.
 */

public class Session {

    private SharedPreferences sharedPreference;

    public Session(Context context){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUser(User user){
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString("user", json);
        edit.apply();
        edit.commit();
    }

    public User getUser(){
        Gson gson = new Gson();
        String json = sharedPreference.getString("user", null);
        User user = gson.fromJson(json, User.class);
        return user;
    }

    public void setSession(String key, String value){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(key, value);
        edit.apply();
        edit.commit();
    }

    public String getSession(String key){
        String value = sharedPreference.getString(key, null);
        return value;
    }

    public void logoutSession(){
        setUser(null);
    }

}

