package com.portol.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.portol.R;

import com.portol.Portol;
import com.portol.adapter.NavDrawerListAdapter;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.user.User;
import com.portol.common.model.user.UserSettings;
import com.portol.dataobject.DrawerItem;
import com.portol.fragment.clickr.PortolClickrPanel;
import com.portol.fragment.content.ItemFocusFragment;
import com.portol.fragment.content.PortolContentPanel;
import com.portol.animation.ViewAnimator;
import com.portol.client.PortolClient;
import com.portol.common.model.PortolToken;

import com.portol.fragment.user.LoginOrRegisterFragment;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.service.DatabaseService;
import com.portol.service.UserService;
import com.portol.view.RoundedImageView;


import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.ZBarScannerView;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

//public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener, LoginOrRegisterFragment.OnLoginOrRegisterListener, ProposalFragment.OnDecisionListener, ViewAnimator.ViewAnimatorListener {
public class MainActivity extends AppCompatActivity implements /*OnSharedPreferenceChangeListener,*/ ViewAnimator.ViewAnimatorListener {

    public static final String loadserver = "https://portol.me";
    public static final String TAG = "MainActivity";
    //public static final String loadserver = "http://localhost";
    public static final int playerport = 8443;
    public static final int paymentport = 9090;
    public static final int cloudplayerport = 3033;
    public static final int contentport = 8080;
    public static final String apiKey_payment = "foo";
    public static final String DEFAULT_CURRENCY = "Bits";
    public static final int DEVICE_REQUEST = 2;
    static final int LOGIN_ACTIVITY_REQUEST = 100;  // The request code
    static final int ADD_BUY_ACTIVITY_REQUEST = 101;
    private static final int qrport = 30303;
//
//    public Player getPaired() {
//        return paired;
//    }
    private static ContentMetadata currentlyViewing = null;
    boolean mUserBounded;
    boolean mDBBounded;
    boolean addDeviceActivitySuccessful = false;
    boolean loggingInActivitySuccessful = false;
    AppConnectResponse resp;
    boolean itemFocusBuySuccessful = false;
    private PlayerRepository playerRepo;
    private boolean itemFocusRequested = false;
    private Portol app;
    //private View mHeaderView;
    private DrawerLayout drawerLayout;

//    public User getCurrentUser() {
//        return currentUser;
//    }
//
//    private User currentUser;
    private LinearLayout linearLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ViewAnimator viewAnimator;
    private ContentMetadata purchased;
    private ListView mDrawerList;
    private NavDrawerListAdapter mAdapter;
    private List<DrawerItem> dataList;
    private PortolToken currentToken = null;
    private Button registerSubmit;
    private EditText emailInput;
    private ContentRepository contentRepo;
    private DatabaseService mUpdaterSvc;
    ServiceConnection mDBConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_LONG).show();
            mDBBounded = false;
            mUpdaterSvc = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mDBBounded = true;
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder) service;
            mUpdaterSvc = (DatabaseService) mLocalBinder.getUpdaterInstance();
        }
    };
    private UserService mUserSvc;
    ServiceConnection mUserConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_LONG).show();
            mUserBounded = false;
            mUserSvc = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mUserBounded = true;
            UserService.LocalBinder mLocalBinder = (UserService.LocalBinder) service;
            mUserSvc = (UserService) mLocalBinder.getUpdaterInstance();
        }
    };
    private PortolClient pClient;
    private ActionBarDrawerToggle mDrawerToggle;

