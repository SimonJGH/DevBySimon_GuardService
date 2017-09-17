package com.simon.guardservice;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


/**
 * Created by Simon on 2017/9/13.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GuardJobProcess1 extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
        startJobSheduler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i("Simon", "-----------------restart-------------");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public void startJobSheduler() {
        try {
            //ydsimon.net.guardprocess
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), GuardJobProcess1.class.getName()));
           // JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, GuardJobProcess1.class));
            builder.setPeriodic(5);
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
