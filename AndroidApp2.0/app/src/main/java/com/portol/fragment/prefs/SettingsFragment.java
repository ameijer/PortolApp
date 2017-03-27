package com.portol.fragment.prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.portol.Portol;
import com.portol.R;
import com.portol.common.model.user.User;
import com.portol.common.model.user.UserSettings;
import com.portol.repository.UserRepository;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by alex on 10/14/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    public static final String CURRENCY_PREF_KEY = "currencypref";
    CharSequence[] currencyEntries = new CharSequence[0];
    CharSequence[] currencyValues = new CharSequence[0];
    UserRepository userRepo;
    User focused = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs);

        final ListPreference currencyList = (ListPreference) findPreference("currencypref");

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setCurrencyListData(currencyList);
        userRepo = ((Portol) getActivity().getApplication()).getUserRepo();


    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            focused = userRepo.getLoggedInUser();
        } catch (Exception e) {
            Log.e(TAG, "error retrieving logged in user in oncreate ", e);
        }

        if (focused.getSettings() == null) {
            UserSettings defaults = new UserSettings();
            defaults.setPreferred(UserSettings.Currency.CREDITS);
            focused.setSettings(defaults);
        }
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    protected void setCurrencyListData(ListPreference lp) {

        for (UserSettings.Currency cur : UserSettings.Currency.values()) {
            currencyEntries = ArrayUtils.add(currencyEntries, cur.name());
            currencyValues = ArrayUtils.add(currencyValues, Integer.toString(cur.ordinal()));
        }

        lp.setEntries(currencyEntries);
        lp.setEntryValues(currencyValues);

        if (focused != null && focused.getSettings() != null) {
            lp.setDefaultValue(Integer.toString(focused.getSettings().getPreferred().ordinal()));
        }

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(CURRENCY_PREF_KEY)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            String prefVal = sharedPreferences.getString(key, "");

            Log.d(TAG, "currency pref changed to: " + prefVal);

            focused.getSettings().setPreferred(UserSettings.Currency.values()[Integer.parseInt(prefVal)]);
        }

        Intent intent = new Intent();
        intent.setAction("com.portol.UserUpdate");
        intent.putExtra("user", focused);
        getActivity().sendBroadcast(intent);
    }


}