//    public void unPair() {
//
//        playerRepo.unPairPlayer(paired);
//        this.paired = null;
//    }
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    /*
     * Panels (Explained in long note in onCreate)
     * ordered by importance
     */
    private PortolContentPanel mContentPanel;
    private PortolClickrPanel mClickrPanel;
    //private PortolAccountActivity mAccountPanel;
    private PortolActionBar mAction;
    private BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ("com.portol.Purchase"):
                    Log.i(TAG, "received intent: " + intent.getAction());
                    String playerId = intent.getStringExtra("playerId");
                    String itemPurchased = intent.getStringExtra("itemPurchased");

                    try {
                        purchased = contentRepo.findByParentContentId(itemPurchased);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Player paired = playerRepo.findPlayerWithId(playerId);

                    mClickrPanel.onBuyConfirm(paired.getCurrentSourceIP(), paired.getPlayerId());
                    itemFocusBuySuccessful = true;
                    // mClickrPanel.displaySmallClickr();
                    //TODO cache player objects locally, useful for devices!
                    //pairedPlayer = app.getPlayerRepo().getPlayer();

                    try {
                        updateToolbar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Thanks for purchasing: " + purchased.getChannelOrVideoTitle(), Toast.LENGTH_LONG).show();


                    break;

                case ("com.portol.ItemFocus"):
                    String contentId = intent.getStringExtra("contentId");

                    mContentPanel.prepTransition(contentId);
                    itemFocusRequested = true;
                    break;

                case ("com.portol.Login"):
                    try {
                        updateToolbar();

                        //currentUser = (User) intent.getSerializableExtra("loggedIn");

//                        paired = playerRepo.getPaired();
//                        if(paired != null){
//
//                            mClickrPanel.displaySmallClickr();
//                        }
                        if (mClickrPanel.getCurrentFocusedFragment() != null && mClickrPanel.getCurrentFocusedFragment() instanceof LoginOrRegisterFragment) {
                            mClickrPanel.revertToPairingOptions();
                        }
                        // mUserSvc.onLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case ("com.portol.PlayerUpdate"):
                    try {
//                        updateToolbar();
//
//                        currentUser = (User) intent.getSerializableExtra("loggedIn");
//
//                        mClickrPanel.revertToPairingOptions();
                        if (mDBBounded) {
                            mUserSvc.onLogin(mUpdaterSvc.isDbInited());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ("com.portol.UserUpdate"):
                    try {

                        updateToolbar();
                    } catch (Exception e) {
                        Log.e(TAG, "error updating user mainactivity intent receiver", e);
                    }
                default:
                    break;
            }

        }
    };
    Thread initialLoader = new Thread() {
        @Override
        public void run() {
            while (mUpdaterSvc == null) {
                Log.i(TAG, "waiting for DB to connect");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (!mUpdaterSvc.isDbInited()) {
                Log.i(TAG, "waiting for DB to init");
            }
//            try {
//                if (null != app.getUserRepo().getLoggedInUser()) {
//                    currentUser = app.getUserRepo().getLoggedInUser();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            playerRepo.removeAllUnpaired();
            User currentUsr = null;
            try {
                currentUsr = app.getUserRepo().getLoggedInUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (currentUsr != null) {

                try {
                    getUserSvc().updateUserInfo();
                    getUserSvc().onLogin(mUpdaterSvc.isDbInited());
                } catch (Exception e) {
                    Log.e("updater service", "error", e);
                }


                if (playerRepo.getAllActivePlayers().size() == 0) {
                    mClickrPanel.displayPairing();
                } else {
                    //TODO re-establish connections?
                    Player paired = playerRepo.getAllActivePlayers().get(0);

                    mClickrPanel.displaySmallClickr(paired);
                }
            } else {
                mClickrPanel.displayLogin();
            }
            //mAccountPanel.displayDefault();
            setupDrawer();
            try {
                updateToolbar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public Portol getApp() {
        return app;
    }

    public DatabaseService getmUpdaterSvc() {
        return mUpdaterSvc;
    }

    public PortolClient getpClient() {
        return pClient;
    }

    public void expandContentBy(float freed) {


        this.mContentPanel.expandBy(freed);
    }

    public void contractContentBy(float needed) {
        this.mContentPanel.contractBy(needed);
    }

    public UserService getUserSvc() {
        return this.mUserSvc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (Portol) this.getApplication();
        this.contentRepo = app.getContentRepo();
        this.playerRepo = app.getPlayerRepo();
        Intent mIntent = new Intent(this, DatabaseService.class);
        bindService(mIntent, mDBConnection, BIND_AUTO_CREATE);

        Intent mIntent2 = new Intent(this, UserService.class);
        bindService(mIntent2, mUserConnection, BIND_AUTO_CREATE);

        pClient = new PortolClient(this);
        //SharedPreferences quickprefs = PreferenceManager.getDefaultSharedPreferences(this);
        //quickprefs.registerOnSharedPreferenceChangeListener(this);

		/*
         * Think of this as a threaded UI designed to package similar functionality, so the user knows
		 * where to look for things. There are three threads (called panels):
		 *
		 * 	1. Media Content (referred to as just content)
		 * 		- Categories (Search, Most viewed, football, etc.)
		 * 		- Content streams (channels and videos)
		 * 	2. Clickr
		 * 		-Play/Pause/Stop
		 * 		-Pair to player using scan
		 * 	3. Account
		 * 		- Login & Register
		 * 		- Manage credits
		 * 	 	- Earn credits
		 * 		- Edit settings
		 *
		 * This main content container relies on the main_content_clickr_user layout.
		 * 	- On my nexus, I like having all three stacked up at 33% each. Perhaps, on focus, the
		 * 	fragment of interest will jump to 50% and push the other two down. Perhaps, also, the
		 * 	user can adjust the size of the three panels manually.
		 * 	- However, the purpose of this is to package functionality into designated modules. Thus,
		 * 	on smaller screens, we could have Media Content & Account on one screen, with Clickr always
		 * 	available on a screen "to the right".
		 *
		 */

        setContentView(R.layout.main_content_clickr_user);


        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addDrawerItems();
        mDrawerList.setOnItemClickListener(new NavItemClickHandler());


        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar) mCustomView.getParent();//first get parent toolbar of current action bar
        parent.setContentInsetsAbsolute(0, 0);//


        mAction = new PortolActionBar();
        mAction.currency = (TextView) findViewById(R.id.currencyTool);
        mAction.userName = (TextView) findViewById(R.id.unameTool);
        mAction.currentCurrency = DEFAULT_CURRENCY;
        mAction.icon = (RoundedImageView) findViewById(R.id.usericTool);
        mAction.userInfoHolder = (LinearLayout) findViewById(R.id.userInfoHolder);
        mAction.root = (LinearLayout) findViewById(R.id.action_bar_root);
        mAction.loginButton = (Button) findViewById(R.id.loginButton);
        mAction.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PortolAccountActivity.class);
                startActivityForResult(i, LOGIN_ACTIVITY_REQUEST);


            }
        });


        // TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        //  mTitleTextView.setText("My Own Title");


//        mAction.userInfoHolder.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Log.d("action bar", "onClick() called");
//                if (mAction.currentCurrency.equalsIgnoreCase("Credits")) {
//                    mAction.currentCurrency = "Bits";
//                    mAction.currency.setText(currentUser.getFunds().getUserBits() + " " + mAction.currentCurrency.charAt(0));
//
//                } else if (mAction.currentCurrency.equalsIgnoreCase("Bits")) {
//                    mAction.currentCurrency = "Credits";
//                    int currentBal = currentUser.getFunds().getUserCredits();
//                    mAction.currency.setText( currentBal + " " + mAction.currentCurrency.charAt(0));
//
//                }
//            }
//        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mContentPanel = new PortolContentPanel(R.id.main_content_container, fragmentManager, this);
        mClickrPanel = new PortolClickrPanel(R.id.main_clickr_container, fragmentManager, this);
//		try {
//			mAccountPanel = new PortolAccountActivity(R.id.main_account_container, fragmentManager, this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

//		//FragmentManager fragmentManager = getFragmentManager();
//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//		CategoryGridPagerFragment categoryGridPagerFragment = new CategoryGridPagerFragment();
//		fragmentTransaction.add(R.id.main_content_container, categoryGridPagerFragment);
//		fragmentTransaction.commit();

        mContentPanel.displayContentLists();
        initialLoader.start();

    }

    private void logoutCurrentUser() throws Exception {
        boolean success = this.pClient.invalidateToken();

        if (!success) {
            Toast.makeText(MainActivity.this, "Remote token invalidation Not yet implemented", Toast.LENGTH_LONG).show();
        }
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        app.getUserRepo().logoutAllUsers();
        this.mUserSvc.onLogout();
        //mAccountPanel = new PortolAccountPanel(R.id.main_account_container, fragmentManager, this);
        //mAccountPanel.setToLogin();


        updateToolbar();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == LOGIN_ACTIVITY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                loggingInActivitySuccessful = true;

            }
        }

        if (requestCode == ItemFocusFragment.GRID_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //   paired = (Player) data.getSerializableExtra("paired");
                resp = (AppConnectResponse) data.getSerializableExtra("AppConnectResponse");

                if (resp != null) {
                    mClickrPanel.onBuyConfirm(resp);
                }
                itemFocusBuySuccessful = true;

            }
        }

        if (requestCode == DEVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // paired = (Player) data.getSerializableExtra("paired");
                resp = (AppConnectResponse) data.getSerializableExtra("AppConnectResponse");

                if (resp != null) {
                    mClickrPanel.onBuyConfirm(resp);
                }
                addDeviceActivitySuccessful = true;

            }
        }
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        if (loggingInActivitySuccessful) {
            mClickrPanel.displayPairing();
            // Reset the boolean flag back to false for next time.
            loggingInActivitySuccessful = false;
        } else if (addDeviceActivitySuccessful) {
            if (resp != null) {

                mClickrPanel.displayLargeClickr(resp.getPlayerID());
                resp = null;
            }
            addDeviceActivitySuccessful = false;
        } else if (itemFocusBuySuccessful) {
            if (resp != null) {

                mClickrPanel.displayLargeClickr(resp.getPlayerID());
                resp = null;
            }
            itemFocusBuySuccessful = false;
        } else if (itemFocusRequested) {
            mContentPanel.focusOnItem();
            itemFocusRequested = false;
        }

    }

    public void updateToolbar() throws Exception {
        //Read the logged in user info here
        //TODO optimize this

        if (this.mUpdaterSvc == null) {
            Intent mIntent = new Intent(this, DatabaseService.class);
            bindService(mIntent, mDBConnection, BIND_AUTO_CREATE);
        }

        if (this.mUserSvc == null) {
            Intent mIntent2 = new Intent(this, UserService.class);
            bindService(mIntent2, mUserConnection, BIND_AUTO_CREATE);
        }
        User currentUser = app.getUserRepo().getLoggedInUser();
//		try {
//			mUserSvc.updateUserInfo();
//		} catch(Exception e){
//			Log.e(TAG, "error updating user info", e);
//		}
//		currentUser = app.getUserRepo().getLoggedInUser();
        if (currentUser != null) {
            mAction.loginButton.setVisibility(View.GONE);
            byte[] pngRaw = Base64.decode(currentUser.getUserImg().getRawData(), Base64.DEFAULT);//currentUser.getUserImg().getRawData().getBytes(Charsets.US_ASCII);


            Bitmap bmp = BitmapFactory.decodeByteArray(pngRaw, 0, pngRaw.length);

            this.mAction.icon.setImageBitmap(bmp);

            if (currentUser.getSettings() == null) {
                Log.w(TAG, "no settings detected for user, establishing defaults");
                UserSettings settings = new UserSettings();
                settings.setPreferred(UserSettings.Currency.CREDITS);
                currentUser.setSettings(settings);
                app.getUserRepo().save(currentUser);

            }
            UserSettings.Currency preferred = currentUser.getSettings().getPreferred();

            if (preferred == UserSettings.Currency.CENTS) {
                mAction.currentCurrency = "Cents";
                String result = currentUser.getFunds().getUserCredits() + " \u00A2";
                mAction.currency.setText(result);

            } else {
                mAction.currentCurrency = "Credits";
                mAction.currency.setText(currentUser.getFunds().getUserCredits() + " " + mAction.currentCurrency.charAt(0));

            }


            this.mAction.userName.setText(currentUser.getUserName());

        } else {
            //No user
            this.mAction.userName.setText("");
            this.mAction.currency.setText("");
            this.mAction.icon.setImageResource(android.R.color.transparent);
            mAction.loginButton.setVisibility(View.VISIBLE);
        }

//        if(this.paired != null){
//            if(this.paired.getHostPlatform().getPlatformColor() != null){
//                this.mAction.root.setBackgroundColor(Color.parseColor("#" + this.paired.getHostPlatform().getPlatformColor() ));
//            }
//        } else {
//            this.mAction.root.setBackgroundColor(getResources().getColor(R.color.primary_dark_material_dark));
//        }

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void addDrawerItems() {
        dataList = new ArrayList<DrawerItem>();
        dataList.add(new DrawerItem("Browse", R.drawable.ic_launcher));
        dataList.add(new DrawerItem("Search", R.drawable.ic_launcher));

        //dataLis.add(new DrawerItem("My Bookmarks", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("My Account", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("Payment Methods", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("Earn Credit", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("Privacy Settings", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("Friends", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("My Devices", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("Purchase History", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("About", R.drawable.ic_launcher));
        dataList.add(new DrawerItem("Settings", R.drawable.ic_launcher));
        //dataList.add(new DrawerItem("Help", R.drawable.ic_launcher));

        dataList.add(new DrawerItem("Log In", R.drawable.ic_launcher));
        dataList.add(new DrawerItem("Log Out", R.drawable.ic_launcher));

        mAdapter = new NavDrawerListAdapter(this, R.layout.nav_drawer_item,
                dataList);
        mDrawerList.setAdapter(mAdapter);
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.portol.Purchase");
        filter.addAction("com.portol.ItemFocus");
        filter.addAction("com.portol.Login");
        filter.addAction("com.portol.PlayerUpdate");
        filter.addAction("com.portol.UserUpdate");
        registerReceiver(receiver2, filter);
    }

    //SCANNER LISTENER
    @Override
    public void onResume() {
        super.onResume();
        //mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        //mScannerView.startCamera();          // Start camera on resume
    }

    //SCANNER LISTENER
    @Override
    public void onPause() {
        super.onPause();
        //mScannerView.stopCamera();           // Stop camera on pause
    }

    private void startPaymentActivity(String qrcontents) {
        //TODO: Attach proposalfragment
    }

    @Override
    public ScreenShotable onSwitch(Resourceble resourceble, ScreenShotable screenShotable, int i) {
        switch (resourceble.getName()) {
            case "CLOSE":
                return screenShotable;
            default:
                return screenShotable;
        }
    }

    public PortolContentPanel getContentPanel() {
        return mContentPanel;
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        String email = sharedPreferences.getString("email", "no email specified");
//        String username = sharedPreferences.getString("username", "no username specified");
//        String password = sharedPreferences.getString("password", "no password specified");
//
//        if (key.equalsIgnoreCase("email")) {
//            //displayCurrentUser(email, username, password);
//        }
//    }

    public PortolClickrPanel getClickrPanel() {
        return mClickrPanel;
    }
	/*
	@Override
	public void onPortolLogin(AppUser loggedIn) {
		System.out.println("User logged into Portol.");
		currentUser = loggedIn;
	}
	*/

	/*
	 * Gives us a way to hold state of the Content, Clickr, Account panel objects
	 * 	& access them from fragments through the activity (this makes it android safe).
	 */

    private class PortolActionBar {
        RoundedImageView icon;
        TextView currency;//, bitcoin, cents;
        TextView userName;
        String currentCurrency = DEFAULT_CURRENCY;
        LinearLayout userInfoHolder;
        LinearLayout root;
        Button loginButton;

    }

    class NavItemClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerLayout.closeDrawers();
            switch (dataList.get(position).getItemName()) {
                case ("Log Out"):

                    mDrawerList.clearChoices();
                    try {
                        logoutCurrentUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case ("Settings"):
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, 0);
                    break;

                default: {
                    Toast.makeText(MainActivity.this, "Not yet implemented", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //public PortolAccountActivity getAccountPanel() {
    //	return mAccountPanel;
    ///}
}