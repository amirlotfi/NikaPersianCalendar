package ir.nikatech.persiancalendar;

import android.support.multidex.MultiDexApplication;

import ir.tapsell.sdk.Tapsell;
import me.cheshmak.android.sdk.core.Cheshmak;

public class CalendarApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Cheshmak.with(getApplicationContext());
        Cheshmak.initTracker("EAN5yetPG4O+pXNLUQibtg==");
        Tapsell.initialize(this, "fpngqttaafbotikfpagdohbmgehsdgltadihfoamogrcobcjlghbrdjpkoaceghihpslpe");
    }
}
