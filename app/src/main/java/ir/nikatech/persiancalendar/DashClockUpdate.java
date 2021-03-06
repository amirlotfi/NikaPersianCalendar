package ir.nikatech.persiancalendar;

import ir.nikatech.persiancalendar.util.UpdateUtils;
import com.google.android.apps.dashclock.api.DashClockExtension;

public class DashClockUpdate extends DashClockExtension {

    @Override
    protected void onUpdateData(int reason) {
        setUpdateWhenScreenOn(true);
        UpdateUtils updateUtils = UpdateUtils.getInstance(getApplicationContext());
        updateUtils.update(false);
        publishUpdate(updateUtils.getExtensionData());
    }

}
