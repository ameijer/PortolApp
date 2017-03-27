package com.portol.fragment.clickr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.portol.R;
import com.portol.activity.AddDeviceActivity;
import com.portol.activity.MainActivity;
import com.portol.common.model.content.HistoryItem;
import com.portol.fragment.BaseFragment;
import com.portol.repository.UserRepository;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Aidan on 7/4/15.
 */
public class PairingChoiceFragment extends BaseFragment {

    public static final int QR_POSIT = 0;
    public static final int TEXT_POSIT = 1;
    OnOptionSelectedListener mCallback;
    private ImageButton qrButton;
    private ImageButton textPairButton;
    private Toast mToast;


    public PairingChoiceFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnOptionSelectedListener) activity;


        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    //TODO:
    // not actually a todo, but making a note here.
    // onCreateView apparently gets called when you use fragmentManager.add, so be aware of this.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.pairing_options, container, false);

        // mScanButton = (Button) mFragmentView.findViewById(R.id.qrScan);
        textPairButton = (ImageButton) mFragmentView.findViewById(R.id.text_pair_button);
        qrButton = (ImageButton) mFragmentView.findViewById(R.id.qr_scan_button);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onOptionSelected(QR_POSIT);
            }
        });

        textPairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onOptionSelected(TEXT_POSIT);
            }
        });


        return mFragmentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    @Override
    public void onResume() {
        super.onResume();

        //Do something everytime the activity regains focus
    }

    // Container Activity must implement this interface
    public interface OnOptionSelectedListener {
        public void onOptionSelected(int position);
    }

}
