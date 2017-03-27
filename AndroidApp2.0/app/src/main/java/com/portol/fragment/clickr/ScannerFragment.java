package com.portol.fragment.clickr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.portol.R;
import com.portol.activity.AddDeviceActivity;
import com.portol.activity.MainActivity;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ScannerFragment.OnScannerDetectionListener} interface
 * to handle interaction events.
 * Use the {@link ScannerFragment} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
    // TODO (GOOGLE): Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    AddDeviceActivity parent;
    // TODO (GOOGLE): Rename and change types of parameters
    private String mParam1;
    private RelativeLayout mCameraHolder;
    private ZBarScannerView mScannerView;
    private FrameLayout cameraFrame;
    ;
    private ImageButton backButton;
    private OnScannerDetectionListener mScannerDetectionListener;
    private PortolClickrPanel mContainingPanel;

    public ScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlusOneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScannerFragment newInstance(String param1, String param2) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.clickr_camera_holder, container, false);
        mCameraHolder = (RelativeLayout) mFragmentView.findViewById(R.id.camera_holder);
        //mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        mScannerView = new ZBarScannerView(inflater.getContext());
        cameraFrame = (FrameLayout) mFragmentView.findViewById(R.id.camera_frame);
        mCameraHolder.addView(mScannerView);

        backButton = (ImageButton) mFragmentView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mScannerView.stopCamera();           // Stop camera on pause
                parent.popBackstack();
            }
        });


        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mScannerDetectionListener = (OnScannerDetectionListener) activity;
            parent = (AddDeviceActivity) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(mContainingPanel.toString()
                    + " must implement OnScannerDetectionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
        //Do something everytime the activity regains focus
    }

    //SCANNER LISTENER
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mScannerDetectionListener = null;
        mScannerView.stopCamera();           // Stop camera on pause
    }

    //SCANNER LISTENER
    @Override
    public void handleResult(Result result) {
        // Pass the result back up to activity
        //TODO: Pre-process the result here.
        mScannerDetectionListener.onQrDetection(result);
        mScannerView.stopCamera();           // Stop camera on pause
        return;
    }

    // Container Activity must implement this interface
    public interface OnScannerDetectionListener {
        public void onQrDetection(Result result);
    }
}