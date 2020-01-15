package com.example.n1app_mdex;

import android.content.Context;
import android.content.pm.PackageManager;


public class RuntimePermissionUtils {
    private RuntimePermissionUtils() {
    }

    public static boolean hasSelfPermissions( Context context,  String... permissions) {
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}