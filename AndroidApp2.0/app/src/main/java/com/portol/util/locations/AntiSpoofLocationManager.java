package com.portol.util.locations;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

/**
 * Created by Aidan on 8/1/15.
 */
public class AntiSpoofLocationManager {
    LocationManager mLocationManager;
    Context mContext;


    public AntiSpoofLocationManager(Context context) {
        mContext = context;
    }

    public boolean isSpoofDetected() {
        boolean spoofDetected = false;

        //Check if in developer mode with location spoofing and there are apps with permissions
        if (isMockSettingsOn()) {
            spoofDetected = isMockPermissionApps();
        }

        return spoofDetected;
    }

    /*
     * Check if the phone is in developer mode and user has enabled mock location injection (disabled by default)
     */
    private boolean isMockSettingsOn() {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    /*
     * Check if any installed apps have control of Mock location data.
     */
    private boolean isMockPermissionApps() {

        int count = 0;

        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(mContext.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("portol", "Anti spoof package manager error " + e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

}
