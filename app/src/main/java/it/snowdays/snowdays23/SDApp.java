package it.snowdays.snowdays23;

import android.app.Application;

public class SDApp extends Application {

    public static final class Constants {
        public static final class Prefs {
            public static final String SESSION = "sessionAccessToken";
            public static final String SESSION_REFRESH = "sessionRefreshToken";
        }
    }

}
