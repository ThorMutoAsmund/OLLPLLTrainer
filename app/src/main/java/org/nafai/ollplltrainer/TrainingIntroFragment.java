package org.nafai.ollplltrainer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TrainingIntroFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public TrainingIntroFragment() {
        // Required empty public constructor
    }

    public static TrainingIntroFragment newInstance() {
        TrainingIntroFragment fragment = new TrainingIntroFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_intro, container, false);
        Button startButton = (Button)view.findViewById(R.id.button_start_training);

        startButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = TrainingFragment.newInstance("");
                FragmentManager fragmentManager = TrainingIntroFragment.this.getFragmentManager(); // getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });

        Trainer trainer = new Trainer(this.getContext());
        int percentageOfUnknownOLLsThatCanBeTrained = trainer.getPercentageOfUnknownOLLsThatCanBeTrained();

        TextView textViewPercentTrainable = (TextView)view.findViewById(R.id.textview_percent_trainable);
        textViewPercentTrainable.setText(percentageOfUnknownOLLsThatCanBeTrained + " %");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
