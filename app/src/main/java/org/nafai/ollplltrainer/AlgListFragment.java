package org.nafai.ollplltrainer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

public class AlgListFragment extends Fragment {
    private static final String ARG_ALG_CLASS = "algClass";

    // TODO: Rename and change types of parameters
    private AlgClass mAlgClass;

    private OnFragmentInteractionListener mListener;

    public AlgListFragment() {
        // Required empty public constructor
        this.mAlgClass = AlgClass.OLL; // default
    }

    public static AlgListFragment newInstance(AlgClass algClass) {
        AlgListFragment fragment = new AlgListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALG_CLASS, algClass.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlgClass = AlgClass.valueOf(getArguments().getString(ARG_ALG_CLASS));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alg_list, container, false);

        WebPageGenerator generator = new WebPageGenerator(this.getContext().getAssets());
        String pageData = generator.Generate(this.mAlgClass);

        WebView webView = (WebView)view.findViewById(R.id.web_view);
        if (pageData != null) {
            //wv.loadUrl(url);
            webView.loadData(pageData, "text/html; charset=utf-8", "UTF-8");
        }

        // Disable selection
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                return true;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
