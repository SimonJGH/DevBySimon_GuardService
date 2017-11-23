package com.simon.guardservice.jobschedule;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.simon.guardservice.ActivityCollector;
import com.simon.guardservice.MainActivity;
import com.simon.guardservice.doubleprocess.LocalGuardService;
import com.simon.guardservice.doubleprocess.RemoteGuardService;

import java.util.List;

import static com.simon.guardservice.ActivityCollector.isActivityExist;


/**
 * Created by Simon on 2017/9/13.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GuardJobProcess extends JobService {
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(GuardJobProcess.this, "MyJobService", Toast.LENGTH_SHORT).show();
            JobParameters param = (JobParameters) msg.obj;
            jobFinished(param, true);
            boolean isLocalServiceWork = isServiceWork(GuardJobProcess.this, "com.simon.guardservice.doubleprocess.LocalGuardService");
            boolean isRemoteServiceWork = isServiceWork(GuardJobProcess.this, "com.simon.guardservice.doubleprocess.RemoteGuardService");
            boolean isExist = ActivityCollector.isActivityExist(MainActivity.class);
            Log.i("Simon", "isLocalServiceWork = " + isLocalServiceWork + "  isRemoteServiceWork = " + isRemoteServiceWork + "  isExist = " + isExist);
            if (!isLocalServiceWork || !isRemoteServiceWork) {
                startService(new Intent(GuardJobProcess.this, LocalGuardService.class));
                startService(new Intent(GuardJobProcess.this, RemoteGuardService.class));
                Toast.makeText(GuardJobProcess.this, "进程启动", Toast.LENGTH_SHORT).show();
            }
            if (!isExist) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            // startActivity(new Intent(GuardJobProcess.this, MainActivity.class));
            return true;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Message m = Message.obtain();
        m.obj = params;
        mHandler.sendMessage(m);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacksAndMessages(null);
        return false;
    }

    // 判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
