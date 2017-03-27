package com.portol.repository;

import android.util.Log;

import com.portol.common.model.PortolPlatform;
import com.portol.common.model.content.HistoryItem;
import com.portol.common.model.player.Player;
import com.portol.common.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;

import co.uk.rushorm.core.RushSearch;

/**
 * Created by alex on 6/8/15.
 */
public class PlayerRepository {

    public static final String TAG = "PlayerRepository";

    private String lastSavedId = null;


    public PlayerRepository() throws Exception {


    }

    public List<Player> getCurrentPlayers(PortolPlatform hasPlayers) throws Exception {


        List<Player> existings = new RushSearch().find(Player.class);


        ArrayList<Player> results = new ArrayList<Player>();
        for (Player player : existings) {
            Log.w(TAG, "fix this inefficient repo call for players on a platform");

            if (player.getHostPlatform().getPlatformId().equalsIgnoreCase(hasPlayers.getPlatformId())) {
                //then we have a match
                results.add(player);
            }


        }
        return results;

    }

    public Player getPaired() {
        Player paired = new RushSearch().whereIsNotNull("currentSourceIP").findSingle(Player.class);
        return paired;
    }

    public Player unPairPlayer(Player toUnpair) {
        if (toUnpair == null) return null;
        Player paired = new RushSearch().whereEqual("playerId", toUnpair.getPlayerId()).findSingle(Player.class);

        paired.setCurrentSourceIP(null);
        paired.save();

        return paired;
    }

    public Player remove(Player toRemove) {
        List<Player> existings = new RushSearch().whereEqual("playerId", toRemove.getPlayerId()).find(Player.class);

        for (Player exist : existings) {
            exist.delete();
        }

        return existings.get(0);
    }

    public Player removeAll() {
        List<Player> existings = new RushSearch().find(Player.class);

        for (Player exist : existings) {
            exist.delete();
        }
        return existings.get(0);
    }


    public synchronized Player save(Player updated) throws Exception {

        if (updated == null) return null;

        List<Player> existings = new RushSearch().whereEqual("playerId", updated.getPlayerId()).find(Player.class);

        for (Player exist : existings) {
            exist.delete();
        }

        updated.save();


        lastSavedId = updated.getId();
        return new RushSearch().whereId(lastSavedId).findSingle(Player.class);

    }

    public List<Player> removeDeadPlayers() {
        List<Player> existings = new RushSearch().whereEqual("status", Player.DEAD).find(Player.class);
        for (Player exist : existings) {
            exist.delete();
        }

        return existings;
    }

    public Player findPlayerWithId(String playerId) {
        Player existings = new RushSearch().whereEqual("playerId", playerId).findSingle(Player.class);
        return existings;

    }

    public List<Player> getAllActivePlayers() {

        List<Player> ereybody = new RushSearch().whereIsNotNull("currentSourceIP").find(Player.class);


        return ereybody;
    }

    public List<Player> getAllPlayers() {

        List<Player> ereybody = new RushSearch().find(Player.class);


        return ereybody;
    }

    public List<Player> removeAllUnpaired() {
        List<Player> paired = new RushSearch().whereIsNull("currentSourceIP").find(Player.class);
        for (Player me : paired) {
            me.delete();
        }
        return paired;
    }

    public synchronized void saveAll(List<Player> toSave) {

        for (Player cur : toSave) {
            try {
                Player saved = this.save(cur);
                Log.i(TAG, "saved player with ID: " + saved.getPlayerId());
            } catch (Exception e) {
                Log.e(TAG, "error saving indiviual player into repo", e);
            }
        }

    }

    public List<Player> getPlatformPlayers(String platId) throws Exception {

        //workaround for now
        PortolPlatform skeleton = new PortolPlatform();
        skeleton.setPlatformId(platId);
        return this.getCurrentPlayers(skeleton);
    }

    public List<Player> getActivePlayersOnPlatform(PortolPlatform plat) {
        List<Player> paired = new RushSearch().whereIsNotNull("currentSourceIP").find(Player.class);
        ArrayList<Player> results = new ArrayList<Player>();
        for (Player player : paired) {
            Log.w(TAG, "fix this inefficient repo call for players on a platform");

            if (player.getHostPlatform().getPlatformId().equalsIgnoreCase(plat.getPlatformId())) {
                //then we have a match
                results.add(player);
            }


        }
        return results;
    }
}
