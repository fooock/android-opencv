package com.fooock.ticket.opencv;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;

/**
 * Check for permission in the device
 */
final class CheckPermission {

    private final Context mContext;

    /**
     * Create this object
     *
     * @param context application context
     */
    CheckPermission(final Context context) {
        mContext = context;
    }

    /**
     * Check if the given permission is granted for this process
     *
     * @param permission permission to check
     * @return true if the permission is granted, false if not
     */
    boolean isEnabled(final String permission) {
        final int callingPermission = mContext.checkPermission(permission,
                Process.myPid(), Process.myUid());
        return callingPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if any of the given permissions are granted
     *
     * @param permissions permissions to check
     * @return true if any of the permissions are granted, false otherwise
     */
    boolean hasAnyPermission(final String... permissions) {
        boolean hasPermission = false;
        for (String permission : permissions) {
            if (isEnabled(permission)) {
                hasPermission = true;
            }
        }
        return hasPermission;
    }

    /**
     * Check if all of the given permissions in the current process are granted
     *
     * @param permissions permissions to check
     * @return true if all permissions are enabled, false otherwise
     */
    boolean hasPermissions(final String... permissions) {
        boolean hasAllPerms = true;
        for (String permission : permissions) {
            if (!isEnabled(permission)) {
                hasAllPerms = false;
            }
        }
        return hasAllPerms;
    }
}