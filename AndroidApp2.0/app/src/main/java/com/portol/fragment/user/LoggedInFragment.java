package com.portol.fragment.user;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.activity.PortolAccountActivity;
import com.portol.common.model.user.User;
import com.portol.common.model.user.UserFunds;

/**
 * Created by alex on 7/5/15.
 */
public class LoggedInFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Logged-In Frag";

    public static final String DEFAULT_CURRENCY = "Credits";

    MainActivity mainActivity;
    ImageView icon;
    TextView currency;//, bitcoin, cents;
    TextView userName;
    User loggedInUser;
    String currentCurrency = DEFAULT_CURRENCY;


    private PortolAccountActivity mContainingPanel;

    public LoggedInFragment() {

    }

    public void onClick(View arg0) {
        Log.d(TAG, "onClick() called");
        if (currentCurrency.equalsIgnoreCase("Credits")) {
            currentCurrency = "Bits";
            currency.setText(currentCurrency + ": " + loggedInUser.getFunds().getUserBits());

        } else if (currentCurrency.equalsIgnoreCase("Bits")) {
            currentCurrency = "Credits";
            currency.setText(currentCurrency + ": " + loggedInUser.getFunds().getUserCredits());

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.account_logged_in, container, false);

        icon = (ImageView) mFragmentView.findViewById(R.id.useric);
        currency = (TextView) mFragmentView.findViewById(R.id.currency);
        currency.setOnClickListener(this);
        //bitcoin = (TextView) mFragmentView.findViewById(R.id.bitcoin);
        //cents = (TextView) mFragmentView.findViewById(R.id.cents);
        userName = (TextView) mFragmentView.findViewById(R.id.uname);

        byte[] pngRaw = loggedInUser.getUserImg().getRawData().getBytes(Charsets.US_ASCII);

        //Display icon
        Bitmap bmp = BitmapFactory.decodeByteArray(pngRaw, 0, pngRaw.length);

        icon.setImageBitmap(bmp);

        UserFunds myFunds = loggedInUser.getFunds();
        //display balances
        currency.setText("Credits: " + myFunds.getUserCredits());
        // bitcoin.setText("Bits: " + myFunds.getUserBits());
        // cents.setText("Cents: " + myFunds.getUserCents());

        userName.setText("Hello, " + this.loggedInUser.getUserName() + "!");

        return mFragmentView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.loggedInUser = mainActivity.getApp().getUserRepo().getLoggedInUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.mainActivity = (MainActivity) activity;
        //mContainingPanel = mainActivity.getAccountPanel();

    }
}
