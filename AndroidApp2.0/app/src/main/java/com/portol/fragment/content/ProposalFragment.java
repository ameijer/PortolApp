package com.portol.fragment.content;


import android.app.Activity;
import android.os.Bundle;

import com.andtinder.view.CardContainer;
import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations.Orientation;
import com.andtinder.view.SimpleCardStackAdapter;
import com.portol.R;
import com.portol.fragment.clickr.ScannerFragment;

import android.support.v4.app.Fragment;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ScannerFragment.OnScannerDetectionListener} interface
 * to handle interaction events.
 * Use the {@link ScannerFragment} factory method to
 * create an instance of this fragment.
 */
public class ProposalFragment extends Fragment {
    // TODO (GOOGLE): Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "qrcontents";

    // TODO (GOOGLE): Rename and change types of parameters
    private String qrcontents;

    private RelativeLayout mDeckBackground;
    private CardContainer mCardContainer;
    private SimpleCardStackAdapter mSimpleCardStackAdapter;

    private OnDecisionListener mDecisionListener;

    public ProposalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qrcontents Parameter 1.
     * @return A new instance of fragment ProposalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProposalFragment newInstance(String qrcontents) {
        ProposalFragment fragment = new ProposalFragment();
        Bundle args = new Bundle();
        //TODO: (AIDAN) Is this doing what I want it to?
        args.putString(ARG_PARAM1, qrcontents);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qrcontents = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.content_propose, container, false);

        mCardContainer = (CardContainer) mFragmentView.findViewById(R.id.cardstack);
        mCardContainer.setOrientation(Orientation.Ordered);
        CardModel mCardModel = buildCard();

        mSimpleCardStackAdapter = new SimpleCardStackAdapter(inflater.getContext());
        mSimpleCardStackAdapter.add(mCardModel);
        mCardContainer.setAdapter(mSimpleCardStackAdapter);

        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDecisionListener = (OnDecisionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Do something everytime the activity regains focus
    }

    //SCANNER LISTENER
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDecisionListener = null;
    }

    private CardModel buildCard() {
        Resources resources = getResources();
        CardModel card = new CardModel("Title1", "Description goes here", resources.getDrawable(R.drawable.heart));

        //TODO: Left bitcoin, Right real money, Down Cancel
        //TODO: Oh, lord... Note the typo -- OnCardDimissed vs. OnCardDismissed
        card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
            @Override
            public void onLike() {
                mDecisionListener.onAccept(qrcontents);
            }

            @Override
            public void onDislike() {
                mDecisionListener.onDecline();
            }

            ;
        });

        return card;
    }

    // Container Activity must implement this interface
    public interface OnDecisionListener {
        public void onAccept(String qrcontents);

        public void onDecline();
    }
}

