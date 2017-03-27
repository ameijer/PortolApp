package com.portol;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;


import com.portol.common.model.PortolPlatform;
import com.portol.common.model.PortolToken;
import com.portol.common.model.content.HistoryItem;
import com.portol.common.model.user.User;
import com.portol.common.model.user.UserFunds;
import com.portol.common.model.user.UserIcon;
import com.portol.repository.CategoriesRepository;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;
import com.portol.service.ConnectivityService;
import com.portol.service.DatabaseService;


import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.RushCore;


public class Portol extends Application {
    public static final String DATABASE_NAME = "snappy";
    private CategoriesRepository categoriesRepo;
    private ContentRepository contentRepo;
    private UserRepository peopleRepo;
    private PlayerRepository playerRepo;

    public CategoriesRepository getCategoriesRepo() {
        return categoriesRepo;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("PORTOL APP", "Starting application services");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent connectivity = new Intent(this, ConnectivityService.class);
        startService(connectivity);


        Log.d("PORTOL APP", "initing repos");
        try {
            this.peopleRepo = new UserRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.categoriesRepo = new CategoriesRepository(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.contentRepo = new ContentRepository(this.categoriesRepo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.playerRepo = new PlayerRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public CategoriesRepository getCatRepo() {
        return this.categoriesRepo;
    }

    public ContentRepository getContentRepo() {
        return this.contentRepo;
    }

    public UserRepository getUserRepo() {
        return this.peopleRepo;
    }

    public PlayerRepository getPlayerRepo() {
        return playerRepo;
    }
}
