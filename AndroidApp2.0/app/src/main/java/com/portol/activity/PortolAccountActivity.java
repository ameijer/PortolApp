package com.portol.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.portol.Portol;
import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.client.PortolClient;
import com.portol.common.model.player.Player;
import com.portol.common.model.user.User;
import com.portol.fragment.PortolPanel;
import com.portol.fragment.user.LoggedInFragment;
import com.portol.fragment.user.LoginOrRegisterFragment;
import com.portol.repository.UserRepository;
import com.portol.service.UserService;

/**
 * Created by Aidan on 7/3/15.
 */
public class PortolAccountActivity extends Activity {
    Portol app;
    boolean mBounded;
    private UserRepository userRepo;
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private EditText mEmailInput;
    private Button mRegisterSubmit;
    private Button mLoginSubmit;
    private PortolClient pClient;
    private UserService mUserSvc;

    ServiceConnection mUserConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_LONG).show();
            mBounded = false;
            mUserSvc = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mBounded = true;
            UserService.LocalBinder mLocalBinder = (UserService.LocalBinder) service;
            mUserSvc = (UserService) mLocalBinder.getUpdaterInstance();
        }
    };

    protected void toggleEmail() {
        //TODO: Add logic such that register button opens emailfield if it's missing, and registers
        //		if the email was there. Actually, once the email field gets displayed -- just keep
        //		it there until somebody says something. Or if the login button gets clicked.
        if (View.GONE == mEmailInput.getVisibility()) {
            mEmailInput.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Portol) this.getApplication();
        pClient = new PortolClient(this);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Intent mIntent2 = new Intent(this, UserService.class);
        bindService(mIntent2, mUserConnection, BIND_AUTO_CREATE);

        try {
            userRepo = app.getUserRepo();
        } catch (Exception e) {
            e.printStackTrace();
        }

// Inflate the layout for this fragment
        setContentView(R.layout.account_login_or_register);

        mUsernameInput = (EditText) findViewById(R.id.userfield);
        mPasswordInput = (EditText) findViewById(R.id.passfield);
        mEmailInput = (EditText) findViewById(R.id.emailfield);
        mEmailInput.setVisibility(View.GONE);
        //getView().setBackgroundColor(Color.WHITE);


        mRegisterSubmit = (Button) findViewById(R.id.portolRegister);
        mLoginSubmit = (Button) findViewById(R.id.portolLogin);

        mRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when play is clicked show stop button and hide play button

                if (checkValidRegisterFields()) {
                    doRegister();
                } else {
                    toggleEmail();
                }

            }
        });

        mLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidLoginFields()) {
                    doLogin();
                }
            }
        });

    }

    protected void doLogin() {
        String username = mUsernameInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        //TODO: why is email here?
        String email = mEmailInput.getText().toString();

        try {
            if (checkValidLoginFields()) {
                User loggedIn = pClient.loginUser(username, password, email);

                if (loggedIn == null) {
                    Toast.makeText(this, "bad username/password combination.", Toast.LENGTH_LONG).show();
                    return;
                }
                User saved = userRepo.save(loggedIn);
                //Send back up to activity

                this.onPortolLogin(loggedIn);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("portol", "Login failed.", e);
            quitDialog(false);
        }
    }

    public void onPortolLogin(User loggedIn) {
        try {
            // this.mUserSvc.onLogin(true);
        } catch (Exception e) {
            Log.e("loginfragment", "error", e);
        }
        quitDialog(true);

    }

    protected void doRegister() {
        String username = mUsernameInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        String email = mEmailInput.getText().toString();
        User registered = null;
        try {
            registered = pClient.registerUser(username, password, email);
            userRepo.save(registered);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("portol", "Login failed.", e);
        }

        //if they aren't logged in already, perform that operation
//        if(registered != null && !registered.isLoggedIn()){
//            this.doLogin(username, password);
//        }


        //finally, clear the fields
        mUsernameInput.setText("");
        mPasswordInput.setText("");
        mEmailInput.setText("");
    }

    protected boolean checkValidRegisterFields() {
        boolean test = true;

        if ((mUsernameInput.getText().toString().isEmpty()) ||
                (mPasswordInput.getText().toString().isEmpty()) ||
                (mEmailInput.getText().toString().isEmpty())) {
            test = false;
        }
        return test;
    }

    private void quitDialog(boolean success) {
        Intent returnIntent = new Intent();
        if (success) {
            setResult(RESULT_OK, returnIntent);
        } else {
            setResult(RESULT_CANCELED, returnIntent);
        }
        finish();
    }

    protected boolean checkValidLoginFields() {
        boolean test = true;

        if ((mUsernameInput.getText().toString() == null) ||
                (mPasswordInput.getText().toString() == null)) {
            test = false;
        }

        return test;
    }


}

