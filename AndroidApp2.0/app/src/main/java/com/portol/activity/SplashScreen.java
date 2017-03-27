package com.portol.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.portol.Portol;
import com.portol.R;
import com.portol.common.model.Category;
import com.portol.runnable.LoadingTask;
import com.portol.runnable.LoadingTask.*;
import com.portol.service.ConnectivityService;
import com.portol.service.DatabaseService;
import com.portol.service.UserService;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.List;

public class SplashScreen extends Activity implements LoadingTaskFinishedListener {
    public static final String TAG = "splash screen";
    private static int SPLASH_TIME_OUT = 3000;
    boolean mBoundedUser;
    boolean mBoundedDB;
    LoadingTask mTask;
    ProgressWheel progressBar;
    private Portol app;
    private SplashScreen me;
    // Splash screen timer
    private UserService mUpdaterSvc;
    ServiceConnection mConnectionUser = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(MainActivity.this, "Service is ", Toast.LENGTH_LONG).show();
            mBoundedUser = false;
            mUpdaterSvc = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mBoundedUser = true;
            UserService.LocalBinder mLocalBinder = (UserService.LocalBinder) service;
            mUpdaterSvc = (UserService) mLocalBinder.getUpdaterInstance();
        }
    };
    private DatabaseService mDatabaseSvc;
    ServiceConnection mConnectionDB = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(MainActivity.this, "Service is ", Toast.LENGTH_LONG).show();
            mBoundedDB = false;
            mDatabaseSvc = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mBoundedDB = true;
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder) service;
            mDatabaseSvc = (DatabaseService) mLocalBinder.getUpdaterInstance();


            while (!mBoundedDB && !mBoundedUser) {
                Log.i(TAG, "waiting for services to bind. DB bound? " + mBoundedDB + " User bound? " + mBoundedUser);
            }

            new LoadingTask(progressBar, me, app, mUpdaterSvc, mDatabaseSvc).execute();


        }
    };
    private ViewFlipper mPlatformFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //boring init stuff
        super.onCreate(savedInstanceState);


        //TODO: I believe the setContentView gets called here immediately before the next
        //thing happens, so you don't actually see anything.

        setContentView(R.layout.splash_screen);
//		new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//
//			@Override
//			public void run() {
//				// This method will be executed once the timer is over
//				// Start your app main activity
//				Intent i = new Intent(SplashScreen.this, MainActivity.class);
//				startActivity(i);
//
//				// close this activity
//				finish();
//			}
//		}, SPLASH_TIME_OUT);

        app = ((Portol) getApplication());


        progressBar = (ProgressWheel) findViewById(R.id.pw_spinner);

        mPlatformFlipper = (ViewFlipper) findViewById(R.id.platform_holder);


        //TODO: Animate. Requires an anim.xml file and .setInAnimation(R.anim.someIn)
        // and .setOutAnimation(R.anim.someOut)
        mPlatformFlipper.setAutoStart(true);
        mPlatformFlipper.setFlipInterval(1800);
        mPlatformFlipper.startFlipping();


        Intent connectivity = new Intent(this, UserService.class);
        startService(connectivity);

        Log.i("PORTOL APP", "Starting up database updating service");
        Intent dbSvc = new Intent(this, DatabaseService.class);
        startService(dbSvc);

        Intent mIntent2 = new Intent(this, UserService.class);
        bindService(mIntent2, mConnectionUser, BIND_AUTO_CREATE);

        Intent mIntent3 = new Intent(this, DatabaseService.class);
        bindService(mIntent3, mConnectionDB, BIND_AUTO_CREATE);


        me = this;


    }//oncreate

    // This is the callback for when your async task has finished
    @Override
    public void onTaskFinished() {
        completeSplash();
    }

    private void completeSplash() {
        startApp();
        finish(); // Don't forget to finish this Splash Activity so the user can't return to it!
    }

    private void startApp() {
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);

    }


    //run all networking stuff in separate threads
    //not strictly necessary for a splash screen but the compiler complains...
//	Thread bootTask = new Thread() {
//		public void run(){
//			try{
//
//				//perform network accesses here, load a db
//				Log.d(TAG, "in boot task, sleep to fake work, beginning network access");
//
//
//
//				app.getCategoriesRepo().refresh();
//
//
//
//
//			} catch (Exception e){ //TODO - better network exception handling
//				Log.e(TAG, "unknownboot error");
//				//die gracefully on network error
//				failToast(getResources().getString(R.string.bootTask_fail), getApplicationContext());
//
//				//MUST RETURN FOR OS TO HANDLE QUIT INTENT
//				return;
//			}
//		}
//	};
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlatformFlipper.stopFlipping();
        if (this.mUpdaterSvc != null) {
            if (mBoundedUser)
                unbindService(mConnectionUser);
            if (mBoundedDB)
                unbindService(mConnectionDB);

        }


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    //make sure to return after this method to go back to home
    public void failToast(String message, Context context) {
        // set toast message and launch toast
//		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.show();
//
//		//die gracefully
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
//		finish();
//		Log.d(TAG, "toast made, don't forget to end current method to quit");
        return;
    }
}