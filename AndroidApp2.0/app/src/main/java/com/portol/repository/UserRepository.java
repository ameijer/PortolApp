package com.portol.repository;

import android.content.Context;
import android.util.Log;

import com.portol.common.model.content.HistoryItem;
import com.portol.common.model.user.User;


import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by alex on 6/8/15.
 */
public class UserRepository {

    public static final String TAG = "UserRepository";
    ConcurrentNavigableMap<Object[], User> treeMap;


    public UserRepository() throws Exception {


    }


    public User getLoggedInUser() throws Exception {


        List<User> existings = new RushSearch().whereEqual("loggedIn", true).find(User.class);
        if (existings == null || existings.size() < 1) return null;
        return existings.get(0);

    }

    public synchronized User save(User updated) throws Exception {

        if (updated == null) return null;

        List<User> existings = new RushSearch().whereId(updated.getUserId()).find(User.class);
        String existingId = null;
        if (existings.size() > 0) {
            existingId = existings.get(0).getId();
        }
        String toSaveId = updated.getId();
        for (User exist : existings) {
            exist.delete();
        }

        RushCore.getInstance().registerObjectWithId(updated, updated.getUserId());
        String updatedtoSaveId = updated.getId();
        updated.save();

        String toFind = updated.getId();

        return new RushSearch().whereId(toFind).findSingle(User.class);

    }


    public List<HistoryItem> getUserHistory(String userId) throws Exception {


        User hasHistory = new RushSearch().whereEqual("userId", userId).findSingle(User.class);
        return hasHistory.getHistory();
    }

    public void logoutAllUsers() throws Exception {

        User loggedin = this.getLoggedInUser();
        loggedin.setLoggedIn(false);
        this.save(loggedin);
    }
}
