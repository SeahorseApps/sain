package com.sain.azmoon.helpers;

import android.content.pm.PackageManager;

public class PackageInspector
{
    public static boolean isPackageInstalled(String packageName, PackageManager packageManager)
    {
        try
        {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public static boolean isChromeInstalled(PackageManager packageManager)
    {
        return isPackageInstalled("com.android.chrome", packageManager);
    }

    public static boolean isFireFoxInstalled(PackageManager packageManager)
    {
        return isPackageInstalled("org.mozilla.firefox", packageManager);
    }
}