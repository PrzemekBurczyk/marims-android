package pl.edu.agh.marims.hub.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import pl.edu.agh.marims.hub.models.ApplicationFile;

public class PackageUtil {

    public static boolean isPackageInstalled(ApplicationFile applicationFile, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(applicationFile.getPackageName(), PackageManager.GET_ACTIVITIES);
            int applicationVersionCode = Integer.parseInt(applicationFile.getFileName().split("(\\()|(\\))")[1]);
            return packageInfo.versionCode == applicationVersionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
