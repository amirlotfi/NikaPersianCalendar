package ir.nikatech.persiancalendar.service;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import co.ronash.pushe.Pushe;
import me.cheshmak.android.sdk.core.Cheshmak;

public class RefreshService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Pushe.initialize(getApplicationContext(),true);
        Cheshmak.with(getApplicationContext());
        Cheshmak.initTracker("EAN5yetPG4O+pXNLUQibtg==");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
