package com.portol.fragment.clickr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.client.PortolPlayerClient;
import com.portol.common.model.SeekStatus;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.fragment.BaseFragment;
import com.portol.fragment.content.ItemFocusFragment;
import com.portol.repository.ContentRepository;

/**
 * Panels that contain this fragment must implement the
 * to handle interaction events.
 * <p>
 * FALSE.. no factory method
 * Use the {@link LargeControllerFragment } factory method to
 * create an instance of this fragment.
 */
public class LargeControllerFragment extends BaseFragment implements PortolPlayerClient.PlayerActivityListener, SeekBar.OnSeekBarChangeListener {
    public static final String TAG = "LargeControllerFragment";
    //TODO: (AIDAN) Wrap login or register into an interface so it can be implemented by anybody.
    //TODO: (AIDAN) Use a LoginOrRegisterFragmentFactory to return a UI for an action bar
    // TODO (GOOGLE): Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO (GOOGLE): Rename and change types of parameters
    private String targetPlayerId;
    private PortolClickrPanel mContainingPanel;
    private ImageButton mGoLeftButton;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private ImageButton mStopButton;
    private ImageButton mGoRightButton;
    private ImageButton backButton;
    private OnMediaControllerListener mOnMediaControllerListener;
    private Button minimize;
    private PortolPlayerClient clickrClient;
    private ImageView cover;
    private ContentRepository contentRepo;
    private SeekStatus currentSeek;
    private SeekBar primarySeek;
    private ItemFocusFragment focusFragment = null;
    private MainActivity mainActivity = null;
    private MovieFaxFragment faxFrag;


    public LargeControllerFragment() {
        // Required empty public constructor
    }

    public static LargeControllerFragment newInstance(String focusedPlayer) {
        Bundle args = new Bundle();
        args.putString("playerId", focusedPlayer);

        LargeControllerFragment fragment = new LargeControllerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            this.targetPlayerId = b.getString("playerId");
        }

        Player paired = mContainingPanel.getMainHolder().getApp().getPlayerRepo().findPlayerWithId(this.targetPlayerId);
        ContentMetadata matching = null;
        if (paired != null) {
            matching = contentRepo.findByParentContentKey(paired.getVideoKey());
        }


        focusFragment = ItemFocusFragment.newInstance(matching.getParentContentId(), R.layout.clickr_item_focus);
        faxFrag = MovieFaxFragment.newInstance(matching, R.layout.moviefax);
        faxFrag.setClient(clickrClient);

        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.clickritemfocus, focusFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.moviefaxlayoutroot, faxFrag)
                .commit();

    }


    //TODO:
    // not actually a todo, but making a note here.
    // onCreateView apparently gets called when you use fragmentManager.add, so be aware of this.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mFragmentView = inflater.inflate(R.layout.clickr_video_controller_maximized, container, false);

        //Portions
//        // mGoLeftButton = (ImageButton) mFragmentView.findViewById(R.id.go_left_button);
        mPlayButton = (ImageButton) mFragmentView.findViewById(R.id.large_clickr_play);
        mPauseButton = (ImageButton) mFragmentView.findViewById(R.id.large_clickr_pause);
        mStopButton = (ImageButton) mFragmentView.findViewById(R.id.large_clickr_stop);
//
//        // mGoRightButton = (ImageButton) mFragmentView.findViewById(R.id.go_right_button);
//
//        //backButton = (ImageButton) mFragmentView.findViewById(R.id.clickr_back_button);
////        backButton.setOnClickListener(new Button.OnClickListener() {
////            public void onClick(View v) {
////                mContainingPanel.revertToPairingOptions();
////            }
////        });
//
//        //  mPauseButton.setVisibility(View.GONE);
//        //getView().setBackgroundColor(Color.WHITE);
//
        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //  mPlayButton.setVisibility(View.GONE);
                //  mPauseButton.setVisibility(View.VISIBLE);
                doPlay();
            }
        });

        mPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // mPauseButton.setVisibility(View.GONE);
                // mPlayButton.setVisibility(View.VISIBLE);
                doPause();
            }
        });

        mStopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doStop();
                //  mPauseButton.setVisibility(View.GONE);
                //  mPlayButton.setVisibility(View.VISIBLE);
            }
        });

