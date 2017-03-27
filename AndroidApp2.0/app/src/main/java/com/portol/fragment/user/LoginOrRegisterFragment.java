package com.portol.fragment.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.portol.Portol;
import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.activity.PortolAccountActivity;
import com.portol.client.PortolClient;
import com.portol.common.model.user.User;
import com.portol.fragment.clickr.PortolClickrPanel;
import com.portol.repository.UserRepository;
import com.portol.service.UserService;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link LoginOrRegisterFragment.OnLoginOrRegisterListener} interface
 * to handle interaction events.
 * Use the {@link LoginOrRegisterFragment} factory method to
 * create an instance of this fragment.
 */
public class LoginOrRegisterFragment extends Fragment {
    //TODO: (AIDAN) Wrap login or register into an interface so it can be implemented by anybody.
    //TODO: (AIDAN) Use a LoginOrRegisterFragmentFactory to return a UI for an action bar
    // TODO (GOOGLE): Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    MainActivity mainActivity;
    boolean mBounded;
    // TODO (GOOGLE): Rename and change types of parameters
    private String mParam1;
    private EditText mUsernameInput;
    private EditText mPasswordInput;
    private EditText mEmailInput;
    private Button mRegisterSubmit;
    private Button mLoginSubmit;
    private UserRepository userRepo;
    private UserService mUpdaterSvc;
    private PortolClient pClient;

    private OnLoginOrRegisterListener mLoginOrRegisterListener;

    private PortolClickrPanel mContainingPanel;

    public LoginOrRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        mContainingPanel = mainActivity.getClickrPanel();
        this.userRepo = ((Portol) getActivity().getApplication()).getUserRepo();
        pClient = new PortolClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.account_login_or_register, container, false);

        mUsernameInput = (EditText) mFragmentView.findViewById(R.id.userfield);
        mPasswordInput = (EditText) mFragmentView.findViewById(R.id.passfield);
        mEmailInput = (EditText) mFragmentView.findViewById(R.id.emailfield);
        mEmailInput.setVisibility(View.GONE);
        //getView().setBackgroundColor(Color.WHITE);


        mRegisterSubmit = (Button) mFragmentView.findViewById(R.id.portolRegister);
        mLoginSubmit = (Button) mFragmentView.findViewById(R.id.portolLogin);

        mRegisterSubmit.setOnClickListener(new OnClickListener() {
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

        mLoginSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidLoginFields()) {
                    doLogin();
                }
            }
        });

        float freed = mContainingPanel.expandTo(30.0F);
        mainActivity.expandContentBy(freed);

        return mFragmentView;
    }

    //TODO:
    // not actually a todo, but making a note here.
    // onCreateView apparently gets called when you use fragmentManager.add, so be aware of this.

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {


            this.mainActivity = (MainActivity) activity;
            this.mUpdaterSvc = this.mainActivity.getUserSvc();
            mContainingPanel = mainActivity.getClickrPanel();

            //ContainingPanel implements the login or register interface.

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginOrRegisterListener");
        }
    }

    public void setLoginListener(OnLoginOrRegisterListener listen) {
        this.mLoginOrRegisterListener = listen;
    }

    protected void toggleEmail() {
        //TODO: Add logic such that register button opens emailfield if it's missing, and registers
        //		if the email was there. Actually, once the email field gets displayed -- just keep
        //		it there until somebody says something. Or if the login button gets clicked.
        if (View.GONE == mEmailInput.getVisibility()) {
            mEmailInput.setVisibility(View.VISIBLE);
        }
    }

    protected void doLogin() {
        String username = mUsernameInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        //TODO: why is email here?
        String email = mEmailInput.getText().toString();

        try {
            if (checkValidLoginFields()) {
                User loggedIn = pClient.loginUser(username, password, email);

                User saved = userRepo.save(loggedIn);
                //Send back up to activity
                // if(mLoginOrRegisterListener != null) {
                this.onPortolLogin(loggedIn);
                // }
                float freed = mContainingPanel.expandTo(15.0F);
                mainActivity.expandContentBy(freed);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("portol", "Login failed.", e);
        }
    }

    private void onPortolLogin(User loggedIn) {

        try {
            this.mUpdaterSvc.onLogin(mContainingPanel.getMainHolder().getmUpdaterSvc().isDbInited());
        } catch (Exception e) {
            Log.e("loginfragment", "error", e);
        }
        System.out.println("User logged into Portol.");
        //fire off an event to the system
//        String playerId =data.getStringExtra("playerId");
//        String platformId = data.getStringExtra("platformId");
//        int position = data.getIntExtra("position", -1);
//        Player paired = (Player) data.getSerializableExtra("player");

//        //alert the system via broadcast intent
//        Toast.makeText(getActivity(), "User " + loggedIn.getUserName() + " logged in", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent();
//        intent.setAction("com.portol.Login");
//        intent.putExtra("userId", loggedIn.getUserId());
//        getActivity().sendBroadcast(intent);

    }

    protected void doRegister() {
        String username = mUsernameInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        String email = mEmailInput.getText().toString();
        User registered = null;
        try {
            registered = pClient.registerUser(username, password, email);
            this.mainActivity.getApp().getUserRepo().save(registered);
            //Send back up to the activity
            if (mLoginOrRegisterListener != null) {
                mLoginOrRegisterListener.onPortolRegister(registered);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("portol", "Login failed.", e);
        }

        //if they aren't logged in already, perform that operation
        if (registered != null && !registered.isLoggedIn()) {
            this.doLogin();
        }


        //finally, clear the fields
        mUsernameInput.setText("");
        mPasswordInput.setText("");
        mEmailInput.setText("");
    }
//
//    private void doLogin(String username, String password) {
//
//        try {
//            if(checkValidLoginFields()) {User loggedIn = pClient.loginUser(username, password, null);
//
//                //save status
//                this.mainActivity.getApp().getUserRepo().save(loggedIn);
//
//                //Send back up to activity
//                if(mLoginOrRegisterListener != null) {
//                    this.onPortolLogin(loggedIn);
//                }
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Log.e("portol", "Login failed.", e);
//        }
//    }

    protected boolean checkValidRegisterFields() {
        boolean test = true;

        if ((mUsernameInput.getText().toString().isEmpty()) ||
                (mPasswordInput.getText().toString().isEmpty()) ||
                (mEmailInput.getText().toString().isEmpty())) {
            test = false;
        }
        return test;
    }

    protected boolean checkValidLoginFields() {
        boolean test = true;

        if ((mUsernameInput.getText().toString() == null) ||
                (mPasswordInput.getText().toString() == null)) {
            test = false;
        }

        return test;
    }

    /*
     * onDetach and onResume are for the Fragment interface.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mLoginOrRegisterListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Do something everytime the activity regains focus
    }

    /*
 * Panels containing OnPortolListener must implement this interface.
 */
    public interface OnLoginOrRegisterListener {
        // TODO: Update argument type and name
        public void onPortolLogin(User loggedIn);

        public void onPortolRegister(User registered);
    }
}
