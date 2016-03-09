package pl.edu.agh.marims.lib.intent;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IntentReader {
    public static String printIntentParams(Map<String, String> intentParams) {
        StringBuilder builder = new StringBuilder();
        builder.append("Intent params:\n");
        for (String key : intentParams.keySet()) {
            builder.append(key + ": " + intentParams.get(key) + "\n");
        }
        return builder.toString();
    }

    /**
     * Important!
     * Remember to declare a special intent in your app manifest file, it should look like this:
     * <p/>
     * <intent-filter>
     * <action android:name="android.intent.action.VIEW"/>
     * <category android:name="android.intent.category.DEFAULT"/>
     * <category android:name="android.intent.category.BROWSABLE"/>
     * <data android:host="runAppWith" android:scheme="marims"/>
     * </intent-filter>
     * <p/>
     * Given declaration allows Android System to run your app via URI
     * (e.g. marims://runAppWith?param1=value1&param2=value2)
     *
     * @param intent - should be obtained from app context
     * @return intentParams - parsed intent query parameters
     */
    public static Map<String, String> readIntentParams(Intent intent) {
        Map<String, String> intentParams = new HashMap<String, String>();
        if (Intent.ACTION_VIEW.equals(intent.getAction()) || Intent.ACTION_MAIN.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                Set<String> queryParameterNames = uri.getQueryParameterNames();
                for (String parameter : queryParameterNames) {
                    intentParams.put(parameter, uri.getQueryParameter(parameter));
                }
            }
        }
        return intentParams;
    }
}