//        // mGoLeftButton.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                doGoLeft();
////            }
////        });
//
//        //mGoRightButton.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                doGoRight();
////            }
////        });


        minimize = (Button) mFragmentView.findViewById(R.id.minimizebutton);

        primarySeek = (SeekBar) mFragmentView.findViewById(R.id.seekr);

        primarySeek.setOnSeekBarChangeListener(this);

        minimize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                mContainingPanel.displaySmallClickr();
            }
        });
        return mFragmentView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivity = (MainActivity) activity;
            mContainingPanel = mainActivity.getClickrPanel();

            //ContainingPanel implements the media controller interface.
            mOnMediaControllerListener = mContainingPanel;

            clickrClient = mContainingPanel.getClickrClient();
            contentRepo = mContainingPanel.getMainHolder().getApp().getContentRepo();

        } catch (ClassCastException e) {
            throw new ClassCastException(mContainingPanel.toString()
                    + " must implement OnMediaControllerListener");
        }


    }

    /*
     * onDetach and onResume are for the Fragment interface.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mOnMediaControllerListener = null;
        if (clickrClient != null) {
            clickrClient.setPlayerActivityListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //Do something everytime the activity regains focus
        if (clickrClient != null) {
            clickrClient.setPlayerActivityListener(this);
        }

    }


    //TODO:ACTUALLY.. maybe this fragment wil handle play pause stops. The only thing to bubble
    // up might be external clickr shit like a qr scan or something.

    private void doPlay() {

        //doPlay
        Log.d("portol (panel)", "Portol play event seen.");
        if (clickrClient != null) {
            Player updatedState = clickrClient.sendPlay();
        }

        //  mOnMediaControllerListener.onPortolPlay();
    }

    private void doPause() {

        Log.d("portol (panel)", "Portol pause event seen.");
        if (clickrClient != null) {
            Player updatedState = clickrClient.sendPause();
        }

        // mOnMediaControllerListener.onPortolPause();
    }

    private void doStop() {

        Log.d("portol (panel)", "Portol stop event seen.");
        if (clickrClient != null) {
            Player updatedState = clickrClient.sendStop();
            this.mContainingPanel.displayPairing();
        }
        //mOnMediaControllerListener.onPortolStop();
    }

    private void doGoLeft() {

        Log.d("portol (panel)", "Portol go left event seen.");
        if (clickrClient != null) {
            Player updatedState = clickrClient.sendSeekBack();
        }
    }

    // mOnMediaControllerListener.onPortolLeft(); }
    private void doGoRight() {
        Log.d("portol (panel)", "Portol go right event seen.");
        if (clickrClient != null) {
            Player updatedState = clickrClient.sendSeekFwd();
        }
    }

    @Override
    public void onSeekUpdate(String playerId, SeekStatus updated) {
        this.currentSeek = updated;
        updateSeekBar();
    }


    protected void updateSeekBar() {

        Runnable updater = new Runnable() {

            @Override
            public void run() {
                double progress = (double) currentSeek.getProgress();
                double total = (double) currentSeek.getStreamDuration();

                double amountDone = progress / total;

                int percentDone = (int) (100 * amountDone);
                primarySeek.setProgress(percentDone);
                Log.d(TAG, "seek percentage: " + percentDone);
            }
        };
        getActivity().runOnUiThread(updater);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (currentSeek != null) {
            int targetTime = (((int) currentSeek.getStreamDuration()) * progress) / 100;
            if (fromUser) {
                SeekStatus target = new SeekStatus();

                target.setStreamDuration(currentSeek.getStreamDuration());


                target.setProgress(targetTime);
                target.setRemaining(currentSeek.getStreamDuration() - targetTime);

                Log.i(TAG, "requesting seek to: " + target.getProgress());

                if (this.clickrClient != null) {
                    clickrClient.sendSeek(target, targetPlayerId);
                } else {
                    Log.e(TAG, "No clickr client found, unable to send seek");
                }

            }
            if (faxFrag != null) {
                faxFrag.updateTime(targetTime);
            }
        } else {
            Log.e(TAG, "error, null seek status");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //mOnMediaControllerListener.onPortolRight(); }
}
