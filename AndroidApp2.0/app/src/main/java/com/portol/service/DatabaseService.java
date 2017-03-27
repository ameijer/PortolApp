package com.portol.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.portol.common.model.PortolPlatform;
import com.portol.common.model.PortolToken;
import com.portol.common.model.bookmark.Bookmark;
import com.portol.common.model.content.CategoryReference;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.HistoryItem;
import com.portol.common.model.content.meta.EPGBar;
import com.portol.common.model.content.meta.Pricing;
import com.portol.common.model.content.meta.Rating;
import com.portol.common.model.content.meta.SeriesInfo;
import com.portol.common.model.payment.Payment;
import com.portol.common.model.player.Player;
import com.portol.common.model.player.StreamState;
import com.portol.common.model.user.User;
import com.portol.common.model.user.UserFunds;
import com.portol.common.model.user.UserIcon;
import com.portol.common.model.user.UserSettings;
import com.portol.repository.CategoriesRepository;
import com.portol.repository.ContentRepository;
import com.portol.Portol;
import com.portol.client.PortolClient;
import com.portol.common.model.Category;
import com.portol.repository.CurrencyColumn;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;

/**
 * Created by alex on 6/8/15.
 */
public class DatabaseService extends Service {

    public static final String TAG = "DatabaseService";
    private Portol app;
    private PortolClient pClient;
    private ContentRepository contentRepo;
    private CategoriesRepository catRepo;
    private boolean dbInited = false;
    Thread initier = new Thread() {
        @Override
        public void run() {
            List<Class<? extends Rush>> classes = new ArrayList<Class<? extends Rush>>();
            classes.add(Category.class);
            classes.add(User.class);
            classes.add(UserFunds.class);
            classes.add(UserIcon.class);
            classes.add(PortolPlatform.class);
            classes.add(PortolToken.class);
            classes.add(ContentMetadata.class);
            classes.add(HistoryItem.class);
            classes.add(EPGBar.class);
            classes.add(Pricing.class);
            classes.add(Rating.class);
            classes.add(UserSettings.class);
            classes.add(SeriesInfo.class);
            classes.add(Bookmark.class);
            classes.add(Player.class);
            classes.add(Payment.class);
            classes.add(StreamState.class);
            classes.add(CategoryReference.class);
            AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
            config.setClasses(classes);
            config.addRushColumn(new CurrencyColumn());
            RushCore.initialize(config);
            dbInited = true;
        }
    };
    private IBinder mBinder = new LocalBinder();

    public boolean isDbInited() {
        return dbInited;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {

        app = (Portol) getApplication();

        initier.start();
        pClient = new PortolClient(this);
        contentRepo = app.getContentRepo();
        catRepo = app.getCategoriesRepo();

    }

    //TODO more conservative calls, this will not scale well...
    public void updateAllContent() {
        try {
            List<ContentMetadata> allContent = null;
            try {
                allContent = pClient.getAllContent();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            contentRepo.resetContent();
            contentRepo.saveContentList(allContent);

            Log.d(TAG, "attempting to retreive all categories for display...");
            List<Category> allCats = null;
            try {
                allCats = pClient.getAllCategories(catRepo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catRepo.saveCategoryList(allCats);
            Log.d(TAG, "All categories downloaded.");
        } catch (Exception e) {
            Log.e(TAG, "error in updateallcontent database svc", e);
        }


    }

    @Override
    public void onDestroy() {

    }

    public class LocalBinder extends Binder {
        public DatabaseService getUpdaterInstance() {
            return DatabaseService.this;
        }
    }

}
