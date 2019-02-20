package org.nafai.ollplltrainer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class TrainingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private Prefs mPrefs;

    private String mParam1;

    private WebView mWebView;

    private WebView mSolutionWebView;

    private OnFragmentInteractionListener mListener;

    private TrainingItem mCurrentTrainingItem = null;

    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment newInstance(String param1) {
        TrainingFragment fragment = new TrainingFragment();
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
        this.mPrefs = new Prefs(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        this.mWebView = (WebView)view.findViewById(R.id.web_view);
        this.mSolutionWebView = (WebView)view.findViewById(R.id.web_view_solution);

        // Disable selection
        this.mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                return true;
            }
        });
        this.mSolutionWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                return true;
            }
        });

        Button buttonShowSolution = (Button)view.findViewById(R.id.button_show_solution);
        buttonShowSolution.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showTrainingSetSolution();
            }
        });
        Button buttonISolvedIt = (Button)view.findViewById(R.id.button_i_solved_it);
        buttonISolvedIt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                increaseTimesPracticed();
                clearTrainingSetSolution();
                createNewTrainingSet();
            }
        });
        Button buttonIDidNotSolveIt = (Button)view.findViewById(R.id.button_i_did_not_solve_it);
        buttonIDidNotSolveIt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                clearTrainingSetSolution();
                createNewTrainingSet();
            }
        });

        createNewTrainingSet();

        return view;
    }

    private void createNewTrainingSet() {
        Trainer trainer = new Trainer(this.getContext());
        this.mCurrentTrainingItem = trainer.createNewTrainingItem();

        WebPageGenerator generator = new WebPageGenerator(this.getContext().getAssets());
        String pageData = generator.generateTrainingItem(AlgClass.OLL, this.mCurrentTrainingItem, this.getContext());
        if (pageData != null) {
            this.mWebView.loadDataWithBaseURL(null, pageData, "text/html", "utf-8", null);
        }
    }

    private void showTrainingSetSolution() {
        if (mCurrentTrainingItem == null) {
            return;
        }

        WebPageGenerator generator = new WebPageGenerator(this.getContext().getAssets());
        String pageData = generator.generateTrainingItemSolution(AlgClass.OLL, this.mCurrentTrainingItem, this.getContext());
        this.mSolutionWebView.loadDataWithBaseURL(null, pageData, "text/html", "utf-8", null);
    }

    private void increaseTimesPracticed() {
        if (this.mCurrentTrainingItem != null) {
            this.mPrefs.increaseTimesPracticed(AlgClass.OLL, this.mCurrentTrainingItem.IdOfAlgToPractice);
        }
    }

    private void clearTrainingSetSolution() {
        this.mSolutionWebView.loadUrl("about:blank");
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
