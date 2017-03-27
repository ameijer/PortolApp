package com.portol.fragment.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.activity.PortolGridActivity;
import com.portol.adapter.DownloadImageTask;

import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.meta.Rating;
import com.portol.common.model.player.Player;
import com.portol.fragment.PortolPanel;
import com.portol.repository.ContentRepository;


//import butterknife.ButterKnife;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class ItemFocusFragment extends Fragment {

    public static final int GRID_REQUEST = 18;
    // @Bind(R.id.title)
    TextView title;
    private ContentRepository contentRepo;
    private ImageButton backButton;
    private PortolPanel mContainingPanel;
    private ImageView coverImage;
    private TextView creatorInfo;
    private TextView info;
    private TextView rating;
    private TextView ratingText;
    private ContentMetadata focused;
    private String contentId;
    //private ImageButton playButton;
    private Button buyButton;
    private Button previewButton;
    private Button favoriteButton;
    private int resId;

    public static ItemFocusFragment newInstance(String id, int resId) {
        Bundle args = new Bundle();
        args.putString("contentId", id);
        args.putInt("resId", resId);
        ItemFocusFragment fragment = new ItemFocusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle b = getArguments();
        if (b != null) {
            this.contentId = b.getString("contentId");
            this.resId = b.getInt("resId");
        }


        this.contentRepo = ((MainActivity) mContainingPanel.holdingActivity).getApp().getContentRepo();
        try {
            this.focused = this.contentRepo.findByParentContentId(this.contentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            MainActivity mainActivity = (MainActivity) activity;
            mContainingPanel = mainActivity.getContentPanel();

        } catch (ClassCastException e) {
            throw new ClassCastException(mContainingPanel.toString()
                    + " must implement OnScannerDetectionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        View myView = inflater.inflate(resId, container, false);

        this.backButton = (ImageButton) myView.findViewById(R.id.focus_back_button);
        this.title = (TextView) myView.findViewById(R.id.focus_title);
        this.title.setText("Title: " + this.focused.getChannelOrVideoTitle());
        this.creatorInfo = (TextView) myView.findViewById(R.id.focus_creatorinfo);
        this.creatorInfo.setText(this.focused.getCreatorInfo());
        this.info = (TextView) myView.findViewById(R.id.focus_info);

        this.previewButton = (Button) myView.findViewById(R.id.preview_button);
        this.favoriteButton = (Button) myView.findViewById(R.id.favorite_button);
        this.buyButton = (Button) myView.findViewById(R.id.buy_button);
        if (buyButton != null) {
            buyButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PortolGridActivity.class);
                    intent.putExtra("buyingContent", focused);
                    startActivityForResult(intent, GRID_REQUEST);
                }
            });
        }

        this.info.setText(this.focused.getInfo());

        this.coverImage = (ImageView) myView.findViewById(R.id.focus_image);
        DownloadImageTask downloader = new DownloadImageTask(this.coverImage);
        downloader.execute(this.focused.getSplashURL());

        this.rating = (TextView) myView.findViewById(R.id.focus_rating);
        this.rating.setText(Double.toString(this.focused.getRatingDouble()));

        StringBuffer textViewContents = new StringBuffer();


        if (this.focused.getRatingList() != null) {

            for (Rating rate : this.focused.getRatingList()) {
                textViewContents.append(rate.getRatingText() + "\n\n");
            }
        }

        this.ratingText = (TextView) myView.findViewById(R.id.focus_ratingText);
        this.ratingText.setText(textViewContents.toString());

        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mContainingPanel.popBackstack();
            }
        });

        try {
            ContentMetadata target = contentRepo.findByParentContentId(contentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((MainActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
