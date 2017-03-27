package com.portol.fragment.clickr;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.adapter.DownloadImageTask;
import com.portol.client.PortolPlayerClient;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.fragment.BaseFragment;
import com.portol.repository.ContentRepository;

/**
 * Panels that contain this fragment must implement the
 * to handle interaction events.
 * <p>
 * FALSE.. no factory method
 * Use the {@link SmallControllerFragment } factory method to
 * create an instance of this fragment.
 */
public class SmallControllerFragment extends BaseFragment {
    //TODO: (AIDAN) Wrap login or register into an interface so it can be implemented by anybody.
    //TODO: (AIDAN) Use a LoginOrRegisterFragmentFactory to return a UI for an action bar
    // TODO (GOOGLE): Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    Button mCloseButton;
    // TODO (GOOGLE): Rename and change types of parameters
    private String mParam1;
    private PortolClickrPanel mContainingPanel;
    private ImageButton mGoLeftButton;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private ImageButton mStopButton;
    private ImageButton mGoRightButton;
    private ImageButton backButton;
    private OnMediaControllerListener mOnMediaControllerListener;
    private Button maximize;
    private PortolPlayerClient clickrClient;
    private ImageView cover;
    private ContentRepository contentRepo;

    public SmallControllerFragment() {
        // Required empty public constructor
    }

    /*
 * Panels containing OnMediaController must implement this interface.
 */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }


    //TODO:
    // not actually a todo, but making a note here.
    // onCreateView apparently gets called when you use fragmentManager.add, so be aware of this.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mFragmentView = inflater.inflate(R.layout.clickr_video_controller_minimized, container, false);

        //Portions
        // mGoLeftButton = (ImageButton) mFragmentView.findViewById(R.id.go_left_button);
        mPlayButton = (ImageButton) mFragmentView.findViewById(R.id.play_button);
        mPauseButton = (ImageButton) mFragmentView.findViewById(R.id.pause_button);
        mStopButton = (ImageButton) mFragmentView.findViewById(R.id.stop_button);
        mCloseButton = (Button) mFragmentView.findViewById(R.id.closebuttonsmall);
        mCloseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //  mPlayButton.setVisibility(View.GONE);
                //  mPauseButton.setVisibility(View.VISIBLE);
                mContainingPanel.displayPairing();
            }
        });

        // mGoRightButton = (ImageButton) mFragmentView.findViewById(R.id.go_right_button);

        //backButton = (ImageButton) mFragmentView.findViewById(R.id.clickr_back_button);
//        backButton.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                mContainingPanel.revertToPairingOptions();
//            }
//        });

        //  mPauseButton.setVisibility(View.GONE);
        //getView().setBackgroundColor(Color.WHITE);

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

        // mGoLeftButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doGoLeft();
//            }
//        });

        //mGoRightButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doGoRight();
//            }
//        });

        cover = (ImageView) mFragmentView.findViewById(R.id.cover);

        ContentMetadata matching = null;
        long start = System.currentTimeMillis();
        final Player paired = mContainingPanel.getMainHolder().getApp().getPlayerRepo().getAllActivePlayers().get(0);

        long runTime = System.currentTimeMillis() - start;

        if (paired != null) {
            matching = contentRepo.findByParentContentKey(paired.getVideoKey());
        }

        if (matching != null) {
            DownloadImageTask downloader = new DownloadImageTask(cover);
            downloader.execute(matching.getSplashURL());
        }
        maximize = (Button) mFragmentView.findViewById(R.id.maximizeButton);
        maximize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when play is clicked show stop button and hide play button
                if (mContainingPanel.isMaximized) {
                    maximize.setText("^");
                } else {
                    maximize.setText("V");
                }
                float freed = mContainingPanel.maximizeToggle();
                mContainingPanel.getMainHolder().expandContentBy(freed);
                mContainingPanel.displayLargeClickr(paired.getPlayerId());
            }
        });


        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            MainActivity mainActivity = (MainActivity) activity;
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

    }

    @Override
    public void onResume() {
        super.onResume();

        //Do something everytime the activity regains focus
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
        }
//            mContainingPanel.getMainHolder().unPair();
        this.mContainingPanel.displayPairing();


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

    //mOnMediaControllerListener.onPortolRight(); }
}
