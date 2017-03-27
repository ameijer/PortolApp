package com.portol.runnable;

/**
 * Created by alex on 8/30/15.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.portol.Portol;
import com.portol.activity.MainActivity;
import com.portol.service.DatabaseService;
import com.portol.service.UserService;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.Random;

public class LoadingTask extends AsyncTask<String, Integer, Integer> {

    public static final String TAG = "LoadingTask";
    private final int TOTAL_DELAY = 4000;
    // This is the progress bar you want to update while the task is in progress
    private final ProgressWheel progressBar;
    // This is the listener that will be told when this task is finished
    private final LoadingTaskFinishedListener finishedListener;
    private final Portol app;
    private UserService mUpdaterSvc;
    private DatabaseService dbSvc;

    /**
     * A Loading task that will load some resources that are necessary for the app to start
     *
     * @param progressBar      - the progress bar you want to update while the task is in progress
     * @param finishedListener - the listener that will be told when this task is finished
     * @param mUpdaterSvc
     * @param mDatabaseSvc
     */
    public LoadingTask(ProgressWheel progressBar, LoadingTaskFinishedListener finishedListener, Portol app, UserService mUpdaterSvc, DatabaseService mDatabaseSvc) {
        super();
        this.progressBar = progressBar;
        this.finishedListener = finishedListener;
        this.app = app;
        this.dbSvc = mDatabaseSvc;
        this.mUpdaterSvc = mUpdaterSvc;
    }

    @Override
    protected Integer doInBackground(String... params) {
//        Log.i("Tutorial", "Starting task with url: "+params[0]);
        if (resourcesDontAlreadyExist()) {
            try {
                downloadResources();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Perhaps you want to return something to your post execute
        return 1234;
    }

    private boolean resourcesDontAlreadyExist() {
        //TODO
        // Here you would query your app's internal state to see if this download had been performed before
        // Perhaps once checked save this in a shared preference for speed of access next time
        return true; // returning true so we show the splash every time
    }

    private void downloadResources() throws Exception {
        // We are just imitating some process thats takes a bit of time (loading of resources / downloading)


        progressBar.setText("Starting Database");

        int increment = (int) ((double) 90.0) / 5;
        int curProgress = increment;
        publishProgress(((int) curProgress));
        long start = System.currentTimeMillis();
        //allot up to 4 seconds for db to spin up
        while (!dbSvc.isDbInited()) {
            Thread.sleep(1000);
            curProgress += increment;
            curProgress = Math.min(90, curProgress);
            publishProgress(((int) curProgress));
            Log.i(TAG, "checking for initailzed database");
        }

        progressBar.setText("Synchronizing");

        Thread updater = new Thread() {

            @Override
            public void run() {
                try {
                    dbSvc.updateAllContent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updater.start();
        app.getCategoriesRepo().refresh();

        Random rand = new Random();
        int count = rand.nextInt(5) + 3;


        if (this.app.getUserRepo().getLoggedInUser() != null) {
            //attach ws if possible

            try {
                //         this.mUpdaterSvc.updateUserInfo();
//               this.mUpdaterSvc.onLogin(dbSvc.isDbInited());
            } catch (Exception e) {
                Log.e("updater service", "error", e);
            }
        }
        long timeUsed = System.currentTimeMillis() - start;

        int remaining = (int) Math.max(0, (this.TOTAL_DELAY - timeUsed));

        // remaining = Math.max(1500, remaining);


        int sleep = remaining / count;
        for (int i = 1; i <= count; i++) {


            // Update the progress bar after every step
            //int progress = (int) (((float)i) / ((float) count)) * 360;
            double progress = 270.0 * (((double) (i * sleep)) / ((double) remaining));
            publishProgress(((int) progress) + 90);

            // Do some long loading things
            //Sync other DBs, perform CDN location work
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ignore) {
            }
        }


        updater.join();

    }

    // private Object mutex = new Object();
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

//        synchronized (mutex) {
//            for (int i = 0; i < values[0]; i++) {
//
//                progressBar.incrementProgress();
//
//            }
//        }
        progressBar.setProgress(values[0]); // This is ran on the UI thread so it is ok to update our progress bar ( a UI view ) here
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        finishedListener.onTaskFinished(); // Tell whoever was listening we have finished
    }

    public interface LoadingTaskFinishedListener {
        void onTaskFinished(); // If you want to pass something back to the listener add a param to this method
    }
}