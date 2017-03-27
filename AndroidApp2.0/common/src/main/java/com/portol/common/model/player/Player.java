package com.portol.common.model.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.payment.Payment;
import com.portol.common.model.user.User;

import org.mongojack.Id;

import java.io.Serializable;
import java.util.UUID;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;


//@ApiObject(description = "Sent from javascript player to load balancer and to cloud player (when possible)")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player implements Serializable, Comparable<Player>, Rush {

    public static final String SPLASH_SCREEN = "SPLASH_SCREEN";
    public static final String STREAMING = "STREAMING";
    public static final String REPEAT_SCREEN = "REPEAT_SCREEN";
    public static final String DONE_STREAMING = "DONE_STREAMING";
    public static final String FAILURE_STREAM = "FAILURE_STREAM";
    public static final String FAILURE_PAYMENT = "FAILURE_PAYMENT";
    public static final String PAUSED = "PAUSED";
    public static final String STOPPED = "STOPPED";
    public static final String DEAD = "DEAD";
    /**
     *
     */
    private static final long serialVersionUID = -6144452656037406537L;
    public final String UNINITIALIZED = "UNINITIALIZED";
    //@ApiObjectField(description = "A unique server-generated ID for this play", format = "long", allowedvalues = "read only variable, player should not modify")
    @Id
    public String playerId = UUID.randomUUID().toString();


    // uninitialized: value when new player queries system for first time
    // splash screen: data for splash screen has been sent to the player
    // streaming: payment received for video, should be connecting/connected to
    // cloud player. The watch again screen has not yet been transmitted
    // Repeat_screen: player has finished at least one streaming session, and
    // the watch again screen data has been transmitted
    // Done streaming: successful transaction completed, this can be marked for
    // later garbage collection if desired
    // Failure stream: Payment was successful, but stream was cut short. Refund
    // should be issued
    // Failure_payment: Bad payment, maybe a double spend, should begin watching IP for bad behavior
    private Long referrerId;
    //private String userId;
    private String userAgent;
    private String ApiKey; // unused as of yet, could be used for the 'free
    private String videoKey;
    private StreamState sState;
    private boolean locked;
    private String playerIP;
    private String qrURL;
    private String status = UNINITIALIZED;
    private PortolPlatform hostPlatform;
    private Preview previewStatus = Preview.NONE;
    //time the payment went through + video playback started
    private long timeStarted;
    private User loggedIn;
    private long initialConnect;
    //time when the payment period paid for is over
    private long timeExpire;
    private Payment playerPayment;
    private String btcAddress;

    //time of last requested segment
    private long lastRequest;

    //tracks the number of cloud players used
    private int numPlayersUsed = 0;
    //null when no cloud player in use
    private String currentCloudPlayerId = null;

    //private Type profile;
    private String currentSourceIP = null;
    private int numPlays;

    public Player(String playerIP, Long playerId, String apiKey, String videoKey) {
        super();
        this.playerIP = playerIP;
        this.referrerId = playerId;
        ApiKey = apiKey;
        this.videoKey = videoKey;
    }

    public Player() {
        super();
    }

    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }

    //used to quickly find a player when a payment comes through

    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    @Override
    public void delete() {
        RushCore.getInstance().delete(this);
    }

    @Override
    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    @Override
    public String getId() {
        return RushCore.getInstance().getId(this);
    }

    public void setId(String id) {
        this.playerId = id;
    }

    public Preview getPreviewStatus() {
        return previewStatus;
    }

    public void setPreviewStatus(Preview previewStatus) {
        this.previewStatus = previewStatus;
    }

    public int getNumPlayersUsed() {
        return numPlayersUsed;
    }

    public void setNumPlayersUsed(int numPlayersUsed) {
        this.numPlayersUsed = numPlayersUsed;
    }

    public int getNumPlays() {
        return numPlays;
    }

    public void setNumPlays(int numPlays) {
        this.numPlays = numPlays;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(Long referrerId) {
        this.referrerId = referrerId;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public long getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(long timeExpire) {
        this.timeExpire = timeExpire;
    }

    public long getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(long lastRequest) {
        this.lastRequest = lastRequest;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String addPlayerUsed(String playerid) {
        return playerid;
    }

    public String getPlayerIP() {
        return playerIP;
    }

    public void setPlayerIP(String playerIP) {
        this.playerIP = playerIP;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getApiKey() {
        return ApiKey;
    }

    public void setApiKey(String apiKey) {
        ApiKey = apiKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Payment getPlayerPayment() {
        return playerPayment;
    }

    public void setPlayerPayment(Payment playerPayment) {
        this.playerPayment = playerPayment;
    }

    public long getInitialConnect() {
        return initialConnect;
    }

    public void setInitialConnect(long initialConnect) {
        this.initialConnect = initialConnect;
    }

    public String getBtcAddress() {
        return btcAddress;
    }

    public void setBtcAddress(String btcAddress) {
        this.btcAddress = btcAddress;
    }

    public String getCurrentCloudPlayerId() {
        return currentCloudPlayerId;
    }

    public void setCurrentCloudPlayerId(String currentCloudPlayerId) {
        this.currentCloudPlayerId = currentCloudPlayerId;
    }

    public String getCurrentSourceIP() {
        return currentSourceIP;
    }

    public void setCurrentSourceIP(String currentSourceIP) {
        this.currentSourceIP = currentSourceIP;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public User getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(User loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getQrURL() {
        return qrURL;
    }

    public void setQrURL(String qrURL) {
        this.qrURL = qrURL;
    }

    public StreamState getsState() {
        return sState;
    }

    public void setsState(StreamState sState) {
        this.sState = sState;
    }

    public PortolPlatform getHostPlatform() {
        return hostPlatform;
    }

    public void setHostPlatform(PortolPlatform hostPlatform) {
        this.hostPlatform = hostPlatform;
    }

    @Override
    public int compareTo(Player another) {
        if (this.getLastRequest() > another.getLastRequest()) {
            //this should be ahead
            return -1;

        }

        if (this.getLastRequest() < another.getLastRequest()) {
            //this should be behind
            return 1;

        }

        return 0;

    }

    public enum Preview {
        NONE, REQUESTED, STREAMING, DONE
    }

}
