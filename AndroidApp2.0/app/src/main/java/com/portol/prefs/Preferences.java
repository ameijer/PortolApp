package com.portol.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.portol.Portol;
import com.portol.R;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    public static final String preferenceFileName = "com.DirectChat.app.SharedPreferences";
    public static final String EXTRA_NAME_CHANGE = "com.DirectChat.app.IntentExtra_NameChange";
    public static final String EXTRA_STATUS_CHANGE = "com.DirectChat.app.IntentExtra_OnlineStatusChange";
    public static final String EXTRA_STATUS_CHANGE_INTENT_INDICATOR = "com.DirectChat.app.IntentExtra_OnlineStatusChange_IntentIndicator";

    public static final String loadserver = "REDACTED";
    public static final String AppServer = loadserver;
    public static final int playerport = 8443;
    public static final int appPort = 5555;
    public static final int paymentport = 9090;
    public static final int cloudplayerport = 3033;
    public static final int contentport = 8080;
    public static final String apiKey_payment = "foo";
    private static final int qrport = 30303;

    Portol app;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Portol) getApplication();
        addPreferencesFromResource(R.xml.prefs);
        Log.d("shared prefs", "prefs inflated");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //registering all sharedPreferenceListeners
        prefs.registerOnSharedPreferenceChangeListener(this);

        //default values
        String userName = prefs.getString("username", getResources().getString(R.string.preferences_changeUserNameTitle));

        EditTextPreference userNameText = (EditTextPreference) getPreferenceScreen().findPreference("username");
        userNameText.setTitle(userName);

        EditTextPreference passwordText = (EditTextPreference) getPreferenceScreen().findPreference("password");
        userNameText.setTitle("**_**");

        Preference wiper = (Preference) findPreference("register");
        //TODO: What the hell is this.. a lambda function??
        wiper.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Context context = getApplicationContext();
                //toast to show db about to be wiped
                Toast toast = Toast.makeText(context, "Registering user...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                toast.show();

                //tell user it is wiped
                Toast toast2 = Toast.makeText(context, "user registered", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                toast2.show();
                return true;
            }


        });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // TODO Auto-generated method stub

    }
}