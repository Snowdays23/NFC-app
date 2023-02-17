package it.snowdays.snowdays23.util.platform;

import android.os.Build;

import it.snowdays.snowdays23.BuildConfig;

public class UserAgent {

    private static final String sUserAgent;

    static {
        sUserAgent = "Snowdays23/" + getAppVersion() +
                " (Linux; Android " + getAndroidVersion() + ")";
    }

    private static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private static String getAndroidVersion() {
        return Build.VERSION.CODENAME;
    }

    public static String get() {
        return sUserAgent;
    }

}
