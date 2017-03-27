package com.portol.fragment.clickr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.portol.R;
import com.portol.activity.AddDeviceActivity;
import com.portol.activity.MainActivity;
import com.portol.fragment.BaseFragment;

import me.dm7.barcodescanner.zbar.Result;

/**
 * Panels that contain this fragment must implement the
 * {@link TextPairFragment.OnPairAttemptListener
 * to handle interaction events.
 * <p>
 * FALSE.. no factory method
 * Use the {@link TextPairFragment } factory method to
 * create an instance of this fragment.
 */
public class TextPairFragment extends BaseFragment {
    public static final String TAG = "TextPairFragment";
    AddDeviceActivity parent;
    private EditText mEditText;
    private Button mSubmit;
    private ImageButton backButton;
    private OnPairAttemptListener mOnPairAttempt;

    public TextPairFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO:
    // not actually a todo, but making a note here.
    // onCreateView apparently gets called when you use fragmentManager.add, so be aware of this.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.clickr_text_pair, container, false);

        //Portions
        mEditText = (EditText) mFragmentView.findViewById(R.id.text_pair);
        mSubmit = (Button) mFragmentView.findViewById(R.id.pair_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide soft keyboard
                InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken(), 0);


                doPair();
            }
        });

        backButton = (ImageButton) mFragmentView.findViewById(R.id.textPair_back_button);
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                parent.popBackstack();
            }
        });

        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            parent = (AddDeviceActivity) activity;

            mOnPairAttempt = (OnPairAttemptListener) parent;

        } catch (ClassCastException e) {
            throw new ClassCastException(parent.toString()
                    + " must implement OnPairAttemptListener");
        }
    }

    /*
     * onDetach and onResume are for the Fragment interface.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mOnPairAttempt = null;

    }

    @Override
    public void onResume() {
        super.onResume();

        //Do something everytime the activity regains focus
    }

    public void clearEditText() {
        this.mEditText.setText("");

    }

    private void doPair() {

        String code = this.mEditText.getText().toString();
        Log.i(TAG, "In doPair, code received: " + code);


        Result result = new Result();
        result.setContents(code);
        //Bubble up to listener
        mOnPairAttempt.onTextPairAttempt(result);
    }
    //TODO:ACTUALLY.. maybe this fragment wil handle play pause stops. The only thing to bubble
    // up might be external clickr shit like a qr scan or something.

    /*
    * Panels containing OnPairAttemptListener must implement this interface.
    */
    public interface OnPairAttemptListener {
        // TODO: Update argument type and name
        public void onTextPairAttempt(Result textPair);
    }
}
